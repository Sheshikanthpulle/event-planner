package com.gds.eventplanner.dto;

import com.gds.eventplanner.domain.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO layer object to status update API
 * 
 * @author Shashi
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusChangeRequestDTO {
	
	private Long eventId;
	private Long eventSecret;
	private Status status;

}
