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
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;

import static com.seebie.server.test.data.TestData.createRandomPersonalInfo;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static com.seebie.server.test.support.MockMvcRunner.EndpointTest;
import static com.seebie.server.test.support.MockMvcRunner.EndpointTest.*;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;

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

	private static final String from = ZonedDateTime.now().minusDays(1).format(ISO_OFFSET_DATE_TIME);
	private static final String to = ZonedDateTime.now().format(ISO_OFFSET_DATE_TIME);

	@PostConstruct
	public void setup() {
		mvc = new MockMvcRunner(mockMvc, converter);
	}

	private static List<Arguments> provideUnauthenticatedTestParameters() {
		return List.of(

				// actuator
				Arguments.of(get("/actuator"), 401),
				Arguments.of(get("/actuator/flyway"), 401),
				Arguments.of(get("/actuator/health"), 401),
				Arguments.of(get("/actuator/info"), 401),
				Arguments.of(get("/actuator/mappings"), 401),
				Arguments.of(get("/actuator/sessions").withParam("username", "admin"), 401),

				// unsecured resources
				Arguments.of(get("/"), 200),
				Arguments.of(get("/favicon.ico"), 200),
				Arguments.of(get("/manifest.json"), 200),

				// user controller
				Arguments.of(post("/registration", registration), 401),
				Arguments.of(get("/login"), 401),
				Arguments.of(get("/user"), 401),

				Arguments.of(put("/user/" + USERNAME + "/personalInfo", registration), 401),
				Arguments.of(post("/user/" + USERNAME + "/password/update", password), 401),
				Arguments.of(get("/user/" + USERNAME), 401),

				Arguments.of(put("/user/" + ADMINNAME + "/personalInfo", info), 401),
				Arguments.of(post("/user/" + ADMINNAME + "/password/update", password), 401),
				Arguments.of(get("/user/" + ADMINNAME), 401),

				// sleep controller
				Arguments.of(post("/user/" + USERNAME + "/sleep", sleepData), 401),
				Arguments.of(get("/user/" + USERNAME + "/sleep"), 401),
				Arguments.of(get("/user/" + USERNAME + "/sleep" + "/1"), 401),
				Arguments.of(put("/user/" + USERNAME + "/sleep" + "/1", sleepData), 401),
				Arguments.of(delete("/user/" + USERNAME + "/sleep" + "/1"), 401),

				Arguments.of(post("/user/" + ADMINNAME + "/sleep", sleepData), 401),
				Arguments.of(get("/user/" + ADMINNAME + "/sleep"), 401),
				Arguments.of(get("/user/" + ADMINNAME + "/sleep" + "/1"), 401),
				Arguments.of(put("/user/" + ADMINNAME + "/sleep" + "/1", sleepData), 401),
				Arguments.of(delete("/user/" + ADMINNAME + "/sleep" + "/1"), 401),

				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").withParam("from", from).withParam("to", to), 401)

			);
	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(

				// actuator
				Arguments.of(get("/actuator"), 200),
				Arguments.of(get("/actuator/flyway"), 200),
				Arguments.of(get("/actuator/health"), 200),
				Arguments.of(get("/actuator/info"), 200),
				Arguments.of(get("/actuator/mappings"), 200),
				Arguments.of(get("/actuator/sessions?username=admin"), 200),
				Arguments.of(get("/actuator/sessions").withParam("username", "admin"), 200),

				// unsecured resources
				Arguments.of(get("/"), 200),
				Arguments.of(get("/favicon.ico"), 200),
				Arguments.of(get("/manifest.json"), 200),

				// user controller
				Arguments.of(post("/registration", registration), 200),
				Arguments.of(get("/login"), 200),
				Arguments.of(get("/user"), 200),

				Arguments.of(put("/user/" + USERNAME + "/personalInfo", info), 200),
				Arguments.of(post("/user/" + USERNAME + "/password/update", password), 200),
				Arguments.of(get("/user/" + USERNAME), 200),

				Arguments.of(put("/user/" + ADMINNAME + "/personalInfo", info), 200),
				Arguments.of(post("/user/" + ADMINNAME + "/password/update", password), 200),
				Arguments.of(get("/user/" + ADMINNAME), 200),

				// sleep controller
				Arguments.of(post("/user/" + USERNAME + "/sleep", sleepData), 200),
				Arguments.of(get("/user/" + USERNAME + "/sleep"), 200),
				Arguments.of(get("/user/" + USERNAME + "/sleep" + "/1"), 200),
				Arguments.of(put("/user/" + USERNAME + "/sleep" + "/1", sleepData), 200),
				Arguments.of(delete("/user/" + USERNAME + "/sleep" + "/1"), 200),

				Arguments.of(post("/user/" + ADMINNAME + "/sleep", sleepData), 200),
				Arguments.of(get("/user/" + ADMINNAME + "/sleep"), 200),
				Arguments.of(get("/user/" + ADMINNAME + "/sleep" + "/1"), 200),
				Arguments.of(put("/user/" + ADMINNAME + "/sleep" + "/1", sleepData), 200),
				Arguments.of(delete("/user/" + ADMINNAME + "/sleep" + "/1"), 200),

				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").withParam("from", from).withParam("to", to), 200)
		);
	}

	private static List<Arguments> provideUserTestParameters() {
		return List.of(

				// actuator
				Arguments.of(get("/actuator"), 403),
				Arguments.of(get("/actuator/flyway"), 403),
				Arguments.of(get("/actuator/health"), 403),
				Arguments.of(get("/actuator/info"), 403),
				Arguments.of(get("/actuator/mappings"), 403),
				Arguments.of(get("/actuator/sessions").withParam("username", "admin"), 403),

				// unsecured resources
				Arguments.of(get("/"), 200),
				Arguments.of(get("/favicon.ico"), 200),
				Arguments.of(get("/manifest.json"), 200),

				// user controller
				Arguments.of(post("/registration", registration), 403),
				Arguments.of(get("/login"), 200),
				Arguments.of(get("/user"), 403),

				Arguments.of(put("/user/" + USERNAME + "/personalInfo", info), 200),
				Arguments.of(post("/user/" + USERNAME + "/password/update", password), 200),
				Arguments.of(get("/user/" + USERNAME), 200),

				Arguments.of(put("/user/" + ADMINNAME + "/personalInfo", info), 403),
				Arguments.of(post("/user/" + ADMINNAME + "/password/update", password), 403),
				Arguments.of(get("/user/" + ADMINNAME), 403),

				// sleep controller
				Arguments.of(post("/user/" + USERNAME + "/sleep", sleepData), 200),
				Arguments.of(get("/user/" + USERNAME + "/sleep"), 200),
				Arguments.of(get("/user/" + USERNAME + "/sleep" + "/1"), 200),
				Arguments.of(put("/user/" + USERNAME + "/sleep" + "/1", sleepData), 200),
				Arguments.of(delete("/user/" + USERNAME + "/sleep" + "/1"), 200),

				Arguments.of(post("/user/" + ADMINNAME + "/sleep", sleepData), 403),
				Arguments.of(get("/user/" + ADMINNAME + "/sleep"), 403),
				Arguments.of(get("/user/" + ADMINNAME + "/sleep" + "/1"), 403),
				Arguments.of(put("/user/" + ADMINNAME + "/sleep" + "/1", sleepData), 403),
				Arguments.of(delete("/user/" + ADMINNAME + "/sleep" + "/1"), 403),

				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").withParam("from", from).withParam("to", to), 200)
		);
	}

	@ParameterizedTest
	@MethodSource("provideUnauthenticatedTestParameters")
	@DisplayName("Unauthenticated Access")
	void testUnauthenticatedSecurity(EndpointTest testData, int expectedStatus) throws Exception {
		mvc.test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminSecurity(EndpointTest testData, int expectedStatus) throws Exception {
		mvc.test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(EndpointTest testData, int expectedStatus) throws Exception {
		mvc.test(testData, expectedStatus);
	}

}