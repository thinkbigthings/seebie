package com.seebie.server.controller;

import com.seebie.server.AppProperties;
import com.seebie.server.dto.*;
import com.seebie.server.mapper.dtotoentity.CsvToSleepData;
import com.seebie.server.mapper.dtotoentity.SleepDetailsToCsv;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.ChallengeService;
import com.seebie.server.service.ImportExportService;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.MvcRequestMapper;
import com.seebie.server.test.data.Request;
import com.seebie.server.test.data.TestData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.sql.DataSource;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

import static com.seebie.server.controller.ControllerValidationTest.testDataObj2Str;
import static com.seebie.server.mapper.entitytodto.ZonedDateTimeConverter.format;
import static com.seebie.server.test.data.TestData.*;
import static com.seebie.server.test.data.ZoneIds.AMERICA_NEW_YORK;
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
@Import(WebSecurityConfig.class)
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
	private static final String password = "new_password";
	private static MockMultipartFile jsonFile; // see setup method

	private static final int goodCsvRows = 1;
	private static final String goodCsvText = createCsv(goodCsvRows);
	private static final MockMultipartFile csvFile = createMultipart(goodCsvText);

	private static final String from = format(ZonedDateTime.now().minusDays(1));
	private static final String to = format(ZonedDateTime.now());

	private static final String[] chartParams = new String[]{"from", from, "to", to};
	private static final HistogramRequest histogramRequest = new HistogramRequest(1, new FilterList(List.of()));
	private static final Challenge challenge = createRandomChallenge(0, 14);

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockMvc mockMvc;

	private static Function<Request, RequestBuilder> toRequest;

	private static TestData.RequestResponseBuilder test = new RequestResponseBuilder();

	@BeforeEach
	public void setup() {

		when(fromCsv.apply(contains(goodCsvText))).thenReturn(List.of());
		when(fromCsv.apply(contains(goodCsvText))).thenReturn(List.of(createRandomSleepData()));

		when(toCsv.apply(anyList())).thenReturn("");

		when(importExportService.saveSleepData(anyString(), anyList())).thenReturn(0L);
		when(importExportService.retrieveSleepDetails(anyString())).thenReturn(List.of());
	}

	@BeforeAll
	public static void setup(@Autowired MappingJackson2HttpMessageConverter converter) throws Exception {

		jsonFile = createMultipart(converter.getObjectMapper().writeValueAsString(randomUserData()));

		// so we get the mapper as configured for the app
		toRequest = new MvcRequestMapper(testDataObj2Str(converter.getObjectMapper()));

		test.post("/api/registration", registration, 401, 403, 200);
		test.get("/api/login", 401, 200, 200);
		test.get("/api/user", 401, 403, 200);

		test.put(STR."/api/user/\{USERNAME}/personalInfo", info, 401, 200, 200);
		test.post(STR."/api/user/\{USERNAME}/password/update", password, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}", 401, 200, 200);

		test.put(STR."/api/user/\{ADMINNAME}/personalInfo", info, 401, 403, 200);
		test.post(STR."/api/user/\{ADMINNAME}/password/update", password, 401, 403, 200);
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
		test.get(STR."/api/user/\{USERNAME}/sleep/download", 401, 200, 200);
		test.post(STR."/api/user/\{USERNAME}/sleep/upload", csvFile, 401, 200, 200);

		test.get(STR."/api/user/\{ADMINNAME}/sleep/chart", chartParams, 401, 403, 200);
		test.post(STR."/api/user/\{ADMINNAME}/sleep/histogram", histogramRequest, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/sleep/download", 401, 403, 200);
		test.post(STR."/api/user/\{ADMINNAME}/sleep/upload", csvFile, 401, 403, 200);

		test.post(STR."/api/user/\{USERNAME}/import/json", jsonFile, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/export/json", 401, 200, 200);

		test.post(STR."/api/user/\{ADMINNAME}/import/json", jsonFile, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/export/json", 401, 403, 200);

		test.post(STR."/api/user/\{USERNAME}/challenge", challenge, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/challenge", new String[]{"zoneId", AMERICA_NEW_YORK}, 401, 200, 200);
		test.get(STR."/api/user/\{USERNAME}/challenge" + "/1", 401, 200, 200);
		test.put(STR."/api/user/\{USERNAME}/challenge" + "/1", challenge, 401, 200, 200);
		test.delete(STR."/api/user/\{USERNAME}/challenge" + "/1", 401, 200, 200);

		test.post(STR."/api/user/\{ADMINNAME}/challenge", challenge, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/challenge", new String[]{"zoneId", AMERICA_NEW_YORK}, 401, 403, 200);
		test.get(STR."/api/user/\{ADMINNAME}/challenge" + "/1", 401, 403, 200);
		test.put(STR."/api/user/\{ADMINNAME}/challenge" + "/1", challenge, 401, 403, 200);
		test.delete(STR."/api/user/\{ADMINNAME}/challenge" + "/1", 401, 403, 200);
	}

	private static List<Arguments> provideUnauthenticatedTestParameters() {
		return test.build(RequestResponseBuilder.Role.UNAUTHENTICATED);
	}

	private static List<Arguments> provideUserTestParameters() {
		return test.build(RequestResponseBuilder.Role.USER);
	}
	private static List<Arguments> provideAdminTestParameters() {
		return test.build(RequestResponseBuilder.Role.ADMIN);
	}


	@ParameterizedTest
	@MethodSource("provideUnauthenticatedTestParameters")
	@DisplayName("Unauthenticated Access")
	void testUnauthenticatedSecurity(Request testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideAdminTestParameters")
	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminSecurity(Request testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	@ParameterizedTest
	@MethodSource("provideUserTestParameters")
	@WithMockUser(username = USERNAME, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(Request testData, int expectedStatus) throws Exception {
		test(testData, expectedStatus);
	}

	private void test(Request testData, int expectedStatus) throws Exception {
		mockMvc.perform(toRequest.apply(testData))
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}

}