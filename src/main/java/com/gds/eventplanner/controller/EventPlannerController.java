package com.gds.eventplanner.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gds.eventplanner.dto.request.CreateEventRequestDTO;
import com.gds.eventplanner.dto.request.JoinEventRequestDTO;
import com.gds.eventplanner.dto.request.RecordUserResRequestDTO;
import com.gds.eventplanner.dto.request.StatusChangeRequestDTO;
import com.gds.eventplanner.dto.response.CreateEventResponseDTO;
import com.gds.eventplanner.dto.response.EventResponseDTO;
import com.gds.eventplanner.exception.CustomException;
import com.gds.eventplanner.service.EventPlannerService;
import com.gds.eventplanner.utils.Constants;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Rest controller call which holds the information about the REST api's exposed and API contracts will be declared here
 * 
 * @author Shashi
 *
 */
@RestController
@RequestMapping("/v1")
public class EventPlannerController {

	private static final Logger log = LoggerFactory.getLogger(EventPlannerController.class);
	
	private final EventPlannerService eventPlannerService;

	public EventPlannerController(EventPlannerService eventService) {
		this.eventPlannerService = eventService;
	}

	/**
	 * POST API exposed to create event with basic details
	 * 
	 * Event initiator will call this APi with the event details and his name. 
	 * After calling creating the event in db, the API response with an event id and secret to the user 
	 * The secret will be used in further status update api's to authorize the initiator
	 * 
	 * New event will be create with 'NEW' status initially
	 * 
	 * @param createEventRequestDTO
	 * @return
	 */
	@Operation(summary = "Create event api", description = "API to create event in database for organizer")
	@PostMapping(Constants.EVENT_PATH)
	private ResponseEntity<CreateEventResponseDTO> createEvent(
			@RequestBody CreateEventRequestDTO createEventRequestDTO) {
		log.debug("Request to create an event in db : {}", createEventRequestDTO);
		if (!StringUtils.hasLength(createEventRequestDTO.getEventName())) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_EVENT_NAME, HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(createEventRequestDTO.getOrganizerName())) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_ORGANIZER_NAME, HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(createEventRequestDTO.getOrganizerEmail())) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_ORGANIZER_NAME, HttpStatus.BAD_REQUEST);
		}
		CreateEventResponseDTO createEventResponse = this.eventPlannerService.createEvent(createEventRequestDTO);
		if (createEventResponse == null) {
			throw new CustomException(Constants.PERSISTANCE_ISSUE, Constants.PERSISTANCE_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		log.debug("Response to create event in db : {}", createEventResponse);
		return new ResponseEntity<CreateEventResponseDTO>(createEventResponse, HttpStatus.CREATED);
	}

	/**
	 * PUT API exposed to update the event status
	 * 
	 * Status represents the session property - There are 3 possible status i.e NEW,ACTIVE,CLOSE
	 * 
	 * NEW - Newly created event
	 * ACTIVE - Event is accepting responses i.e. the session is opened
	 * CLOSE - Event is no longer accepting responses i.e. the session is closed
	 * 
	 * @param eventId
	 * @param statusChangeRequestDTO
	 * @return
	 */
	@Operation(summary = "Update event session status api", description = "API to start or stop the session by changing status")
	@PutMapping(Constants.EVENT_PATH)
	private ResponseEntity<EventResponseDTO> updateEventStatus(@RequestBody StatusChangeRequestDTO statusChangeRequestDTO) {
		log.debug("Request to update an event status in db : {}", statusChangeRequestDTO);

		if (statusChangeRequestDTO.getEventId() == null) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_ID, HttpStatus.BAD_REQUEST);
		}
		if (statusChangeRequestDTO.getEventSecret() == null) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_EVENT_SECRET, HttpStatus.BAD_REQUEST);
		}
		if (statusChangeRequestDTO.getStatus() == null) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.IVALID_SESSION_STATUS, HttpStatus.BAD_REQUEST);
		}
		EventResponseDTO eventResponseDTO = this.eventPlannerService.updateEventStatus(statusChangeRequestDTO);
		if (eventResponseDTO == null) {
			throw new CustomException(Constants.PERSISTANCE_ISSUE,Constants.PERSISTANCE_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		log.debug("Response to update an event status in db : {}", eventResponseDTO);
		return new ResponseEntity<EventResponseDTO>(eventResponseDTO, HttpStatus.OK);
	}
	

	/**
	 * GET API exposed to fetch the details of an event
	 * 
	 * User can call this API with event id to join in the session and fetch the event details including other's responses
	 * 
	 * @param eventId
	 * @return
	 */
	@Operation(summary = "Get event details api", description = "API to get event details and available responses for organizer and joined users")
	@GetMapping(Constants.EVENT_WITH_PATH_PARAM)
	private ResponseEntity<EventResponseDTO> fetchEventDetails(@PathVariable(value = "eventId") final Long eventId,@RequestParam(value = "emailId", required = false) final String emailId, @RequestParam(value = "eventSecret", required = false) final Long eventSecret) {
		log.debug("Request to fetch an event details : {}", eventId);
		if (eventId == null) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_ID, HttpStatus.BAD_REQUEST);
		}
		if (emailId == null && eventSecret == null) {
			throw new CustomException(Constants.BAD_REQUEST, "Please provide user email or event secret to fetch the details", HttpStatus.BAD_REQUEST);
		}
		EventResponseDTO eventResponseDTO = this.eventPlannerService.fetchEventDetails(eventId, emailId, eventSecret);
		if (eventResponseDTO == null) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.PERSISTANCE_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		log.debug("Response to fetch an event details : {}", eventResponseDTO);
		return new ResponseEntity<EventResponseDTO>(eventResponseDTO, HttpStatus.OK);
	}

	/**
	 * POST API with event id in the path to record  the user's response
	 * 
	 * User will call this API with his name, response and event id
	 * 
	 * @param eventId
	 * @param joinEventRequestDTO
	 * @return
	 */
	@Operation(summary = "Join event api", description = "API to join the event for users within the session")
	@PostMapping(Constants.EVENT_JOINING_PATH)
	private ResponseEntity<Long> recordUserResponse(@RequestBody JoinEventRequestDTO joinEventRequestDTO) {
		log.debug("Request to join user to event : {}", joinEventRequestDTO);
		if (joinEventRequestDTO.getEventId() == null) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_ID, HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(joinEventRequestDTO.getUserName())) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_USER_NAME, HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(joinEventRequestDTO.getUserEmail())) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_USER_EMAIL, HttpStatus.BAD_REQUEST);
		}

		Long userJoiningId = this.eventPlannerService.joinUserToEvent(joinEventRequestDTO);
		
		if (userJoiningId == null) {
			throw new CustomException(Constants.PERSISTANCE_ISSUE, Constants.PERSISTANCE_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		log.debug("Response to record user response : {}", userJoiningId);
		return new ResponseEntity<Long>(userJoiningId, HttpStatus.CREATED);
	}
	
	/**
	 * POST API with event id and user details to join the user to the session to record  the user's response
	 * 
	 * User will call this API with his name, response and event id
	 * 
	 * @param joinId
	 * @param joinEventRequestDTO
	 * @return
	 */
	@Operation(summary = "Response to event api", description = "API to respond to the event for users within the session")
	@PutMapping(Constants.EVENT_USER_REPONSE_PATH)
	private ResponseEntity<EventResponseDTO> joinUserToEvent(@PathVariable(value = "joinId") final Long joinId, @RequestBody RecordUserResRequestDTO recordUserResRequestDTO) {
		log.debug("Request to record user response : {}", recordUserResRequestDTO);
		if (recordUserResRequestDTO.getEventId() == null) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_ID, HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(recordUserResRequestDTO.getUserResponse())) {
			throw new CustomException(Constants.BAD_REQUEST, Constants.INVALID_USER_NAME, HttpStatus.BAD_REQUEST);
		}

		EventResponseDTO event = this.eventPlannerService.recordUserResponse(joinId, recordUserResRequestDTO);
		
		if (event == null) {
			throw new CustomException(Constants.PERSISTANCE_ISSUE, Constants.PERSISTANCE_MESSAGE,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		log.debug("Response to record user response : {}", event);
		return new ResponseEntity<EventResponseDTO>(event, HttpStatus.OK);
	}

}