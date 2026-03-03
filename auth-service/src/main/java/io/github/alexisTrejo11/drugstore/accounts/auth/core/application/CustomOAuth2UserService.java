package io.github.alexisTrejo11.drugstore.accounts.auth.core.application;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Password;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserRole;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.AuthUseCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
		private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CustomOAuth2UserService.class);
    private final AuthUseCases authUseCases;

		@Autowired
    public CustomOAuth2UserService(AuthUseCases authUseCases) {
        this.authUseCases = authUseCases;
    }

		// TODO: Adjust command handler to support OAuth2 user creation without password and with default role
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
				String provider = userRequest.getClientRegistration().getRegistrationId();
				String pictureUrl = oAuth2User.getAttribute("picture");

        log.info("OAuth2 login attempt for email: {}", email);

				SignupCommand.PersonalInfo personalInfo = new SignupCommand.PersonalInfo(
						name != null ? name.split(" ")[0] : "Unknown",
						name != null && name.split(" ").length > 1 ? name.split(" ")[1] : "User",
						null, // Date of birth not provided by OAuth2 provider
						"Unknown" // Gender not provided by OAuth2 provider
				);

        SignupCommand command = SignupCommand.builder()
		        .email(new Email(email))
		        .personalInfo(personalInfo)
		        .role(UserRole.CUSTOMER)
		        .provider(provider)
		        .pictureUrl(pictureUrl)
		        .password(Password.DEFAULT_OAUTH_PASSWORD) // Placeholder password, not used for OAuth2 users
		        .build();

         authUseCases.signUp(command);

        return oAuth2User;
    }
}