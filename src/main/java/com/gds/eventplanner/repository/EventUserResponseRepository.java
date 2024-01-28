package com.gds.eventplanner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gds.eventplanner.domain.Event;
import com.gds.eventplanner.domain.EventUserResponse;

public interface EventUserResponseRepository extends JpaRepository<EventUserResponse, Long> {
	
	@Query("select eur from EventUserResponse eur where eur.event = :event and eur.userEmail = :userEmail")
	public EventUserResponse getUserResponse(Event event, String userEmail);
	
	@Query("select eur from EventUserResponse eur where eur.event = :event")
	public List<EventUserResponse> getAllUserResponsesByEventId(Event event);

}
