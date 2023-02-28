package com.seebie.server.controller;


import com.seebie.server.AppProperties;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.support.MvcTestRunner;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.seebie.server.test.data.TestData.createRandomPersonalInfo;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

	private static final String USERNAME = "someuser";
	private static final String ADMINNAME = "admin";

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

	private MvcTestRunner mvc;

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = new SleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();

	private static final RegistrationRequest invalidRegistration = new RegistrationRequest("", null, null);
	private static final SleepData invalidSleepData = new SleepData("", 0, new HashSet<>(), null, null);
	private static final PersonalInfo invalidInfo = new PersonalInfo(null, null);

	@PostConstruct
	public void setup() {
		mvc = new MvcTestRunner(converter);
	}

	@Test
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("Chart Data Access")
	public void testArguments() throws Exception {

		var from  = ZonedDateTime.now().minusDays(1).format(ISO_OFFSET_DATE_TIME);
		var to = ZonedDateTime.now().format(ISO_OFFSET_DATE_TIME);

		// TODO test validation and security, do we need to modify all the other tests? We use empty body for GET reqs

		var urlParams = new LinkedMultiValueMap<String, String>();
		urlParams.put("from", List.of(from));
		urlParams.put("to", List.of(to));

		// we don't need to url encode the parameters here
		mockMvc.perform(get("/user/" + USERNAME + "/sleep/chart")
						.contentType(APPLICATION_JSON)
						.params(urlParams)
						.secure(true))
				.andDo(print())
				.andExpect(status().is(200));

	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(

				Arguments.of(POST, "/registration", registration, 200),
				Arguments.of(POST, "/registration", invalidRegistration, 400)
		);
	}

	private static List<Arguments> provideUserTestParameters() {
		return List.of(

				Arguments.of(PUT, "/user/" + USERNAME + "/personalInfo", info, 200),
				Arguments.of(PUT, "/user/" + USERNAME + "/personalInfo", invalidInfo, 400),

				Arguments.of(POST, "/user/" + USERNAME + "/sleep", sleepData, 200),
				Arguments.of(POST, "/user/" + USERNAME + "/sleep", invalidSleepData, 400),

				Arguments.of(PUT, "/user/" + USERNAME + "/sleep" + "/1", sleepData, 200),
				Arguments.of(PUT, "/user/" + USERNAME + "/sleep" + "/1", invalidSleepData, 400)
		);
	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {
		mvc.test(mockMvc, httpMethod, url, reqBody, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {
		mvc.test(mockMvc, httpMethod, url, reqBody, expectedStatus);
	}


}