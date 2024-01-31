package com.gds.eventplanner.service;

import com.gds.eventplanner.dto.request.CreateEventRequestDTO;
import com.gds.eventplanner.dto.request.JoinEventRequestDTO;
import com.gds.eventplanner.dto.request.RecordUserResRequestDTO;
import com.gds.eventplanner.dto.request.StatusChangeRequestDTO;
import com.gds.eventplanner.dto.response.CreateEventResponseDTO;
import com.gds.eventplanner.dto.response.EventResponseDTO;
import com.gds.eventplanner.exception.CustomException;

/**
 * EventService is a service layer interface to define the service contracts related to Event
 * 
 * @author Shashi
 *
 */
public interface EventPlannerService {
	public CreateEventResponseDTO createEvent(CreateEventRequestDTO createEventRequestDTO);
	public EventResponseDTO updateEventStatus(StatusChangeRequestDTO statusChangeRequestDTO) throws CustomException;
	public EventResponseDTO fetchEventDetails(Long id, String emailId, Long eventSecret) throws CustomException;
	public Long joinUserToEvent(JoinEventRequestDTO joinEventRequestDTO) throws CustomException;
	public EventResponseDTO recordUserResponse(Long joinId, RecordUserResRequestDTO recordUserResRequestDTO) throws CustomException;

}
