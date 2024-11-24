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
import com.seebie.server.test.data.RoleArgumentsBuilder;
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
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.seebie.server.mapper.entitytodto.ZonedDateTimeConverter.format;
import static com.seebie.server.security.WebSecurityConfig.API_LOGIN;
import static com.seebie.server.test.data.TestData.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * This class tests that security annotations are applied and that appropriate responses are returned.
 * It is specific to testing annotations on controllers.
 *
 * If it tried to test access to unsecure static resources like "/", "/favicon.ico", "/manifest.json", and so on,
 * the controller would fail with a 404 instead of 200 unless those static resources were present
 * in server/build/resources/main/static in which case the test would pass.
 * Since that would require copying contents of reactjs/public into server/build/resources/main/static
 * as a dependency of the test phase, it doesn't really fit as a unit test.
 *
 * testing open access to static resources is really a better job of an integration test that happens after the
 * web content is copied from the ui anyway.
 *
 */
@WebMvcTest(properties = {
		// this is a sensitive property and should not be included in the main application.properties
		"app.security.rememberMe.key=0ef16205-ba16-4154-b843-8bd1709b1ef4",
})
@EnableConfigurationProperties(value = {AppProperties.class})
@Import({WebSecurityConfig.class, WebSecurityBeanProvider.class})
@Isolated
public class ControllerSecurityTest {

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
	private static MockMultipartFile jsonFile; // see setup method

	private static final int goodCsvRows = 1;
	private static final String goodCsvText = createCsv(goodCsvRows);
	private static final MockMultipartFile csvFile = createMultipart(goodCsvText);

	private static final String from = format(ZonedDateTime.now().minusDays(1));
	private static final String to = format(ZonedDateTime.now());

	private static final String[] challengeParams = new String[]{"currentDate", LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)};
	private static final String[] chartParams = new String[]{"from", from, "to", to};
	private static final HistogramRequest histogramRequest = new HistogramRequest(1, new FilterList(List.of()));
	private static final ChallengeDto challenge = createRandomChallenge(0, 14);

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockMvc mockMvc;

	private static RoleArgumentsBuilder test;
    @Autowired
    private UserService userService;

	@BeforeEach
	public void setup() {

		when(fromCsv.apply(contains(goodCsvText))).thenReturn(List.of());
		when(fromCsv.apply(contains(goodCsvText))).thenReturn(List.of(createRandomSleepData()));

		when(toCsv.apply(anyList())).thenReturn("");

		when(userService.getUser(USERNAME)).thenReturn(createRandomUser(USERNAME));
		when(userService.getUser(ADMINNAME)).thenReturn(createRandomUser(ADMINNAME));

		when(importExportService.saveSleepData(anyString(), anyList())).thenReturn(0L);
		when(importExportService.retrieveSleepDetails(anyString())).thenReturn(List.of());
	}

	@BeforeAll
	public static void setup(@Autowired MappingJackson2HttpMessageConverter converter) throws Exception {

		jsonFile = createMultipart(converter.getObjectMapper().writeValueAsString(randomUserData()));

		// so we get the mapper as configured for the app
		test = new RoleArgumentsBuilder(converter.getObjectMapper());

		test.post("/api/registration", registration, 401, 403, 200);
		test.get(API_LOGIN, 401, 200, 200);
		test.get("/api/user", 401, 403, 200);

		test.put(STR."/api/user/\{USERNAME}/personalInfo", info, 401, 200, 200);
		test.post(STR."/api/user/\{USERNAME}/password/update", pwReset, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}", 401, 200, 200);

		test.put(STR."/api/user/\{ADMINNAME}/personalInfo", info, 401, 403, 200);
		test.post(STR."/api/user/\{ADMINNAME}/password/update", pwReset, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}", 401, 403, 200);

		test.post(STR."/api/user/\{USERNAME}/sleep", sleepData, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/sleep", 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/sleep" + "/1", 401, 200, 200);
		test.put(STR."/api/user/\{USERNAME}/sleep" + "/1", sleepData, 401, 200, 200);
		test.delete(STR."/api/user/\{USERNAME}/sleep" + "/1", 401, 200, 200);

		test.post(STR."/api/user/\{ADMINNAME}/sleep", sleepData, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/sleep", 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/sleep" + "/1", 401, 403, 200);
		test.put(STR."/api/user/\{ADMINNAME}/sleep" + "/1", sleepData, 401, 403, 200);
		test.delete(STR."/api/user/\{ADMINNAME}/sleep" + "/1", 401, 403, 200);

		test.get(STR."/api/user/\{USERNAME}/sleep/chart", chartParams, 401, 200, 200);
		test.post(STR."/api/user/\{USERNAME}/sleep/histogram", histogramRequest, 401, 200, 200);

		test.get(STR."/api/user/\{ADMINNAME}/sleep/chart", chartParams, 401, 403, 200);
		test.post(STR."/api/user/\{ADMINNAME}/sleep/histogram", histogramRequest, 401, 403, 200);

		test.post(STR."/api/user/\{USERNAME}/import/json", jsonFile, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/export/json", 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/export/csv", 401, 200, 200);
		test.post(STR."/api/user/\{USERNAME}/import/csv", csvFile, 401, 200, 200);

		test.post(STR."/api/user/\{ADMINNAME}/import/json", jsonFile, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/export/json", 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/export/csv", 401, 403, 200);
		test.post(STR."/api/user/\{ADMINNAME}/import/csv", csvFile, 401, 403, 200);

		test.post(STR."/api/user/\{USERNAME}/challenge", challenge, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/challenge", challengeParams, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/challenge" + "/1", 401, 200, 200);
		test.put(STR."/api/user/\{USERNAME}/challenge" + "/1", challenge, 401, 200, 200);
		test.delete(STR."/api/user/\{USERNAME}/challenge" + "/1", 401, 200, 200);

		test.post(STR."/api/user/\{ADMINNAME}/challenge", challenge, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/challenge", challengeParams, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/challenge" + "/1", 401, 403, 200);
		test.put(STR."/api/user/\{ADMINNAME}/challenge" + "/1", challenge, 401, 403, 200);
		test.delete(STR."/api/user/\{ADMINNAME}/challenge" + "/1", 401, 403, 200);
	}

	private static List<Arguments> provideUnauthenticatedTestParameters() {
		return test.getArguments(RoleArgumentsBuilder.Role.UNAUTHENTICATED);
	}

	private static List<Arguments> provideUserTestParameters() {
		return test.getArguments(RoleArgumentsBuilder.Role.USER);
	}

	private static List<Arguments> provideAdminTestParameters() {
		return test.getArguments(RoleArgumentsBuilder.Role.ADMIN);
	}


	@ParameterizedTest
	@MethodSource("provideUnauthenticatedTestParameters")
	@DisplayName("Unauthenticated Access")
	void testUnauthenticatedSecurity(RequestBuilder testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminSecurity(RequestBuilder testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(RequestBuilder testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	private void test(RequestBuilder testData, int expectedStatus) throws Exception {
		mockMvc.perform(testData)
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}

}