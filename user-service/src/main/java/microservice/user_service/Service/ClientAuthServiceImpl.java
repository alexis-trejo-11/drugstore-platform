package microservice.user_service.Service;


import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.LoginDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Cart.CartFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.user_service.Mappers.UserMapper;
import microservice.user_service.Model.User;
import microservice.user_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ClientAuthServiceImpl implements ClientAuthService {

    private final CartFacadeService cartFacadeService;
    private final UserRepository userRepository;
    private final ClientFacadeService clientFacadeService;
    private final UserMapper userMapper;
    private final AuthDomainService authDomainService;

    @Autowired
    public ClientAuthServiceImpl(CartFacadeService cartFacadeService,
                                 UserRepository userRepository,
                                 @Qualifier("clientFacadeService") ClientFacadeService clientFacadeService,
                                 UserMapper userMapper,
                                 AuthDomainService authDomainService) {
        this.cartFacadeService = cartFacadeService;
        this.userRepository = userRepository;
        this.clientFacadeService = clientFacadeService;
        this.userMapper = userMapper;
        this.authDomainService = authDomainService;
    }

    @Override
    @Cacheable(value = "userCache", key = "#clientSignUpDTO.email", unless = "#result.success == false")
    public Result<Void> validateUniqueFields(ClientSignUpDTO clientSignUpDTO) {
        if (clientSignUpDTO.getEmail() != null) {
            Optional<User> userEmailOptional = userRepository.findByEmail(clientSignUpDTO.getEmail());
            if (userEmailOptional.isPresent()) {
                return Result.error("Email Already Taken");
            }
        }

        if (clientSignUpDTO.getPhoneNumber() != null) {
            Optional<User> userPhoneOptional = userRepository.findByPhoneNumber(clientSignUpDTO.getPhoneNumber());
            if (userPhoneOptional.isPresent()) {
                return Result.error("Phone Number Already Taken");
            }
        }
        return Result.success();
    }

    @Override
    @CacheEvict(value = "userCache", allEntries = true)
    public String processClientSignup(ClientSignUpDTO clientSignUpDTO) {
        ClientInsertDTO clientInsertDTO = userMapper.clientSignupDtoToClientInsertDTO(clientSignUpDTO);

        // Create the client synchronously (wait for the result)
        ClientDTO clientDTO = clientFacadeService.createClient(clientInsertDTO).join();
        // Create the client's cart synchronously (wait for the result)
        cartFacadeService.createClientCart(clientDTO.getId()).join();

        return authDomainService.processClientUserCreation(clientSignUpDTO, clientDTO);
    }
}
