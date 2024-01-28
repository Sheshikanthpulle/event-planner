package com.gds.eventplanner.dto;

import com.gds.eventplanner.domain.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO to create event API
 * 
 * @author Shashi
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventResponseDTO {
	private Long eventId;
	private Long eventSecret;
	private Status sessionStatus;
}
