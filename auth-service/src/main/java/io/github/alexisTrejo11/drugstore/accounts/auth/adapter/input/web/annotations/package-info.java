/**
 * Composed OpenAPI "decorators": each {@code @XxxOperation} bundles {@code @Operation} and
 * {@code @ApiResponses} (and {@code @SecurityRequirement} when needed) so controllers stay lean.
 * SpringDoc resolves these via Spring's merged annotation model (same as using the raw swagger
 * annotations on the method).
 */
package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations;
