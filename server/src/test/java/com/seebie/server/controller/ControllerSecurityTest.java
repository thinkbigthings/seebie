package com.seebie.server.controller;


import com.seebie.server.AppProperties;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.support.MockMvcRunner;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;

import static com.seebie.server.test.data.TestData.createRandomPersonalInfo;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;

import com.seebie.server.test.data.HttpCall;
import static com.seebie.server.test.data.HttpCall.*;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;


@WebMvcTest
@EnableConfigurationProperties(value = {AppProperties.class})
@Import(WebSecurityConfig.class)
@DisplayName("Endpoint Security")
public class ControllerSecurityTest {

	@MockBean
	private UserService service;

	@MockBean
	private SleepService sleepService;

	private static MockMvcRunner mvc;

	private static final String USERNAME = "someuser";
	private static final String ADMINNAME = "admin";

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = new SleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();
	private static final String password = "new_password";

	private static final String from = ZonedDateTime.now().minusDays(1).format(ISO_OFFSET_DATE_TIME);
	private static final String to = ZonedDateTime.now().format(ISO_OFFSET_DATE_TIME);

	@BeforeAll
	public static void setup(@Autowired MappingJackson2HttpMessageConverter converter, @Autowired MockMvc mockMvc) {
		// so we get the mapper as configured for the app
		mvc = new MockMvcRunner(mockMvc, converter);
	}

	private static List<Arguments> provideUnauthenticatedTestParameters() {
		return List.of(

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

				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").params("from", from, "to", to), 401)

			);
	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(

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

				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").params("from", from, "to", to), 200)
		);
	}

	private static List<Arguments> provideUserTestParameters() {
		return List.of(

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

				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").params("from", from, "to", to), 200)
		);
	}

	@ParameterizedTest
	@MethodSource("provideUnauthenticatedTestParameters")
	@DisplayName("Unauthenticated Access")
	void testUnauthenticatedSecurity(HttpCall testData, int expectedStatus) throws Exception {
		mvc.test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminSecurity(HttpCall testData, int expectedStatus) throws Exception {
		mvc.test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(HttpCall testData, int expectedStatus) throws Exception {
		mvc.test(testData, expectedStatus);
	}

}