#!/usr/bin/env python3
"""
Merge *-service/docs/project/source/*.md files into global platform source docs.

The generated frontmatter keeps the same keys expected by docs/project/source/schema.ts.
Service boundaries are emitted as YAML comments inside merged arrays/maps and as HTML
comments around each service's Markdown notes.

Usage:
    python scripts/merge_service_sources.py
    python scripts/merge_service_sources.py --dry-run
    python scripts/merge_service_sources.py --file APISchema.md
"""

from __future__ import annotations

import argparse
import re
import sys
from dataclasses import dataclass
from pathlib import Path
from typing import Callable


PLATFORM_NAME = "Drugstore Platform"
PLATFORM_REPO = "https://github.com/alexisTrejo11/drugstore-platform"
GLOBAL_SOURCE_DIR = "docs/project/source"

DOC_TITLES = {
    "APISchema.md": "API Schema",
    "ProjectArchitecture.md": "Project Architecture",
    "ProjectCodeShowCase.md": "Project Code Showcase",
    "ProjectFeature.md": "Project Features",
    "ProjectInfrastructure.md": "Project Infrastructure",
    "ProjectMetadata.md": "Project Metadata",
    "ProjectOverview.md": "Project Overview",
}


@dataclass(frozen=True)
class SourceDocument:
    service_name: str
    source_path: Path
    raw_frontmatter: str
    body: str


def split_frontmatter(text: str, source_path: Path) -> tuple[str, str]:
    """Return raw YAML frontmatter and Markdown body."""
    text = text.lstrip("\ufeff")
    if not text.startswith("---"):
        return "", text.strip()

    lines = text.splitlines(keepends=True)
    if not lines or lines[0].strip() != "---":
        return "", text.strip()

    closing_index = None
    for index, line in enumerate(lines[1:], start=1):
        if line.strip() == "---":
            closing_index = index
            break

    if closing_index is None:
        raise ValueError(f"Invalid frontmatter in {source_path}: missing closing ---")

    raw_yaml = "".join(lines[1:closing_index]).strip()
    raw_body = "".join(lines[closing_index + 1 :]).strip()
    return raw_yaml, raw_body


def is_service_source_dir(source_dir: Path) -> bool:
    """True for *-service/docs/project/source directories."""
    return (
        source_dir.name == "source"
        and source_dir.parent.name == "project"
        and source_dir.parent.parent.name == "docs"
        and source_dir.parent.parent.parent.name.endswith("-service")
    )


def discover_source_dirs(root: Path, service_glob: str, output_dir: Path) -> list[Path]:
    """Find service docs/project/source directories while excluding the global output."""
    output_dir = output_dir.resolve()
    source_dirs: list[Path] = []

    for source_dir in root.glob(service_glob):
        if not source_dir.is_dir():
            continue

        resolved = source_dir.resolve()
        if resolved == output_dir:
            continue
        if not is_service_source_dir(source_dir):
            continue
        if not any(source_dir.glob("*.md")):
            continue

        source_dirs.append(source_dir)

    return sorted(source_dirs, key=service_name_for)


def service_name_for(source_dir: Path) -> str:
    """Return the service folder name for a docs/project/source directory."""
    if is_service_source_dir(source_dir):
        return source_dir.parent.parent.parent.name
    return source_dir.parent.name


def load_documents(
    root: Path, source_dirs: list[Path], selected_files: set[str] | None
) -> dict[str, list[SourceDocument]]:
    grouped: dict[str, list[SourceDocument]] = {}

    for source_dir in source_dirs:
        service_name = service_name_for(source_dir)
        for source_path in sorted(source_dir.glob("*.md"), key=lambda path: path.name):
            if selected_files and source_path.name not in selected_files:
                continue

            raw_frontmatter, body = split_frontmatter(
                source_path.read_text(encoding="utf-8"), source_path
            )
            grouped.setdefault(source_path.name, []).append(
                SourceDocument(
                    service_name=service_name,
                    source_path=source_path.relative_to(root),
                    raw_frontmatter=raw_frontmatter,
                    body=body,
                )
            )

    return {
        filename: sorted(docs, key=lambda doc: doc.service_name)
        for filename, docs in sorted(grouped.items())
    }


def title_for(filename: str) -> str:
    if filename in DOC_TITLES:
        return DOC_TITLES[filename]
    stem = Path(filename).stem
    return re.sub(r"(?<!^)([A-Z])", r" \1", stem).replace("_", " ").title()


def yaml_string(value: str) -> str:
    escaped = value.replace("\\", "\\\\").replace('"', '\\"')
    return f'"{escaped}"'


def keyed_blocks(lines: list[str], indent: int) -> dict[str, list[str]]:
    pattern = re.compile(rf"^ {{{indent}}}([A-Za-z][A-Za-z0-9_]*):(.*)$")
    blocks: dict[str, list[str]] = {}
    current_key: str | None = None

    for line in lines:
        match = pattern.match(line)
        if match:
            key = match.group(1)
            current_key = key
            blocks[key] = [line]
        elif current_key is not None:
            blocks[current_key].append(line)

    return blocks


def top_level_blocks(raw_frontmatter: str) -> dict[str, list[str]]:
    return keyed_blocks(raw_frontmatter.splitlines(), indent=0)


def top_children(document: SourceDocument, key: str) -> list[str]:
    block = top_level_blocks(document.raw_frontmatter).get(key, [])
    return block_children(block, child_indent=2)


def nested_children(document: SourceDocument, parent_key: str, child_key: str) -> list[str]:
    parent = top_level_blocks(document.raw_frontmatter).get(parent_key, [])
    child = keyed_blocks(parent[1:], indent=2).get(child_key, []) if parent else []
    return block_children(child, child_indent=4)


def block_children(block: list[str], child_indent: int) -> list[str]:
    if not block:
        return []

    inline_value = block[0].split(":", 1)[1].strip()
    if inline_value == "[]":
        return []
    if inline_value:
        return [f"{' ' * child_indent}{inline_value}"]

    children = block[1:]
    if len(children) == 1 and children[0].strip() == "[]":
        return []
    return children


def top_scalar(document: SourceDocument, key: str, default: str = "") -> str:
    return scalar_from_block(
        top_level_blocks(document.raw_frontmatter).get(key, []), indent=0, default=default
    )


def nested_scalar(
    document: SourceDocument, parent_key: str, child_key: str, default: str = ""
) -> str:
    parent = top_level_blocks(document.raw_frontmatter).get(parent_key, [])
    child = keyed_blocks(parent[1:], indent=2).get(child_key, []) if parent else []
    return scalar_from_block(child, indent=2, default=default)


def scalar_from_block(block: list[str], indent: int, default: str = "") -> str:
    if not block:
        return default

    inline_value = block[0].split(":", 1)[1].strip()
    if inline_value in {"|-", "|", ">-", ">"}:
        child_indent = indent + 2
        prefix = " " * child_indent
        return "\n".join(
            line[child_indent:] if line.startswith(prefix) else line.strip()
            for line in block[1:]
        ).strip()
    if inline_value:
        return inline_value.strip('"').strip("'")
    return default


def append_top_list(lines: list[str], key: str, documents: list[SourceDocument]) -> None:
    lines.append(f"{key}:")
    found_any = False
    for document in documents:
        children = top_children(document, key)
        if not children:
            continue
        found_any = True
        lines.append(f"  # {document.service_name}")
        lines.extend(children)
    if not found_any:
        lines.append("  []")


def append_nested_list(
    lines: list[str], parent_key: str, child_key: str, documents: list[SourceDocument]
) -> None:
    lines.append(f"  {child_key}:")
    found_any = False
    for document in documents:
        children = nested_children(document, parent_key, child_key)
        if not children:
            continue
        found_any = True
        lines.append(f"    # {document.service_name}")
        lines.extend(children)
    if not found_any:
        lines.append("    []")


def append_combined_scalar(
    lines: list[str],
    key: str,
    intro: str,
    documents: list[SourceDocument],
    indent: int = 0,
    parent_key: str | None = None,
    child_key: str | None = None,
) -> None:
    prefix = " " * indent
    content_prefix = " " * (indent + 2)
    lines.append(f"{prefix}{key}: |-")
    lines.append(f"{content_prefix}{intro}")

    for document in documents:
        value = (
            nested_scalar(document, parent_key, child_key)
            if parent_key and child_key
            else top_scalar(document, key)
        )
        if not value:
            continue
        lines.append(f"{content_prefix}")
        lines.append(f"{content_prefix}# {document.service_name}")
        for value_line in value.splitlines():
            lines.append(f"{content_prefix}{value_line}")


def render_api_schema(documents: list[SourceDocument]) -> str:
    api_types = {top_scalar(document, "type", "REST") for document in documents}
    lines = [f"type: {'REST' if api_types == {'REST'} else 'Mixed'}"]
    append_top_list(lines, "httpEndpoints", documents)
    return "\n".join(lines)


def render_list_only(documents: list[SourceDocument], keys: list[str]) -> str:
    lines: list[str] = []
    for key in keys:
        append_top_list(lines, key, documents)
    return "\n".join(lines)


def render_overview(documents: list[SourceDocument]) -> str:
    lines = [
        "problemStatement:",
        f'  problemTitle: "{PLATFORM_NAME} Service Problem Statements"',
    ]
    append_combined_scalar(
        lines,
        "problemDescription",
        f"Combined problem descriptions from all {PLATFORM_NAME} services.",
        documents,
        indent=2,
        parent_key="problemStatement",
        child_key="problemDescription",
    )

    lines.append("  problemList:")
    for document in documents:
        children = nested_children(document, "problemStatement", "problemList")
        if children:
            lines.append(f"    # {document.service_name}")
            lines.extend(children)

    lines.extend(
        ["solution:", f'  solutionTitle: "{PLATFORM_NAME} Service Solutions"', "  solutionList:"]
    )
    for document in documents:
        children = nested_children(document, "solution", "solutionList")
        if children:
            lines.append(f"    # {document.service_name}")
            lines.extend(children)

    lines.extend(
        [
            "keyMetrics:",
            f'  metricsTitle: "{PLATFORM_NAME} Service Key Metrics"',
            "  metricsList:",
        ]
    )
    for document in documents:
        children = nested_children(document, "keyMetrics", "metricsList")
        if children:
            lines.append(f"    # {document.service_name}")
            lines.extend(children)

    lines.extend(
        [
            "links:",
            f"  github: {PLATFORM_REPO}",
            "  demo: null",
            f"  documentation: {GLOBAL_SOURCE_DIR}",
            "  dockerHub: null",
            "mediaGallery:",
            f'  title: "{PLATFORM_NAME} Service Gallery"',
            '  description: "Merged media gallery items from all services."',
        ]
    )
    append_nested_list(lines, "mediaGallery", "items", documents)
    append_top_list(lines, "mediaItems", documents)
    append_top_list(lines, "metrics", documents)
    return "\n".join(lines)


def render_architecture(documents: list[SourceDocument]) -> str:
    lines: list[str] = []
    for key in [
        "layers",
        "designPatterns",
        "scalabilityStrategies",
        "securityStrategies",
        "cacheStrategies",
        "architectureFeatures",
    ]:
        append_top_list(lines, key, documents)

    lines.append("architectureDiagram:")
    for key in ["legendItems", "nodes", "connections"]:
        append_nested_list(lines, "architectureDiagram", key, documents)

    lines.append("dataFlow:")
    for key in ["requestFlow", "eventFlow"]:
        append_nested_list(lines, "dataFlow", key, documents)

    lines.append("techDecisions:")
    append_nested_list(lines, "techDecisions", "decisions", documents)
    return "\n".join(lines)


PLATFORM_DESCRIPTION = (
    "Multi-service drugstore e-commerce platform built with Java 23 and Spring Boot. "
    "Eleven bounded contexts (auth, users, catalog, cart, orders, payments, inventory, "
    "stores, addresses, employees, notifications) communicate via REST, gRPC, and "
    "message buses — backed by PostgreSQL, Redis, and observability tooling."
)

PLATFORM_TECH_STACK = [
    "Java 23",
    "Spring Boot 3.3+",
    "Spring Data JPA + Flyway",
    "PostgreSQL 15",
    "Redis 7",
    "Apache Kafka + RabbitMQ",
    "gRPC + Protobuf",
    "Spring Security + JWT + OAuth2",
    "SpringDoc OpenAPI",
    "Micrometer + Prometheus + Loki + Grafana",
    "Docker + Gradle",
    "Lombok",
]


def table_summary(text: str, max_len: int = 90) -> str:
    text = text.strip()
    if not text:
        return "Documentation pending."

    for separator in (". ", ".\n", ": ", " — ", " - "):
        index = text.find(separator)
        if index == -1:
            continue
        end = index + (1 if separator.startswith(".") else 0)
        candidate = text[:end].strip()
        if len(candidate) >= 20:
            text = candidate
            break

    if len(text) <= max_len:
        return text

    trimmed = text[: max_len - 1].rsplit(" ", 1)[0]
    return f"{trimmed}…"


def service_summary(document: SourceDocument) -> tuple[str, str, str]:
    name = top_scalar(document, "name") or document.service_name.replace("-", " ").title()
    description = table_summary(top_scalar(document, "description"))
    status = top_scalar(document, "status", "unknown")
    return name, description, status


def render_metadata_body(documents: list[SourceDocument]) -> str:
    lines = [
        "# Project Metadata",
        "",
        "## About this platform",
        "",
        "Drugstore Platform is a **learning-oriented microservice codebase** for pharmacy "
        "retail. Each `*-service/` folder is a Spring Boot application with its own "
        "database schema, API, and domain model. Together they show how to split a "
        "monolith into focused services while keeping clear boundaries.",
        "",
        "## Services at a glance",
        "",
        "| Service | Purpose | Status |",
        "| --- | --- | --- |",
    ]

    for document in documents:
        _, description, status = service_summary(document)
        lines.append(f"| **{document.service_name}** | {description} | {status} |")

    lines.extend(
        [
            "",
            "## What you'll learn",
            "",
            "- **Bounded contexts** — one deployable service per business capability "
            "(cart, orders, inventory, and similar domains).",
            "- **Layered and hexagonal design** — domain logic isolated from HTTP, "
            "persistence, and messaging adapters.",
            "- **Auth patterns** — JWT access and refresh tokens, OAuth2 social login, "
            "and role-based access control.",
            "- **Data and caching** — PostgreSQL with Flyway migrations; Redis for cache "
            "and rate limiting.",
            "- **Integration styles** — synchronous REST and gRPC; asynchronous Kafka "
            "and RabbitMQ events.",
            "- **Observability** — Actuator health checks, Prometheus metrics, and "
            "centralized logging with Loki.",
            "",
            "## Maintenance",
            "",
            "> Auto-generated by `scripts/merge_service_sources.py`. "
            "Edit service-level `*-service/docs/project/source/ProjectMetadata.md` files, "
            "then run `python scripts/merge_service_sources.py --file ProjectMetadata.md`.",
            "",
        ]
    )
    return "\n".join(lines)


def render_metadata(documents: list[SourceDocument]) -> str:
    lines = [
        'projectId: "drugstore-platform"',
        "featured: true",
        f'name: "{PLATFORM_NAME}"',
        'language: "Java"',
        'category: "microservice-platform"',
        'framework: "Spring Boot"',
        'version: "1.0"',
        f'repositoryUrl: "{PLATFORM_REPO}"',
        "liveDemoUrl: null",
        f"description: {yaml_string(PLATFORM_DESCRIPTION)}",
        "techStack:",
    ]
    lines.extend(f"  - {yaml_string(item)}" for item in PLATFORM_TECH_STACK)
    lines.extend(["status: stable", 'createdAt: "2025-01-01"', 'updatedAt: "2026-06-19"'])
    return "\n".join(lines)


FrontmatterRenderer = Callable[[list[SourceDocument]], str]

FRONTMATTER_RENDERERS: dict[str, FrontmatterRenderer] = {
    "APISchema.md": render_api_schema,
    "ProjectArchitecture.md": render_architecture,
    "ProjectCodeShowCase.md": lambda docs: render_list_only(docs, ["codeExamples"]),
    "ProjectFeature.md": lambda docs: render_list_only(docs, ["features"]),
    "ProjectInfrastructure.md": lambda docs: render_list_only(
        docs, ["deploymentLayers", "dockerFiles", "cloudServices", "metrics"]
    ),
    "ProjectMetadata.md": render_metadata,
    "ProjectOverview.md": render_overview,
}


def merged_frontmatter(filename: str, documents: list[SourceDocument]) -> str:
    renderer = FRONTMATTER_RENDERERS.get(filename)
    if renderer:
        return renderer(documents)

    keys = sorted(top_level_blocks(documents[0].raw_frontmatter))
    return render_list_only(documents, keys)


def merged_body(filename: str, documents: list[SourceDocument]) -> str:
    if filename == "ProjectMetadata.md":
        return render_metadata_body(documents)

    lines = [
        f"# {title_for(filename)}",
        "",
        "> Auto-generated by `scripts/merge_service_sources.py`. "
        "Edit service-level `docs/project/source/*.md` files, then regenerate.",
        "",
    ]

    for document in documents:
        lines.extend(
            [
                f"<!-- BEGIN {document.service_name} -->",
                f"<!-- Source: {document.source_path.as_posix()} -->",
            ]
        )

        if document.body:
            lines.extend([document.body, ""])
        else:
            lines.extend(["_No additional notes._", ""])

        lines.extend([f"<!-- END {document.service_name} -->", ""])

    return "\n".join(lines).rstrip() + "\n"


def render_merged_file(filename: str, documents: list[SourceDocument]) -> str:
    return (
        "---\n"
        f"{merged_frontmatter(filename, documents)}\n"
        "---\n\n"
        f"{merged_body(filename, documents)}"
    )


def repo_root() -> Path:
    return Path(__file__).resolve().parents[1]


def parse_args() -> argparse.Namespace:
    root = repo_root()
    parser = argparse.ArgumentParser(
        description=(
            "Merge *-service/docs/project/source YAML frontmatter into "
            f"{GLOBAL_SOURCE_DIR}/."
        )
    )
    parser.add_argument(
        "--root",
        type=Path,
        default=root,
        help="Repository root (default: parent of scripts/).",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=root / GLOBAL_SOURCE_DIR,
        help=f"Output directory for merged source files (default: {GLOBAL_SOURCE_DIR}).",
    )
    parser.add_argument(
        "--service-glob",
        default="*-service/docs/project/source",
        help="Glob used from --root to discover service source dirs.",
    )
    parser.add_argument(
        "--file",
        action="append",
        default=[],
        help="Only merge a specific source filename. Can be repeated.",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Print planned writes without changing files.",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    root = args.root.resolve()
    output_dir = args.output.resolve()
    selected_files = set(args.file) if args.file else None

    if not root.is_dir():
        print(f"Root directory not found: {root}", file=sys.stderr)
        return 1

    source_dirs = discover_source_dirs(root, args.service_glob, output_dir)
    if not source_dirs:
        print(f"No service source directories found with glob: {args.service_glob}")
        return 1

    grouped = load_documents(root, source_dirs, selected_files)
    if selected_files:
        missing = sorted(selected_files.difference(grouped))
        if missing:
            print(f"No matching service docs found for: {', '.join(missing)}")
            return 1
    if not grouped:
        print("No source documents found.")
        return 1

    if not args.dry_run:
        output_dir.mkdir(parents=True, exist_ok=True)

    for filename, documents in grouped.items():
        output_path = output_dir / filename
        content = render_merged_file(filename, documents)
        if args.dry_run:
            services = ", ".join(document.service_name for document in documents)
            print(f"Would write {output_path} ({len(documents)} service(s): {services})")
            continue

        output_path.write_text(content, encoding="utf-8")
        print(f"Wrote {output_path} ({len(documents)} service(s))")

    print(f"\nDone - {len(grouped)} merged file(s).")
    return 0


if __name__ == "__main__":
    sys.exit(main())
