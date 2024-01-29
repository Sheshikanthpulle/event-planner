package com.gds.eventplanner.service.impl;

import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gds.eventplanner.domain.Event;
import com.gds.eventplanner.domain.EventUserResponse;
import com.gds.eventplanner.domain.Status;
import com.gds.eventplanner.dto.CreateEventRequestDTO;
import com.gds.eventplanner.dto.CreateEventResponseDTO;
import com.gds.eventplanner.dto.EventDTO;
import com.gds.eventplanner.dto.EventUserResponseDTO;
import com.gds.eventplanner.dto.StatusChangeRequestDTO;
import com.gds.eventplanner.dto.UserResponseDTO;
import com.gds.eventplanner.exception.CustomException;
import com.gds.eventplanner.repository.EventRepository;
import com.gds.eventplanner.repository.EventUserResponseRepository;
import com.gds.eventplanner.service.EventPlannerService;

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
					.eventId(persistedEvent.getEventId())
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
	public EventDTO updateEventStatus(StatusChangeRequestDTO statusChangeRequestDTO) throws CustomException {
		Optional<Event> existingEventOptn = eventRepository.findById(statusChangeRequestDTO.getEventId());
		
		if(!existingEventOptn.isPresent()) {
			throw new CustomException("Not found", "Invalid event id", HttpStatus.NOT_FOUND);
		}
		
		Event existingEvent = existingEventOptn.get();
		if(!existingEvent.getEventSecret().equals(statusChangeRequestDTO.getEventSecret())) {
			throw new CustomException("Bad Request", "Invalid event secret passed", HttpStatus.BAD_REQUEST);
		}
		
		String finalizedResponse = existingEvent.getFinalizedResponse();
		// Finalizing the response randomly from all the responses on closing the session
		if(statusChangeRequestDTO.getStatus().equals(Status.CLOSE)) {
			List<EventUserResponse> eventUserResponses = eventUserResponseRepository.getAllUserResponsesByEventId(existingEvent)
					.stream().filter(userRes -> userRes.getResponse() != null).collect(Collectors.toList());
			if(eventUserResponses != null && eventUserResponses.size() > 0) {
				finalizedResponse = eventUserResponses.get((int) ((Math.random() * (eventUserResponses.size())))).getResponse();
			}
			
		}
		Event updatedEvent = eventRepository.save(Event.builder()
				.eventId(existingEvent.getEventId())
				.eventName(existingEvent.getEventName())
				.organizerName(existingEvent.getOrganizerName())
				.organizerEmail(existingEvent.getOrganizerEmail())
				.eventSecret(existingEvent.getEventSecret())
				.sessionStatus(statusChangeRequestDTO.getStatus())
				.finalizedResponse(finalizedResponse)
				.build()
				);
		
		return EventDTO.builder()
				.eventId(updatedEvent.getEventId())
				.eventName(updatedEvent.getEventName())
				.organizerName(updatedEvent.getOrganizerName())
				.sessionStatus(updatedEvent.getSessionStatus())
				.finalizedResponse(updatedEvent.getFinalizedResponse())
				.responses(eventUserResponseRepository.getAllUserResponsesByEventId(updatedEvent)
						.stream().map(response -> EventUserResponseDTO.builder().userName(response.getUserName())
								.response(response.getResponse()).build()).collect(Collectors.toList()))
				.build();
	}

	/**
	 * Method to fetch event details including all responses based on id
	 * 
	 */
	@Override
	@Transactional
	public EventDTO fetchEventDetails(Long id) throws CustomException {
		Optional<Event> existingEventOptn = eventRepository.findById(id);
		
		if(!existingEventOptn.isPresent()) {
			throw new CustomException("Not found", "Invalid event id", HttpStatus.NOT_FOUND);
		}
		
		Event existingEvent = existingEventOptn.get();
		return EventDTO.builder()
				.eventId(existingEvent.getEventId())
				.eventName(existingEvent.getEventName())
				.organizerName(existingEvent.getOrganizerName())
				.sessionStatus(existingEvent.getSessionStatus())
				.finalizedResponse(existingEvent.getFinalizedResponse())
				.responses(eventUserResponseRepository.getAllUserResponsesByEventId(existingEvent)
						.stream().map(response -> EventUserResponseDTO.builder().userName(response.getUserName())
								.response(response.getResponse()).build()).collect(Collectors.toList()))
				.build();
	}

	/**
	 * Method to record user response in cache
	 * 
	 */
	@Override
	@Transactional
	public EventDTO recordUserResponse(UserResponseDTO userResponseDTO) throws CustomException {
		Optional<Event> existingEventOptn = eventRepository.findById(userResponseDTO.getEventId());
		
		if(!existingEventOptn.isPresent()) {
			throw new CustomException("Not found", "Invalid event id", HttpStatus.NOT_FOUND);
		}
		
		Event existingEvent = existingEventOptn.get();
		
		if(existingEvent.getSessionStatus().equals(Status.NEW)) {
			throw new CustomException("Session not active", "Session not started yet", HttpStatus.FAILED_DEPENDENCY);
		}

		if(existingEvent.getSessionStatus().equals(Status.CLOSE)) {
			throw new CustomException("Session got closed", "Session already got closed", HttpStatus.FAILED_DEPENDENCY);
		}
		
		EventUserResponse eventUserResponse = eventUserResponseRepository.getUserResponse(existingEvent,userResponseDTO.getUserEmail());
		
		if(eventUserResponse != null) {
			eventUserResponseRepository.save(EventUserResponse.builder()
					.eventUserResponseId(eventUserResponse.getEventUserResponseId())
					.event(existingEvent)
					.userEmail(eventUserResponse.getUserEmail())
					.userName(eventUserResponse.getUserName())
					.response(userResponseDTO.getResponse())
					.build());
		} else {
			eventUserResponseRepository.save(EventUserResponse.builder()
					.event(existingEvent)
					.userEmail(userResponseDTO.getUserEmail())
					.userName(userResponseDTO.getUserName())
					.response(userResponseDTO.getResponse())
					.build());
		}
		
		return EventDTO.builder()
				.eventId(existingEvent.getEventId())
				.eventName(existingEvent.getEventName())
				.organizerName(existingEvent.getOrganizerName())
				.sessionStatus(existingEvent.getSessionStatus())
				.finalizedResponse(existingEvent.getFinalizedResponse())
				.responses(eventUserResponseRepository.getAllUserResponsesByEventId(existingEvent)
						.stream().map(response -> EventUserResponseDTO.builder().userName(response.getUserName())
								.response(response.getResponse()).build()).collect(Collectors.toList()))
				.build();
	}

}
