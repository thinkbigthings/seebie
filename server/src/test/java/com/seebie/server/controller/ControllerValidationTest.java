package com.seebie.server.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.seebie.server.AppProperties;
import com.seebie.server.dto.PersonalInfo;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.dto.SleepData;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
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

import java.util.HashSet;
import java.util.List;

import static com.seebie.server.test.data.TestData.createRandomPersonalInfo;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
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

	private RequestBodyJson jsonWriter;

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = new SleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();

	private static final RegistrationRequest invalidRegistration = new RegistrationRequest("", null, null);
	private static final SleepData invalidSleepData = new SleepData("", 0, new HashSet<>(), null, null);
	private static final PersonalInfo invalidInfo = new PersonalInfo(null, null);

	@PostConstruct
	public void setup() {
		jsonWriter = new RequestBodyJson(converter);
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

		mockMvc.perform(request(httpMethod, url).content(jsonWriter.toJson(reqBody)).contentType(APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {

		mockMvc.perform(request(httpMethod, url).content(jsonWriter.toJson(reqBody)).contentType(APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}

	public static class RequestBodyJson {

		private ObjectMapper mapper;

		public RequestBodyJson(MappingJackson2HttpMessageConverter converter) {
			mapper = converter.getObjectMapper();
		}

		public String toJson(Object requestBody) throws Exception {
			return switch (requestBody) {
				case String s -> s;
				case PersonalInfo p -> mapper.writerFor(p.getClass()).writeValueAsString(p);
				case RegistrationRequest r -> mapper.writerFor(r.getClass()).writeValueAsString(r);
				case SleepData d -> mapper.writerFor(d.getClass()).writeValueAsString(d);
				default -> throw new IllegalStateException("Can't create request body for " + requestBody);
			};
		}
	}


}