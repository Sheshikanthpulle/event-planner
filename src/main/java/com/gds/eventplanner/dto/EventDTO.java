package com.gds.eventplanner.dto;

import java.util.List;

import com.gds.eventplanner.domain.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event details response DTO
 * 
 * @author Shashi
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDTO {
	private Long eventId;
	private String eventName;
	private String organizerName;
	private Status sessionStatus;
	private String finalizedResponse;
	private List<EventUserResponseDTO> responses;
}
