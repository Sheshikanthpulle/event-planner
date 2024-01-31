package com.gds.eventplanner.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO to create event API
 * 
 * @author Shashi
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequestDTO {
	private String eventName;
	private String organizerName;
	private String organizerEmail;
}
