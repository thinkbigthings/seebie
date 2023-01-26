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
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.util.HashSet;

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

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MappingJackson2HttpMessageConverter converter;

	@Autowired
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	private MockMvc mockMvc;

	@MockBean
	private SleepService sleepService;

	private	ObjectWriter writer;
	private static final String USERNAME = "someusername";

	@BeforeEach
	public void setup() {
 		writer = converter.getObjectMapper().writerFor(SleepData.class);
	}

	@Test
	@WithMockUser(username = USERNAME)
	public void testValidData() throws Exception {

		var validData = new SleepData(LocalDate.now(), 10, "", 0, new HashSet<>());

		var reqBuilder = post("/user/"+ USERNAME + "/sleep")
				.content(writer.writeValueAsString(validData))
				.contentType(MediaType.APPLICATION_JSON);

		// this tests that the validation is applied
		mockMvc.perform(reqBuilder)
				.andDo(print())
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = USERNAME)
	public void testInvalidData() throws Exception {

		var invalidData = new SleepData(null, 0, null, 0, null);

		var reqBuilder = post("/user/"+ USERNAME + "/sleep")
				.content(writer.writeValueAsString(invalidData))
				.contentType(MediaType.APPLICATION_JSON);

		// this tests that the validation is applied
		mockMvc.perform(reqBuilder)
				.andDo(print())
				.andExpect(status().is4xxClientError());
	}
}