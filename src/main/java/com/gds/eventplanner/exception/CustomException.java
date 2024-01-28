package com.gds.eventplanner.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception class to handle the exception responses
 * 
 * @author Shashi
 *
 */
public class CustomException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final String details;
	private final HttpStatus status;

	/**
	 * This is parameterized constructor of CustomException class
	 * 
	 * @param message
	 * @param details
	 */
	public CustomException(String message, String details, HttpStatus status) {
		super(message);
		this.details = details;
		this.status = status;
	}

	/**
	 * This method returns the exception details
	 * 
	 * @return details
	 */
	public String getDetails() {
		return details;
	}
	
	/**
	 * This method returns the exception status
	 * 
	 * @return status
	 */
	public HttpStatus getStatus() {
		return status;
	}
	

}