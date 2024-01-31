package com.gds.eventplanner.dto.response;

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
public class EventResponseDTO {
	private Long eventId;
	private String eventName;
	private String organizerName;
	private Status sessionStatus;
	private String pickedResponse;
	private List<EventUserResponseDTO> responses;
}
