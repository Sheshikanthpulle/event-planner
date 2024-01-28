package com.gds.eventplanner.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Controller advice class to handle and respond when the exceptions cached in the APIs  
 * 
 * @author Shashi
 *
 */
@ControllerAdvice
public class CustomExceptionHandler {
	
	/**
	 * This method returns the exception message and details
	 * 
	 * @param ex
	 * @return res
	 */
	@ExceptionHandler(value = CustomException.class)
	public ResponseEntity<Object> handleCustomException(CustomException ex) {
		Map<String, String> res = new HashMap<>();
		CustomException exceptionResponse = new CustomException(ex.getMessage(), ex.getDetails(), ex.getStatus());
		res.put("MESSAGE", exceptionResponse.getMessage());
		res.put("DETAILS", exceptionResponse.getDetails());
		return new ResponseEntity<>(res, ex.getStatus());
	}
}
