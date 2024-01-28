package com.gds.eventplanner.service;

import com.gds.eventplanner.dto.CreateEventRequestDTO;
import com.gds.eventplanner.dto.CreateEventResponseDTO;
import com.gds.eventplanner.dto.EventDTO;
import com.gds.eventplanner.dto.StatusChangeRequestDTO;
import com.gds.eventplanner.dto.UserResponseDTO;
import com.gds.eventplanner.exception.CustomException;

/**
 * EventService is a service layer interface to define the service contracts related to Event
 * 
 * @author Shashi
 *
 */
public interface EventPlannerService {
	public CreateEventResponseDTO createEvent(CreateEventRequestDTO createEventRequestDTO);
	public EventDTO updateEventStatus(StatusChangeRequestDTO statusChangeRequestDTO) throws CustomException;
	public EventDTO fetchEventDetails(Long id) throws CustomException;
	public EventDTO recordUserResponse(UserResponseDTO userResonseDTO) throws CustomException;

}
