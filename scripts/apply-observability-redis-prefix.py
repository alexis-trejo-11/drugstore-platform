#!/usr/bin/env python3
"""Apply address-service observability + Redis prefix patterns across monorepo services."""

from __future__ import annotations

import re
import shutil
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

SERVICES = [
    {
        "name": "auth-service",
        "pkg": "io.github.alexisTrejo11.drugstore.accounts.config",
        "redis_prefix": "drugstore:auth:",
        "app_name": "auth-service",
        "prom_target": "auth-service:8080",
        "redis": True,
        "redis_config": "RedisConfig.java",
        "cache_config": None,
        "rate_limiter_config": None,
    },
    {
        "name": "cart-service",
        "pkg": "io.github.alexisTrejo11.drugstore.carts.config",
        "redis_prefix": "drugstore:cart:",
        "app_name": "cart-service",
        "prom_target": "cart-service:8080",
        "redis": True,
        "redis_config": "RedisConfig.java",
        "cache_config": "RedisCacheConfig.java",
        "rate_limiter_config": None,
    },
    {
        "name": "employee-service",
        "pkg": "io.github.alexisTrejo11.drugstore.employees.config",
        "redis_prefix": "drugstore:employee:",
        "app_name": "employee-service",
        "prom_target": "employee-service:8080",
        "redis": True,
        "redis_config": None,
        "cache_config": "ratelimit/RedisCacheConfig.java",
        "rate_limiter_config": "ratelimit/RateLimiterConfig.java",
    },
    {
        "name": "inventory-service",
        "pkg": "io.github.alexisTrejo11.drugstore.inventories.config",
        "redis_prefix": "drugstore:inventory:",
        "app_name": "inventory-service",
        "prom_target": "inventory-service:8080",
        "redis": True,
        "redis_config": None,
        "cache_config": "ratelimit/RedisCacheConfig.java",
        "rate_limiter_config": "ratelimit/RateLimiterConfig.java",
    },
    {
        "name": "notification-service",
        "pkg": "io.github.alexisTrejo11.drugstore.notifications.config",
        "redis_prefix": "drugstore:notification:",
        "app_name": "notification-service",
        "prom_target": "notification-service:8080",
        "redis": False,
        "redis_config": None,
        "cache_config": None,
        "rate_limiter_config": None,
    },
    {
        "name": "order-service",
        "pkg": "io.github.alexisTrejo11.drugstore.order.config",
        "redis_prefix": "drugstore:order:",
        "app_name": "order-service",
        "prom_target": "order-service:8080",
        "redis": True,
        "redis_config": None,
        "cache_config": "ratelimit/RedisCacheConfig.java",
        "rate_limiter_config": "ratelimit/RateLimiterConfig.java",
    },
    {
        "name": "payment-service",
        "pkg": "io.github.alexisTrejo11.drugstore.payments.config",
        "redis_prefix": "drugstore:payment:",
        "app_name": "payment-service",
        "prom_target": "payment-service:8080",
        "redis": True,
        "redis_config": "RedisConfig.java",
        "cache_config": None,
        "rate_limiter_config": None,
    },
    {
        "name": "product-service",
        "pkg": "io.github.alexisTrejo11.drugstore.products.config",
        "redis_prefix": "drugstore:product:",
        "app_name": "product-service",
        "prom_target": "product-service:8080",
        "redis": True,
        "redis_config": None,
        "cache_config": "RedisCacheConfig.java",
        "rate_limiter_config": "ratelimit/RateLimiterConfig.java",
    },
    {
        "name": "store-service",
        "pkg": "io.github.alexisTrejo11.drugstore.stores.config",
        "redis_prefix": "drugstore:store:",
        "app_name": "store-service",
        "prom_target": "store-service:8080",
        "redis": True,
        "redis_config": None,
        "cache_config": None,
        "rate_limiter_config": "ratelimit/RateLimiterConfig.java",
    },
    {
        "name": "user-service",
        "pkg": "io.github.alexisTrejo11.drugstore.users.config",
        "redis_prefix": "drugstore:user:",
        "app_name": "user-service",
        "prom_target": "user-service:8080",
        "redis": True,
        "redis_config": "RedisConfig.java",
        "cache_config": None,
        "rate_limiter_config": "ratelimit/RateLimiterConfig.java",
    },
]

REDIS_PROPERTIES = '''package {pkg};

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "app.redis")
@Data
public class RedisProperties {{

  private String keyPrefix = "{prefix}";

  public String normalizedPrefix() {{
    if (keyPrefix == null || keyPrefix.isBlank()) {{
      return "";
    }}
    return keyPrefix.endsWith(":") ? keyPrefix : keyPrefix + ":";
  }}

  public String prefixKey(String key) {{
    if (key == null || key.isBlank()) {{
      return normalizedPrefix();
    }}
    String prefix = normalizedPrefix();
    if (prefix.isEmpty()) {{
      return key;
    }}
    return key.startsWith(prefix) ? key : prefix + key;
  }}
}}
'''

PREFIXED_SERIALIZER = '''package {pkg};

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class PrefixedStringRedisSerializer implements RedisSerializer<String> {{

  private final RedisProperties redisProperties;
  private final StringRedisSerializer delegate = StringRedisSerializer.UTF_8;

  public PrefixedStringRedisSerializer(RedisProperties redisProperties) {{
    this.redisProperties = redisProperties;
  }}

  @Override
  public byte[] serialize(String key) {{
    if (key == null) {{
      return null;
    }}
    return delegate.serialize(redisProperties.prefixKey(key));
  }}

  @Override
  public String deserialize(byte[] bytes) {{
    if (bytes == null) {{
      return null;
    }}
    String key = delegate.deserialize(bytes);
    String prefix = redisProperties.normalizedPrefix();
    if (!prefix.isEmpty() && key.startsWith(prefix)) {{
      return key.substring(prefix.length());
    }}
    return key;
  }}
}}
'''

REDIS_CONFIG_BEAN = '''package {pkg};

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {{

  @Bean
  public RedisTemplate<String, Object> redisTemplate(
      RedisConnectionFactory connectionFactory,
      RedisProperties redisProperties) {{
    PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);

    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(keySerializer);
    template.setHashKeySerializer(keySerializer);
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.afterPropertiesSet();
    return template;
  }}

  @Bean
  public StringRedisTemplate stringRedisTemplate(
      RedisConnectionFactory connectionFactory,
      RedisProperties redisProperties) {{
    StringRedisTemplate template = new StringRedisTemplate(connectionFactory);
    template.setKeySerializer(new PrefixedStringRedisSerializer(redisProperties));
    template.setHashKeySerializer(new PrefixedStringRedisSerializer(redisProperties));
    template.afterPropertiesSet();
    return template;
  }}
}}
'''

LOKI_BLOCK_YML = """
loki:
  push-url: ${{{{LOKI_PUSH_URL:{docker_default}}}}}
  env: ${{{{LOKI_ENV:{docker_env}}}}}
"""

REDIS_BLOCK_YML = """
  redis:
    key-prefix: ${{{{REDIS_KEY_PREFIX:{prefix}}}}}}
"""


def pkg_to_path(pkg: str) -> Path:
    return Path(*pkg.split("."))


def delete_legacy_dirs(service_dir: Path) -> None:
    for name in ("nginx", "observability", "logstash"):
        path = service_dir / name
        if path.exists():
            shutil.rmtree(path)
            print(f"  removed {path.relative_to(ROOT)}")


def patch_logback(path: Path, app_name: str) -> None:
    if not path.exists():
        text = f'''<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="LOG_PATH" value="${{LOG_PATH:-logs}}" />
    <property name="APP_NAME" value="${{SPRING_APPLICATION_NAME:-{app_name}}}" />
    <springProperty scope="context" name="LOKI_PUSH_URL" source="loki.push-url" />
    <springProperty scope="context" name="LOKI_ENV" source="loki.env" defaultValue="local" />

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{{yyyy-MM-dd HH:mm:ss.SSS}} [%thread] %-5level %logger{{36}} - %msg%n</pattern>
        </encoder>
    </appender>

    <springProfile name="!test">
        <appender name="FILE_APP" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>${{LOG_PATH}}/${{APP_NAME}}.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>${{LOG_PATH}}/${{APP_NAME}}.%d{{yyyy-MM-dd}}.log.gz</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{{yyyy-MM-dd HH:mm:ss.SSS}} [%thread] %-5level %logger{{36}} - %msg%n</pattern>
            </encoder>
        </appender>

        <appender name="LOKI" class="com.github.loki4j.logback.Loki4jAppender">
            <http>
                <url>${{LOKI_PUSH_URL}}</url>
            </http>
            <format>
                <label>
                    <pattern>app=${{APP_NAME}},env=${{LOKI_ENV}},level=%level</pattern>
                </label>
                <message>
                    <pattern>l=%level c=%logger{{20}} t=%thread traceId=%X{{traceId:-none}} | %msg %ex</pattern>
                </message>
            </format>
        </appender>
    </springProfile>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <springProfile name="!test">
            <appender-ref ref="FILE_APP" />
            <appender-ref ref="LOKI" />
        </springProfile>
    </root>
</configuration>
'''
        path.write_text(text)
        print(f"  created {path.relative_to(ROOT)}")
        return

    text = path.read_text()
    if "springProperty scope=\"context\" name=\"LOKI_PUSH_URL\" source=\"loki.push-url\"" not in text:
        text = re.sub(
            r"(<property name=\"APP_NAME\"[^>]*/>\s*)",
            r"\1    <springProperty scope=\"context\" name=\"LOKI_PUSH_URL\" source=\"loki.push-url\" />\n"
            r"    <springProperty scope=\"context\" name=\"LOKI_ENV\" source=\"loki.env\" defaultValue=\"local\" />\n",
            text,
            count=1,
        )
        text = re.sub(
            r"<property name=\"LOKI_PUSH_URL\" value=\"\$\{LOKI_PUSH_URL:-[^\"]+\}\" />\s*",
            "",
            text,
        )
    text = re.sub(
        r"<url>http://loki:3100/loki/api/v1/push</url>",
        "<url>${LOKI_PUSH_URL}</url>",
        text,
    )
    text = re.sub(
        r"<pattern>app=\$\{APP_NAME\},env=prod,level=%level</pattern>",
        "<pattern>app=${APP_NAME},env=${LOKI_ENV},level=%level</pattern>",
        text,
    )
    path.write_text(text)
    print(f"  updated {path.relative_to(ROOT)}")


def inject_redis_prefix_yaml(path: Path, prefix: str) -> None:
    if not path.exists():
        return
    text = path.read_text()
    marker = "app:\n  redis:\n    key-prefix:"
    if marker in text or "key-prefix: ${REDIS_KEY_PREFIX:" in text:
        return
    block = f"  redis:\n    key-prefix: ${{REDIS_KEY_PREFIX:{prefix}}}\n"
    if re.search(r"^app:\n", text, re.M):
        text = re.sub(r"^app:\n", f"app:\n{block}", text, count=1, flags=re.M)
    else:
        text = text.replace("\nlogging:", f"\napp:\n{block}\nlogging:", 1)
    path.write_text(text)
    print(f"  patched redis prefix in {path.relative_to(ROOT)}")


def inject_loki_yaml(path: Path, docker: bool) -> None:
    if not path.exists():
        return
    text = path.read_text()
    if "loki:\n  push-url:" in text:
        return
    if docker:
        block = (
            "\nloki:\n"
            "  push-url: ${LOKI_PUSH_URL:http://loki:3100/loki/api/v1/push}\n"
            "  env: ${LOKI_ENV:docker}\n"
        )
    else:
        block = (
            "\nloki:\n"
            "  push-url: ${LOKI_PUSH_URL:http://localhost:3100/loki/api/v1/push}\n"
            "  env: ${LOKI_ENV:local}\n"
        )
    if "\nlogging:" in text:
        text = text.replace("\nlogging:", block + "\nlogging:", 1)
    else:
        text += block
    path.write_text(text)
    print(f"  patched loki in {path.relative_to(ROOT)}")


def patch_env_example(path: Path, prefix: str, include_redis: bool) -> None:
    if not path.exists():
        return
    text = path.read_text()
    if "LOKI_PUSH_URL=" not in text:
        loki = """
# --- Optional: logging / Loki ---
# Docker full stack:  http://loki:3100/loki/api/v1/push
# Local Loki on host: http://localhost:3100/loki/api/v1/push
# AWS / Grafana Cloud: https://logs-prod-xxx.grafana.net/loki/api/v1/push
LOKI_PUSH_URL=http://loki:3100/loki/api/v1/push
# LOKI_ENV=local|docker|prod
"""
        if "# --- Optional: logging" in text:
            text = text.replace("# --- Optional: logging", loki.strip() + "\n\n# --- Optional: logging", 1)
        elif "REDIS_URL=" in text:
            text = re.sub(r"(REDIS_URL=.*\n)", r"\1" + loki, text, count=1)
        else:
            text += loki

    if include_redis and "REDIS_KEY_PREFIX=" not in text:
        text = re.sub(
            r"(REDIS_URL=.*\n)",
            r"\1REDIS_KEY_PREFIX=" + prefix + "\n",
            text,
            count=1,
        )
    path.write_text(text)
    print(f"  updated {path.relative_to(ROOT)}")


def patch_rate_limiter_config(path: Path, pkg: str) -> None:
    if not path.exists():
        return
    text = path.read_text()
    if "PrefixedStringRedisSerializer" in text:
        return
    if "RedisTemplate" not in text:
        return
    imports = f"import {pkg}.PrefixedStringRedisSerializer;\nimport {pkg}.RedisProperties;\n"
    if imports.strip() not in text:
        text = text.replace("import org.springframework.context.annotation.Bean;", imports + "import org.springframework.context.annotation.Bean;")
    text = re.sub(
        r"public RedisTemplate<String, Object> redisTemplate\(RedisConnectionFactory connectionFactory\)",
        "public RedisTemplate<String, Object> redisTemplate(\n      RedisConnectionFactory connectionFactory,\n      RedisProperties redisProperties)",
        text,
    )
    text = text.replace(
        "template.setKeySerializer(new StringRedisSerializer());",
        "PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);\n    template.setKeySerializer(keySerializer);\n    template.setHashKeySerializer(keySerializer);",
    )
    text = re.sub(r"\nimport org.springframework.data.redis.serializer.StringRedisSerializer;\n", "\n", text)
    path.write_text(text)
    print(f"  patched {path.relative_to(ROOT)}")


def patch_redis_config(path: Path, pkg: str) -> None:
    if not path.exists():
        return
    text = path.read_text()
    if "PrefixedStringRedisSerializer" in text:
        return
    if "StringRedisTemplate" in text and "redisTemplate" in text:
        # auth-style RedisConfig
        if f"import {pkg}.RedisProperties;" not in text:
            text = text.replace(
                "import org.springframework.context.annotation.Configuration;",
                f"import {pkg}.PrefixedStringRedisSerializer;\nimport {pkg}.RedisProperties;\nimport org.springframework.context.annotation.Configuration;",
            )
        text = re.sub(
            r"public RedisTemplate<String, Object> redisTemplate\(RedisConnectionFactory connectionFactory\)",
            "public RedisTemplate<String, Object> redisTemplate(\n      RedisConnectionFactory connectionFactory,\n      RedisProperties redisProperties)",
            text,
        )
        text = re.sub(
            r"StringRedisSerializer stringSerializer = new StringRedisSerializer\(\);\s*template.setKeySerializer\(stringSerializer\);\s*template.setHashKeySerializer\(stringSerializer\);",
            "PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);\n    template.setKeySerializer(keySerializer);\n    template.setHashKeySerializer(keySerializer);",
            text,
            flags=re.S,
        )
        text = re.sub(
            r"public StringRedisTemplate stringRedisTemplate\(RedisConnectionFactory connectionFactory\) \{\s*return new StringRedisTemplate\(connectionFactory\);\s*\}",
            "public StringRedisTemplate stringRedisTemplate(\n      RedisConnectionFactory connectionFactory,\n      RedisProperties redisProperties) {\n    StringRedisTemplate template = new StringRedisTemplate(connectionFactory);\n    PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);\n    template.setKeySerializer(keySerializer);\n    template.setHashKeySerializer(keySerializer);\n    template.afterPropertiesSet();\n    return template;\n  }",
            text,
            flags=re.S,
        )
        text = re.sub(r"\nimport org.springframework.data.redis.serializer.StringRedisSerializer;\n", "\n", text)
    elif "CacheManager cacheManager" in text:
        # user-style combined config
        if f"import {pkg}.RedisProperties;" not in text:
            text = text.replace(
                "import org.springframework.context.annotation.Configuration;",
                f"import {pkg}.PrefixedStringRedisSerializer;\nimport {pkg}.RedisProperties;\nimport org.springframework.context.annotation.Configuration;",
            )
        text = re.sub(
            r"public RedisTemplate<String, Object> redisTemplate\(RedisConnectionFactory connectionFactory\)",
            "public RedisTemplate<String, Object> redisTemplate(\n        RedisConnectionFactory connectionFactory,\n        RedisProperties redisProperties)",
            text,
        )
        text = text.replace(
            "template.setKeySerializer(new StringRedisSerializer());",
            "PrefixedStringRedisSerializer keySerializer = new PrefixedStringRedisSerializer(redisProperties);\n        template.setKeySerializer(keySerializer);",
        )
        text = text.replace(
            "template.setHashKeySerializer(new StringRedisSerializer());",
            "template.setHashKeySerializer(keySerializer);",
        )
        text = re.sub(
            r"public CacheManager cacheManager\(RedisConnectionFactory connectionFactory\)",
            "public CacheManager cacheManager(\n        RedisConnectionFactory connectionFactory,\n        RedisProperties redisProperties)",
            text,
        )
        text = text.replace(
            "RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()\n            .entryTtl",
            "RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()\n            .prefixCacheNameWith(redisProperties.normalizedPrefix())\n            .entryTtl",
        )
        text = re.sub(r"\nimport org.springframework.data.redis.serializer.StringRedisSerializer;\n", "\n", text)
    path.write_text(text)
    print(f"  patched {path.relative_to(ROOT)}")


def patch_cache_config(path: Path, pkg: str) -> None:
    if not path.exists():
        return
    text = path.read_text()
    if "RedisProperties" in text and "prefixCacheNameWith" in text:
        return
    if f"import {pkg}.RedisProperties;" not in text:
        text = text.replace(
            "import org.springframework.cache.CacheManager;",
            f"import {pkg}.RedisProperties;\nimport org.springframework.cache.CacheManager;",
        )
    text = re.sub(
        r"public CacheManager cacheManager\(RedisConnectionFactory connectionFactory\)",
        "public CacheManager cacheManager(\n      RedisConnectionFactory connectionFactory,\n      RedisProperties redisProperties)",
        text,
    )
    if ".prefixCacheNameWith" not in text:
        text = text.replace(
            "RedisCacheConfiguration.defaultCacheConfig()\n        .entryTtl",
            "RedisCacheConfiguration.defaultCacheConfig()\n        .prefixCacheNameWith(redisProperties.normalizedPrefix())\n        .entryTtl",
        )
        text = text.replace(
            "RedisCacheConfiguration.defaultCacheConfig()\n            .entryTtl",
            "RedisCacheConfiguration.defaultCacheConfig()\n            .prefixCacheNameWith(redisProperties.normalizedPrefix())\n            .entryTtl",
        )
    path.write_text(text)
    print(f"  patched cache {path.relative_to(ROOT)}")


def ensure_redis_java(service: dict, service_dir: Path) -> None:
    if not service["redis"]:
        return
    pkg = service["pkg"]
    java_dir = service_dir / "src/main/java" / pkg_to_path(pkg)
    java_dir.mkdir(parents=True, exist_ok=True)

    props = java_dir / "RedisProperties.java"
    if not props.exists():
        props.write_text(REDIS_PROPERTIES.format(pkg=pkg, prefix=service["redis_prefix"]))
        print(f"  created {props.relative_to(ROOT)}")

    ser = java_dir / "PrefixedStringRedisSerializer.java"
    if not ser.exists():
        ser.write_text(PREFIXED_SERIALIZER.format(pkg=pkg))
        print(f"  created {ser.relative_to(ROOT)}")

    if service["redis_config"]:
        cfg = java_dir / service["redis_config"]
        if not cfg.exists():
            cfg.write_text(REDIS_CONFIG_BEAN.format(pkg=pkg))
            print(f"  created {cfg.relative_to(ROOT)}")
        else:
            patch_redis_config(cfg, pkg)

    if service["rate_limiter_config"]:
        patch_rate_limiter_config(java_dir / service["rate_limiter_config"], pkg)

    if service["cache_config"]:
        patch_cache_config(java_dir / service["cache_config"], pkg)


def patch_build_gradle(path: Path) -> None:
    if not path.exists():
        return
    text = path.read_text()
    additions = []
    if "loki-logback-appender" not in text:
        additions.append("    implementation 'com.github.loki4j:loki-logback-appender:1.5.2'")
    if "micrometer-registry-prometheus" not in text:
        additions.append("    implementation 'io.micrometer:micrometer-registry-prometheus'")
    if not additions:
        return
    text = text.replace(
        "dependencies {",
        "dependencies {\n" + "\n".join(additions),
        1,
    )
    path.write_text(text)
    print(f"  updated {path.relative_to(ROOT)} dependencies")


def main() -> None:
    for service in SERVICES:
        name = service["name"]
        service_dir = ROOT / name
        if not service_dir.is_dir():
            print(f"SKIP missing {name}")
            continue
        print(f"\n=== {name} ===")
        delete_legacy_dirs(service_dir)
        patch_logback(service_dir / "src/main/resources/logback-spring.xml", service["app_name"])

        inject_loki_yaml(service_dir / "src/main/resources/application.yml", docker=False)
        inject_loki_yaml(service_dir / "src/main/resources/application-docker.yml", docker=True)
        if service["redis"]:
            inject_redis_prefix_yaml(service_dir / "src/main/resources/application.yml", service["redis_prefix"])
            inject_redis_prefix_yaml(service_dir / "src/main/resources/application-docker.yml", service["redis_prefix"])

        patch_env_example(service_dir / ".env.example", service["redis_prefix"], service["redis"])
        ensure_redis_java(service, service_dir)
        if name == "notification-service":
            patch_build_gradle(service_dir / "build.gradle")

    print("\nDone.")


if __name__ == "__main__":
    main()
