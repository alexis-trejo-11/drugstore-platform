package microservice.order_service.external.users.infrastructure.api.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.domain.ports.input.UserService;
import microservice.order_service.external.users.infrastructure.api.dto.UserInsertRequest;
import microservice.order_service.external.users.infrastructure.api.dto.UserResponse;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
public class UserController {
    private final UserService userService;
    private final ResponseMapper<UserResponse, User> responseMapper;

    @GetMapping("/{id}")
    public ResponseWrapper<UserResponse> getUserById(@PathVariable String id) {
        User user = userService.getUserByID(UserID.of(id));
        UserResponse userResponse = responseMapper.toResponse(user);
        return ResponseWrapper.success(userResponse);
    }

    @GetMapping("/email/{email}")
    public ResponseWrapper<UserResponse> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserByEmail(email);
        UserResponse userResponse = responseMapper.toResponse(user);
        return ResponseWrapper.success(userResponse);
    }


    @GetMapping("/phone/{phoneNumber}")
    public ResponseWrapper<UserResponse> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        User user = userService.getUserByPhoneNumber(phoneNumber);
        UserResponse userResponse = responseMapper.toResponse(user);
        return ResponseWrapper.success(userResponse);
    }


    @PostMapping
    public ResponseWrapper<UserResponse> createUser(@Valid @RequestBody UserInsertRequest request) {
        User createdUser = userService.createUser(request.toCommand());
        UserResponse userResponse = responseMapper.toResponse(createdUser);
        return ResponseWrapper.success(userResponse);
    }


    @PutMapping("/{id}")
    public ResponseWrapper<UserResponse> updateUser(@PathVariable String id, @Valid @RequestBody UserInsertRequest request) {
        var command = request.toCommand(id);
        User updatedUser = userService.updateUser(command);
        UserResponse userResponse = responseMapper.toResponse(updatedUser);
        return ResponseWrapper.success(userResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseWrapper<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(UserID.of(id));
        return ResponseWrapper.success();
    }

}
