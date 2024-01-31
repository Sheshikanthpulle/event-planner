package com.gds.eventplanner.utils;

public final class Constants {

	private Constants() {

	}

	// API path constants
	public static final String EVENT_PATH = "/event";
	public static final String EVENT_WITH_PATH_PARAM = "/event/{eventId}";
	public static final String EVENT_JOINING_PATH = "/event/join";
	public static final String EVENT_USER_REPONSE_PATH= "/event/join/{joinId}";
	
	// Exception message constants
	public static final String BAD_REQUEST = "Bad Request";
	public static final String NOT_FOUND = "Not Found";
	public static final String PERSISTANCE_ISSUE = "Persistance Issue";
	
	// Exception details constants
	public static final String INVALID_ID = "Event id is mandatory. Please pass it in the request and try again";
	public static final String INVALID_EVENT_NAME = "Event name is mandatory. Please pass it in the request and try again";
	public static final String INVALID_EVENT_SECRET = "Event secret is mandatory. Please pass it in the request and try again";
	public static final String INVALID_ORGANIZER_NAME = "Organizer name is mandatory. Please pass it in the request and try again";
	public static final String INVALID_ORGANIZER_EMAIL = "Organizer email is mandatory. Please pass it in the request and try again";
	public static final String INVALID_USER_NAME = "User name is mandatory. Please pass it in the request and try again";
	public static final String INVALID_USER_EMAIL = "User email is mandatory. Please pass it in the request and try again";
	public static final String IVALID_SESSION_STATUS = "Session status is mandatory. Please pass it in the request and try again";
	public static final String INVALID_RESPONSE_OBJECT = "Response is mandatory. Please pass it in the request and try again";
	public static final String INVALID_RESPONSE = "User Response is mandatory. Please pass it in the request and try again";
	public static final String PERSISTANCE_MESSAGE = "Unsuccessfull request. Please try again";
	
	
}
