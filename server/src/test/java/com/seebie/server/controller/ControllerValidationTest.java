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
import com.seebie.server.test.data.ArgumentsBuilder;
import com.seebie.server.test.data.RoleArgumentsBuilder;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;

import static com.seebie.server.mapper.dtotoentity.CsvToSleepData.missingHeader;
import static com.seebie.server.mapper.entitytodto.LocalDateTimeConverter.format;
import static com.seebie.server.test.data.TestData.*;
import static com.seebie.server.test.data.TestData.createRandomSleepData;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
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
})
@EnableConfigurationProperties(value = {AppProperties.class})
@Import({WebSecurityConfig.class, WebSecurityBeanProvider.class})
@Isolated
public class ControllerValidationTest {

	@MockBean
	private DataSource dataSource;

	@MockBean
	private UserService service;

	@MockBean
	private ImportExportService importExportService;

	@MockBean
	private SleepService sleepService;

	@MockBean
	private ChallengeService challengeService;

	@MockBean
	private CsvToSleepData fromCsv;

	@MockBean
	private SleepDetailsToCsv toCsv;

	private static final String USERNAME = "someuser";
	private static final String ADMINNAME = "admin";

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

	private static final String from = format(LocalDateTime.now().minusDays(1));
	private static final String to = format(LocalDateTime.now());

	private static final LocalDateTime fromDate = LocalDateTime.now().minusDays(1);
	private static final LocalDateTime toDate = LocalDateTime.now();

	private static final HistogramRequest validHistReq = new HistogramRequest(60, new FilterList(List.of(new DateRange(fromDate, toDate))));
	private static final HistogramRequest invalidHistReq = new HistogramRequest(60, new FilterList(List.of(new DateRange(toDate, fromDate))));

	private static final String badCsvText = "test";
	private static final int goodCsvRows = 1;
	private static final String goodCsvText = createCsv(goodCsvRows);
	private static final MockMultipartFile badCsv = createMultipart(badCsvText);
	private static final MockMultipartFile goodCsv = createMultipart(goodCsvText);

	private static MockMultipartFile badJson = createMultipart("{[ text");
	private static MockMultipartFile goodJson; // see setup method

	private static final ChallengeDto invalidChallenge = new ChallengeDto("", "", null, null);
	private static final ChallengeDto validChallenge = TestData.createRandomChallenge(0, 14);

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockMvc mockMvc;

	private static RoleArgumentsBuilder test;
	private static ArgumentsBuilder user;
	private static ArgumentsBuilder admin;

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

		// so we get the mapper as configured for the app
		test = new RoleArgumentsBuilder(converter.getObjectMapper());

		goodJson = createMultipart(converter.getObjectMapper().writeValueAsString(randomUserData()));

		user = new ArgumentsBuilder(converter.getObjectMapper());
		admin = new ArgumentsBuilder(converter.getObjectMapper());
	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(
			admin.args(POST, "/api/registration", registration, 200),
			admin.args(POST, "/api/registration", invalidRegistration, 400)
		);
	}

	private static List<Arguments> provideUserTestParameters() {
		return List.of(
			user.args(PUT, STR."/api/user/\{USERNAME}/personalInfo", info, 200),
			user.args(PUT, STR."/api/user/\{USERNAME}/personalInfo", invalidInfo, 400),

			user.args(POST, STR."/api/user/\{USERNAME}/password/update", pwReset, 200),
			user.args(POST, STR."/api/user/\{USERNAME}/password/update", invalidPw, 400),

			user.args(POST, STR."/api/user/\{USERNAME}/sleep", sleepData, 200),
			user.args(POST, STR."/api/user/\{USERNAME}/sleep", invalidSleepData, 400),
			user.args(POST, STR."/api/user/\{USERNAME}/sleep", badDurationSleepData, 400),

			user.args(PUT, STR."/api/user/\{USERNAME}/sleep/1", sleepData, 200),
			user.args(PUT, STR."/api/user/\{USERNAME}/sleep/1", invalidSleepData, 400),
			user.args(PUT, STR."/api/user/\{USERNAME}/sleep/1", badDurationSleepData, 400),

			user.args(GET, STR."/api/user/\{USERNAME}/sleep/chart", "", new String[]{"from", from, "to", to}, 200),
			user.args(GET, STR."/api/user/\{USERNAME}/sleep/chart", "", new String[]{"from", "",   "to", ""}, 400),
			user.args(GET, STR."/api/user/\{USERNAME}/sleep/chart", "", new String[]{"from", to,   "to", from}, 400),

			user.args(POST, STR."/api/user/\{USERNAME}/sleep/histogram", validHistReq,   200),
			user.args(POST, STR."/api/user/\{USERNAME}/sleep/histogram", invalidHistReq,   400),

			user.args(POST, STR."/api/user/\{USERNAME}/import/json", badJson, 400),
			user.args(POST, STR."/api/user/\{USERNAME}/import/json", goodJson, 200),
			user.args(POST, STR."/api/user/\{USERNAME}/import/csv", badCsv, 400),
			user.args(POST, STR."/api/user/\{USERNAME}/import/csv", goodCsv, 200),

			user.args(POST, STR."/api/user/\{USERNAME}/challenge", invalidChallenge, 400),
			user.args(POST, STR."/api/user/\{USERNAME}/challenge", validChallenge, 200),

			user.args(PUT, STR."/api/user/\{USERNAME}/challenge/1", invalidChallenge, 400),
			user.args(PUT, STR."/api/user/\{USERNAME}/challenge/1", validChallenge, 200)
		);

	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminValidation(RequestBuilder testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserValidation(RequestBuilder testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	private void test(RequestBuilder testData, int expectedStatus) throws Exception {
		mockMvc.perform(testData)
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}
}