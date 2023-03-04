package com.seebie.server.controller;


import com.seebie.server.AppProperties;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.AppRequest;
import com.seebie.server.test.data.DtoJsonMapper;
import com.seebie.server.test.data.MvcRequestMapper;
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
import java.util.HashSet;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.test.data.TestData.createRandomPersonalInfo;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.seebie.server.test.data.AppRequest.*;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// almost of the full stack is used, and your code will be called in exactly the same way
// as if it were processing a real HTTP request but without the cost of starting the server
// WebMvcTest (as opposed to pure unit test) is good for testing:
//   HTTP request mapping
//   Input field validation
//   Serialization / Deserialization
//   Error handling

@WebMvcTest
@DisplayName("Controller Validation")
@EnableConfigurationProperties(value = {AppProperties.class})
@Import(WebSecurityConfig.class)
public class ControllerValidationTest {

	@MockBean
	private UserService service;

	@MockBean
	private SleepService sleepService;

	private static final String USERNAME = "someuser";
	private static final String ADMINNAME = "admin";

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = new SleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();

	private static final RegistrationRequest invalidRegistration = new RegistrationRequest("", null, null);
	private static final SleepData invalidSleepData = new SleepData("", 0, new HashSet<>(), null, null);
	private static final PersonalInfo invalidInfo = new PersonalInfo(null, null);

	private static final String from = ZonedDateTime.now().minusDays(1).format(ISO_OFFSET_DATE_TIME);
	private static final String to = ZonedDateTime.now().format(ISO_OFFSET_DATE_TIME);

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockMvc mockMvc;

	private static Function<AppRequest, MockHttpServletRequestBuilder> toRequest;

	@BeforeAll
	public static void setup(@Autowired MappingJackson2HttpMessageConverter converter) {

		// so we get the mapper as configured for the app
		toRequest = new MvcRequestMapper(new DtoJsonMapper(converter.getObjectMapper()));
	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(

				Arguments.of(post("/registration", registration), 200),
				Arguments.of(post("/registration", invalidRegistration), 400)
		);
	}

	private static List<Arguments> provideUserTestParameters() {

		return List.of(

				Arguments.of(put("/user/" + USERNAME + "/personalInfo", info), 200),
				Arguments.of(put("/user/" + USERNAME + "/personalInfo", invalidInfo), 400),

				Arguments.of(post("/user/" + USERNAME + "/sleep", sleepData), 200),
				Arguments.of(post("/user/" + USERNAME + "/sleep", invalidSleepData), 400),

				Arguments.of(put("/user/" + USERNAME + "/sleep" + "/1", sleepData), 200),
				Arguments.of(put("/user/" + USERNAME + "/sleep" + "/1", invalidSleepData), 400),

				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").params("from", from, "to", to),   200),
				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").params("from", "",   "to", ""),   400),
				Arguments.of(get("/user/" + USERNAME + "/sleep/chart").params("from", to,   "to", from), 400)
		);
	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminValidation(AppRequest testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserValidation(AppRequest testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	private void test(AppRequest testData, int expectedStatus) throws Exception {
		mockMvc.perform(toRequest.apply(testData))
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}
}