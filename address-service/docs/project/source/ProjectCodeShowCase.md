---
codeExamples:
  - id: "postal-code-validator-factory"
    title: "Postal Code Validator Factory Pattern"
    description: "Factory pattern implementation that creates country-specific postal code validators based on ISO country code"
    category: "Design Patterns"
    duration: "5 min read"
    views: 0
    tags:
      - "Factory Pattern"
      - "Validation"
      - "Strategy Pattern"
      - "Multi-Country Support"
    files:
      - name: "PostalCodeValidatorFactory.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation/PostalCodeValidatorFactory.java"
        language: "java"
        content: |
          package io.github.alexisTrejo11.drugstore.address.utils.validation;

          import java.util.HashMap;
          import java.util.List;
          import java.util.Map;

          import org.springframework.stereotype.Component;

          /**
           * Factory for managing postal code validators by country
           */
          @Component
          public class PostalCodeValidatorFactory {

            private final Map<String, PostalCodeValidator> validators;

            public PostalCodeValidatorFactory(List<PostalCodeValidator> validatorList) {
              this.validators = new HashMap<>();
              validatorList.forEach(validator -> validators.put(validator.getCountryCode().toUpperCase(), validator));
            }

            /**
             * Gets validator for a specific country
             *
             * @param countryCode ISO 3166-1 alpha-2 country code
             * @return validator or default validator if country not supported
             */
            public PostalCodeValidator getValidator(String countryCode) {
              return validators.getOrDefault(countryCode.toUpperCase(), new DefaultPostalCodeValidator());
            }

            /**
             * Checks if a country has specific validation rules
             *
             * @param countryCode ISO 3166-1 alpha-2 country code
             * @return true if specific validator exists
             */
            public boolean hasValidator(String countryCode) {
              return validators.containsKey(countryCode.toUpperCase());
            }
          }
        highlighted: true
        explanation: "Factory uses Spring's dependency injection to collect all PostalCodeValidator implementations and stores them in a map keyed by country code. getValidator() returns the appropriate validator or falls back to DefaultPostalCodeValidator."

      - name: "PostalCodeValidator.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation/PostalCodeValidator.java"
        language: "java"
        content: |
          package io.github.alexisTrejo11.drugstore.address.utils.validation;

          /**
           * Interface for country-specific postal code validation
           */
          public interface PostalCodeValidator {

            /**
             * Validates if the given postal code matches the expected format
             *
             * @param postalCode the postal code to validate
             * @return true if valid, false otherwise
             */
            boolean isValid(String postalCode);

            /**
             * Returns the ISO 3166-1 alpha-2 country code for this validator
             *
             * @return country code (e.g., "US", "MX")
             */
            String getCountryCode();

            /**
             * Returns a human-readable description of the expected format
             *
             * @return format description
             */
            String getFormatDescription();
          }
        highlighted: false
        explanation: "Strategy interface that defines the contract for all postal code validators."

      - name: "USPostalCodeValidator.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation/USPostalCodeValidator.java"
        language: "java"
        content: |
          package io.github.alexisTrejo11.drugstore.address.utils.validation;

          import org.springframework.stereotype.Component;

          /**
           * Validator for United States ZIP codes
           * Format: 12345 or 12345-6789
           */
          @Component
          public class USPostalCodeValidator implements PostalCodeValidator {

              private static final String ZIP_REGEX = "^\\d{5}(-\\d{4})?$";

              @Override
              public boolean isValid(String postalCode) {
                  return postalCode != null && postalCode.matches(ZIP_REGEX);
              }

              @Override
              public String getCountryCode() {
                  return "US";
              }

              @Override
              public String getFormatDescription() {
                  return "US ZIP code (12345 or 12345-6789)";
              }
          }
        highlighted: false
        explanation: "Example of a concrete strategy implementation for US ZIP codes."

  - id: "rate-limiting-aspect"
    title: "Redis Rate Limiting with AOP Aspect"
    description: "Aspect-oriented programming implementation for rate limiting API endpoints using Redis as distributed counter"
    category: "Security"
    duration: "7 min read"
    views: 0
    tags:
      - "AOP"
      - "Rate Limiting"
      - "Redis"
      - "Security"
    files:
      - name: "RateLimitAspect.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/config/ratelimit/RateLimitAspect.java"
        language: "java"
        content: |
          package io.github.alexisTrejo11.drugstore.address.config.ratelimit;

          import jakarta.servlet.http.HttpServletRequest;
          import libs_kernel.config.rate_limit.RateLimit;
          import libs_kernel.config.rate_limit.RateLimitProfile;
          import libs_kernel.response.ResponseWrapper;
          import lombok.RequiredArgsConstructor;
          import lombok.extern.slf4j.Slf4j;
          import org.aspectj.lang.ProceedingJoinPoint;
          import org.aspectj.lang.annotation.Around;
          import org.aspectj.lang.annotation.Aspect;
          import org.springframework.http.HttpStatus;
          import org.springframework.web.context.request.RequestContextHolder;
          import org.springframework.web.context.request.ServletRequestAttributes;

          import java.time.Duration;

          @Aspect
          @Component
          @Slf4j
          @RequiredArgsConstructor
          public class RateLimitAspect {

              private final RedisRateLimiter redisRateLimiter;

              @Around("@annotation(rateLimit)")
              public Object checkRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
                  RateLimitProfile profile = rateLimit.profile();

                  HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
                  String clientKey = getClientKey(request, profile);

                  int maxRequests = getMaxRequests(profile);
                  Duration duration = getDuration(profile);

                  if (!redisRateLimiter.isAllowed(clientKey, maxRequests, duration)) {
                      log.warn("Rate limit exceeded for key: {}", clientKey);
                      return ResponseWrapper.error(HttpStatus.TOO_MANY_REQUESTS.value(), "Rate limit exceeded. Please try again later.", "RATE_LIMIT_EXCEEDED");
                  }

                  return joinPoint.proceed();
              }

              private String getClientKey(HttpServletRequest request, RateLimitProfile profile) {
                  String clientIp = getClientIp(request);
                  String endpoint = request.getRequestURI();
                  return String.format("%s:%s:%s", profile.name(), clientIp, endpoint);
              }

              private String getClientIp(HttpServletRequest request) {
                  String forwardedFor = request.getHeader("X-Forwarded-For");
                  if (forwardedFor != null && !forwardedFor.isEmpty()) {
                      return forwardedFor.split(",")[0].trim();
                  }
                  return request.getRemoteAddr();
              }

              private int getMaxRequests(RateLimitProfile profile) {
                  return switch (profile) {
                      case STANDARD -> 60;
                      case SENSITIVE -> 10;
                  };
              }

              private Duration getDuration(RateLimitProfile profile) {
                  return Duration.ofMinutes(1);
              }
          }
        highlighted: true
        explanation: "AOP aspect that intercepts methods annotated with @RateLimit and checks Redis-based rate limiting before proceeding with the method execution."

      - name: "RedisRateLimiter.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/config/ratelimit/RedisRateLimiter.java"
        language: "java"
        content: |
          package io.github.alexisTrejo11.drugstore.address.config.ratelimit;

          import lombok.RequiredArgsConstructor;
          import org.springframework.data.redis.core.RedisTemplate;
          import org.springframework.data.redis.core.ValueOperations;
          import org.springframework.stereotype.Component;

          import java.time.Duration;
          import java.util.concurrent.TimeUnit;

          @Component
          @RequiredArgsConstructor
          public class RedisRateLimiter {
            private final RedisTemplate<String, Object> redisTemplate;

            public boolean isAllowed(String key, int maxRequests, Duration duration) {
              String redisKey = "rate_limit:" + key;
              ValueOperations<String, Object> ops = redisTemplate.opsForValue();
              Long current = ops.increment(redisKey);

              if (current == null) {
                return false;
              }

              if (current == 1) {
                redisTemplate.expire(redisKey, duration.toSeconds(), TimeUnit.SECONDS);
              }

              return current <= maxRequests;
            }

            public RateLimitInfo getRateLimitInfo(String key, int maxRequests) {
              String redisKey = "rate_limit:" + key;
              ValueOperations<String, Object> ops = redisTemplate.opsForValue();

              Object currentObj = ops.get(redisKey);
              long current = switch (currentObj) {
                case null -> 0;
                case Integer i -> i.longValue();
                case Long l -> l;
                default -> Long.parseLong(currentObj.toString());
              };

              long remaining = Math.max(0, maxRequests - current);
              long resetAfter = getTTL(redisKey);

              return new RateLimitInfo(maxRequests, remaining, resetAfter);
            }

            private Long getTTL(String key) {
              return redisTemplate.getExpire(key, TimeUnit.SECONDS);
            }

            public void resetLimit(String key) {
              String redisKey = "rate_limit:" + key;
              redisTemplate.delete(redisKey);
            }

            public static class RateLimitInfo {
              private final long limit;
              private final long remaining;
              private final long resetAfter;

              public RateLimitInfo(long limit, long remaining, long resetAfter) {
                this.limit = limit;
                this.remaining = remaining;
                this.resetAfter = resetAfter;
              }

              public long getLimit() { return limit; }
              public long getRemaining() { return remaining; }
              public long getResetAfter() { return resetAfter; }
            }
          }
        highlighted: false
        explanation: "Redis-backed rate limiter using token bucket algorithm. Uses atomic increment and TTL for time window."

  - id: "address-service-crud"
    title: "Address Service CRUD Operations"
    description: "Core service class demonstrating transaction management, validation, and business logic for address operations"
    category: "Business Logic"
    duration: "10 min read"
    views: 0
    tags:
      - "Service Layer"
      - "JPA"
      - "Transactions"
      - "CRUD"
    files:
      - name: "AddressService.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/service/AddressService.java"
        language: "java"
        content: |
          @Service
          @RequiredArgsConstructor
          @Slf4j
          public class AddressService {

            private final AddressRepository addressRepository;
            private final AddressValidator addressValidator;
            private final AddressMapper addressMapper;
            private final AddressServiceProperties properties;

            @Transactional
            public Address createAddress(String userId, String role, AddressRequest request) {
              addressValidator.validate(request);

              AddressEntity.UserType userType = determineUserType(role);
              validateAddressLimit(userId, userType);

              AddressEntity entity = addressMapper.toEntity(userId, userType, request);

              if (Boolean.TRUE.equals(request.isDefault()) || isFirstAddress(userId)) {
                setAsDefaultAddress(entity);
              }

              AddressEntity savedEntity = addressRepository.save(entity);
              log.info("Address created successfully for user: {}, addressId: {}", userId, savedEntity.getId());

              return Address.fromEntity(savedEntity);
            }

            @Transactional
            public Address updateAddress(String addressId, AddressRequest request, String userId) {
              addressValidator.validate(request);

              UUID id = parseUUID(addressId);
              AddressEntity entity = findAddressEntityForUpdate(id, userId);

              addressMapper.updateEntity(entity, request);

              if (Boolean.TRUE.equals(request.isDefault()) && !entity.getIsDefault()) {
                setAsDefaultAddress(entity);
              }

              AddressEntity updatedEntity = addressRepository.save(entity);
              log.info("Address updated successfully: {}", addressId);

              return Address.fromEntity(updatedEntity);
            }

            @Transactional
            public void deleteAddress(String addressId, String userId) {
              UUID id = parseUUID(addressId);

              AddressEntity entity = addressRepository.findByIdAndUserIdAndActiveTrue(id, userId)
                  .orElseThrow(() -> new UnauthorizedAddressAccessException(addressId, userId));
              softDeleteAddress(entity);
              log.info("Address deleted successfully by user: {}, addressId: {}", userId, addressId);
            }

            private void validateAddressLimit(String userId, AddressEntity.UserType userType) {
              long currentAddressCount = addressRepository.countByUserIdAndActiveTrue(userId);
              int limit = properties.getAddressLimit(userType.name());

              if (currentAddressCount >= limit) {
                throw new AddressLimitExceededException(userId, limit, userType.name());
              }
            }

            private void setAsDefaultAddress(AddressEntity newDefaultAddress) {
              addressRepository.resetDefaultAddressForUser(newDefaultAddress.getUserId());
              newDefaultAddress.setIsDefault(true);
            }

            private void softDeleteAddress(AddressEntity entity) {
              entity.setActive(false);
              addressRepository.save(entity);
            }
          }
        highlighted: true
        explanation: "Service layer with @Transactional methods for create, update, and delete operations. Includes business logic for address limits, default address handling, and soft delete."

      - name: "Address.java (DTO)"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/utils/dto/Address.java"
        language: "java"
        content: |
          @Schema(description = "Address response object")
          public record Address(

              @Schema(description = "Address unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
              String id,

              @Schema(description = "User ID who owns this address", example = "123e4567-e89b-12d3-a456-426614174001")
              String userId,

              @Schema(description = "Street address", example = "123 Main St")
              String street,

              @Schema(description = "City", example = "New York")
              String city,

              @Schema(description = "State/Province", example = "NY")
              String state,

              @Schema(description = "Country code", example = "US")
              String country,

              @Schema(description = "Postal/ZIP code", example = "10001")
              String postalCode,

              @Schema(description = "Additional address details", example = "Apt 4B")
              String additionalDetails,

              @Schema(description = "Whether this is the default address", example = "false")
              Boolean isDefault,

              @Schema(description = "Address creation timestamp")
              LocalDateTime createdAt,

              @Schema(description = "Address last update timestamp")
              LocalDateTime updatedAt
          ) {

            public static Address fromEntity(AddressEntity entity) {
              return new Address(
                  entity.getId() != null ? entity.getId().toString() : null,
                  entity.getUserId(),
                  entity.getStreet(),
                  entity.getCity(),
                  entity.getState(),
                  entity.getCountry(),
                  entity.getPostalCode(),
                  entity.getAdditionalDetails(),
                  entity.getIsDefault(),
                  entity.getCreatedAt(),
                  entity.getUpdatedAt()
              );
            }
          }
        highlighted: false
        explanation: "Immutable DTO using Java record. fromEntity() factory method converts JPA entity to API response object."

  - id: "dual-controller-architecture"
    title: "Dual Controller Architecture (User vs Admin)"
    description: "Separate controllers for user self-service and admin operations with different authorization requirements"
    category: "API Design"
    duration: "6 min read"
    views: 0
    tags:
      - "REST Controller"
      - "Role-Based Access"
      - "Spring Security"
    files:
      - name: "UserAddressController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/controller/UserAddressController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/user/addresses")
          @Tag(name = "User Address Management", description = "Endpoints for users to manage their own addresses (requires USER or ADMIN role)")
          @SecurityRequirement(name = "bearerAuth")
          public class UserAddressController {

            private final AddressService addressService;

            @Autowired
            public UserAddressController(AddressService addressService) {
              this.addressService = addressService;
            }

            @GetMapping
            @GetMyAddressesAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<List<AddressSummary>> getMyAddresses(
                @AuthenticationPrincipal AuthUserDetails userDetails) {
              UserAuthValidator.assertUserInContext(userDetails);

              List<AddressSummary> summaryResponses = addressService.findAddressSummariesByUserId(userDetails.getUserId());
              return ResponseWrapper.success(summaryResponses);
            }

            @GetMapping("/{addressId}")
            @GetAddressByIdAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<Address> getAddressById(
                @Parameter(description = "Address ID", required = true) @PathVariable String addressId,
                @AuthenticationPrincipal AuthUserDetails userDetails) {
              UserAuthValidator.assertUserInContext(userDetails);

              Address address = addressService.findAddressByIdAndUserId(addressId, userDetails.getUserId());
              return ResponseWrapper.success(address);
            }

            @PostMapping
            @RateLimit(profile = RateLimitProfile.SENSITIVE)
            @CreateUserAddressAnnotation
            public ResponseEntity<ResponseWrapper<Address>> createAddress(
                @Parameter(description = "Address details", required = true) @Valid @RequestBody AddressRequest addressRequest,
                @AuthenticationPrincipal AuthUserDetails userDetails) {
              UserAuthValidator.assertUserInContext(userDetails);

              Address addressCreated = addressService.createAddress(
                  userDetails.getUserId(),
                  userDetails.getRole(),
                  addressRequest);

              return ResponseEntity
                  .status(HttpStatus.CREATED)
                  .body(ResponseWrapper.created(addressCreated, "Address"));
            }

            @PutMapping("/{addressId}")
            @UpdateUserAddressAnnotation
            @RateLimit(profile = RateLimitProfile.SENSITIVE)
            public ResponseWrapper<Address> updateAddress(
                @Parameter(description = "Address ID", required = true) @PathVariable String addressId,
                @Parameter(description = "Updated address details", required = true) @Valid @RequestBody AddressRequest addressRequest,
                @AuthenticationPrincipal AuthUserDetails userDetails) {
              UserAuthValidator.assertUserInContext(userDetails);

              Address response = addressService.updateAddress(addressId, addressRequest, userDetails.getUserId());
              return ResponseWrapper.success(response);
            }

            @DeleteMapping("/{addressId}")
            @DeleteUserAddressAnnotation
            @RateLimit(profile = RateLimitProfile.SENSITIVE)
            public ResponseWrapper<Void> deleteAddress(
                @Parameter(description = "Address ID", required = true) @PathVariable String addressId,
                @AuthenticationPrincipal AuthUserDetails userDetails) {

              UserAuthValidator.assertUserInContext(userDetails);
              addressService.deleteAddress(addressId, userDetails.getUserId());
              return ResponseWrapper.success("Address deleted successfully");
            }

            @PutMapping("/{addressId}/set-default")
            @SetDefaultAddressAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<Address> setAddressAsDefault(
                @Parameter(description = "Address ID", required = true) @PathVariable String addressId,
                @AuthenticationPrincipal AuthUserDetails userDetails) {

              Address response = addressService.setAddressAsDefault(addressId, userDetails.getUserId());
              return ResponseWrapper.success(response);
            }
          }
        highlighted: true
        explanation: "User controller with @SecurityRequirement for JWT auth. Users can only access their own addresses. @RateLimit annotation applies rate limiting profiles."

      - name: "AddressAdminController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/address/controller/AddressAdminController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/addresses/admin")
          @RequiredArgsConstructor
          @Tag(name = "Admin Address Management", description = "Endpoints for administrative address management (requires ADMIN role)")
          @SecurityRequirement(name = "bearerAuth")
          public class AddressAdminController {

            private final AddressService addressService;

            @GetMapping
            @GetAllAddressesAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<Page<AddressSummary>> getAllAddresses(
                @Parameter(description = "Pagination parameters") @PageableDefault(size = 20) Pageable pageable) {
              Page<AddressSummary> addresses = addressService.findAllAddresses(pageable);
              return ResponseWrapper.found(addresses, "Addresses");
            }

            @GetMapping("/{id}")
            @GetAddressByIdAdminAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<Address> getAddressById(
                @Parameter(description = "Address ID", required = true) @PathVariable String id) {
              Address address = addressService.findAddressById(id);
              return ResponseWrapper.found(address, "Address");
            }

            @GetMapping("/user/{userId}")
            @GetAddressesByUserIdAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<List<Address>> getAddressesByUserId(
                @Parameter(description = "User ID", required = true) @PathVariable String userId) {
              List<Address> addresses = addressService.findAddressesByUserId(userId);
              return ResponseWrapper.found(addresses, "Addresses");
            }

            @PostMapping
            @CreateAddressForUserAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseEntity<ResponseWrapper<Address>> createAddressForUser(
                @Parameter(description = "User ID to assign the address to", required = true) @RequestParam String userId,
                @Parameter(description = "Address details", required = true) @Valid @RequestBody AddressRequest addressRequest) {

              Address address = addressService.createAddress(userId, "ADMIN", addressRequest);
              return ResponseEntity.status(HttpStatus.CREATED)
                  .body(ResponseWrapper.created(address, "Address"));
            }

            @PutMapping("/{id}")
            @UpdateAddressAdminAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<Address> updateAddress(
                @Parameter(description = "Address ID", required = true) @PathVariable String id,
                @Parameter(description = "Updated address details", required = true) @Valid @RequestBody AddressRequest addressRequest) {

              Address address = addressService.updateAddress(id, addressRequest);
              return ResponseWrapper.updated(address, "Address");
            }

            @DeleteMapping("/{id}")
            @DeleteAddressAdminAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<Void> deleteAddress(
                @Parameter(description = "Address ID", required = true) @PathVariable String id) {

              addressService.deleteAddress(id);
              return ResponseWrapper.success("Address");
            }

            @PutMapping("/{id}/set-default-for-user/{userId}")
            @SetDefaultAddressAdminAnnotation
            @RateLimit(profile = RateLimitProfile.STANDARD)
            public ResponseWrapper<Address> setAddressAsDefault(
                @Parameter(description = "Address ID", required = true) @PathVariable String id,
                @Parameter(description = "User ID", required = true) @PathVariable String userId) {

              Address address = addressService.setAddressAsDefault(id, userId);
              return ResponseWrapper.updated(address, "Address");
            }
          }
        highlighted: false
        explanation: "Admin controller without user-specific restrictions. Admin can access any address and manage addresses for any user."
---
# CodeShowCase

> 4 comprehensive code examples covering key patterns: Factory/Strategy for validation, AOP for rate limiting, Service layer for business logic, and dual-controller architecture. All examples are production-ready code from the actual codebase. Potential additions: Kafka event publishing example, JPA entity with @Embedded/@Embeddable example, or custom annotation meta-annotation example.
