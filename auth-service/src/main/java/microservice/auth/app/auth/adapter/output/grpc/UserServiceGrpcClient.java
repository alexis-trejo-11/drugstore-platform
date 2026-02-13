package microservice.auth.app.auth.adapter.output.grpc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import microservice.auth.app.User;
import microservice.auth.app.auth.core.ports.output.UserServiceClient;
import microservice.order_service.external.users.application.service.UserService;

@Component
public class UserServiceGrpcClient implements UserServiceClient {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserServiceGrpcClient.class);
  private final UserServiceGrpc.UserServiceBlockingStub userServiceStub;
  private final UserGrpcMapper userGrpcMapper;

  @Autowired
  public UserServiceGrpcClient(UserServiceGrpc.UserServiceBlockingStub userServiceStub, UserGrpcMapper userGrpcMapper) {
    this.userServiceStub = userServiceStub;
    this.userGrpcMapper = userGrpcMapper;
  }

  @Override
  public boolean isEmailUnique(String email) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isPhoneUnique(String phone) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void validateUserCredentials(String email, String password) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isUserExists(String email) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public User getUserByEmail(String email) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public User getUserByPhone(String phone) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public User getUserById(String userId) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
