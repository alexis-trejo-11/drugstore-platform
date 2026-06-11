package libs_kernel.config.rate_limit;

public enum RateLimitProfile {
	STANDARD,
	SENSITIVE,
	PUBLIC,
	ADMIN,
	/** Customer-facing reads; maps to YAML key {@code customer-read}. */
	CUSTOMER_READ,
	/** Customer-facing writes; maps to YAML key {@code customer-write}. */
	CUSTOMER_WRITE
}