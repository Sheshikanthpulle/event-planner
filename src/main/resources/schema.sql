--liquibase formatted sql
        
--changeset nvoxland:2
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** ednesday-January-31-2024   
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO ***  event_id_seq
-- SQLINES DEMO *** ------------------------------------

   CREATE SEQUENCE  "GDS_ADMIN"."event_id_seq"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO ***  event_user_reponse_id_seq
-- SQLINES DEMO *** ------------------------------------

   CREATE SEQUENCE  "GDS_ADMIN"."event_user_reponse_id_seq"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 21 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** ent
-- SQLINES DEMO *** ------------------------------------

  -- SQLINES LICENSE FOR EVALUATION USE ONLY
  CREATE TABLE "GDS_ADMIN"."event" 
   (	"event_id" DECIMAL(19,0), 
	"event_name" VARCHAR(100), 
	"event_secret" DECIMAL(19,0), 
	"finalized_response" VARCHAR(100), 
	"organizer_email" VARCHAR(100), 
	"organizer_name" VARCHAR(100), 
	"session_status" VARCHAR(10)
   ) 
   ;
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** ent_user_response
-- SQLINES DEMO *** ------------------------------------

  -- SQLINES LICENSE FOR EVALUATION USE ONLY
  CREATE TABLE "GDS_ADMIN"."event_user_response" 
   (	"event_user_response_id" DECIMAL(19,0), 
	"response" VARCHAR(100), 
	"user_email" VARCHAR(100), 
	"user_name" VARCHAR(100), 
	"event_event_id" DECIMAL(19,0)
   ) 
   ;
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** ent
-- SQLINES DEMO *** ------------------------------------

  -- SQLINES LICENSE FOR EVALUATION USE ONLY
  CREATE TABLE "GDS_ADMIN"."event" 
   (	"event_id" DECIMAL(19,0), 
	"event_name" VARCHAR(100), 
	"event_secret" DECIMAL(19,0), 
	"finalized_response" VARCHAR(100), 
	"organizer_email" VARCHAR(100), 
	"organizer_name" VARCHAR(100), 
	"session_status" VARCHAR(10)
   ) 
   ;
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** Table event
-- SQLINES DEMO *** ------------------------------------

  ALTER TABLE "GDS_ADMIN"."event" MODIFY ("event_id" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event" MODIFY ("event_name" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event" MODIFY ("event_secret" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event" MODIFY ("organizer_email" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event" MODIFY ("organizer_name" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event" MODIFY ("session_status" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event" ADD PRIMARY KEY ("event_id")
   ;
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** Table event_user_response
-- SQLINES DEMO *** ------------------------------------

  ALTER TABLE "GDS_ADMIN"."event_user_response" MODIFY ("event_user_response_id" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event_user_response" MODIFY ("user_email" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event_user_response" MODIFY ("user_name" NOT NULL ENABLE);
  ALTER TABLE "GDS_ADMIN"."event_user_response" ADD PRIMARY KEY ("event_user_response_id")
   ;
