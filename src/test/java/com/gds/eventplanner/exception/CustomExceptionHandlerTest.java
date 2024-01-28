package com.gds.eventplanner.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 *  Unit test for CustomExceptionHandler class
 *   
 * @author Shashi
 *
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class CustomExceptionHandlerTest {

	@Autowired
	CustomExceptionHandler customExceptionHandler;

	@Test
	void handleCustomExceptionTest() throws Exception {

		CustomException ex = new CustomException("No Records", "NodataFound", HttpStatus.NOT_FOUND);

		Map<String, String> res = new HashMap<>();
		res.put("MESSAGE", ex.getMessage());
		res.put("DETAILS", ex.getDetails());

		ResponseEntity<Object> result = customExceptionHandler.handleCustomException(ex);

		assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);

	}

	
}
