--liquibase formatted sql
        
--changeset eventplanner:1

--------------------------------------------------------
--  File created - Wednesday-January-31-2024   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence event_id_seq
--------------------------------------------------------

   CREATE SEQUENCE  event_id_seq  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Sequence event_user_reponse_id_seq
--------------------------------------------------------

   CREATE SEQUENCE  event_user_reponse_id_seq  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Table event
--------------------------------------------------------

  CREATE TABLE event 
   (	id NUMBER(19,0) DEFAULT event_id_seq.NEXTVAL, 
	event_name VARCHAR2(100 CHAR), 
	event_secret NUMBER(19,0), 
	picked_response VARCHAR2(100 CHAR), 
	organizer_email VARCHAR2(100 CHAR), 
	organizer_name VARCHAR2(100 CHAR), 
	session_status VARCHAR2(10 CHAR)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE USERS 
  ;
--------------------------------------------------------
--  DDL for Table event_user_response
--------------------------------------------------------

  CREATE TABLE event_user_response 
   (	event_user_response_id NUMBER(19,0)  DEFAULT event_user_reponse_id_seq.NEXTVAL, 
	response VARCHAR2(100 CHAR), 
	user_email VARCHAR2(100 CHAR), 
	user_name VARCHAR2(100 CHAR), 
	event_id NUMBER(19,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE USERS 
  ;

--------------------------------------------------------
--  Constraints for Table event
--------------------------------------------------------

  ALTER TABLE event MODIFY (id NOT NULL ENABLE);
  ALTER TABLE event MODIFY (event_name NOT NULL ENABLE);
  ALTER TABLE event MODIFY (event_secret NOT NULL ENABLE);
  ALTER TABLE event MODIFY (organizer_email NOT NULL ENABLE);
  ALTER TABLE event MODIFY (organizer_name NOT NULL ENABLE);
  ALTER TABLE event MODIFY (session_status NOT NULL ENABLE);
  ALTER TABLE event ADD PRIMARY KEY (id)
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE USERS  ENABLE
  ;
--------------------------------------------------------
--  Constraints for Table event_user_response
--------------------------------------------------------

  ALTER TABLE event_user_response MODIFY (event_user_response_id NOT NULL ENABLE);
  ALTER TABLE event_user_response MODIFY (user_email NOT NULL ENABLE);
  ALTER TABLE event_user_response MODIFY (user_name NOT NULL ENABLE);
  ALTER TABLE event_user_response ADD PRIMARY KEY (event_user_response_id)
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE USERS  ENABLE
  ;

  --------------------------------------------------------
--  Ref Constraints for Table EVENT_USER_RESPONSE
--------------------------------------------------------

  ALTER TABLE EVENT_USER_RESPONSE ADD CONSTRAINT EVENT_USER_RESPONSE_FK1 FOREIGN KEY (EVENT_ID)
	  REFERENCES EVENT (ID) ENABLE
     ;
