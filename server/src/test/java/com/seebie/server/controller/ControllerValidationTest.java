package com.seebie.server.controller;


import com.seebie.server.AppProperties;
import com.seebie.server.dto.*;
import com.seebie.server.mapper.dtotoentity.CsvToSleepData;
import com.seebie.server.mapper.entitytodto.SleepDetailsToCsv;
import com.seebie.server.security.WebSecurityBeanProvider;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.ChallengeService;
import com.seebie.server.service.ImportExportService;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.WithCustomMockUser;
import com.seebie.server.test.data.MultiRequestBuilder;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Isolated;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.seebie.server.mapper.dtotoentity.CsvToSleepData.missingHeader;
import static com.seebie.server.test.data.TestData.*;
import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// almost of the full stack is used, and your code will be called in exactly the same way
// as if it were processing a real HTTP request but without the cost of starting the server
// WebMvcTest (as opposed to pure unit test) is good for testing:
//   HTTP request mapping
//   Input field validation
//   Serialization / Deserialization
//   Error handling

@WebMvcTest(properties = {
		// this is a sensitive property and should not be included in the main application.properties
		"app.security.rememberMe.key=0ef16205-ba16-4154-b843-8bd1709b1ef4",
		"logging.level.org.springframework.security=DEBUG",
		"logging.level.org.springframework.security.web.access.expression=DEBUG",
		"logging.level.org.springframework.security.web.authentication=DEBUG",
		"logging.level.org.springframework.security.web.context=DEBUG",
		"logging.level.org.springframework.security.oauth2=DEBUG",
		"logging.level.org.springframework.security.filter=DEBUG"
})
@EnableConfigurationProperties(value = {AppProperties.class})
@Import({WebSecurityConfig.class, WebSecurityBeanProvider.class})
@Isolated
public class ControllerValidationTest {

	@MockitoBean
	private DataSource dataSource;

	@MockitoBean
	private UserService service;

	@MockitoBean
	private ImportExportService importExportService;

	@MockitoBean
	private SleepService sleepService;

	@MockitoBean
	private ChallengeService challengeService;

	@MockitoBean
	private CsvToSleepData fromCsv;

	@MockitoBean
	private SleepDetailsToCsv toCsv;

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockMvc mockMvc;

	private static MultiRequestBuilder requestBuilder;

	private static final String USER_PUBLIC_ID = "someuser";
	private static final String ADMIN_PUBLIC_ID = "admin";
	private static final String USER_LOGIN_ID = "someuser@example.com";
	private static final String ADMIN_LOGIN_ID = "admin@example.com";

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = createRandomSleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();
	private static final PasswordResetRequest pwReset = new PasswordResetRequest("new_password");
	private static final PasswordResetRequest invalidPw = new PasswordResetRequest("");

	private static final RegistrationRequest invalidRegistration = new RegistrationRequest("", null, null);
	private static final SleepData invalidSleepData = new SleepData("", 0, (LocalDateTime)null, null, AMERICA_NEW_YORK);

	private static final LocalDateTime stopTime = LocalDateTime.now();
	private static final LocalDateTime startTime = stopTime.minusHours(8);
	private static final SleepData badDurationSleepData = new SleepData("", 481, startTime, stopTime, AMERICA_NEW_YORK);

	private static final PersonalInfo invalidInfo = new PersonalInfo(null, null);

	private static final LocalDate today = LocalDate.now();
	private static final LocalDate yesterday = today.minusDays(1);
	private static final String from = yesterday.format(ISO_LOCAL_DATE);
	private static final String to = today.format(ISO_LOCAL_DATE);

	private static final LocalDate toDate = LocalDate.now();
	private static final LocalDate fromDate = toDate.minusDays(1);

	private static final HistogramRequest validHistReq = new HistogramRequest(60, new FilterList(List.of(new DateRange(fromDate, toDate))));
	private static final HistogramRequest invalidHistReq = new HistogramRequest(60, new FilterList(List.of(new DateRange(toDate, fromDate))));

	private static final List<String> NO_PARAMS = List.of();
	private static final String badCsvText = "test";
	private static final int goodCsvRows = 1;
	private static final String goodCsvText = createCsv(goodCsvRows);
	private static final MockMultipartFile badCsv = createMultipart(badCsvText);
	private static final MockMultipartFile goodCsv = createMultipart(goodCsvText);

	private static MockMultipartFile badJson = createMultipart("{[ text");
	private static MockMultipartFile goodJson; // see setup method

	private static final ChallengeDto invalidChallenge = new ChallengeDto("", "", null, null);
	private static final ChallengeDto validChallenge = TestData.createRandomChallenge(0, 14);

	@BeforeEach
	public void setup() {

		when(fromCsv.apply(eq(badCsvText))).thenThrow(missingHeader());
		when(fromCsv.apply(eq(goodCsvText))).thenReturn(List.of(createRandomSleepData()));

		when(importExportService.saveSleepData(anyString(), anyList())).thenReturn(0L);
		when(importExportService.retrieveSleepDetails(anyString())).thenReturn(List.of());
		when(fromCsv.apply(contains(goodCsvText))).thenReturn(List.of(createRandomSleepData()));

		when(importExportService.saveSleepData(anyString(), anyList())).thenReturn(0L);
		when(importExportService.retrieveSleepDetails(anyString())).thenReturn(List.of());
	}

	@BeforeAll
	public static void setup(@Autowired MappingJackson2HttpMessageConverter converter) throws Exception {

		goodJson = createMultipart(converter.getObjectMapper().writeValueAsString(randomUserData()));

		requestBuilder = new MultiRequestBuilder(converter.getObjectMapper());
	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(
			Arguments.of(POST, "/api/registration", registration, NO_PARAMS, 200),
			Arguments.of(POST, "/api/registration", invalidRegistration, NO_PARAMS, 400)
		);
	}

	private static List<Arguments> provideUserTestParameters() {
		return List.of(

			Arguments.of(PUT, "/api/user/"+ USER_PUBLIC_ID +"/personalInfo", info, NO_PARAMS, 200),
			Arguments.of(PUT, "/api/user/"+ USER_PUBLIC_ID +"/personalInfo", invalidInfo, NO_PARAMS, 400),

			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/password/update", pwReset, NO_PARAMS, 200),
			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/password/update", invalidPw, NO_PARAMS, 400),

			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/sleep", sleepData, NO_PARAMS, 200),
			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/sleep", invalidSleepData, NO_PARAMS, 400),
			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/sleep", badDurationSleepData, NO_PARAMS, 400),

			Arguments.of(PUT, "/api/user/"+ USER_PUBLIC_ID +"/sleep/1", sleepData, NO_PARAMS, 200),
			Arguments.of(PUT, "/api/user/"+ USER_PUBLIC_ID +"/sleep/1", invalidSleepData, NO_PARAMS, 400),
			Arguments.of(PUT, "/api/user/"+ USER_PUBLIC_ID +"/sleep/1", badDurationSleepData, NO_PARAMS, 400),

			Arguments.of(GET, "/api/user/"+ USER_PUBLIC_ID +"/sleep/chart", "", List.of("from", from, "to", to), 200),
			Arguments.of(GET, "/api/user/"+ USER_PUBLIC_ID +"/sleep/chart", "", List.of("from", "",   "to", ""), 400),
			Arguments.of(GET, "/api/user/"+ USER_PUBLIC_ID +"/sleep/chart", "", List.of("from", to,   "to", from), 400),

			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/sleep/histogram", validHistReq, NO_PARAMS,   200),
			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/sleep/histogram", invalidHistReq, NO_PARAMS,   400),

			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/import/json", badJson, NO_PARAMS, 400),
			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/import/json", goodJson, NO_PARAMS, 200),
			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/import/csv", badCsv, NO_PARAMS, 400),
			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/import/csv", goodCsv, NO_PARAMS, 200),

			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/challenge", invalidChallenge, NO_PARAMS, 400),
			Arguments.of(POST, "/api/user/"+ USER_PUBLIC_ID +"/challenge", validChallenge, NO_PARAMS, 200),

			Arguments.of(PUT, "/api/user/"+ USER_PUBLIC_ID +"/challenge/1", invalidChallenge, NO_PARAMS, 400),
			Arguments.of(PUT, "/api/user/"+ USER_PUBLIC_ID +"/challenge/1", validChallenge, NO_PARAMS, 200)
		);

	}

	@ParameterizedTest(name = "{0} {1}")
	@MethodSource("provideAdminTestParameters")
	@WithCustomMockUser(publicId = ADMIN_PUBLIC_ID, username= ADMIN_LOGIN_ID, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminValidation(HttpMethod http, String url, Object body, List<String> params, int expectedStatus) throws Exception {
		test(requestBuilder.toMvcRequest(http, url, body, params), expectedStatus);
	}

	@ParameterizedTest(name = "{0} {1}")
	@MethodSource("provideUserTestParameters")
	@WithCustomMockUser(publicId = USER_PUBLIC_ID, username = USER_LOGIN_ID, roles = {"USER"})
	@DisplayName("User Access")
	void testUserValidation(HttpMethod http, String url, Object body, List<String> params, int expectedStatus) throws Exception {
		test(requestBuilder.toMvcRequest(http, url, body, params), expectedStatus);
	}

	private void test(RequestBuilder testData, int expectedStatus) throws Exception {
		mockMvc.perform(testData)
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}
}
