package io.github.alexisTrejo11.drugstore.users.app.user.adapter.output.grpc.server;

import com.microservices.grpc.user.*;
import com.microservices.grpc.user.UserServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Component;

@Component
public class UserGrpcServer extends UserServiceGrpc.UserServiceImplBase {

    @Override
    public void isEmailUnique(EmailRequest request, StreamObserver<BoolResponse> responseObserver) {
        // TODO: Implementar lógica para verificar si el email es único
        BoolResponse response = BoolResponse.newBuilder()
                .setValue(true)
                .setMessage("Email is unique")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isPhoneNumberUnique(PhoneNumberRequest request, StreamObserver<BoolResponse> responseObserver) {
        // TODO: Implementar lógica para verificar si el número de teléfono es único
        BoolResponse response = BoolResponse.newBuilder()
                .setValue(true)
                .setMessage("Phone number is unique")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isUserExistsByEmail(EmailRequest request, StreamObserver<BoolResponse> responseObserver) {
        // TODO: Implementar lógica para verificar si el usuario existe por email
        BoolResponse response = BoolResponse.newBuilder()
                .setValue(false)
                .setMessage("User does not exist")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void isUserExistsByPhoneNumber(PhoneNumberRequest request, StreamObserver<BoolResponse> responseObserver) {
        // TODO: Implementar lógica para verificar si el usuario existe por teléfono
        BoolResponse response = BoolResponse.newBuilder()
                .setValue(false)
                .setMessage("User does not exist")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void validateUserCredentials(ValidateUserCredentialsRequest request, StreamObserver<ValidateUserCredentialsResponse> responseObserver) {
        // TODO: Implementar lógica para validar credenciales de usuario
        ValidateUserCredentialsResponse response = ValidateUserCredentialsResponse.newBuilder()
                .setIsValid(false)
                .setUserId("")
                .setMessage("Invalid credentials")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByEmail(EmailRequest request, StreamObserver<UserResponse> responseObserver) {
        // TODO: Implementar lógica para obtener usuario por email
        UserResponse response = UserResponse.newBuilder()
                .setUserId("")
                .setEmail(request.getEmail())
                .setFirstName("")
                .setLastName("")
                .setPhoneNumber("")
                .setRole("")
                .setTwoFactorEnabled(false)
                .setEmailVerified(false)
                .setStatus("")
                .setCreatedAt("")
                .setUpdatedAt("")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByPhone(PhoneNumberRequest request, StreamObserver<UserResponse> responseObserver) {
        // TODO: Implementar lógica para obtener usuario por teléfono
        UserResponse response = UserResponse.newBuilder()
                .setUserId("")
                .setEmail("")
                .setFirstName("")
                .setLastName("")
                .setPhoneNumber(request.getPhoneNumber())
                .setRole("")
                .setTwoFactorEnabled(false)
                .setEmailVerified(false)
                .setStatus("")
                .setCreatedAt("")
                .setUpdatedAt("")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserById(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {
        // TODO: Implementar lógica para obtener usuario por ID
        UserResponse response = UserResponse.newBuilder()
                .setUserId(request.getUserId())
                .setEmail("")
                .setFirstName("")
                .setLastName("")
                .setPhoneNumber("")
                .setRole("")
                .setTwoFactorEnabled(false)
                .setEmailVerified(false)
                .setStatus("")
                .setCreatedAt("")
                .setUpdatedAt("")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
