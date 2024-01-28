package com.gds.eventplanner.dto;

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
public class UserResponseDTO {
	private Long eventId;
	private String userName;
	private String userEmail;
	private String response;
}
