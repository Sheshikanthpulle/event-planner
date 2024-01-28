package com.gds.eventplanner.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gds.eventplanner.dto.CreateEventRequestDTO;
import com.gds.eventplanner.dto.CreateEventResponseDTO;
import com.gds.eventplanner.dto.EventDTO;
import com.gds.eventplanner.dto.StatusChangeRequestDTO;
import com.gds.eventplanner.dto.UserResponseDTO;
import com.gds.eventplanner.exception.CustomException;
import com.gds.eventplanner.service.EventPlannerService;

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
	@PostMapping("/event")
	private ResponseEntity<CreateEventResponseDTO> createEvent(
			@RequestBody CreateEventRequestDTO createEventRequestDTO) {
		log.debug("Request to create an event in db : {}", createEventRequestDTO);
		if (!StringUtils.hasLength(createEventRequestDTO.getEventName())) {
			throw new CustomException("Bad Request", "Event name is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(createEventRequestDTO.getOrganizerName())) {
			throw new CustomException("Bad Request", "Organizer name is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(createEventRequestDTO.getOrganizerEmail())) {
			throw new CustomException("Bad Request", "Organizer email is mandatory", HttpStatus.BAD_REQUEST);
		}
		CreateEventResponseDTO createEventResponse = this.eventPlannerService.createEvent(createEventRequestDTO);
		if (createEventResponse == null) {
			throw new CustomException("Persistant Issue", "Unable to persist event data, Please try again",
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
	@PutMapping("/event")
	private ResponseEntity<EventDTO> updateEventStatus(@RequestBody StatusChangeRequestDTO statusChangeRequestDTO) {
		log.debug("Request to update an event status in db : {}", statusChangeRequestDTO);

		if (statusChangeRequestDTO.getEventId() == null) {
			throw new CustomException("Bad Request", "Event id is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (statusChangeRequestDTO.getEventSecret() == null) {
			throw new CustomException("Bad Request", "Event Secret id is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (statusChangeRequestDTO.getStatus() == null) {
			throw new CustomException("Bad Request", "Status is mandatory", HttpStatus.BAD_REQUEST);
		}
		EventDTO eventDTO = this.eventPlannerService.updateEventStatus(statusChangeRequestDTO);
		if (eventDTO == null) {
			throw new CustomException("Persistant Issue", "Unable to update event status, Please try again",
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		log.debug("Response to update an event status in db : {}", eventDTO);
		return new ResponseEntity<EventDTO>(eventDTO, HttpStatus.OK);
	}
	

	/**
	 * GET API exposed to fetch the details of an event
	 * 
	 * User can call this API with event id to join in the session and fetch the event details including other's responses
	 * 
	 * @param eventId
	 * @return
	 */
	@GetMapping("/event/{eventId}")
	private ResponseEntity<EventDTO> fetchEventDetails(@PathVariable(value = "eventId") final Long eventId) {
		log.debug("Request to fetch an event details : {}", eventId);
		if (eventId == null) {
			throw new CustomException("Bad Request", "Event id is mandatory", HttpStatus.BAD_REQUEST);
		}
		EventDTO eventDTO = this.eventPlannerService.fetchEventDetails(eventId);
		if (eventDTO == null) {
			throw new CustomException("Persistant Issue", "Unable to fetch event details, Please try again",
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		log.debug("Response to fetch an event details : {}", eventDTO);
		return new ResponseEntity<EventDTO>(eventDTO, HttpStatus.OK);
	}

	/**
	 * POST API with event id in the path to record  the user's response
	 * 
	 * User will call this API with his name, response and event id
	 * 
	 * @param eventId
	 * @param userResponseDTO
	 * @return
	 */
	@PostMapping("/event/{eventId}")
	private ResponseEntity<EventDTO> recordUserResponse(@PathVariable(value = "eventId") final Long eventId, @RequestBody UserResponseDTO userResponseDTO) {
		log.debug("Request to record user response : {}", userResponseDTO);
		if (eventId == null) {
			throw new CustomException("Bad Request", "Event id is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (userResponseDTO == null) {
			throw new CustomException("Bad Request", "Please respond with your choice of location", HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(userResponseDTO.getUserName())) {
			throw new CustomException("Bad Request", "User name is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(userResponseDTO.getUserEmail())) {
			throw new CustomException("Bad Request", "User email is mandatory", HttpStatus.BAD_REQUEST);
		}
		if (!StringUtils.hasLength(userResponseDTO.getResponse())) {
			throw new CustomException("Bad Request", "location is mandatory", HttpStatus.BAD_REQUEST);
		}

		EventDTO eventDTO = this.eventPlannerService.recordUserResponse(userResponseDTO);
		
		if (eventDTO == null) {
			throw new CustomException("Persistant Issue", "Unable to record your response, Please try again",
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
		log.debug("Response to record user response : {}", eventDTO);
		return new ResponseEntity<EventDTO>(eventDTO, HttpStatus.OK);
	}

}