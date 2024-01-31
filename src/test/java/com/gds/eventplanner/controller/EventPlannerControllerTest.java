package com.gds.eventplanner.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gds.eventplanner.dto.request.CreateEventRequestDTO;
import com.gds.eventplanner.dto.request.StatusChangeRequestDTO;
import com.gds.eventplanner.dto.response.CreateEventResponseDTO;
import com.gds.eventplanner.dto.response.EventResponseDTO;
import com.gds.eventplanner.exception.CustomException;
import com.gds.eventplanner.service.EventPlannerService;
import com.gds.eventplanner.utils.Constants;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class EventPlannerControllerTest {
	
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@MockBean
	EventPlannerService eventService;
	
	@BeforeEach
	public void setUp() {
		DefaultMockMvcBuilder builder = MockMvcBuilders.webAppContextSetup(this.wac);
		this.mockMvc = builder.build();
	}

	@Autowired
	EventPlannerController EventPlannerController;

	@Autowired
	private ResourceLoader resourceLoader;
	ObjectMapper obj = new ObjectMapper();

	String getFile(String location) throws Exception {
		Resource resource = resourceLoader.getResource(location);
		InputStream input = resource.getInputStream();
		byte[] bdata = FileCopyUtils.copyToByteArray(input);
		String data = new String(bdata, StandardCharsets.UTF_8);
		return data;
	}

	@Test
	void createEventTest() throws JsonMappingException, JsonProcessingException, Exception {
		CreateEventResponseDTO createEventResponseDTO = obj.readValue(getFile("classpath:createEventResposeData.json"),
				new TypeReference<CreateEventResponseDTO>() {
				});

		when(eventService.createEvent(Mockito.any())).thenReturn(createEventResponseDTO);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/v1/event")
				.accept(MediaType.APPLICATION_JSON).content(getFile("classpath:createEventData.json"))
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(requestBuilder).andDo(print())
				.andExpect(content().json(getFile("classpath:createEventResposeData.json")));
		assertNotNull(HttpStatus.CREATED);

	}
	
	@Test
	void createEventExceptionTest() throws Exception, JsonProcessingException, Exception {

		CustomException ex = new CustomException(Constants.BAD_REQUEST,Constants.INVALID_EVENT_NAME,HttpStatus.BAD_REQUEST);
		when(eventService.createEvent(Mockito.any())).thenThrow(ex);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/v1/event")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(getFile("classpath:createEventExData.json"))
				.contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON_VALUE));
		assertNotNull(HttpStatus.BAD_REQUEST);
	}

	@Test
	void updateEventStatusTest() throws JsonMappingException, JsonProcessingException, Exception {
		EventResponseDTO eventResponseDTO = obj.readValue(getFile("classpath:eventRes.json"),
				new TypeReference<EventResponseDTO>() {
				});

		when(eventService.updateEventStatus(Mockito.any())).thenReturn(eventResponseDTO);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/v1/event")
				.accept(MediaType.APPLICATION_JSON).content(getFile("classpath:updateStatusData.json"))
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(requestBuilder).andDo(print())
				.andExpect(content().json(getFile("classpath:eventRes.json")));
		assertNotNull(HttpStatus.OK);

	}
	
	@Test
	void updateEventStatusExceptionTest() throws Exception, JsonProcessingException, Exception {

		CustomException ex = new CustomException(Constants.BAD_REQUEST,Constants.INVALID_ID,HttpStatus.BAD_REQUEST);
		when(eventService.createEvent(Mockito.any())).thenThrow(ex);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.put("/v1/event")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(getFile("classpath:updateStatusExData.json"))
				.contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON_VALUE));
		assertNotNull(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	void userResponseExceptionTest() throws Exception, JsonProcessingException, Exception {

		CustomException ex = new CustomException(Constants.BAD_REQUEST,Constants.INVALID_USER_NAME,HttpStatus.BAD_REQUEST);
		when(eventService.createEvent(Mockito.any())).thenThrow(ex);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/v1/event/102")
				.accept(MediaType.APPLICATION_JSON_VALUE).content(getFile("classpath:userResponseEx.json"))
				.contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON_VALUE));
		assertNotNull(HttpStatus.BAD_REQUEST);
	}

	@Test
	void fetchEventDetailsTest() throws JsonMappingException, JsonProcessingException, Exception {

		EventResponseDTO eventResponseDTO = obj.readValue(getFile("classpath:eventRes.json"),
				new TypeReference<EventResponseDTO>() {
				});
		when(eventService.fetchEventDetails(Mockito.any(), Mockito.any(),Mockito.any())).thenReturn(eventResponseDTO);
		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
				.get("/v1/event/102").contentType(MediaType.APPLICATION_JSON);
		this.mockMvc.perform(builder.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(MockMvcResultMatchers.status().isOk()).andDo(print())
				.andExpect(content().json(getFile("classpath:eventRes.json")));
		assertNotNull(HttpStatus.OK);

	}

}
