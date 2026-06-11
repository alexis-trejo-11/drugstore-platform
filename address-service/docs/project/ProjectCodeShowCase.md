# Project Code Showcase

## 1) Postal Code Validator Factory Pattern
**Category:** Design Patterns  
**Highlights:**
- Factory assembles validators from Spring DI.
- Strategy interface per country.
- Default validator fallback.

**Main files:**
- `src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation/PostalCodeValidatorFactory.java`
- `src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation/PostalCodeValidator.java`
- `src/main/java/io/github/alexisTrejo11/drugstore/address/utils/validation/USPostalCodeValidator.java`

## 2) Redis Rate Limiting with AOP
**Category:** Security  
**Highlights:**
- `@Around` advice enforces limits before controller execution.
- Redis atomic increments with TTL window reset.
- Multiple profiles for endpoint sensitivity.

**Main files:**
- `src/main/java/io/github/alexisTrejo11/drugstore/address/config/ratelimit/RateLimitAspect.java`
- `src/main/java/io/github/alexisTrejo11/drugstore/address/config/ratelimit/RedisRateLimiter.java`

## 3) Address Service CRUD Orchestration
**Category:** Business Logic  
**Highlights:**
- Transactional create/update/delete.
- Address limit checks by role.
- Default-address reset and soft-delete behavior.

**Main files:**
- `src/main/java/io/github/alexisTrejo11/drugstore/address/service/AddressService.java`
- `src/main/java/io/github/alexisTrejo11/drugstore/address/utils/dto/Address.java`

## 4) Dual Controller Architecture (User/Admin)
**Category:** API Design  
**Highlights:**
- Separate route groups for user and admin flows.
- Shared service layer with role-aware behavior.
- OpenAPI annotations and rate-limit integration.

**Main files:**
- `src/main/java/io/github/alexisTrejo11/drugstore/address/controller/UserAddressController.java`
- `src/main/java/io/github/alexisTrejo11/drugstore/address/controller/AddressAdminController.java`
