package com.seebie.server.controller;


import com.seebie.server.AppProperties;
import com.seebie.server.dto.*;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.SleepService;
import com.seebie.server.service.UserService;
import jakarta.validation.Valid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

import static com.seebie.server.test.data.TestData.createRandomPersonalInfo;
import static com.seebie.server.test.data.TestData.createRandomUserRegistration;
import static org.springframework.http.HttpMethod.*;
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


//@WebMvcTest
//@DisplayName("Controller Validation")
//@EnableConfigurationProperties(value = {AppProperties.class})
//@Import(WebSecurityConfig.class)
public class ControllerValidationTest {
//
//	private static final String USERNAME = "someuser";
//	private static final String ADMINNAME = "admin";
//
//	@Autowired
//	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//	private MappingJackson2HttpMessageConverter converter;
//
//	@Autowired
//	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//	private MockMvc mockMvc;
//
//	@MockBean
//	private UserService service;
//
//	@MockBean
//	private SleepService sleepService;
//
//	private static final RegistrationRequest registration = createRandomUserRegistration();
//	private static final SleepData sleepData = new SleepData();
//	private static final PersonalInfo info = createRandomPersonalInfo();
//	private static final RegistrationRequest invalidRegistration = createRandomUserRegistration();
//	private static final SleepData invalidSleepData = new SleepData();
//	private static final PersonalInfo invalidInfo = createRandomPersonalInfo();
//
//	private static List<Arguments> provideAdminTestParameters() {
//		return List.of(
//
//				// user controller
//				Arguments.of(POST, "/registration", registration, 200),
//				Arguments.of(PUT, "/user/" + USERNAME + "/personalInfo", info, 200),
//
//				Arguments.of(POST, "/registration", invalidRegistration, 200),
//				Arguments.of(PUT, "/user/" + USERNAME + "/personalInfo", invalidInfo, 200),
//
//				// sleep controller
//				Arguments.of(POST, "/user/" + USERNAME + "/sleep", sleepData, 200),
//				Arguments.of(PUT, "/user/" + USERNAME + "/sleep" + "/1", sleepData, 200),
//
//				Arguments.of(POST, "/user/" + USERNAME + "/sleep", invalidSleepData, 200),
//				Arguments.of(PUT, "/user/" + USERNAME + "/sleep" + "/1", invalidSleepData, 200)
//		);
//	}
//
//	private static List<Arguments> provideUserTestParameters() {
//		return List.of(
//
//		);
//	}
//
//	@ParameterizedTest
//	@MethodSource("provideUnauthenticatedTestParameters")
//	@DisplayName("Unauthenticated Access")
//	void testUnauthenticatedSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {
//
//		mockMvc.perform(request(httpMethod, url).content(toJson(reqBody)).contentType(APPLICATION_JSON))
//				.andDo(print())
//				.andExpect(status().is(expectedStatus));
//	}
//
//	@ParameterizedTest
//	@MethodSource("provideAdminTestParameters")
//	@WithMockUser(username = ADMINNAME, roles = {"ADMIN"})
//	@DisplayName("Admin Access")
//	void testAdminSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {
//
//		mockMvc.perform(request(httpMethod, url).content(toJson(reqBody)).contentType(APPLICATION_JSON))
//				.andDo(print())
//				.andExpect(status().is(expectedStatus));
//	}
//
//	@ParameterizedTest
//	@MethodSource("provideUserTestParameters")
//	@WithMockUser(username = USERNAME, roles = {"USER"})
//	@DisplayName("User Access")
//	void testUserSecurity(HttpMethod httpMethod, String url, Object reqBody, int expectedStatus) throws Exception {
//
//		mockMvc.perform(request(httpMethod, url).content(toJson(reqBody)).contentType(APPLICATION_JSON))
//				.andDo(print())
//				.andExpect(status().is(expectedStatus));
//	}
//
//	String toJson(Object requestBody) throws Exception {
//		return switch (requestBody) {
//			case String s -> s;
//			case PersonalInfo p -> converter.getObjectMapper().writerFor(p.getClass()).writeValueAsString(p);
//			case RegistrationRequest r -> converter.getObjectMapper().writerFor(r.getClass()).writeValueAsString(r);
//			case SleepData d -> converter.getObjectMapper().writerFor(d.getClass()).writeValueAsString(d);
//			default -> throw new IllegalStateException("Can't create request body for " + requestBody);
//		};
//	}

}