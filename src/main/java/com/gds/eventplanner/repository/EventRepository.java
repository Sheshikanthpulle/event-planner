package com.gds.eventplanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gds.eventplanner.domain.Event;
/**
 * JPA Repository to communicate with the database
 * 
 * This class helps to persist and retrieves data from database for the respective entity (@Event) 
 * 
 * @author Shashi
 *
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}
