package microservice.user_service;

import io.github.alexisTrejo11.drugstore.users.UserServiceApplication;
import libs_kernel.log.audit.AuditLogger;
import libs_kernel.security.ApiSecurityResponseWriter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
    classes = UserServiceApplication.class,
    properties = "app.rate-limit.global.enabled=false"
)
@ActiveProfiles("test")
class UserServiceApplicationTests {

    @MockBean
    private AuditLogger auditLogger;
    @MockBean
    private ApiSecurityResponseWriter apiSecurityResponseWriter;

	@Test
	void contextLoads() {
	}

}
