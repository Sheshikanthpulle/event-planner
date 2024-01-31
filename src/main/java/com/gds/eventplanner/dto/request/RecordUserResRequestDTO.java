package com.gds.eventplanner.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO layer object to record user response
 * @author Shashi
 *
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordUserResRequestDTO {
	private Long eventId;
	private String userResponse;
}
