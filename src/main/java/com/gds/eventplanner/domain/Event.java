package com.gds.eventplanner.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Event domain class which reflects the event table in database
 * 
 * @author Shashi
 *
 */
@Entity
@Table(name = "EVENT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EventSeqGenerator")
	@SequenceGenerator(name = "EventSeqGenerator", sequenceName = "EVENT_ID_SEQ", allocationSize = 1)
	private Long eventId;
	
	@Column(name = "EVENT_NAME", length = 100, nullable = false)
	private String eventName;

	@Column(name = "ORGANIZER_NAME", length = 100, nullable = false)
	private String organizerName;
	
	@Column(name = "ORGANIZER_EMAIL", length = 100, nullable = false)
	private String organizerEmail;
	
	@Column(name = "EVENT_SECRET", nullable = false)
	private Long eventSecret;
	
	@Column(name = "FINALIZED_RESPONSE", length = 100, nullable = true)
	private String finalizedResponse;
	
	@Column(name = "SESSION_STATUS", length = 10, nullable = false)
	@Enumerated(EnumType.STRING)
	private Status sessionStatus;
	
	@OneToMany(mappedBy = "event")
    @JsonIgnoreProperties(value = { "event" }, allowSetters = true)
    private Set<EventUserResponse> eventUserResponses;
	
}
