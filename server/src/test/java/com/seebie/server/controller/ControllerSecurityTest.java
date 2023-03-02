package com.seebie.server.controller;


import com.seebie.server.AppProperties;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.support.MockMvcRunner;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.seebie.server.test.data.TestData.createRandomPersonalInfo;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static org.springframework.http.HttpMethod.*;

// The next three annotations could be replaced by @WebMvcTest
// except we need @SpringBootTest to expose actuator endpoints
@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration
@DisplayName("Endpoint Security")
@EnableConfigurationProperties(value = {AppProperties.class})
@Import(WebSecurityConfig.class)
public class ControllerSecurityTest {

	private static final String USERNAME = "someuser";
	private static final String ADMINNAME = "admin";

	// so we get the mapper as configured for the app
	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MappingJackson2HttpMessageConverter converter;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MockMvc mockMvc;

	@MockBean
	private UserService service;

	@MockBean
	private SleepService sleepService;

	private MockMvcRunner mvc;

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = new SleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();
	private static final String password = "new_password";

	@PostConstruct
	public void setup() {
		mvc = new MockMvcRunner(mockMvc, converter);
	}

	private static List<Arguments> provideUnauthenticatedTestParameters() {
		return List.of(

				// actuator
				Arguments.of(GET, "/actuator", "", 401),
				Arguments.of(GET, "/actuator/flyway", "", 401),
				Arguments.of(GET, "/actuator/health", "", 401),
				Arguments.of(GET, "/actuator/info", "", 401),
				Arguments.of(GET, "/actuator/mappings", "", 401),
				Arguments.of(GET, "/actuator/sessions?username=admin", "", 401),

				// unsecured resources
				Arguments.of(GET, "/", "", 200),
				Arguments.of(GET, "/favicon.ico", "", 200),
				Arguments.of(GET, "/manifest.json", "", 200),

				// user controller
				Arguments.of(POST, "/registration", registration, 401),
				Arguments.of(GET, "/login", "", 401),
				Arguments.of(GET, "/user", "", 401),

				Arguments.of(PUT, "/user/" + USERNAME + "/personalInfo", registration, 401),
				Arguments.of(POST, "/user/" + USERNAME + "/password/update", password, 401),
				Arguments.of(GET, "/user/" + USERNAME, "", 401),

				Arguments.of(PUT, "/user/" + ADMINNAME + "/personalInfo", info, 401),
				Arguments.of(POST, "/user/" + ADMINNAME + "/password/update", password, 401),
				Arguments.of(GET, "/user/" + ADMINNAME, "", 401),

				// sleep controller
				Arguments.of(POST, "/user/" + USERNAME + "/sleep", sleepData, 401),
				Arguments.of(GET, "/user/" + USERNAME + "/sleep", "", 401),
				Arguments.of(GET, "/user/" + USERNAME + "/sleep" + "/1", "", 401),
				Arguments.of(PUT, "/user/" + USERNAME + "/sleep" + "/1", sleepData, 401),
				Arguments.of(DELETE, "/user/" + USERNAME + "/sleep" + "/1", "", 401),

				Arguments.of(POST, "/user/" + ADMINNAME + "/sleep", sleepData, 401),
				Arguments.of(GET, "/user/" + ADMINNAME + "/sleep", "", 401),
				Arguments.of(GET, "/user/" + ADMINNAME + "/sleep" + "/1", "", 401),
				Arguments.of(PUT, "/user/" + ADMINNAME + "/sleep" + "/1", sleepData, 401),
				Arguments.of(DELETE, "/user/" + ADMINNAME + "/sleep" + "/1", "", 401)

		);
	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(

				// actuator
				Arguments.of(GET, "/actuator", "", 200),
				Arguments.of(GET, "/actuator/flyway", "", 200),
				Arguments.of(GET, "/actuator/health", "", 200),
				Arguments.of(GET, "/actuator/info", "", 200),
				Arguments.of(GET, "/actuator/mappings", "", 200),
				Arguments.of(GET, "/actuator/sessions?username=admin", "", 200),

				// unsecured resources
				Arguments.of(GET, "/", "", 200),
				Arguments.of(GET, "/favicon.ico", "", 200),
				Arguments.of(GET, "/manifest.json", "", 200),

				// user controller
				Arguments.of(POST, "/registration", registration, 200),
				Arguments.of(GET, "/login", "", 200),
				Arguments.of(GET, "/user", "", 200),

				Arguments.of(PUT, "/user/" + USERNAME + "/personalInfo", info, 200),
				Arguments.of(POST, "/user/" + USERNAME + "/password/update", password, 200),
				Arguments.of(GET, "/user/" + USERNAME, "", 200),

				Arguments.of(PUT, "/user/" + ADMINNAME + "/personalInfo", info, 200),
				Arguments.of(POST, "/user/" + ADMINNAME + "/password/update", password, 200),
				Arguments.of(GET, "/user/" + ADMINNAME, "", 200),

				// sleep controller
				Arguments.of(POST, "/user/" + USERNAME + "/sleep", sleepData, 200),
				Arguments.of(GET, "/user/" + USERNAME + "/sleep", "", 200),
				Arguments.of(GET, "/user/" + USERNAME + "/sleep" + "/1", "", 200),
				Arguments.of(PUT, "/user/" + USERNAME + "/sleep" + "/1", sleepData, 200),
				Arguments.of(DELETE, "/user/" + USERNAME + "/sleep" + "/1", "", 200),

				Arguments.of(POST, "/user/" + ADMINNAME + "/sleep", sleepData, 200),
				Arguments.of(GET, "/user/" + ADMINNAME + "/sleep", "", 200),
				Arguments.of(GET, "/user/" + ADMINNAME + "/sleep" + "/1", "", 200),
				Arguments.of(PUT, "/user/" + ADMINNAME + "/sleep" + "/1", sleepData, 200),
				Arguments.of(DELETE, "/user/" + ADMINNAME + "/sleep" + "/1", "", 200)
		);
	}

	private static List<Arguments> provideUserTestParameters() {
		return List.of(

				// actuator
				Arguments.of(GET, "/actuator", "", 403),
				Arguments.of(GET, "/actuator/flyway", "", 403),
				Arguments.of(GET, "/actuator/health", "", 403),
				Arguments.of(GET, "/actuator/info", "", 403),
				Arguments.of(GET, "/actuator/mappings", "", 403),
				Arguments.of(GET, "/actuator/sessions?username=admin", "", 403),

				// unsecured resources
				Arguments.of(GET, "/", "", 200),
				Arguments.of(GET, "/favicon.ico", "", 200),
				Arguments.of(GET, "/manifest.json", "", 200),

				// user controller
				Arguments.of(POST, "/registration", registration, 403),
				Arguments.of(GET, "/login", "", 200),
				Arguments.of(GET, "/user", "", 403),

				Arguments.of(PUT, "/user/" + USERNAME + "/personalInfo", info, 200),
				Arguments.of(POST, "/user/" + USERNAME + "/password/update", password, 200),
				Arguments.of(GET, "/user/" + USERNAME, "", 200),

				Arguments.of(PUT, "/user/" + ADMINNAME + "/personalInfo", info, 403),
				Arguments.of(POST, "/user/" + ADMINNAME + "/password/update", password, 403),
				Arguments.of(GET, "/user/" + ADMINNAME, "", 403),

				// sleep controller
				Arguments.of(POST, "/user/" + USERNAME + "/sleep", sleepData, 200),
				Arguments.of(GET, "/user/" + USERNAME + "/sleep", "", 200),
				Arguments.of(GET, "/user/" + USERNAME + "/sleep" + "/1", "", 200),
				Arguments.of(PUT, "/user/" + USERNAME + "/sleep" + "/1", sleepData, 200),
				Arguments.of(DELETE, "/user/" + USERNAME + "/sleep" + "/1", "", 200),

				Arguments.of(POST, "/user/" + ADMINNAME + "/sleep", sleepData, 403),
				Arguments.of(GET, "/user/" + ADMINNAME + "/sleep", "", 403),
				Arguments.of(GET, "/user/" + ADMINNAME + "/sleep" + "/1", "", 403),
				Arguments.of(PUT, "/user/" + ADMINNAME + "/sleep" + "/1", sleepData, 403),
				Arguments.of(DELETE, "/user/" + ADMINNAME + "/sleep" + "/1", "", 403)
		);
	}

	@ParameterizedTest
	@MethodSource("provideUnauthenticatedTestParameters")
	@DisplayName("Unauthenticated Access")
	void testUnauthenticatedSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {
		mvc.test(httpMethod, url, reqBody, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {
		mvc.test(httpMethod, url, reqBody, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {
		mvc.test(httpMethod, url, reqBody, expectedStatus);
	}

}