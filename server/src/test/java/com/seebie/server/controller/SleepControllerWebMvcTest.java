package com.seebie.server.controller;


import com.fasterxml.jackson.databind.ObjectWriter;
import com.seebie.server.AppProperties;
import com.seebie.server.dto.SleepData;
import com.seebie.server.security.WebSecurityConfig;
import com.seebie.server.service.SleepService;
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


import java.time.LocalDate;
import java.util.HashSet;

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


@WebMvcTest(SleepController.class)
@EnableConfigurationProperties(value = {AppProperties.class})
@Import(WebSecurityConfig.class)
public class SleepControllerWebMvcTest {

	private static final String USERNAME = "someusername";
	private static final String SLEEP_URL = "/user/"+ USERNAME + "/sleep";

	private MockHttpServletRequestBuilder userSleepReq = post(SLEEP_URL).contentType(APPLICATION_JSON);

	private	ObjectWriter writer;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MappingJackson2HttpMessageConverter converter;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MockMvc mockMvc;

	@MockBean
	private SleepService sleepService;


	@BeforeEach
	public void setup() {
 		writer = converter.getObjectMapper().writerFor(SleepData.class);
	}

	@Test
	@WithMockUser(username = USERNAME)
	public void testValidData() throws Exception {

		var validData = new SleepData(LocalDate.now(), 10, "", 0, new HashSet<>());

		// this tests that the validation is applied
		mockMvc.perform(userSleepReq.content(writer.writeValueAsString(validData)))
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = USERNAME)
	public void testInvalidData() throws Exception {

		var invalidData = new SleepData(null, 0, null, 0, null);

		// this tests that the validation is applied
		mockMvc.perform(userSleepReq.content(writer.writeValueAsString(invalidData)))
				.andDo(print())
				.andExpect(status().is4xxClientError());
	}
}