package com.seebie.server.controller;


import com.fasterxml.jackson.databind.ObjectWriter;
import com.seebie.server.AppProperties;
import com.seebie.server.dto.RegistrationRequest;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// almost of the full stack is used, and your code will be called in exactly the same way
// as if it were processing a real HTTP request but without the cost of starting the server
// WebMvcTest (as opposed to pure unit test) is good for testing:
//   HTTP request mapping
//   Input field validation
//   Serialization / Deserialization
//   Error handling


@WebMvcTest(UserController.class)
@EnableConfigurationProperties(value = {AppProperties.class})
@Import(WebSecurityConfig.class)
public class UserControllerWebMvcTest {

	private static final String USERNAME = "someusername";
	private static final String REGISTRATION_URL = "/registration";

	private MockHttpServletRequestBuilder registrationReq = post(REGISTRATION_URL).contentType(APPLICATION_JSON);

	private	ObjectWriter writer;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MappingJackson2HttpMessageConverter converter;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MockMvc mockMvc;

	@MockBean
	private UserService service;


	@BeforeEach
	public void setup() {
 		writer = converter.getObjectMapper().writerFor(RegistrationRequest.class);
	}

	@Test
	@WithMockUser(username = USERNAME, roles={"ADMIN"})
	public void testGoodRegistrationValidation() throws Exception {

		var validRegistration = new RegistrationRequest("username_here", "password", "x@y.com");

		// this tests that the validation is applied
		mockMvc.perform(registrationReq.content(writer.writeValueAsString(validRegistration)))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = USERNAME, roles={"ADMIN"})
	public void testBadRegistrationValidation() throws Exception {

		var invalidRegistration = new RegistrationRequest("spaces require encoding", "password", "x@y.com");

		// this tests that the validation is applied
		mockMvc.perform(registrationReq.content(writer.writeValueAsString(invalidRegistration)))
				.andDo(print())
				.andExpect(status().is4xxClientError());
	}
}