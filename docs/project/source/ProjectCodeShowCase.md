---
codeExamples:
  # address-service
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
  # auth-service
  - id: "use-cases-orchestrator"
    title: "DDD UseCasesOrchestrator Pattern"
    description: "Central orchestrator implementing 4 port interfaces (AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases) following Domain-Driven Design"
    category: "Architecture"
    duration: "10 min read"
    views: 0
    tags:
      - "DDD"
      - "Ports & Adapters"
      - "Orchestrator"
      - "Clean Architecture"
    files:
      - name: "UseCasesOrquestrator.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/UseCasesOrquestrator.java"
        language: "java"
        content: |
          /**
           * UseCasesOrquestrator - Orchestrator for all authentication use cases
           * This is the entry point for the authentication bounded context
           */
          @Service
          @Slf4j
          @RequiredArgsConstructor
          public class UseCasesOrquestrator
              implements AuthUseCases, LogoutUseCases, PasswordUseCases, RegisterUseCases, TwoFaConfigUseCases {

            private final RegisterUseCase registerUseCase;
            private final LoginUseCase loginUseCase;
            private final RefreshAccessTokenUseCase refreshAccessTokenUseCase;
            private final LogoutUseCase logoutUseCase;
            private final LogoutAllUseCase logoutAllUseCase;
            private final TwoFactorLoginUseCase twoFactorLoginUseCase;
            private final ForgotPasswordUseCase forgotPasswordUseCase;
            private final ValidateResetTokenUseCase validateResetTokenUseCase;
            private final ResetPasswordUseCase resetPasswordUseCase;
            private final ChangePasswordUseCase changePasswordUseCase;
            private final ActivateAccountUseCase activateAccountUseCase;
            private final EnableTwoFactorAuthUseCase enableTwoFactorAuthUseCase;
            private final DisableTwoFactorAuthUseCase disableTwoFactorAuthUseCase;
            private final SendValidationCodeUseCase sendValidationCodeUseCase;
            private final VerifyTwoFactorCodeUseCase verifyTwoFactorCodeUseCase;

            @Override
            public SignUpResult register(SignupCommand command) {
                log.info("AuthUseCases: Executing signup use case for email: {}", command.email().value());
                return registerUseCase.execute(command);
            }

            @Override
            public SessionPayload login(LoginCommand command) {
                log.info("AuthUseCases: Executing login use case for identifier: {}", maskIdentifier(command.identifier()));
                return loginUseCase.execute(command);
            }

            // ... other delegation methods
          }
        highlighted: true
        explanation: "Orchestrator pattern: single entry point delegating to specific use case implementations. Implements multiple port interfaces for clean API."

  - id: "token-factory"
    title: "TokenFactory - Multi-Type Token Creation"
    description: "Factory creating 4 token types: ACCESS (JWT), REFRESH (JWT), ACTIVATION (numeric), TWO_FA (numeric) using JJWT library"
    category: "Security"
    duration: "8 min read"
    views: 0
    tags:
      - "Factory Pattern"
      - "JWT"
      - "JJWT"
      - "Token Management"
    files:
      - name: "TokenFactory.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/security/tokens/factory/TokenFactory.java"
        language: "java"
        content: |
          @Component
          @RequiredArgsConstructor
          @Slf4j
          public class TokenFactory {
            private final JwtProperties jwtProperties;
            private static final SecureRandom SECURE_RANDOM = new SecureRandom();

            public Token createToken(TokenType type, UserClaims userClaims) {
                return switch (type) {
                    case ACCESS -> createAccessToken(userClaims);
                    case REFRESH -> createRefreshToken(userClaims);
                    case ACTIVATION -> createActivationToken(userClaims);
                    case TWO_FA -> createTwoFaToken(userClaims);
                };
            }

            private Token createAccessToken(UserClaims userClaims) {
                Map<String, Object> claims = new HashMap<>();
                claims.put("role", userClaims.role());
                claims.put("userId", userClaims.userId());
                claims.put("email", userClaims.email());
                claims.put("type", "access");

                long expirationMs = jwtProperties.getAccessTokenExpirationSeconds();
                String tokenCode = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userClaims.userId())
                    .setIssuer(jwtProperties.getIssuer())
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plusMillis(expirationMs)))
                    .signWith(getSigningKey())
                    .compact();

                return new Token(tokenCode, "ACCESS", Duration.ofMillis(expirationMs),
                    LocalDateTime.now().plusSeconds(expirationMs / 1000), userClaims);
            }

            private Token createRefreshToken(UserClaims userClaims) {
                // Similar to access token but with different claims and longer expiration
                // Default: 7 days
            }

            private Token createActivationToken(UserClaims userClaims) {
                String tokenCode = generateNumericToken(activationTokenLength); // 6 digits
                return new Token(tokenCode, "ACTIVATION",
                    Duration.ofMinutes(activationExpirationMinutes),
                    LocalDateTime.now().plusMinutes(activationExpirationMinutes), userClaims);
            }
          }
        highlighted: true
        explanation: "Factory pattern with switch expression for different token types. JWTs signed with HMAC-SHA key, numeric tokens for activation/2FA."

      - name: "TokenType.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/security/tokens/TokenType.java"
        language: "java"
        content: |
          public enum TokenType {
              ACCESS,     // JWT access token (short-lived, ~15min)
              REFRESH,     // JWT refresh token (long-lived, ~7 days)
              ACTIVATION,  // Numeric code (6 digits, 15min) for email activation
              TWO_FA       // Numeric code (6 digits, 5min) for 2FA
          }
        highlighted: false
        explanation: "Enum defining the 4 token types supported by the auth service."

  - id: "kafka-event-producer"
    title: "Kafka Event Publishing"
    description: "UserEventProducer publishes 8+ event types to Kafka topics with CompletableFuture for non-blocking operations"
    category: "Messaging"
    duration: "12 min read"
    views: 0
    tags:
      - "Kafka"
      - "Events"
      - "DDD"
      - "Pub/Sub"
    files:
      - name: "UserEventProducer.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/messaging/kafka/producer/UserEventProducer.java"
        language: "java"
        content: |
          @Component
          public class UserEventProducer implements UserEventPublisher {
            @Value("${kafka.topics.user.created:user.created}")
            private String userCreatedTopic;

            @Value("${kafka.topics.user.registered:user-registered}")
            private String userRegisteredTopic;

            @Value("${kafka.topics.auth.password-changed:auth.password-changed}")
            private String passwordChangedTopic;

            // ... more topic configs

            @Override
            public boolean publishUserCreated(UserCreatedEvent event) {
                log.info("Publishing UserCreatedEvent for userId: {}", event.userId());

                try {
                    CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
                        userCreatedTopic, event.userId(), event);

                    future.whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("UserCreatedEvent published successfully");
                        } else {
                            log.error("Failed to publish UserCreatedEvent: {}", ex.getMessage());
                        }
                    });

                    future.get(timeoutSeconds, TimeUnit.SECONDS);
                    return true;
                } catch (Exception e) {
                    log.error("Error publishing UserCreatedEvent", e);
                    return false;
                }
            }
          }
        highlighted: true
        explanation: "Uses KafkaTemplate with CompletableFuture for async publishing. Events include: UserCreated, UserUpdated, UserDeleted, UserRegistered, UserLogin, PasswordChanged, AccountActivated, TwoFactorEnabled/Disabled."

  - id: "grpc-user-service-client"
    title: "gRPC Client for User-Service"
    description: "UserServiceGrpcClient communicates with user-service via Protobuf/gRPC for user CRUD operations"
    category: "Communication"
    duration: "7 min read"
    views: 0
    tags:
      - "gRPC"
      - "Protobuf"
      - "Microservices"
      - "RPC"
    files:
      - name: "UserServiceGrpcClient.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/grpc/UserServiceGrpcClient.java"
        language: "java"
        content: |
          @Component
          @Slf4j
          public class UserServiceGrpcClient implements UserServiceClient {
            private final UserGrpcServiceBlockingStub blockingStub;

            public Optional<UserDto> getUserByEmail(String email) {
                log.debug("gRPC: Getting user by email: {}", maskEmail(email));

                try {
                    UserRequest request = UserRequest.newBuilder()
                        .setIdentifier(email)
                        .setIdentifierType(IdentifierType.EMAIL)
                        .build();

                    UserResponse response = blockingStub.getUser(request);
                    return Optional.of(UserGrpcMapper.toDto(response));

                } catch (StatusRuntimeException e) {
                    if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                        log.debug("User not found by email: {}", maskEmail(email));
                        return Optional.empty();
                    }
                    log.error("gRPC error getting user by email", e);
                    throw new UserServiceException("gRPC call failed", e);
                }
            }

            // ... other gRPC methods: createUser, updateUser, deleteUser, etc.
          }
        highlighted: true
        explanation: "Uses gRPC blocking stub for synchronous calls. Protobuf messages defined in .proto files. Maps between Protobuf and domain objects using UserGrpcMapper."

      - name: "UserGrpcMapper.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/adapter/output/grpc/UserGrpcMapper.java"
        language: "java"
        content: |
          public class UserGrpcMapper {
            public static UserDto toDto(UserResponse response) {
                return UserDto.builder()
                    .userId(response.getUserId())
                    .email(response.getEmail())
                    .name(response.getName())
                    .phoneNumber(response.getPhoneNumber())
                    .role(response.getRole())
                    .enabled(response.getEnabled())
                    .build();
            }

            public static UserRequest toCreateRequest(CreateUserCommand command) {
                return UserRequest.newBuilder()
                    .setEmail(command.email().value())
                    .setPassword(command.password().value())
                    .setName(command.name())
                    .setPhoneNumber(command.phoneNumber().value())
                    .setRole(command.role())
                    .build();
            }
          }
        highlighted: false
        explanation: "Mapper between Protobuf messages and domain objects/DTOs."

  - id: "redis-session-repository"
    title: "Redis Session Management"
    description: "RedisSessionRepository manages JWT refresh token sessions with blacklisting support for immediate revocation"
    category: "Persistence"
    duration: "6 min read"
    views: 0
    tags:
      - "Redis"
      - "Sessions"
      - "JWT"
      - "Blacklisting"
    files:
      - name: "RedisSessionRepository.java"
        path: "PLACEHOLDER: Check adapter/output/persistence/ for actual file"
        language: "java"
        content: |
          @Repository
          @Slf4j
          public class RedisSessionRepository implements SessionRepository {
            private final RedisTemplate<String, Object> redisTemplate;
            private final JwtProperties jwtProperties;

            @Override
            public void saveSession(String refreshToken, JwtSession session) {
                String key = "session:" + refreshToken;
                long ttlSeconds = jwtProperties.getRefreshTokenExpirationSeconds();
                redisTemplate.opsForValue().set(key, session, ttlSeconds, TimeUnit.SECONDS);
                log.debug("Session saved for user: {}", session.userId());
            }

            @Override
            public Optional<JwtSession> getSession(String refreshToken) {
                String key = "session:" + refreshToken;
                JwtSession session = (JwtSession) redisTemplate.opsForValue().get(key);
                return Optional.ofNullable(session);
            }

            @Override
            public void blacklistSession(String refreshToken) {
                String blacklistKey = "blacklist:" + refreshToken;
                redisTemplate.opsForValue().set(blacklistKey, "true",
                    jwtProperties.getRefreshTokenExpirationSeconds(), TimeUnit.SECONDS);
                log.info("Session blacklisted: {}", maskToken(refreshToken));
            }
          }
        highlighted: true
        explanation: "Stores refresh token sessions in Redis with TTL matching token expiration. Blacklisting allows immediate session revocation before expiration."

  - id: "command-pattern"
    title: "Command Pattern for Requests"
    description: "Each request type has a corresponding Command object (SignupCommand, LoginCommand, ChangePasswordCommand) with validation"
    category: "Design Patterns"
    duration: "5 min read"
    views: 0
    tags:
      - "Command Pattern"
      - "CQRS-like"
      - "DDD"
    files:
      - name: "SignupCommand.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/command/SignupCommand.java"
        language: "java"
        content: |
          public record SignupCommand(
              Email email,
              Password password,
              String name,
              PhoneNumber phoneNumber,
              String role
          ) {
            public static SignupCommand fromRequest(SignupRequest request, String role) {
                return new SignupCommand(
                    new Email(request.email()),
                    new Password(request.password()),
                    request.name(),
                    new PhoneNumber(request.phoneNumber()),
                    role
                );
            }
          }
        highlighted: true
        explanation: "Command objects encapsulate request data with domain value objects (Email, Password, PhoneNumber) for validation at the domain level."

      - name: "LoginCommand.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/accounts/auth/core/application/command/login/LoginCommand.java"
        language: "java"
        content: |
          public record LoginCommand(
              String identifier,  // email or phone number
              String password
          ) {
            public static LoginCommand fromRequest(LoginRequest request) {
                return new LoginCommand(request.emailOrPhoneNumber(), request.password());
            }
          }
        highlighted: false
        explanation: "Simple command for login with identifier (email or phone) and password."

  - id: "http-integration-tests"
    title: "Full-stack HTTP integration tests"
    description: "AuthEndpointsIntegrationTest boots the Spring context with Testcontainers Redis/Kafka, wires an in-process UserService gRPC server (InMemoryUserGrpcServer), and drives register→activate→login and related flows using real Redis opaque tokens."
    category: "Quality"
    duration: "15 min read"
    views: 0
    tags:
      - "JUnit 5"
      - "Testcontainers"
      - "Spring Boot Test"
      - "gRPC"
    files:
      - name: "AuthEndpointsIntegrationTest.java"
        path: "src/test/java/io/github/alexisTrejo11/drugstore/accounts/integration/AuthEndpointsIntegrationTest.java"
        language: "java"
        content: |
          @SpringBootTest(classes = AuthServiceApplication.class,
              webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
          @ActiveProfiles({"integration-test", "test"})
          @Testcontainers(disabledWithoutDocker = true)
          class AuthEndpointsIntegrationTest {

            @Container static final GenericContainer<?> REDIS =
                new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

            @Container static final KafkaContainer KAFKA =
                new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.6.1"));

            @DynamicPropertySource
            static void registerProps(DynamicPropertyRegistry r) {
              r.add("spring.data.redis.host", REDIS::getHost);
              r.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
              r.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
              r.add("grpc.client.user-service.host", () -> "localhost");
              r.add("grpc.client.user-service.port", () -> grpcPort());
            }
            // Netty gRPC server + InMemoryUserGrpcServer started in static initializer
          }
        highlighted: true
        explanation: "Uses real infrastructure boundaries (Redis keys, Kafka producers, gRPC stub) so regressions in wiring or token handling surface in CI when Docker is available."
  # cart-service
  - id: "cart-aggregate-root"
    title: "Cart Aggregate Root Pattern"
    description: "Cart.java is the aggregate root encapsulating items and afterwardsItems with business logic for add/update/remove/clear operations"
    category: "Domain"
    duration: "15 min read"
    views: 0
    tags:
      - "DDD"
      - "Aggregate Root"
      - "Domain Model"
      - "Business Logic"
    files:
      - name: "Cart.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/Cart.java"
        language: "java"
        content: |
          /**
           * Cart aggregate root - represents a customer's shopping cart.
           * This is the main domain entity with comprehensive business logic.
           */
          public class Cart {
            private static final int MAX_ITEMS_PER_CART = 100;

            private CartId id;
            private CustomerId customerId;
            private List<CartItem> items;
            private List<AfterwardsItem> afterwardsItems;
            private CartTimeStamps timeStamps;

            /**
             * Creates a new Cart for a customer with generated ID.
             */
            public static Cart create(CreateCartParams params) {
                CartValidation.requireNonNull(params, "CreateCartParams");
                CartValidation.requireNonNull(params.customerId(), "Customer ID");

                Cart cart = new Cart();
                cart.id = CartId.generate();
                cart.customerId = params.customerId();

                log.info("Created new Cart: id={}, customerId={}", cart.id, cart.customerId);
                return cart;
            }

            /**
             * Adds a single item to the cart. If item exists, quantities are merged.
             */
            public void addItem(CartItem item) {
                CartValidation.requireNonNull(item, "Cart item");

                Optional<CartItem> existingItem = findItemByProductId(item.getProductId());

                if (existingItem.isPresent()) {
                    existingItem.get().mergeWith(item);
                } else {
                    validateCanAddItem();
                    items.add(item);
                }

                timeStamps.markAsUpdated();
            }

            public void moveItemsToAfterwards(List<ProductId> productIds) {
                List<CartItem> itemsToMove = items.stream()
                    .filter(item -> productIds.contains(item.getProductId()))
                    .toList();

                List<AfterwardsItem> afterwardsItemsToAdd = itemsToMove.stream()
                    .map(AfterwardsItem::createFromItem)
                    .toList();

                this.afterwardsItems.addAll(afterwardsItemsToAdd);
                this.items.removeAll(itemsToMove);
                timeStamps.markAsUpdated();
            }

            public ItemPrice calculateTotal() {
                if (items.isEmpty()) return ItemPrice.zero();
                return items.stream()
                    .map(CartItem::calculateTotal)
                    .reduce(ItemPrice.zero(), ItemPrice::add);
            }
          }
        highlighted: true
        explanation: "Aggregate root pattern: Cart encapsulates all business logic for item management, enforces invariants (max 100 items), and handles afterwards feature."

  - id: "value-objects"
    title: "Value Objects Pattern"
    description: "Strongly-typed values: CartId (UUID), CustomerId, ProductId, Quantity (with validation), ItemPrice (BigDecimal wrapper)"
    category: "Domain"
    duration: "8 min read"
    views: 0
    tags:
      - "DDD"
      - "Value Objects"
      - "Type Safety"
      - "Validation"
    files:
      - name: "Quantity.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/valueobjects/Quantity.java"
        language: "java"
        content: |
          public record Quantity(int value) {
            public Quantity {
                if (value < 0) {
                    throw new InvalidQuantityException("Quantity cannot be negative: " + value);
                }
                if (value == 0) {
                    throw new InvalidQuantityException("Quantity cannot be zero");
                }
                this.value = value;
            }

            public static Quantity of(int value) {
                return new Quantity(value);
            }

            public Quantity add(Quantity other) {
                return new Quantity(this.value + other.value);
            }

            public Quantity subtract(Quantity other) {
                return new Quantity(this.value - other.value);
            }
          }
        highlighted: true
        explanation: "Value object with validation in canonical constructor, factory method, and arithmetic operations."

      - name: "CartId.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/model/valueobjects/CartId.java"
        language: "java"
        content: |
          public class CartId extends AbstractId {
            public CartId(String value) {
                super(value);
            }

            public static CartId generate() {
                return new CartId(UUID.randomUUID().toString());
            }
          }
        highlighted: false
        explanation: "Extends AbstractId, provides factory method for UUID generation."

  - id: "grpc-service"
    title: "gRPC Service for Inter-Service Communication"
    description: "CartGrpcService exposes GetUserCart and ClearCart endpoints for order-service integration during checkout"
    category: "Communication"
    duration: "10 min read"
    views: 0
    tags:
      - "gRPC"
      - "Protobuf"
      - "Microservices"
      - "Checkout Flow"
    files:
      - name: "CartGrpcService.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/input/grpc/CartGrpcService.java"
        language: "java"
        content: |
          @GrpcService
          public class CartGrpcService extends CartServiceGrpc.CartServiceImplBase {

            private final CartQueryUseCase cartQueryUseCase;
            private final CartCommandUseCase cartCommandUseCase;
            private final CartGrpcMapper mapper;

            @Override
            public void getUserCart(GetUserCartRequest request,
                                        StreamObserver<CartResponse> responseObserver) {
                try {
                    log.info("gRPC GetUserCart called for userId: {}", request.getUserId());

                    GetCartByCustomerIdQuery query =
                        GetCartByCustomerIdQuery.from(request.getUserId());
                    Cart cart = cartQueryUseCase.getCartByCustomerId(query);

                    CartResponse response = mapper.toGrpcResponse(cart);

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();

                } catch (Exception e) {
                    log.error("Error in gRPC GetUserCart", e);
                    responseObserver.onError(e);
                }
            }

            @Override
            public void clearCart(ClearCartRequest request,
                                   StreamObserver<ClearCartResponse> responseObserver) {
                // Converts request, creates ClearCartCommand, executes
                // Returns ClearCartResponse with success/failure
            }
          }
        highlighted: true
        explanation: "gRPC service extends generated base class, uses Protobuf messages, calls use cases."

      - name: "CartGrpcMapper.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/adapter/input/grpc/mapper/CartGrpcMapper.java"
        language: "java"
        content: |
          @Component
          public class CartGrpcMapper {
            public CartResponse toGrpcResponse(Cart cart) {
                return CartResponse.newBuilder()
                    .setId(cart.getId().value())
                    .setCustomerId(cart.getCustomerId().value())
                    .addAllItems(toGrpcCartItems(cart.getItems()))
                    .build();
            }
          }
        highlighted: false
        explanation: "Maps between domain objects and Protobuf messages."

  - id: "kafka-consumer"
    title: "Kafka Product Event Consumer"
    description: "ProductEventConsumer listens to product-events topic and updates cart items via ProductEventHandler"
    category: "Messaging"
    duration: "7 min read"
    views: 0
    tags:
      - "Kafka"
      - "Events"
      - "Product Updates"
      - "Event-Driven"
    files:
      - name: "ProductEventConsumer.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/product/adapter/input/message/kafka/ProductEventConsumer.java"
        language: "java"
        content: |
          @Component
          public class ProductEventConsumer {
            private final ProductEventHandler eventHandler;

            @KafkaListener(topics = "product-events",
                          groupId = "${spring.kafka.consumer.group-id}")
            public void consume(ProductEvent event) {
                log.info("Received product event: type={}, id={}",
                          event.getEventType(), event.getPayload().getId());
                eventHandler.handle(event);
            }
          }
        highlighted: true
        explanation: "Listens to product-events topic, delegates to ProductEventHandler for processing."

      - name: "ProductEventHandler.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/product/adapter/input/message/ProductEventHandler.java"
        language: "java"
        content: |
          @Service
          @Slf4j
          public class ProductEventHandler {
            // Handles product update/delete events
            // Updates cart items with new prices or removes unavailable products
          }
        highlighted: false
        explanation: "Handles product events: price changes, availability updates, product deletions."

  - id: "command-pattern"
    title: "Command Pattern for Cart Operations"
    description: "Command objects: CreateCartCommand, UpdateCartCommand, ClearCartCommand, CreateAfterwardsCommand, RemoveAfterwardsCommand"
    category: "Design Patterns"
    duration: "6 min read"
    views: 0
    tags:
      - "Command Pattern"
      - "CQRS-like"
      - "DDD"
    files:
      - name: "UpdateCartCommand.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/application/command/UpdateCartCommand.java"
        language: "java"
        content: |
          public record UpdateCartCommand(
              CustomerId customerId,
              List<CartItem> items
          ) {
            public static UpdateCartCommand from(String customerId,
                                              List<CartItemRequest> requests) {
                List<CartItem> items = requests.stream()
                    .map(req -> CartItem.create(
                        new ReconstructCartItemParams(
                            null, null,
                            new ProductId(req.productId()),
                            req.productName(),
                            new ItemPrice(req.unitPrice()),
                            Quantity.of(req.quantity()),
                            new ItemPrice(req.discountPerUnit())
                        )))
                    .toList();

                return new UpdateCartCommand(
                    new CustomerId(customerId), items);
            }
          }
        highlighted: true
        explanation: "Command object encapsulating update request with domain object conversion."

  - id: "domain-events"
    title: "Domain Events (PLACEHOLDER)"
    description: "CartPurchasedEvent is defined but not published to Kafka yet. Should be published when cart is cleared after successful order."
    category: "Domain"
    duration: "5 min read"
    views: 0
    tags:
      - "Domain Events"
      - "Kafka"
      - "PLACEHOLDER"
    files:
      - name: "CartPurchasedEvent.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/carts/cart/core/domain/events/CartPurchasedEvent.java"
        language: "java"
        content: |
          // PLACEHOLDER: This event should be published to Kafka
          // when cart is cleared after successful order
          public class CartPurchasedEvent implements DomainEvent {
              // Event data: cartId, customerId, items, total, timestamp
          }
        highlighted: true
        explanation: "Domain event that should be published to Kafka topic (e.g., cart.purchased) after successful order."
  # employee-service
  - id: "command-query-separation"
    title: "Command/Query Separation (CQS)"
    description: "Separate controllers and services for commands (writes) and queries (reads) with dedicated command/query objects"
    category: "Architectural"
    duration: "12 min read"
    views: 0
    tags:
      - "CQS"
      - "DDD"
      - "Separation of Concerns"
      - "REST API"
    files:
      - name: "EmployeeCommandController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeCommandController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/employees")
          @Tag(name = "Employee Command Operations", description = "Endpoints for creating, updating...")
          @SecurityRequirement(name = "bearerAuth")
          public class EmployeeCommandController {

              @PostMapping
              @RateLimit(profile = RateLimitProfile.SENSITIVE)
              public ResponseEntity<ResponseWrapper<EmployeeId>> createEmployee(
                      @Valid @RequestBody CreateEmployeeRequest request) {

                  CreateEmployeeCommand command = request.toCommand();
                  EmployeeId employeeId = employeeCommandService.createEmployee(command);

                  return ResponseEntity.status(HttpStatus.CREATED)
                      .body(ResponseWrapper.created(employeeId));
              }

              @PatchMapping("/{id}/suspend")
              @RateLimit(profile = RateLimitProfile.SENSITIVE)
              public ResponseWrapper<Void> suspendEmployee(
                      @EmployeeIdPathParameter @PathVariable String id,
                      @RequestParam String reason,
                      @RequestParam String suspendedBy) {

                  SuspendEmployeeCommand command = new SuspendEmployeeCommand(
                      EmployeeId.of(id), reason, suspendedBy);
                  employeeCommandService.suspendEmployee(command);

                  return ResponseWrapper.success("Employee suspended successfully");
              }
              // ... more command endpoints
          }
        highlighted: true
        explanation: "Command controller handles writes (POST, PUT, PATCH, DELETE) with @RateLimit annotations using profiles from libs-kernel."

      - name: "EmployeeQueryController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeQueryController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/employees")
          @Tag(name = "Employee Query Operations", description = "Endpoints for querying...")
          @SecurityRequirement(name = "bearerAuth")
          public class EmployeeQueryController {

              @GetMapping("/{id}")
              @RateLimit(profile = RateLimitProfile.PUBLIC)
              public ResponseWrapper<EmployeeResponse> getEmployeeById(
                      @EmployeeIdPathParameter @PathVariable String id) {

                  GetEmployeeByIdQuery query = new GetEmployeeByIdQuery(EmployeeId.of(id));
                  Optional<Employee> employee = employeeQueryService.getEmployeeById(query);

                  EmployeeResponse response = responseMapper.toResponse(employee.get());
                  return ResponseWrapper.found(response, "Employee");
              }

              @GetMapping("/search")
              @RateLimit(profile = RateLimitProfile.PUBLIC)
              public ResponseWrapper<PageResponse<EmployeeResponse>> searchEmployees(
                      @ModelAttribute SearchEmployeesRequest request) {

                  SearchEmployeesQuery query = request.toQuery();
                  Page<Employee> employeesPage = employeeQueryService.searchEmployees(query);

                  PageResponse<EmployeeResponse> pageResponse =
                      responseMapper.toResponsePage(employeesPage);
                  return ResponseWrapper.found(pageResponse, "Employees");
              }
          }
        highlighted: false
        explanation: "Query controller handles reads (GET) with PUBLIC rate limit profile for higher throughput."

  - id: "jpa-entity-model"
    title: "Rich JPA Entity with Enums"
    description: "EmployeeEntity with embedded objects, certifications list, workday schedule as JSONB, and comprehensive enums for roles, types, and status"
    category: "Persistence"
    duration: "15 min read"
    views: 0
    tags:
      - "JPA"
      - "Entity Model"
      - "Enums"
      - "JSONB"
    files:
      - name: "EmployeeEntity.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
        language: "java"
        content: |
          @Entity
          @Table(name = "employees", indexes = {
              @Index(name = "idx_employee_number", columnList = "employeeNumber", unique = true),
              @Index(name = "idx_employee_status", columnList = "status"),
              @Index(name = "idx_employee_role", columnList = "role"),
              @Index(name = "idx_employee_store", columnList = "storeId"),
              @Index(name = "idx_employee_deleted", columnList = "deletedAt")
          })
          public class EmployeeEntity {

              @Id
              private String id;

              @Column(nullable = false, unique = true, length = 20)
              private String employeeNumber;

              @Enumerated(EnumType.STRING)
              @Column(nullable = false, length = 50)
              private EmployeeRoleEnum role;

              @Enumerated(EnumType.STRING)
              @Column(nullable = false, length = 20)
              private EmployeeTypeEnum employeeType;

              @Enumerated(EnumType.STRING)
              @Column(nullable = false, length = 20)
              private EmployeeStatusEnum status;

              @JdbcTypeCode(SqlTypes.JSON)
              @Column(columnDefinition = "jsonb")
              private Map<String, Object> workdaySchedule = new HashMap<>();

              @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
              @JoinColumn(name = "employee_id")
              private List<CertificationEntity> certifications = new ArrayList<>();

              // Audit fields
              private LocalDateTime deletedAt;

              public void markAsDeleted() {
                  this.deletedAt = LocalDateTime.now();
                  this.status = EmployeeStatusEnum.TERMINATED;
              }

              public void markAsRestored() {
                  this.deletedAt = null;
                  this.status = EmployeeStatusEnum.ACTIVE;
              }
          }
        highlighted: true
        explanation: "Rich JPA entity with 5 indexes, JSONB column for workday schedule, enums stored as strings, and soft delete support."

      - name: "EmployeeRoleEnum.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
        language: "java"
        content: |
          public enum EmployeeRoleEnum {
              PHARMACIST,
              PHARMACY_TECHNICIAN,
              STORE_MANAGER,
              ASSISTANT_MANAGER,
              CASHIER,
              INVENTORY_CLERK,
              DELIVERY_DRIVER,
              CUSTOMER_SERVICE_REP,
              JANITOR
          }
        highlighted: false
        explanation: "9 employee roles stored as strings in PostgreSQL for readability."

  - id: "rate-limiting"
    title: "Rate Limiting with libs-kernel"
    description: "Uses @RateLimit annotations from shared libs-kernel library with three profiles: PUBLIC (60/min), STANDARD (60/min), SENSITIVE (10/min)"
    category: "Security"
    duration: "8 min read"
    views: 0
    tags:
      - "Rate Limiting"
      - "Redis"
      - "libs-kernel"
      - "AOP"
    files:
      - name: "EmployeeCommandController.java (Rate Limits)"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeCommandController.java"
        language: "java"
        content: |
          // Command endpoints use SENSITIVE or STANDARD profiles
          @PostMapping
          @RateLimit(profile = RateLimitProfile.SENSITIVE)
          public ResponseEntity<ResponseWrapper<EmployeeId>> createEmployee(...) { ... }

          @PutMapping("/{id}")
          @RateLimit(profile = RateLimitProfile.STANDARD)
          public ResponseWrapper<Void> updateEmployee(...) { ... }

          @PatchMapping("/{id}/role")
          @RateLimit(profile = RateLimitProfile.SENSITIVE)
          public ResponseWrapper<Void> changeRole(...) { ... }
        highlighted: true
        explanation: "All command endpoints have @RateLimit annotations with appropriate profiles based on sensitivity."

      - name: "EmployeeQueryController.java (Rate Limits)"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/controller/EmployeeQueryController.java"
        language: "java"
        content: |
          // Query endpoints use PUBLIC profile for higher throughput
          @GetMapping("/{id}")
          @RateLimit(profile = RateLimitProfile.PUBLIC)
          public ResponseWrapper<EmployeeResponse> getEmployeeById(...) { ... }

          @GetMapping("/by-number/{employeeNumber}")
          @RateLimit(profile = RateLimitProfile.PUBLIC)
          public ResponseWrapper<EmployeeResponse> getEmployeeByNumber(...) { ... }

          @GetMapping("/search")
          @RateLimit(profile = RateLimitProfile.PUBLIC)
          public ResponseWrapper<PageResponse<EmployeeResponse>> searchEmployees(...) { ... }
        highlighted: false
        explanation: "Query endpoints use PUBLIC profile (60/min) for higher throughput on read operations."

  - id: "soft-delete-pattern"
    title: "Soft Delete with Audit Trail"
    description: "Employees are soft-deleted via deletedAt timestamp, with restore functionality. Audit fields track created/updated/deleted operations."
    category: "Persistence"
    duration: "6 min read"
    views: 0
    tags:
      - "Soft Delete"
      - "Audit Trail"
      - "JPA"
    files:
      - name: "EmployeeEntity.java (Soft Delete)"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
        language: "java"
        content: |
          // Soft delete
          public void markAsDeleted() {
              this.deletedAt = LocalDateTime.now();
              this.status = EmployeeStatusEnum.TERMINATED;
          }

          public void markAsRestored() {
              this.deletedAt = null;
              this.status = EmployeeStatusEnum.ACTIVE;
          }

          public boolean isDeleted() {
              return deletedAt != null;
          }
        highlighted: true
        explanation: "Soft delete sets deletedAt timestamp and updates status to TERMINATED. Restore resets deletedAt and sets status to ACTIVE."

      - name: "Audit Fields"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/EmployeeEntity.java"
        language: "java"
        content: |
          // Audit fields
          @Column(nullable = false, updatable = false)
          private LocalDateTime createdAt;

          @Column(nullable = false)
          private LocalDateTime updatedAt;

          @Column(updatable = false, length = 100)
          private String createdBy;

          @Column(length = 100)
          private String lastModifiedBy;

          @PrePersist
          protected void onCreate() {
              if (createdAt == null) {
                  createdAt = LocalDateTime.now();
              }
              updatedAt = LocalDateTime.now();
          }

          @PreUpdate
          protected void onUpdate() {
              updatedAt = LocalDateTime.now();
          }
        highlighted: false
        explanation: "Audit fields automatically managed via @PrePersist and @PreUpdate JPA callbacks."

  - id: "specification-pattern"
    title: "Specification Pattern for Dynamic Queries"
    description: "EmployeeSpecificationBuilder creates dynamic JPA criteria queries for searching employees with multiple filters"
    category: "Persistence"
    duration: "7 min read"
    views: 0
    tags:
      - "Specification"
      - "JPA Criteria"
      - "Dynamic Queries"
    files:
      - name: "EmployeeSpecificationBuilder.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/specification/EmployeeSpecificationBuilder.java"
        language: "java"
        content: |
          public class EmployeeSpecificationBuilder {
              // Builds dynamic JPA Specifications based on search criteria
              // Supports filtering by: firstName, lastName, role, status, employeeType, storeId
              // Uses optional filters that are only added if provided
          }
        highlighted: true
        explanation: "Dynamic query building for /api/v2/employees/search endpoint with optional filters."

      - name: "SearchEmployeesRequest.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/inbound/rest/dto/request/SearchEmployeesRequest.java"
        language: "java"
        content: |
          public record SearchEmployeesRequest(
              String firstName,
              String lastName,
              String role,
              String status,
              String employeeType,
              String storeId
          ) {
              public SearchEmployeesQuery toQuery() {
                  return new SearchEmployeesQuery(/* map fields to query */);
              }
          }
        highlighted: false
        explanation: "Request DTO maps to SearchEmployeesQuery for the specification builder."

  - id: "embeddable-objects"
    title: "Embeddable Objects for Complex Data"
    description: "ContactInfoEmbeddable and AddressEmbeddable for storing complex objects in JPA entity"
    category: "Persistence"
    duration: "5 min read"
    views: 0
    tags:
      - "Embeddable"
      - "JPA"
      - "Complex Objects"
    files:
      - name: "ContactInfoEmbeddable.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/ContactInfoEmbeddable.java"
        language: "java"
        content: |
          @Embeddable
          public class ContactInfoEmbeddable {
              private String email;
              private String phoneNumber;

              @Embedded
              @AttributeOverrides({
                  @AttributeOverride(name = "street", column = @Column(name = "address_street")),
                  @AttributeOverride(name = "city", column = @Column(name = "address_city")),
                  // ... more overrides
              })
              private AddressEmbeddable address;
          }
        highlighted: true
        explanation: "Embeddable objects allow storing complex data in a single table with column name overrides."

      - name: "AddressEmbeddable.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/employees/adapter/outbound/persistence/entity/AddressEmbeddable.java"
        language: "java"
        content: |
          @Embeddable
          public class AddressEmbeddable {
              private String street;
              private String city;
              private String state;
              private String country;
              private String postalCode;
          }
        highlighted: false
        explanation: "Simple embeddable for address fields, embedded within ContactInfoEmbeddable."
  # inventory-service
  - id: "batch-tracking-entity"
    title: "Inventory Batch Tracking with Expiration"
    description: "JPA entity implementing batch tracking with lot numbers, expiration dates, and status management for pharmaceutical inventory compliance"
    category: "database"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "JPA"
      - "Entity"
      - "Batch Tracking"
      - "Pharmaceutical"
    files:
      - name: "InventoryBatchEntity.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/outbound/persistence/model/InventoryBatchEntity.java"
        language: "java"
        content: |
          @Entity
          @Table(name = "inventory_batches")
          @Getter
          @Setter
          @Builder
          @NoArgsConstructor
          @AllArgsConstructor
          public class InventoryBatchEntity {
              @Id
              @Column(name = "id", length = 36)
              private String id;

              @Column(name = "inventory_id", nullable = false, length = 36)
              private String inventoryId;

              @Column(name = "batch_number", nullable = false, unique = true)
              private String batchNumber;

              @Column(name = "lot_number")
              private String lotNumber;

              @Column(name = "quantity", nullable = false)
              private Integer quantity;

              @Column(name = "available_quantity", nullable = false)
              private Integer availableQuantity;

              @Column(name = "expiration_date", nullable = false)
              private LocalDateTime expirationDate;

              @Enumerated(EnumType.STRING)
              @Column(name = "status", nullable = false)
              private BatchStatus status;

              @Column(name = "storage_conditions")
              private String storageConditions;

              @Column(name = "received_date")
              private LocalDateTime receivedDate;

              @PrePersist
              protected void onCreate() {
                  if (createdAt == null) {
                      createdAt = LocalDateTime.now();
                  }
                  if (updatedAt == null) {
                      updatedAt = LocalDateTime.now();
                  }
              }
          }
        highlighted: true
        explanation: "Entity with batch_number unique constraint, expiration_date tracking, and BatchStatus enum for lifecycle management (ACTIVE, EXPIRED, DAMAGED, QUARANTINED)"

  - id: "inventory-reservation"
    title: "Stock Reservation System"
    description: "REST controller implementing stock reservation pattern for order processing with confirm, release, and cancel operations"
    category: "api"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "REST API"
      - "Reservation Pattern"
      - "Order Processing"
      - "CQRS"
    files:
      - name: "InventoryReservationController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/inbound/api/rest/controller/InventoryReservationController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/inventories")
          @RequiredArgsConstructor
          public class InventoryReservationController {
              private final ReservationUseCase reservationUseCase;
              private final ResponseMapper<ReservationResponse, StockReservation> responseMapper;

              @PostMapping("/{inventoryId}/stock/reservations")
              public ResponseEntity<ResponseWrapper<ReservationId>> reserveStock(
                      @PathVariable String inventoryId,
                      @Valid @RequestBody ReserveStockRequest request) {
                  ReservationId reservationId = reservationUseCase.reserveStock(request.toCommand(inventoryId));
                  return ResponseEntity.status(HttpStatus.CREATED)
                      .body(ResponseWrapper.created(reservationId, "Stock Reservation"));
              }

              @PatchMapping("/stock/reservations/{reservationId}/confirm")
              public ResponseWrapper<Void> confirmReservation(@PathVariable String reservationId) {
                  var command = new ConfirmReservationCommand(ReservationId.of(reservationId), UserId.of("system"));
                  reservationUseCase.confirmReservation(command);
                  return ResponseWrapper.updated(null, "Reservation confirmed");
              }

              @PatchMapping("/reservations/{reservationId}/stock/release")
              public ResponseWrapper<Void> releaseReservation(@PathVariable String reservationId,
                                                              @RequestParam(required = false) String reason) {
                  String reasonValue = (reason != null) ? reason : "Released by system";
                  var command = new ReleaseReservationCommand(ReservationId.of(reservationId), reasonValue);
                  reservationUseCase.releaseReservation(command);
                  return ResponseWrapper.updated(null, "Reservation released");
              }

              @DeleteMapping("reservations/{reservationId}")
              public ResponseWrapper<Void> cancelReservation(@PathVariable String reservationId) {
                  reservationUseCase.cancelReservation(ReservationId.of(reservationId));
                  return ResponseWrapper.deleted(null, "Reservation");
              }
          }
        highlighted: true
        explanation: "REST API with reserve, confirm, release, and cancel operations. Uses ReservationUseCase port and ResponseWrapper from shared kernel."

  - id: "stock-movements"
    title: "Inventory Stock Movements (Adjust & Transfer)"
    description: "Controller handling inventory adjustments and transfers between locations with audit trail via InventoryMovement entities"
    category: "api"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "REST API"
      - "Stock Movement"
      - "Audit Trail"
      - "Transfer"
    files:
      - name: "InventoryStockMovementController.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/inventories/inventory/adapter/inbound/api/rest/controller/InventoryStockMovementController.java"
        language: "java"
        content: |
          @RestController
          @RequestMapping("/api/v2/inventories")
          @RequiredArgsConstructor
          public class InventoryStockMovementController {
              private final StockMovementUseCase stockMovementUseCase;
              private final ResponseMapper<MovementResponse, InventoryMovement> responseMapper;

              @PostMapping("/{inventoryId}/stocks-movements/adjust")
              public ResponseEntity<ResponseWrapper<AdjustmentId>> adjustInventory(
                      @PathVariable String inventoryId,
                      @Valid @RequestBody AdjustInventoryRequest request) {
                  AdjustmentId adjustmentId = stockMovementUseCase.adjustInventory(request.toCommand(inventoryId));
                  return ResponseEntity.status(HttpStatus.CREATED)
                      .body(ResponseWrapper.created(adjustmentId, "Stock Adjustment"));
              }

              @PostMapping("/{sourceInventoryId}/stocks/movements/transfer")
              public ResponseWrapper<Void> transferInventory(
                      @PathVariable String sourceInventoryId,
                      @Valid @RequestBody TransferInventoryRequest request) {
                  stockMovementUseCase.transferInventory(request.toCommand(sourceInventoryId));
                  return ResponseWrapper.success("Inventory transferred successfully");
              }

              @GetMapping("/{inventoryId}/stocks/movements")
              public ResponseWrapper<PageResponse<MovementResponse>> getInventoryMovements(
                      @PathVariable String inventoryId,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
                  var query = GetInventoryMovementsQuery.of(inventoryId, startDate, endDate);
                  Page<InventoryMovement> movements = stockMovementUseCase.getInventoryMovements(query);
                  PageResponse<MovementResponse> movementResponses = responseMapper.toResponsePage(movements);
                  return ResponseWrapper.found(movementResponses, "Inventory Movements");
              }
          }
        highlighted: true
        explanation: "Supports stock adjustments (with reason tracking) and transfers between inventory locations. Movements query supports date range filtering with ISO DateTime format."

  - id: "rabbitmq-integration"
    title: "RabbitMQ Messaging Integration"
    description: "Inventory service uses RabbitMQ (AMQP) for async messaging, unlike other services that use Kafka - creates platform inconsistency"
    category: "messaging"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "RabbitMQ"
      - "AMQP"
      - "Messaging"
      - "Inconsistency"
    files:
      - name: "build.gradle (RabbitMQ dependency)"
        path: "build.gradle"
        language: "groovy"
        content: |
          dependencies {
              // RabbitMQ
              implementation 'org.springframework.boot:spring-boot-starter-amqp'
          }
        highlighted: true
        explanation: "**CRITICAL**: Uses spring-boot-starter-amqp for RabbitMQ while other services (address, auth, cart) use spring-kafka. This inconsistency prevents seamless inter-service communication."

  - id: "redis-caching-config"
    title: "Redis Cache Configuration"
    description: "Spring Boot cache abstraction with Redis backend, 1-hour TTL for general cache, lettuce connection pool"
    category: "caching"
    duration: "PLACEHOLDER"
    views: 0
    tags:
      - "Redis"
      - "Cache"
      - "Spring Boot"
      - "Lettuce"
    files:
      - name: "application.yml (Redis config)"
        path: "src/main/resources/application.yml"
        language: "yaml"
        content: |
          spring:
            data:
              redis:
                host: ${SPRING_REDIS_HOST:localhost}
                port: ${SPRING_REDIS_PORT:6379}
                password: ${SPRING_REDIS_PASSWORD:redispass}
                timeout: 2000ms
                lettuce:
                  pool:
                    max-active: 8
                    max-idle: 8
                    min-idle: 0
                    max-wait: 1000ms

            cache:
              type: redis
              redis:
                time-to-live: 3600000  # 1 hour
                cache-null-values: false
                enable-statistics: true
        highlighted: true
        explanation: "Redis configured with environment variable support, lettuce pool sizing, and 1-hour TTL. Cache statistics enabled for monitoring."
  # notification-service
  - id: ""
    title: ""
    description: ""
    category: ""
    duration: ""
    views: 0
    tags:
      - ""
    files:
      - name: ""
        path: ""
        language: ""
        content: ""
        highlighted: false
        explanation: ""
  # order-service
  - id: "order-domain-model"
    title: "Order Aggregate Root - Rich Domain Model"
    description: "The Order class serves as the aggregate root, encapsulating all business logic, state transitions, and validation rules following DDD principles."
    category: "Domain Model"
    duration: "10 min read"
    views: 0
    tags:
      - "DDD"
      - "Aggregate Root"
      - "Business Logic"
      - "Java 23"
    files:
      - name: "Order.java"
        path: "src/main/java/microservice/order_service/orders/domain/models/Order.java"
        language: "java"
        content: |
          @Builder
          @Getter
          @NoArgsConstructor
          @AllArgsConstructor
          public class Order {
              private OrderID id;
              private DeliveryMethod deliveryMethod;
              private OrderStatus status;
              private String notes;
              private Money taxFee;
              private Money serviceFee;
              private PickupInfo pickupInfo;
              private DeliveryInfo deliveryInfo;
              private List<OrderItem> items;
              private UserID userID;
              private PaymentID paymentID;
              private OrderTimestamps orderTimestamps;
              private Currency orderCurrency;
              public final static Currency DEFAULT_CURRENCY = Currency.getInstance("MXN");

              public static Order create(UserID userID, DeliveryMethod deliveryMethod,
                  String notes, Money serviceFee, Money taxAmount, List<OrderItem> items) {
                  validateCreateParameters(userID, deliveryMethod, serviceFee, taxAmount, items);
                  Currency validatedCurrency = validateAndGetCurrency(serviceFee, taxAmount, DEFAULT_CURRENCY);

                  Order order = new Order();
                  order.id = OrderID.generate();
                  order.userID = userID;
                  order.deliveryMethod = deliveryMethod;
                  order.status = OrderStatus.PENDING;
                  order.notes = notes;
                  order.orderCurrency = validatedCurrency;
                  order.taxFee = taxAmount;
                  order.serviceFee = serviceFee;
                  order.items = new ArrayList<>();
                  order.orderTimestamps = OrderTimestamps.create();
                  order.items = new ArrayList<>(items);
                  order.assignItems(items);
                  return order;
              }

              public void confirm(PaymentID paymentID, LocalDateTime estimatedDeliveryDate) {
                  if (estimatedDeliveryDate != null && estimatedDeliveryDate.isBefore(LocalDateTime.now())) {
                      throw new InvalidOrderDataException("Estimated delivery date cannot be in the past");
                  }
                  if (paymentID == null) {
                      throw new InvalidOrderDataException("Payment ID cannot be null when confirming");
                  }
                  this.paymentID = paymentID;
                  changeStatus(OrderStatus.CONFIRMED);
              }

              public void changeStatus(OrderStatus newStatus) {
                  if (!this.status.canTransitionTo(newStatus)) {
                      throw new IllegalStateException(String.format("Cannot transition from %s to %s", this.status, newStatus));
                  }
                  this.status = newStatus;
                  this.orderTimestamps.orderUpdated();
              }

              public void complete() {
                  if (deliveryMethod == DeliveryMethod.STORE_PICKUP) {
                      markAsPickedUp();
                  } else {
                      markAsDelivered();
                  }
              }
          }
        highlighted: true
        explanation: "Rich domain model with factory method, state transitions, and business rule validation"

  - id: "order-status-state-machine"
    title: "Order Status State Machine"
    description: "Enum-based state machine defining valid order status transitions with clear business rules."
    category: "State Machine"
    duration: "5 min read"
    views: 0
    tags:
      - "State Pattern"
      - "Enum"
      - "Business Rules"
      - "Java 23"
    files:
      - name: "OrderStatus.java"
        path: "src/main/java/microservice/order_service/orders/domain/models/enums/OrderStatus.java"
        language: "java"
        content: |
          @Getter
          public enum OrderStatus {
              PENDING("pending", "Order has been placed but not yet processed"),
              CONFIRMED("confirmed", "Order has been confirmed and is being prepared"),
              PREPARING("preparing", "Order is being prepared for delivery/pickup"),
              READY_FOR_PICKUP("ready_for_pickup", "Order is ready to be picked up"),
              OUT_FOR_DELIVERY("out_for_delivery", "Order is out for delivery"),
              DELIVERED("delivered", "Order has been delivered"),
              PICKED_UP("picked_up", "Order has been picked up by customer"),
              CANCELLED("cancelled", "Order has been cancelled"),
              RETURNED("returned", "Order has been returned");

              private final String code;
              private final String description;

              public boolean canTransitionTo(OrderStatus newStatus) {
                  return switch (this) {
                      case PENDING -> Arrays.asList(CONFIRMED, CANCELLED).contains(newStatus);
                      case CONFIRMED -> Arrays.asList(PREPARING, CANCELLED).contains(newStatus);
                      case PREPARING -> Arrays.asList(READY_FOR_PICKUP, OUT_FOR_DELIVERY, CANCELLED).contains(newStatus);
                      case READY_FOR_PICKUP -> Arrays.asList(PICKED_UP, CANCELLED).contains(newStatus);
                      case OUT_FOR_DELIVERY -> Arrays.asList(DELIVERED, CANCELLED, RETURNED).contains(newStatus);
                      case RETURNED -> Arrays.asList(OUT_FOR_DELIVERY, CANCELLED).contains(newStatus);
                      case DELIVERED, PICKED_UP -> Objects.equals(RETURNED, newStatus);
                      case CANCELLED -> false;
                  };
              }

              public boolean isTerminal() {
                  return this == DELIVERED || this == PICKED_UP || this == CANCELLED || this == RETURNED;
              }
          }
        highlighted: true
        explanation: "Switch expression with enhanced enum pattern for type-safe state transitions"

  - id: "hexagonal-architecture-ports"
    title: "Hexagonal Architecture - Ports Definition"
    description: "Domain ports (interfaces) define contracts for input and output adapters, following hexagonal architecture principles."
    category: "Architecture"
    duration: "8 min read"
    views: 0
    tags:
      - "Hexagonal Architecture"
      - "Ports and Adapters"
      - "Interfaces"
      - "DDD"
    files:
      - name: "OrderApplicationFacade.java"
        path: "src/main/java/microservice/order_service/orders/domain/ports/input/OrderApplicationFacade.java"
        language: "java"
        content: |
          package microservice.order_service.orders.core.ports.input;

          public interface OrderApplicationFacade extends OrderCommandService, OrderQueryService {
          }
        highlighted: false
        explanation: "Facade interface combines command and query service ports"

      - name: "OrderCommandService.java"
        path: "src/main/java/microservice/order_service/orders/domain/ports/input/OrderCommandService.java"
        language: "java"
        content: |
          public interface OrderCommandService {
              CreateOrderCommandResponse createDeliveryOrder(CreateDeliveryOrderCommand command);
              CreateOrderCommandResponse createPickupOrder(CreatePickupOrderCommand command);
              void updateDeliveryAddress(UpdateOrderAddressCommand command);
              void updateDeliverMethod(UpdateOrderDeliverMethodCommand command);
              void deleteOrder(DeleteOrderCommand command);

              // Common Status Updates
              UpdateOrderStatusCommandResult confirmOrder(ConfirmOrderCommand command);
              UpdateOrderStatusCommandResult startPreparingOrder(PrepareOrderCommand command);
              UpdateOrderStatusCommandResult completeOrder(CompleteOrderCommand command);

              // Shipping and Delivery
              UpdateOrderStatusCommandResult shipOrder(ShipOrderCommand command);
              UpdateOrderStatusCommandResult returnOrder(OrderDeliverFailCommand command);
              CancelOrderCommandResponse cancelOrder(CancelOrderCommand command);

              // Pickup and In-Store Orders
              UpdateOrderStatusCommandResult readyForPickupOrder(OrderReadyToPickupCommand command);
          }
        highlighted: true
        explanation: "Input port defining all command operations the application supports"

      - name: "EventPublisher.java"
        path: "src/main/java/microservice/order_service/orders/domain/ports/output/EventPublisher.java"
        language: "java"
        content: |
          package microservice.order_service.orders.core.ports.output;

          public interface EventPublisher {
              void publish(Object event);
          }
        highlighted: false
        explanation: "Output port for publishing domain events to event bus"

  - id: "rest-controller-example"
    title: "REST Controller with OpenAPI Annotations"
    description: "SaleOrderController demonstrates REST API design with OpenAPI annotations, role-based security, and request/response mapping."
    category: "API Design"
    duration: "7 min read"
    views: 0
    tags:
      - "REST API"
      - "Spring Boot"
      - "OpenAPI"
      - "Swagger"
    files:
      - name: "SaleOrderController.java"
        path: "src/main/java/microservice/order_service/orders/infrastructure/api/controller/SaleOrderController.java"
        language: "java"
        content: |
          @Tag(name = "Orders", description = "Endpoints for complete purchaseOrder lifecycle management")
          @SecurityRequirement(name = "bearerAuth")
          @RestController
          @RequiredArgsConstructor
          @RequestMapping(value = "/api/v2/sale-orders", produces = "application/json")
          public class SaleOrderController {

              private final OrderApplicationFacade orderService;
              private final ResponseMapper<OrderResponse, OrderQueryResult> mapper;
              private final EntityDetailMapper<OrderDetailResult, OrderDetailResponse> detailMapper;

              @SearchOrdersOperation
              @GetMapping("/search")
              public ResponseWrapper<PageResponse<OrderResponse>> searchOrders(
                  @Valid OrderSearchRequest request) {
                  SearchOrdersQuery query = SearchOrdersQuery.fromRequest(request);
                  Page<OrderQueryResult> resultPage = orderService.searchOrders(query);
                  PageResponse<OrderResponse> response = mapper.toResponsePage(resultPage);
                  return ResponseWrapper.success(response, "Orders found successfully");
              }

              @CreateOrderOperation
              @PostMapping(consumes = "application/json")
              public ResponseWrapper<CreateOrderCommandResponse> createOrder(
                  @Valid @RequestBody CreateOrderRequest request) {
                  if (request.deliveryMethod() != null) {
                      var command = request.toDeliveryOrderCommand();
                      var result = orderService.createDeliveryOrder(command);
                      return ResponseWrapper.created(result, "PurchaseOrder");
                  }
                  var command = request.toPickupOrderCommand();
                  var result = orderService.createPickupOrder(command);
                  return ResponseWrapper.created(result, "PurchaseOrder");
              }
          }
        highlighted: true
        explanation: "Clean controller with custom OpenAPI annotations and unified response wrapper"

  - id: "decorator-pattern-caching"
    title: "Decorator Pattern for Cross-Cutting Concerns"
    description: "CachingUserServiceDecorator demonstrates the Decorator pattern to add caching behavior transparently."
    category: "Design Patterns"
    duration: "6 min read"
    views: 0
    tags:
      - "Decorator Pattern"
      - "Caching"
      - "Spring Cache"
      - "Redis"
    files:
      - name: "CachingUserServiceDecorator.java"
        path: "src/main/java/microservice/order_service/external/users/application/service/decorator/CachingUserServiceDecorator.java"
        language: "java"
        content: |
          @RequiredArgsConstructor
          @Service
          public class CachingUserServiceDecorator implements UserService {

              private final UserService delegate;
              private final CacheManager cacheManager;

              @Override
              public User getUserByID(String userID) {
                  Cache cache = cacheManager.getCache("users");
                  if (cache != null) {
                      Cache.ValueWrapper wrapper = cache.get(userID);
                      if (wrapper != null) {
                          return (User) wrapper.get();
                      }
                  }

                  User user = delegate.getUserByID(userID);

                  if (cache != null && user != null) {
                      cache.put(userID, user);
                  }
                  return user;
              }

              @Override
              public void clearCache(String userID) {
                  Cache cache = cacheManager.getCache("users");
                  if (cache != null) {
                      cache.evict(userID);
                  }
              }
          }
        highlighted: true
        explanation: "Decorator adds caching transparently without modifying the original UserService"

  - id: "domain-events"
    title: "Domain Events for Event-Driven Architecture"
    description: "Domain events (OrderCreatedEvent, OrderStatusChangedEvent) enable loose coupling and async processing."
    category: "Event-Driven"
    duration: "5 min read"
    views: 0
    tags:
      - "Domain Events"
      - "DDD"
      - "Event-Driven"
      - "Decoupling"
    files:
      - name: "OrderCreatedEvent.java"
        path: "src/main/java/microservice/order_service/orders/domain/models/events/OrderCreatedEvent.java"
        language: "java"
        content: |
          public record OrderCreatedEvent(
              OrderID orderId,
              UserID userID,
              Money totalAmount,
              LocalDateTime createdAt
          ) {
              public OrderCreatedEvent {
                  if (orderId == null) throw new IllegalArgumentException("Order ID cannot be null");
                  if (userID == null) throw new IllegalArgumentException("UserID cannot be null");
                  if (totalAmount == null) throw new IllegalArgumentException("Total amount cannot be null");
                  if (createdAt == null) throw new IllegalArgumentException("Created at cannot be null");
              }
          }
        highlighted: true
        explanation: "Java 23 record with validation in canonical constructor for immutable domain event"

      - name: "OrderStatusChangedEvent.java"
        path: "src/main/java/microservice/order_service/orders/domain/models/events/OrderStatusChangedEvent.java"
        language: "java"
        content: |
          public record OrderStatusChangedEvent(
              OrderID orderID,
              OrderStatus oldStatus,
              OrderStatus newStatus,
              LocalDateTime changedAt
          ) {
              public OrderStatusChangedEvent {
                  if (orderID == null) throw new IllegalArgumentException("Order ID cannot be null");
                  if (oldStatus == null) throw new IllegalArgumentException("Old status cannot be null");
                  if (newStatus == null) throw new IllegalArgumentException("New status cannot be null");
                  if (changedAt == null) throw new IllegalArgumentException("Changed at cannot be null");
              }
          }
        highlighted: false
        explanation: "Event capturing status transition for async notification and audit trail"
  # payment-service
  - id: ""
    title: ""
    description: ""
    category: ""
    duration: ""
    views: 0
    tags:
      - ""
    files:
      - name: ""
        path: ""
        language: ""
        content: ""
        highlighted: false
        explanation: ""
  # product-service
  - id: "controller-endpoints"
    title: "HTTP API endpoint orchestration"
    description: "Controller methods map HTTP requests to use-case commands/queries and wrap responses."
    category: "api"
    duration: "5 min"
    views: 0
    tags:
      - "spring-web"
      - "controller"
      - "validation"
    files:
      - name: "ProductController"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/products/adapter/in/web/controller/ProductController.java"
        language: "java"
        content: "@GetMapping, @PostMapping, @PutMapping, @PatchMapping, @DeleteMapping over /api/v2/products"
        highlighted: false
        explanation: "Defines API surface and delegates business logic to command/query use cases."

  - id: "cache-decorator"
    title: "Use-case decorator for cache-first reads"
    description: "CachingProductQueryUseCases implements cache lookup, fallback execution, and cache put."
    category: "performance"
    duration: "4 min"
    views: 0
    tags:
      - "spring-cache"
      - "redis"
      - "decorator"
    files:
      - name: "CachingProductQueryUseCases"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/products/core/application/usecase/decorator/CachingProductQueryUseCases.java"
        language: "java"
        content: "productById/productBySKU/productByBarcode/productSearch cache access and key construction"
        highlighted: false
        explanation: "Provides transparent read optimization while preserving the same port interface."

  - id: "security-filter-chain"
    title: "Stateless JWT security chain"
    description: "SecurityConfig defines role-based route access and JWT filter insertion."
    category: "security"
    duration: "3 min"
    views: 0
    tags:
      - "spring-security"
      - "jwt"
      - "rbac"
    files:
      - name: "SecurityConfig"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/products/config/security/SecurityConfig.java"
        language: "java"
        content: "permitAll for docs/health + role checks for write endpoints + denyAll fallback"
        highlighted: false
        explanation: "Hardens API access model with explicit allowlist and stateless processing."
  # store-service
  - id: "ex-security-chain"
    title: "Security filter chain — anonymous reads vs role-gated writes"
    description: "Shows CSRF off, JWT filter, and the split between permitAll GET under /api/v2/stores and verb-specific hasAnyRole for mutations."
    category: "security"
    duration: "5 min read"
    views: 0
    tags:
      - "Spring Security"
      - "JWT"
    files:
      - name: "SecurityConfig.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/stores/config/security/SecurityConfig.java"
        language: "java"
        highlighted: true
        content: |
          .requestMatchers(HttpMethod.GET, "/api/v2/stores/**").permitAll()
          .requestMatchers(HttpMethod.POST, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
          .requestMatchers(HttpMethod.PUT, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
          .requestMatchers(HttpMethod.PATCH, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
          .requestMatchers(HttpMethod.DELETE, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
        explanation: "Documented in OpenAPI as bearer-secured, but GETs do not require a token — align docs or policy before production."

  - id: "ex-cache-redis"
    title: "Redis CacheManager with Jackson JSON values"
    description: "30-minute TTL, nulls disabled, custom ObjectMapper including JavaTimeModule and default typing activation."
    category: "performance"
    duration: "4 min read"
    views: 0
    tags:
      - "Redis"
      - "Jackson"
    files:
      - name: "CacheConfig.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/stores/config/CacheConfig.java"
        language: "java"
        highlighted: true
        content: |
          RedisCacheConfiguration.defaultCacheConfig()
              .entryTtl(Duration.ofMinutes(30))
              .disableCachingNullValues()
          objectMapper.activateDefaultTyping(
              objectMapper.getPolymorphicTypeValidator(),
              ObjectMapper.DefaultTyping.NON_FINAL);
        explanation: "Default typing can be unsafe if untrusted data reaches Redis; prefer explicit DTO types or disable typing for hardened deployments."

  - id: "ex-usecase-cache-evict"
    title: "Transactional command + broad cache eviction"
    description: "Every mutating use case evicts `stores`, `store_searches`, and `store_status` entirely to avoid stale reads."
    category: "application"
    duration: "3 min read"
    views: 0
    tags:
      - "Spring Cache"
      - "Transactional"
    files:
      - name: "StoreCommandUseCasesImpl.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/stores/application/usecase/StoreCommandUseCasesImpl.java"
        language: "java"
        highlighted: false
        content: |
          @Transactional
          @CacheEvict(value = { "stores", "store_searches", "store_status" }, allEntries = true)
          public CreateStoreResult createStore(CreateStoreCommand command) { ... }
        explanation: "Simple correctness; can increase Redis churn at high write rates — consider finer-grained eviction later."

  - id: "ex-event-stub"
    title: "Outbound messaging adapter (no-op)"
    description: "Implements port but does not call Kafka; logs success even when nothing is sent."
    category: "integration"
    duration: "2 min read"
    views: 0
    tags:
      - "Kafka"
      - "TODO"
    files:
      - name: "StoreEventPublisherImpl.java"
        path: "src/main/java/io/github/alexisTrejo11/drugstore/stores/infrastructure/outbound/external/messaging/StoreEventPublisherImpl.java"
        language: "java"
        highlighted: true
        content: |
          public void publishStoreStatusChanged(StoreStatusChangedEvent event) {
            try {
              log.info("Publishing StoreStatusChangedEvent for store: {}", event.getStoreId());
              log.info("StoreStatusChangedEvent published successfully for store: {}", event.getStoreId());
            } catch (Exception e) {
              log.error("Failed to publish ...", e);
            }
          }
        explanation: "Misleading log line says 'published successfully' without broker interaction — fix before relying on logs for ops."
  # user-service
  - id: "security-manager-routes"
    title: "JWT + role gates for manager API"
    description: "SecurityConfig permits actuator and Swagger assets, requires ADMIN/MANAGER for /api/v2/users/manager/**, authenticated for other /api/**."
    category: "security"
    duration: "5 min read"
    views: 0
    tags:
      - "Spring Security"
      - "JWT"
    files:
      - name: "SecurityConfig.java"
        path: "user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/config/security/SecurityConfig.java"
        language: "java"
        content: |
          @Bean
          public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
              .csrf(AbstractHttpConfigurer::disable)
              .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/v2/users/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().denyAll())
              .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
            return http.build();
          }
        highlighted: true
        explanation: "**Note:** Actuator is permitAll here — harden in production (IP allowlist or auth). Swagger paths are ignored via WebSecurityCustomizer."

  - id: "command-bus-create-user"
    title: "Manager create user → CommandBus"
    description: "HTTP DTO maps to CreateUserCommand with fixed CUSTOMER role for self-service style onboarding by staff."
    category: "application"
    duration: "3 min read"
    views: 0
    tags:
      - "CQRS"
      - "REST"
    files:
      - name: "UserManagerController.java (excerpt)"
        path: "user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/input/rest/UserManagerController.java"
        language: "java"
        content: |
          @PostMapping("/")
          public ResponseEntity<ResponseWrapper<?>> createUser(@Valid @RequestBody UserRequest userRequest) {
            CreateUserCommand command = userRequest.toCommand(UserRole.CUSTOMER);
            CommandResult commandResult = commandBus.dispatch(command);
            var response = ResponseWrapper.success(commandResult.data(), "User created successfully");
            return ResponseEntity.status(201).body(response);
          }
        highlighted: true
        explanation: "**Product note:** Creating users always assigns CUSTOMER — extend if EMPLOYEE provisioning is needed via separate command."

  - id: "kafka-consumer-ack"
    title: "Kafka listener with manual ack"
    description: "Successful path acknowledges; failures log and throw — risking infinite delivery without DLQ."
    category: "messaging"
    duration: "4 min read"
    views: 0
    tags:
      - "Kafka"
      - "Reliability"
    files:
      - name: "UserEventConsumer.java (excerpt)"
        path: "user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/output/messaging/kafka/consumer/UserEventConsumer.java"
        language: "java"
        content: |
          @KafkaListener(topics = "${kafka.topics.user.created}", ...)
          public void consumeUserCreated(@Payload String payload, ..., Acknowledgment acknowledgment) {
            try {
              UserCreatedEvent event = objectMapper.readValue(payload, UserCreatedEvent.class);
              userCreatedEventHandler.handle(event);
              acknowledgment.acknowledge();
            } catch (Exception e) {
              // TODO: retry or DLQ
              throw new RuntimeException("Failed to process UserCreatedEvent", e);
            }
          }
        highlighted: true
        explanation: "**Danger:** operator must monitor consumer lag / errors; poison messages stall partition progress."

  - id: "grpc-not-started"
    title: "gRPC UserGrpcServer stub (no server lifecycle)"
    description: "Implements Generated UserServiceGrpc but repository search shows no ServerBuilder wiring."
    category: "integration"
    duration: "2 min read"
    views: 0
    tags:
      - "gRPC"
      - "Incomplete"
    files:
      - name: "UserGrpcServer.java (class header)"
        path: "user-service/src/main/java/io/github/alexisTrejo11/drugstore/users/user/adapter/output/grpc/server/UserGrpcServer.java"
        language: "java"
        content: |
          @Component
          public class UserGrpcServer extends UserServiceGrpc.UserServiceImplBase {
            // implements RPC methods — no @PostConstruct Netty ServerBuilder found in codebase
          }
        highlighted: false
        explanation: "**Missing:** add spring-grpc starter or lifecycle bean invoking ServerBuilder.forPort(grpcPort).addService(...) .start()."

  - id: "placeholder-integration-test"
    title: "E2E pattern with seeded users + JWT"
    description: "UserServiceE2EIntegrationTest exercises happy paths with H2/Test profile repositories."
    category: "testing"
    duration: "6 min read"
    views: 0
    tags:
      - "JUnit"
      - "MockMvc"
    files:
      - name: "UserServiceE2EIntegrationTest.java"
        path: "user-service/src/test/java/microservice/user_service/UserServiceE2EIntegrationTest.java"
        language: "java"
        content: "// See repo — builds JWT via jjwt Keys.hmacShaKeyFor(jwt.secret) for bearer tokens in tests."
        highlighted: false
        explanation: "Good regression harness; extend with chaos / Kafka testcontainers for messaging paths."
---

# Project Code Showcase

> Auto-generated by `scripts/merge_service_sources.py`. Edit service-level `docs/project/source/*.md` files, then regenerate.

<!-- BEGIN address-service -->
<!-- Source: address-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase

> 4 comprehensive code examples covering key patterns: Factory/Strategy for validation, AOP for rate limiting, Service layer for business logic, and dual-controller architecture. All examples are production-ready code from the actual codebase. Potential additions: Kafka event publishing example, JPA entity with @Embedded/@Embeddable example, or custom annotation meta-annotation example.

<!-- END address-service -->

<!-- BEGIN auth-service -->
<!-- Source: auth-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase

> Examples span DDD, security, messaging, persistence, and **full-stack integration tests** under `src/test/.../integration/`. Potential additions: OAuth2 success handler walkthrough, CustomOAuth2UserService, value objects, domain events, and more unit-level tests.

<!-- END auth-service -->

<!-- BEGIN cart-service -->
<!-- Source: cart-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase

> 6 comprehensive code examples covering DDD, gRPC, Kafka, and design patterns. Has 11 unit test files for domain layer. PLACEHOLDER issues: CartPurchasedEvent not published to Kafka, @RateLimit not applied to REST endpoints, no integration tests for gRPC. Potential additions: Circuit Breaker for external calls, caching annotations (@Cacheable), event publishing implementation.

<!-- END cart-service -->

<!-- BEGIN employee-service -->
<!-- Source: employee-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase

> 6 comprehensive code examples covering CQS, JPA entity model, rate limiting, soft delete, specifications, and embeddable objects. Uses @RateLimit from libs-kernel (correctly implemented unlike cart-service). PLACEHOLDER issues: No Dockerfile/docker-compose.yml, no Kafka event publishing, no unit/integration tests found. Potential additions: Kafka events (employee.created/updated/deleted), caching annotations, Kubernetes manifests, CI/CD pipeline.

<!-- END employee-service -->

<!-- BEGIN inventory-service -->
<!-- Source: inventory-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase

> **OBSERVATIONS:**
> 1. **Batch Tracking**: `InventoryBatchEntity` provides pharmaceutical-grade batch tracking with lot numbers, expiration dates, and status (ACTIVE, EXPIRED, DAMAGED, QUARANTINED). This is critical for drugstore compliance.
> 2. **Reservation Pattern**: The reservation system allows temporary stock reservation during order processing with confirm/release lifecycle - essential for e-commerce scenarios.
> 3. **Stock Movements**: Adjustments track reasons (damage, loss, correction) while transfers move stock between inventory locations. Both create audit trails via `InventoryMovement` entities.
> 4. **RabbitMQ Inconsistency**: Using RabbitMQ while other services use Kafka is a **major architectural inconsistency**. Consider migrating to Kafka for platform uniformity.
> 5. **Missing gRPC**: Unlike address-service, auth-service, and cart-service, inventory-service doesn't expose gRPC endpoints for inter-service communication.
> 6. **CQRS Pattern**: Controllers use command/query separation (e.g., `GetInventoryMovementsQuery`, `AdjustInventoryRequest.toCommand()`).
> 7. **Integration tests**: `InventoryApiIntegrationTest` drives REST APIs through MockMvc under profile `test` (H2, real JWT validation via `IntegrationTestJwtSupport`). See `docs/project/generated/ProjectFeature.md`.

<!-- END inventory-service -->

<!-- BEGIN notification-service -->
<!-- Source: notification-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase
> Notes goes here....

<!-- END notification-service -->

<!-- BEGIN order-service -->
<!-- Source: order-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase
> Code examples showcasing DDD, hexagonal architecture, state machine pattern, REST API design with OpenAPI, Decorator pattern for caching, and domain events for event-driven architecture. All examples are from actual production code in the Order Service.

<!--
  OBSERVATIONS FOR CodeShowCase:
  ✅ POSITIVE:
    - 6 comprehensive code examples covering key architectural concepts
    - Real production code from the actual codebase
    - Examples show modern Java 23 features (records, enhanced switch, Lombok)
    - Covers DDD patterns (Aggregate Root, Value Objects, Domain Events)
    - Shows design patterns (Decorator, Facade, State, Builder)
    - Code examples have explanations and highlighted sections
    - OpenAPI annotation usage demonstrated

  ⚠️ WARNINGS / MISSING / DANGEROUS:
    - "views: 0" is placeholder - no actual view tracking implemented
    - CachingUserServiceDecorator example shows manual cache management - could use @Cacheable annotation instead
    - No unit test code examples included - test coverage unknown
    - Order.create() factory method doesn't show OrderCreatedEvent being published (should happen after persistence)
    - Domain events (OrderCreatedEvent, OrderStatusChangedEvent) are defined but no example of EventPublisher usage
    - codeExamples don't include error handling or edge case examples
    - No example of OrderSpecifications usage for dynamic queries
    - Record classes used for DTOs but Java 23 is required - limits compatibility
    - Could add examples of: OrderItem, Money value object, OrderTimestamps
-->

<!-- END order-service -->

<!-- BEGIN payment-service -->
<!-- Source: payment-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase
> Notes goes here....

<!-- END payment-service -->

<!-- BEGIN product-service -->
<!-- Source: product-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase

## Notes

- Audit logs currently show `serviceName` as `address-service` in runtime output, indicating incorrect audit metadata wiring.
- Caching command decorator clears entire SKU/barcode/search caches on writes; acceptable for now but may become expensive at scale.

<!-- END product-service -->

<!-- BEGIN store-service -->
<!-- Source: store-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase

Curated slices for reviewers: **security matrix**, **Redis serialization choices**, **cache eviction strategy**, and the **stub event publisher** (integration gap).

> [!danger] Observability deception risk  
> Success logs on `StoreEventPublisherImpl` do **not** imply Kafka delivery — do not wire alerts on that string alone.

<!-- END store-service -->

<!-- BEGIN user-service -->
<!-- Source: user-service/docs/project/source/ProjectCodeShowCase.md -->
# CodeShowCase

Curated excerpts reference real paths under `user-service/`. Inline snippets are abbreviated for documentation; verify against Git for exact line fidelity.

<!-- END user-service -->
