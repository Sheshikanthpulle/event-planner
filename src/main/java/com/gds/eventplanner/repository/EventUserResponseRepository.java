package com.gds.eventplanner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gds.eventplanner.domain.Event;
import com.gds.eventplanner.domain.EventUserResponse;

public interface EventUserResponseRepository extends JpaRepository<EventUserResponse, Long>, JpaSpecificationExecutor<EventUserResponse>  {
		
	@Query("select eur from EventUserResponse eur where eur.event = :event")
	public List<EventUserResponse> getAllUserResponsesByEventId(Event event);
	
	public EventUserResponse findByEventIdAndUserEmail(Long eventId, String userEmail);
	
	@Modifying
	@Query("update EventUserResponse eur set eur.response = :response where eur.eventUserResponseId = :id")
	int setResponse( @Param("id") Long id, @Param("response") String response);

}
