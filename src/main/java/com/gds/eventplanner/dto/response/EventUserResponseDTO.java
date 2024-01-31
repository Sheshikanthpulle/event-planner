package com.gds.eventplanner.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventUserResponseDTO {
	private String userName;
	private String userEmail;
	private String response;
}
