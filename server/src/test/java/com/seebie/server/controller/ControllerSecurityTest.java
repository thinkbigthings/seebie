package com.seebie.server.controller;

import com.seebie.server.AppProperties;
import com.seebie.server.dto.*;
import com.seebie.server.mapper.dtotoentity.CsvToSleepData;
import com.seebie.server.mapper.entitytodto.SleepDetailsToCsv;
import com.seebie.server.security.WebSecurityBeanProvider;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.*;
import com.seebie.server.test.WithCustomMockUser;
import com.seebie.server.test.data.MultiRequestBuilder;
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
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.seebie.server.security.WebSecurityConfig.API_LOGIN;
import static com.seebie.server.test.data.TestData.*;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static org.mockito.ArgumentMatchers.*;
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
 * Since that would require copying contents of react/public into server/build/resources/main/static
 * as a dependency of the test phase, it doesn't really fit as a unit test.
 *
 * testing open access to static resources is really a better job of an integration test that happens after the
 * web content is copied from the ui anyway.
 *
 */

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
public class ControllerSecurityTest {

	@MockitoBean
	private DataSource dataSource;

	@MockitoBean
	private MessageService messageService;

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

	private static RoleArgumentsBuilder test;
	private static MultiRequestBuilder requestBuilder;

	private static final String USER_PUBLIC_ID = "0c40d394-c856-49b1-bc78-a86121157288";
	private static final String ADMIN_PUBLIC_ID = "62884376-21e9-4e9f-8f53-b685cd510e65";
	private static final String USER_LOGIN_ID = "someuser@example.com";
	private static final String ADMIN_LOGIN_ID = "admin@example.com";

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = createRandomSleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();
	private static final PasswordResetRequest pwReset = new PasswordResetRequest("new_password");
	private static MockMultipartFile jsonFile; // see setup method

	private static final int goodCsvRows = 1;
	private static final String goodCsvText = createCsv(goodCsvRows);
	private static final MockMultipartFile csvFile = createMultipart(goodCsvText);

	private static final LocalDate today = LocalDate.now();
	private static final LocalDate yesterday = today.minusDays(1);
	private static final String from = yesterday.format(ISO_LOCAL_DATE);
	private static final String to = today.format(ISO_LOCAL_DATE);

	private static final List<String> challengeParams = List.of("currentDate", LocalDate.now().format(ISO_LOCAL_DATE));
	private static final List<String> chartParams = List.of("from", from, "to", to);
	private static final HistogramRequest histogramRequest = new HistogramRequest(1, new FilterList(List.of()));
	private static final ChallengeDto challenge = createRandomChallenge(0, 14);

	private static final MessageDto validChat = randomUserMessage();

    @Autowired
    private UserService userService;

	@BeforeEach
	public void setup() {

		when(fromCsv.apply(contains(goodCsvText))).thenReturn(List.of());
		when(fromCsv.apply(contains(goodCsvText))).thenReturn(List.of(createRandomSleepData()));

		when(toCsv.apply(anyList())).thenReturn("");

		var userUuid = UUID.fromString(USER_PUBLIC_ID);
		var adminUuid = UUID.fromString(ADMIN_PUBLIC_ID);
		when(userService.getUser(userUuid)).thenReturn(createRandomUser(userUuid));
		when(userService.getUser(adminUuid)).thenReturn(createRandomUser(adminUuid));

		when(importExportService.saveSleepData(any(UUID.class), anyList())).thenReturn(0L);
		when(importExportService.retrieveSleepDetails(any(UUID.class))).thenReturn(List.of());
	}

	@BeforeAll
	public static void setup(@Autowired MappingJackson2HttpMessageConverter converter) throws Exception {

		jsonFile = createMultipart(converter.getObjectMapper().writeValueAsString(randomUserData()));

		// so we get the mapper as configured for the app
		requestBuilder = new MultiRequestBuilder(converter.getObjectMapper());

		test = new RoleArgumentsBuilder();

		test.post("/api/registration", registration, 401, 403, 200);
		test.get(API_LOGIN, 401, 200, 200);
		test.get("/api/user", 401, 403, 200);

		test.put("/api/user/"+ USER_PUBLIC_ID +"/personalInfo", info, 401, 200, 200);
		test.post("/api/user/"+ USER_PUBLIC_ID +"/password/update", pwReset, 401, 200, 200);
		test.get("/api/user/"+ USER_PUBLIC_ID, 401, 200, 200);

		test.put("/api/user/"+ ADMIN_PUBLIC_ID +"/personalInfo", info, 401, 403, 200);
		test.post("/api/user/"+ ADMIN_PUBLIC_ID +"/password/update", pwReset, 401, 403, 200);
		test.get("/api/user/"+ ADMIN_PUBLIC_ID, 401, 403, 200);

		test.post("/api/user/"+ USER_PUBLIC_ID +"/sleep", sleepData, 401, 200, 200);
		test.get("/api/user/"+ USER_PUBLIC_ID +"/sleep", 401, 200, 200);
		test.get("/api/user/"+ USER_PUBLIC_ID +"/sleep" + "/1", 401, 200, 200);
		test.put("/api/user/"+ USER_PUBLIC_ID +"/sleep" + "/1", sleepData, 401, 200, 200);
		test.delete("/api/user/"+ USER_PUBLIC_ID +"/sleep" + "/1", 401, 200, 200);

		test.post("/api/user/"+ ADMIN_PUBLIC_ID +"/sleep", sleepData, 401, 403, 200);
		test.get("/api/user/"+ ADMIN_PUBLIC_ID +"/sleep", 401, 403, 200);
		test.get("/api/user/"+ ADMIN_PUBLIC_ID +"/sleep" + "/1", 401, 403, 200);
		test.put("/api/user/"+ ADMIN_PUBLIC_ID +"/sleep" + "/1", sleepData, 401, 403, 200);
		test.delete("/api/user/"+ ADMIN_PUBLIC_ID +"/sleep" + "/1", 401, 403, 200);

		test.get("/api/user/"+ USER_PUBLIC_ID +"/sleep/chart", chartParams, 401, 200, 200);
		test.post("/api/user/"+ USER_PUBLIC_ID +"/sleep/histogram", histogramRequest, 401, 200, 200);

		test.get("/api/user/"+ ADMIN_PUBLIC_ID +"/sleep/chart", chartParams, 401, 403, 200);
		test.post("/api/user/"+ ADMIN_PUBLIC_ID +"/sleep/histogram", histogramRequest, 401, 403, 200);

		test.post("/api/user/"+ USER_PUBLIC_ID +"/import/json", jsonFile, 401, 200, 200);
		test.get("/api/user/"+ USER_PUBLIC_ID +"/export/json", 401, 200, 200);
		test.get("/api/user/"+ USER_PUBLIC_ID +"/export/csv", 401, 200, 200);
		test.post("/api/user/"+ USER_PUBLIC_ID +"/import/csv", csvFile, 401, 200, 200);

		test.post("/api/user/"+ ADMIN_PUBLIC_ID +"/import/json", jsonFile, 401, 403, 200);
		test.get("/api/user/"+ ADMIN_PUBLIC_ID +"/export/json", 401, 403, 200);
		test.get("/api/user/"+ ADMIN_PUBLIC_ID +"/export/csv", 401, 403, 200);
		test.post("/api/user/"+ ADMIN_PUBLIC_ID +"/import/csv", csvFile, 401, 403, 200);

		test.post("/api/user/"+ USER_PUBLIC_ID +"/challenge", challenge, 401, 200, 200);
		test.get("/api/user/"+ USER_PUBLIC_ID +"/challenge", challengeParams, 401, 200, 200);
		test.get("/api/user/"+ USER_PUBLIC_ID +"/challenge" + "/1", 401, 200, 200);
		test.put("/api/user/"+ USER_PUBLIC_ID +"/challenge" + "/1", challenge, 401, 200, 200);
		test.delete("/api/user/"+ USER_PUBLIC_ID +"/challenge" + "/1", 401, 200, 200);

		test.post("/api/user/"+ ADMIN_PUBLIC_ID +"/challenge", challenge, 401, 403, 200);
		test.get("/api/user/"+ ADMIN_PUBLIC_ID +"/challenge", challengeParams, 401, 403, 200);
		test.get("/api/user/"+ ADMIN_PUBLIC_ID +"/challenge" + "/1", 401, 403, 200);
		test.put("/api/user/"+ ADMIN_PUBLIC_ID +"/challenge" + "/1", challenge, 401, 403, 200);
		test.delete("/api/user/"+ ADMIN_PUBLIC_ID +"/challenge" + "/1", 401, 403, 200);

		test.get("/api/user/"+ USER_PUBLIC_ID +"/chat", 401, 200, 200);
		test.post("/api/user/"+ USER_PUBLIC_ID +"/chat", validChat, 401, 200, 200);
		test.delete("/api/user/"+ USER_PUBLIC_ID +"/chat", 401, 200, 200);
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

	@ParameterizedTest(name = "{5} {0} {1}")
	@MethodSource("provideUnauthenticatedTestParameters")
	@DisplayName("Unauthenticated Access")
	void testUnauthenticatedSecurity(HttpMethod http, String url, Object body, List<String> params, int expectedStatus, RoleArgumentsBuilder.Role role) throws Exception {
		test(requestBuilder.toMvcRequest(http, url, body, params), expectedStatus);
	}

	@ParameterizedTest(name = "{5} {0} {1}")
	@MethodSource("provideAdminTestParameters")
	@WithCustomMockUser(publicId = ADMIN_PUBLIC_ID, username= ADMIN_LOGIN_ID, roles = {"ADMIN"})
	@DisplayName("Admin Access")
	void testAdminSecurity(HttpMethod http, String url, Object body, List<String> params, int expectedStatus, RoleArgumentsBuilder.Role role) throws Exception {
		test(requestBuilder.toMvcRequest(http, url, body, params), expectedStatus);
	}

	@ParameterizedTest(name = "{5} {0} {1}")
	@MethodSource("provideUserTestParameters")
	@WithCustomMockUser(publicId = USER_PUBLIC_ID, username = USER_LOGIN_ID, roles = {"USER"})
	@DisplayName("User Access")
	void testUserSecurity(HttpMethod http, String url, Object body, List<String> params, int expectedStatus, RoleArgumentsBuilder.Role role) throws Exception {
		test(requestBuilder.toMvcRequest(http, url, body, params), expectedStatus);
	}

	private void test(RequestBuilder testData, int expectedStatus) throws Exception {
		mockMvc.perform(testData)
				.andDo(print())
				.andExpect(status().is(expectedStatus));
	}

}
