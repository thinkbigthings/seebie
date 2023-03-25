package com.seebie.server.controller;


import com.seebie.server.AppProperties;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.entity.User;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.AppRequest;
import com.seebie.server.test.data.DtoJsonMapper;
import com.seebie.server.test.data.MvcRequestMapper;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeToString.format;
import static com.seebie.server.test.data.TestData.createRandomPersonalInfo;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
@EnableConfigurationProperties(value = {AppProperties.class})
@Import(WebSecurityConfig.class)
@DisplayName("Endpoint Security")
public class ControllerSecurityTest {

	@MockBean
	private UserService service;

	@MockBean
	private SleepService sleepService;

	private static final String USERNAME = "someuser";
	private static final String ADMINNAME = "admin";

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = new SleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();
	private static final String password = "new_password";

	private static final String from = format(ZonedDateTime.now().minusDays(1));
	private static final String to = format(ZonedDateTime.now());

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockMvc mockMvc;

	private static Function<AppRequest, MockHttpServletRequestBuilder> toRequest;

	private static TestData.ArgumentBuilder test;

	@BeforeEach
	public void setup() {
		when(sleepService.exportCsv(ArgumentMatchers.any(String.class))).thenReturn("");
	}

	@BeforeAll
	public static void setup(@Autowired MappingJackson2HttpMessageConverter converter) {

		// so we get the mapper as configured for the app
		toRequest = new MvcRequestMapper(new DtoJsonMapper(converter.getObjectMapper()));
		
		test = new TestData.ArgumentBuilder();
	}

	private static List<Arguments> provideUnauthenticatedTestParameters() {
		return List.of(

				// unsecured resources
				test.get("/", 200),
				test.get("/favicon.ico", 200),
				test.get("/manifest.json", 200),

				// user controller
				test.post("/registration", registration, 401),
				test.get("/login", 401),
				test.get("/user", 401),

				test.put("/user/" + USERNAME + "/personalInfo", registration, 401),
				test.post("/user/" + USERNAME + "/password/update", password, 401),
				test.get("/user/" + USERNAME, 401),

				test.put("/user/" + ADMINNAME + "/personalInfo", info, 401),
				test.post("/user/" + ADMINNAME + "/password/update", password, 401),
				test.get("/user/" + ADMINNAME, 401),

				// sleep controller
				test.post("/user/" + USERNAME + "/sleep", sleepData, 401),
				test.get("/user/" + USERNAME + "/sleep", 401),
				test.get("/user/" + USERNAME + "/sleep" + "/1", 401),
				test.put("/user/" + USERNAME + "/sleep" + "/1", sleepData, 401),
				test.delete("/user/" + USERNAME + "/sleep" + "/1", 401),

				test.post("/user/" + ADMINNAME + "/sleep", sleepData, 401),
				test.get("/user/" + ADMINNAME + "/sleep", 401),
				test.get("/user/" + ADMINNAME + "/sleep" + "/1", 401),
				test.put("/user/" + ADMINNAME + "/sleep" + "/1", sleepData, 401),
				test.delete("/user/" + ADMINNAME + "/sleep" + "/1", 401),

				test.get("/user/" + USERNAME + "/sleep/chart", new String[]{"from", from, "to", to}, 401),
				test.get("/user/" + USERNAME + "/sleep/download", 401)
			);
	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(

				// unsecured resources
				test.get("/", 200),
				test.get("/favicon.ico", 200),
				test.get("/manifest.json", 200),

				// user controller
				test.post("/registration", registration, 200),
				test.get("/login", 200),
				test.get("/user", 200),

				test.put("/user/" + USERNAME + "/personalInfo", info, 200),
				test.post("/user/" + USERNAME + "/password/update", password, 200),
				test.get("/user/" + USERNAME, 200),

				test.put("/user/" + ADMINNAME + "/personalInfo", info, 200),
				test.post("/user/" + ADMINNAME + "/password/update", password, 200),
				test.get("/user/" + ADMINNAME, 200),

				// sleep controller
				test.post("/user/" + USERNAME + "/sleep", sleepData, 200),
				test.get("/user/" + USERNAME + "/sleep", 200),
				test.get("/user/" + USERNAME + "/sleep" + "/1", 200),
				test.put("/user/" + USERNAME + "/sleep" + "/1", sleepData, 200),
				test.delete("/user/" + USERNAME + "/sleep" + "/1", 200),

				test.post("/user/" + ADMINNAME + "/sleep", sleepData, 200),
				test.get("/user/" + ADMINNAME + "/sleep", 200),
				test.get("/user/" + ADMINNAME + "/sleep" + "/1", 200),
				test.put("/user/" + ADMINNAME + "/sleep" + "/1", sleepData, 200),
				test.delete("/user/" + ADMINNAME + "/sleep" + "/1", 200),

				test.get("/user/" + USERNAME + "/sleep/chart", new String[]{"from", from, "to", to}, 200),
				test.get("/user/" + USERNAME + "/sleep/download", 200)
		);
	}

	private static List<Arguments> provideUserTestParameters() {
		return List.of(

				// unsecured resources
				test.get("/", 200),
				test.get("/favicon.ico", 200),
				test.get("/manifest.json", 200),

				// user controller
				test.post("/registration", registration, 403),
				test.get("/login", 200),
				test.get("/user", 403),

				test.put("/user/" + USERNAME + "/personalInfo", info, 200),
				test.post("/user/" + USERNAME + "/password/update", password, 200),
				test.get("/user/" + USERNAME, 200),

				test.put("/user/" + ADMINNAME + "/personalInfo", info, 403),
				test.post("/user/" + ADMINNAME + "/password/update", password, 403),
				test.get("/user/" + ADMINNAME, 403),

				// sleep controller
				test.post("/user/" + USERNAME + "/sleep", sleepData, 200),
				test.get("/user/" + USERNAME + "/sleep", 200),
				test.get("/user/" + USERNAME + "/sleep" + "/1", 200),
				test.put("/user/" + USERNAME + "/sleep" + "/1", sleepData, 200),
				test.delete("/user/" + USERNAME + "/sleep" + "/1", 200),

				test.post("/user/" + ADMINNAME + "/sleep", sleepData, 403),
				test.get("/user/" + ADMINNAME + "/sleep", 403),
				test.get("/user/" + ADMINNAME + "/sleep" + "/1", 403),
				test.put("/user/" + ADMINNAME + "/sleep" + "/1", sleepData, 403),
				test.delete("/user/" + ADMINNAME + "/sleep" + "/1", 403),

				test.get("/user/" + USERNAME + "/sleep/chart", new String[]{"from", from, "to", to}, 200),
				test.get("/user/" + USERNAME + "/sleep/download", 200)
		);
	}

	@ParameterizedTest
	@MethodSource("provideUnauthenticatedTestParameters")
	@DisplayName("Unauthenticated Access")
	void testUnauthenticatedSecurity(AppRequest testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminSecurity(AppRequest testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(AppRequest testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	private void test(AppRequest testData, int expectedStatus) throws Exception {
		mockMvc.perform(toRequest.apply(testData))
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}

}