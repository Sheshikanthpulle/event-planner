package com.gds.eventplanner.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gds.eventplanner.domain.Event;
import com.gds.eventplanner.domain.EventUserResponse;
import com.gds.eventplanner.domain.Status;
import com.gds.eventplanner.dto.request.CreateEventRequestDTO;
import com.gds.eventplanner.dto.request.JoinEventRequestDTO;
import com.gds.eventplanner.dto.request.RecordUserResRequestDTO;
import com.gds.eventplanner.dto.request.StatusChangeRequestDTO;
import com.gds.eventplanner.dto.response.CreateEventResponseDTO;
import com.gds.eventplanner.dto.response.EventResponseDTO;
import com.gds.eventplanner.dto.response.EventUserResponseDTO;
import com.gds.eventplanner.exception.CustomException;
import com.gds.eventplanner.repository.EventRepository;
import com.gds.eventplanner.repository.EventUserResponseRepository;
import com.gds.eventplanner.service.EventPlannerService;
import com.gds.eventplanner.utils.Constants;

/**
 * Implementation layer to implement the service contacts related to event
 * 
 * @author Shashi
 *
 */
@Service
public class EventPlannerServiceImpl implements EventPlannerService {

	private static final long EVENT_SECRET_RANGE = 10000L;
	
	private final EventRepository eventRepository;
	private final EventUserResponseRepository eventUserResponseRepository;
	
	public EventPlannerServiceImpl(EventRepository eventRepository, EventUserResponseRepository eventUserResponseRepository) {
		this.eventRepository = eventRepository;
		this.eventUserResponseRepository = eventUserResponseRepository;
	}

	/**
	 * Method to create event class to persist the event details into database
	 * 
	 */
	@Override
	@Transactional
	public CreateEventResponseDTO createEvent(CreateEventRequestDTO createEventRequestDTO) {
		Event event = Event.builder()
				.eventName(createEventRequestDTO.getEventName())
				.organizerName(createEventRequestDTO.getOrganizerName())
				.organizerEmail(createEventRequestDTO.getOrganizerEmail())
				.eventSecret(Double.valueOf(Math.random() * EVENT_SECRET_RANGE).longValue()) // Generating event secret and storing database
				.sessionStatus(Status.NEW)
				.build();
		
		Event persistedEvent = eventRepository.save(event); // persists event details into db
		if(persistedEvent != null) { // checking if persist successful or not

			return CreateEventResponseDTO.builder()
					.eventId(persistedEvent.getId())
					.eventSecret(persistedEvent.getEventSecret())
					.sessionStatus(persistedEvent.getSessionStatus()).build();
		}
		
		return null;
	}

	/**
	 * Update event status in database - start session / close session
	 * 
	 */
	@Override
	@Transactional
	public EventResponseDTO updateEventStatus(StatusChangeRequestDTO statusChangeRequestDTO) throws CustomException {
		Optional<Event> existingEventOptn = eventRepository.findById(statusChangeRequestDTO.getEventId());
		
		if(!existingEventOptn.isPresent()) {
			throw new CustomException(Constants.NOT_FOUND, "Invalid event id", HttpStatus.NOT_FOUND);
		}
		
		Event existingEvent = existingEventOptn.get();
		if(!existingEvent.getEventSecret().equals(statusChangeRequestDTO.getEventSecret())) {
			throw new CustomException(Constants.BAD_REQUEST, "Invalid event secret passed", HttpStatus.BAD_REQUEST);
		}
		
		if(existingEvent.getSessionStatus().equals(Status.CLOSED)) {
			throw new CustomException(Constants.BAD_REQUEST, "The session already closed", HttpStatus.FAILED_DEPENDENCY);
		}
		
		String pickedResponse = existingEvent.getPickedResponse();
		// Finalizing the response randomly from all the responses on closing the session
		if(statusChangeRequestDTO.getStatus().equals(Status.CLOSED)) {
			List<EventUserResponse> eventUserResponses = eventUserResponseRepository.getAllUserResponsesByEventId(existingEvent)
					.stream().filter(userRes -> userRes.getResponse() != null).collect(Collectors.toList());
			if(eventUserResponses != null && eventUserResponses.size() > 0) {
				pickedResponse = eventUserResponses.get((int) ((Math.random() * (eventUserResponses.size())))).getResponse();
			}
			
		}
		Event updatedEvent = eventRepository.save(Event.builder()
				.id(existingEvent.getId())
				.eventName(existingEvent.getEventName())
				.organizerName(existingEvent.getOrganizerName())
				.organizerEmail(existingEvent.getOrganizerEmail())
				.eventSecret(existingEvent.getEventSecret())
				.sessionStatus(statusChangeRequestDTO.getStatus())
				.pickedResponse(pickedResponse)
				.build()
				);
		
		return EventResponseDTO.builder()
				.eventId(updatedEvent.getId())
				.eventName(updatedEvent.getEventName())
				.organizerName(updatedEvent.getOrganizerName())
				.sessionStatus(updatedEvent.getSessionStatus())
				.pickedResponse(updatedEvent.getPickedResponse())
				.responses(eventUserResponseRepository.getAllUserResponsesByEventId(updatedEvent)
						.stream().map(response -> EventUserResponseDTO.builder().userName(response.getUserName())
								.userEmail(response.getUserEmail()).response(response.getResponse()).build()).collect(Collectors.toList()))
				.build();
	}

	/**
	 * Method to fetch event details including all responses based on id
	 * 
	 */
	@Override
	@Transactional
	public EventResponseDTO fetchEventDetails(Long id, String emailId, Long eventSecret) throws CustomException {
		Event existingEvent = null;
		
		if(eventSecret != null) {
			existingEvent = eventRepository.findById(id).get();
			
			if(existingEvent != null && !eventSecret.equals(existingEvent.getEventSecret())) {
				throw new CustomException("Access Denied", "Unauthorized access", HttpStatus.UNAUTHORIZED);
			}

		}
		
		if(emailId != null && eventSecret == null) {		
			EventUserResponse eventUserResponse = eventUserResponseRepository.findByEventIdAndUserEmail(id, emailId);
			
			if(eventUserResponse == null) {
				throw new CustomException("Access Denied", "Please join the session to access the details", HttpStatus.UNAUTHORIZED);
			}
			
			existingEvent = eventUserResponse.getEvent();
		}

		return EventResponseDTO.builder()
				.eventId(existingEvent.getId())
				.eventName(existingEvent.getEventName())
				.organizerName(existingEvent.getOrganizerName())
				.sessionStatus(existingEvent.getSessionStatus())
				.pickedResponse(existingEvent.getPickedResponse())
				.responses(eventUserResponseRepository.getAllUserResponsesByEventId(existingEvent)
						.stream().map(response -> EventUserResponseDTO.builder().userName(response.getUserName())
								.userEmail(response.getUserEmail()).response(response.getResponse()).build()).collect(Collectors.toList()))
				.build();
	}

	/**
	 * Method to join user to event
	 * 
	 */
	@Override
	@Transactional
	public Long joinUserToEvent(JoinEventRequestDTO joinEventRequestDTO) throws CustomException {
		Event existingEvent = this.getEventIfAvailableAndActive(joinEventRequestDTO.getEventId());
		
		EventUserResponse eventUserResponse = eventUserResponseRepository.findByEventIdAndUserEmail(joinEventRequestDTO.getEventId(), joinEventRequestDTO.getUserEmail());
		
		if(eventUserResponse != null) {
			eventUserResponseRepository.save(EventUserResponse.builder()
					.eventUserResponseId(eventUserResponse.getEventUserResponseId())
					.event(existingEvent)
					.userEmail(eventUserResponse.getUserEmail())
					.userName(eventUserResponse.getUserName())
					.response(eventUserResponse.getResponse())
					.build());
			return eventUserResponse.getEventUserResponseId();
		} else {
			EventUserResponse persistedEventUserResponse = eventUserResponseRepository.save(EventUserResponse.builder()
					.event(existingEvent)
					.userEmail(joinEventRequestDTO.getUserEmail())
					.userName(joinEventRequestDTO.getUserName())
					.build());
			return persistedEventUserResponse.getEventUserResponseId();
		}
		
	}
	
	/**
	 * Method to record user response to perticular event
	 */
	@Transactional
	@Override
	public EventResponseDTO recordUserResponse(Long joinId, RecordUserResRequestDTO recordUserResRequestDTO)
			throws CustomException {
		Event existingEvent = this.getEventIfAvailableAndActive(recordUserResRequestDTO.getEventId());
		
		if(existingEvent != null) {
			int updatedCount = eventUserResponseRepository.setResponse(joinId,recordUserResRequestDTO.getUserResponse());
			if(updatedCount <= 0) {
				throw new CustomException("Access Denied", "Invalid join id. Please join the session before responding", HttpStatus.UNAUTHORIZED);
			}
			
			return EventResponseDTO.builder()
					.eventId(existingEvent.getId())
					.eventName(existingEvent.getEventName())
					.organizerName(existingEvent.getOrganizerName())
					.sessionStatus(existingEvent.getSessionStatus())
					.responses(eventUserResponseRepository.getAllUserResponsesByEventId(existingEvent)
							.stream().map(response -> EventUserResponseDTO.builder().userName(response.getUserName())
									.userEmail(response.getUserEmail()).response(response.getResponse()).build()).collect(Collectors.toList()))
					.build();
		}
		
		return null;
	}

	
	
	
	/**
	 * This method returns event if it is available and active based on eventId
	 * 
	 * @param eventId
	 * @return
	 * @throws CustomException
	 */
	private Event getEventIfAvailableAndActive(Long eventId) throws CustomException {
		
		Optional<Event> existingEventOptn = eventRepository.findById(eventId);
		
		if(!existingEventOptn.isPresent()) {
			throw new CustomException("Not found", "Invalid event id", HttpStatus.NOT_FOUND);
		}
		
		Event existingEvent = existingEventOptn.get();
		
		if(existingEvent.getSessionStatus().equals(Status.NEW)) {
			throw new CustomException("Session not active", "Session not started yet", HttpStatus.FAILED_DEPENDENCY);
		}

		if(existingEvent.getSessionStatus().equals(Status.CLOSED)) {
			throw new CustomException("Session got closed", "Session already got closed", HttpStatus.FAILED_DEPENDENCY);
		}
		
		if(existingEvent.getSessionStatus().equals(Status.ACTIVE)) {
			return existingEvent;
		}
		
		return null;
	}

}
