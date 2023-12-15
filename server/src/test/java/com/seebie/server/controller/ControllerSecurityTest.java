package com.seebie.server.controller;


import com.seebie.server.AppProperties;
import com.seebie.server.dto.*;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.ChallengeService;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import com.seebie.server.test.data.AppRequest;
import com.seebie.server.test.data.MvcRequestMapper;
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
	private SleepService sleepService;

	@MockBean
	private ChallengeService challengeService;

	private static final String USERNAME = "someuser";
	private static final String ADMINNAME = "admin";

	private static final RegistrationRequest registration = createRandomUserRegistration();
	private static final SleepData sleepData = createRandomSleepData();
	private static final PersonalInfo info = createRandomPersonalInfo();
	private static final String password = "new_password";
	private static final MockMultipartFile file = createMultipart(createCsv(1));

	private static final String from = format(ZonedDateTime.now().minusDays(1));
	private static final String to = format(ZonedDateTime.now());

	private static final String[] chartParams = new String[]{"from", from, "to", to};
	private static final HistogramRequest histogramRequest = new HistogramRequest(1, new FilterList(List.of()));

	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockMvc mockMvc;

	private static Function<AppRequest, RequestBuilder> toRequest;

	private static TestData.ArgumentBuilder test;

	@BeforeEach
	public void setup() {

		when(sleepService.saveCsv(anyString(), anyString())).thenReturn(0L);
		when(sleepService.retrieveCsv(anyString())).thenReturn("");
	}

	@BeforeAll
	public static void setup(@Autowired MappingJackson2HttpMessageConverter converter) {

		// so we get the mapper as configured for the app
		toRequest = new MvcRequestMapper(testDataObj2Str(converter.getObjectMapper()));
		
		test = new TestData.ArgumentBuilder();
	}

	private static List<Arguments> provideUnauthenticatedTestParameters() {
		return List.of(

				// user controller
				test.post("/api/registration", registration, 401),
				test.get("/api/login", 401),
				test.get("/api/user", 401),

				test.put(STR."/api/user/\{USERNAME}/personalInfo", registration, 401),
				test.post(STR."/api/user/\{USERNAME}/password/update", password, 401),
				test.get(STR."/api/user/\{USERNAME}", 401),

				test.put(STR."/api/user/\{ADMINNAME}/personalInfo", info, 401),
				test.post(STR."/api/user/\{ADMINNAME}/password/update", password, 401),
				test.get(STR."/api/user/\{ADMINNAME}", 401),

				// sleep controller
				test.post(STR."/api/user/\{USERNAME}/sleep", sleepData, 401),
				test.get(STR."/api/user/\{USERNAME}/sleep", 401),
				test.get(STR."/api/user/\{USERNAME}/sleep" + "/1", 401),
				test.put(STR."/api/user/\{USERNAME}/sleep" + "/1", sleepData, 401),
				test.delete(STR."/api/user/\{USERNAME}/sleep" + "/1", 401),

				test.post(STR."/api/user/\{ADMINNAME}/sleep", sleepData, 401),
				test.get(STR."/api/user/\{ADMINNAME}/sleep", 401),
				test.get(STR."/api/user/\{ADMINNAME}/sleep" + "/1", 401),
				test.put(STR."/api/user/\{ADMINNAME}/sleep" + "/1", sleepData, 401),
				test.delete(STR."/api/user/\{ADMINNAME}/sleep" + "/1", 401),

				test.get(STR."/api/user/\{USERNAME}/sleep/chart", chartParams, 401),
				test.post(STR."/api/user/\{USERNAME}/sleep/histogram", histogramRequest, 401),
				test.get(STR."/api/user/\{USERNAME}/sleep/download", 401),
				test.post(STR."/api/user/\{USERNAME}/sleep/upload", file, 401)


		);
	}

	private static List<Arguments> provideAdminTestParameters() {
		return List.of(

				// user controller
				test.post("/api/registration", registration, 200),
				test.get("/api/login", 200),
				test.get("/api/user", 200),

				test.put(STR."/api/user/\{USERNAME}/personalInfo", info, 200),
				test.post(STR."/api/user/\{USERNAME}/password/update", password, 200),
				test.get("/api/user/" + USERNAME, 200),

				test.put(STR."/api/user/\{ADMINNAME}/personalInfo", info, 200),
				test.post(STR."/api/user/\{ADMINNAME}/password/update", password, 200),
				test.get("/api/user/" + ADMINNAME, 200),

				// sleep controller
				test.post(STR."/api/user/\{USERNAME}/sleep", sleepData, 200),
				test.get(STR."/api/user/\{USERNAME}/sleep", 200),
				test.get(STR."/api/user/\{USERNAME}/sleep" + "/1", 200),
				test.put(STR."/api/user/\{USERNAME}/sleep" + "/1", sleepData, 200),
				test.delete(STR."/api/user/\{USERNAME}/sleep" + "/1", 200),

				test.post(STR."/api/user/\{ADMINNAME}/sleep", sleepData, 200),
				test.get(STR."/api/user/\{ADMINNAME}/sleep", 200),
				test.get(STR."/api/user/\{ADMINNAME}/sleep" + "/1", 200),
				test.put(STR."/api/user/\{ADMINNAME}/sleep" + "/1", sleepData, 200),
				test.delete(STR."/api/user/\{ADMINNAME}/sleep" + "/1", 200),

				test.get(STR."/api/user/\{USERNAME}/sleep/chart", chartParams, 200),
				test.post(STR."/api/user/\{USERNAME}/sleep/histogram", histogramRequest, 200),
				test.get(STR."/api/user/\{USERNAME}/sleep/download", 200),
				test.post(STR."/api/user/\{USERNAME}/sleep/upload", file, 200)
		);
	}

	private static List<Arguments> provideUserTestParameters() {
		return List.of(

				// user controller
				test.post("/api/registration", registration, 403),
				test.get("/api/login", 200),
				test.get("/api/user", 403),

				test.put(STR."/api/user/\{USERNAME}/personalInfo", info, 200),
				test.post(STR."/api/user/\{USERNAME}/password/update", password, 200),
				test.get("/api/user/" + USERNAME, 200),

				// user controller - should not access other user endpoints
				test.put(STR."/api/user/\{ADMINNAME}/personalInfo", info, 403),
				test.post(STR."/api/user/\{ADMINNAME}/password/update", password, 403),
				test.get("/api/user/" + ADMINNAME, 403),

				// sleep controller
				test.post(STR."/api/user/\{USERNAME}/sleep", sleepData, 200),
				test.get(STR."/api/user/\{USERNAME}/sleep", 200),
				test.get(STR."/api/user/\{USERNAME}/sleep" + "/1", 200),
				test.put(STR."/api/user/\{USERNAME}/sleep" + "/1", sleepData, 200),
				test.delete(STR."/api/user/\{USERNAME}/sleep" + "/1", 200),

				test.get(STR."/api/user/\{USERNAME}/sleep/chart", new String[]{"from", from, "to", to}, 200),
				test.get(STR."/api/user/\{USERNAME}/sleep/download", 200),
				test.post(STR."/api/user/\{USERNAME}/sleep/upload", file, 200),

				// sleep controller - should not access other user endpoints

				test.post(STR."/api/user/\{ADMINNAME}/sleep", sleepData, 403),
				test.get(STR."/api/user/\{ADMINNAME}/sleep", 403),
				test.get(STR."/api/user/\{ADMINNAME}/sleep" + "/1", 403),
				test.put(STR."/api/user/\{ADMINNAME}/sleep" + "/1", sleepData, 403),
				test.delete(STR."/api/user/\{ADMINNAME}/sleep" + "/1", 403),

				test.get(STR."/api/user/\{ADMINNAME}/sleep/chart", new String[]{"from", from, "to", to}, 403),
				test.get(STR."/api/user/\{ADMINNAME}/sleep/download", 403),
				test.post(STR."/api/user/\{ADMINNAME}/sleep/upload", file, 403)
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