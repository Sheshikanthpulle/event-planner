package com.gds.eventplanner.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO layer object to user response record API
 * 
 * @author Shashi
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinEventRequestDTO {
	private Long eventId;
	private String userName;
	private String userEmail;
}
