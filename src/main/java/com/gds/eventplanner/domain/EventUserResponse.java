package com.gds.eventplanner.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
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
 * Event user response domain class which reflects the event_user_response table in database
 * 
 * @author Shashi
 *
 */
@Entity
@Table(name = "EVENT_USER_RESPONSE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventUserResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EventUserResponseIdSeqGenerator")
	@SequenceGenerator(name = "EventUserResponseIdSeqGenerator", sequenceName = "EVENT_USER_REPONSE_ID_SEQ", allocationSize = 1)
	private Long eventUserResponseId;
	
	@Column(name = "USER_NAME", length = 100, nullable = false)
	private String userName;

	@Column(name = "USER_EMAIL", length = 100, nullable = false)
	private String userEmail;
	
	@Column(name = "RESPONSE", length = 100, nullable = true)
	private String response;
	
	@ManyToOne
    @JsonIgnoreProperties(value = { "eventUserResponses" }, allowSetters = true)
    private Event event;
}