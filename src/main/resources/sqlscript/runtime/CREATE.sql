-- Database configuration (commented out - these are typically set by PostgreSQL automatically)
-- PostgreSQL-specific settings would be configured at the database/server level

-- User and schema setup (PostgreSQL equivalent)
-- Note: In PostgreSQL, schemas are created differently and SA user may not be needed

-- CREATE SCHEMA IF NOT EXISTS public AUTHORIZATION current_user;

-- Version table
CREATE TABLE version (
  id SERIAL PRIMARY KEY,
  actualversion INTEGER,
  updatedate TIMESTAMP,
  updatecomment VARCHAR(255)
);

-- Messages table
CREATE TABLE messages (
  messageid VARCHAR(255) PRIMARY KEY,
  initdateutc TIMESTAMP,
  senddateutc TIMESTAMP,
  direction INTEGER,
  rawfilename VARCHAR(512),
  state INTEGER,
  signature INTEGER,
  encryption INTEGER,
  senderid VARCHAR(255),
  receiverid VARCHAR(255),
  syncmdn INTEGER,
  headerfilename VARCHAR(512),
  rawdecryptedfilename VARCHAR(512),
  senderhost VARCHAR(255),
  useragent VARCHAR(255),
  contentmic VARCHAR(255),
  msgcompression INTEGER DEFAULT 0 NOT NULL,
  messagetype INTEGER DEFAULT 1 NOT NULL,
  asyncmdnurl VARCHAR(512),
  msgsubject VARCHAR(255),
  resendcounter INTEGER DEFAULT 0 NOT NULL,
  userdefinedid VARCHAR(255),
  secureconnection INTEGER DEFAULT 0 NOT NULL
);

CREATE INDEX idx_messages_initdate ON messages(initdateutc);
CREATE INDEX idx_messages_contentmic ON messages(contentmic);
CREATE INDEX idx_messages_state ON messages(state);

-- MDN table
CREATE TABLE mdn (
  messageid VARCHAR(255) PRIMARY KEY,
  relatedmessageid VARCHAR(255),
  initdateutc TIMESTAMP,
  direction INTEGER,
  rawfilename VARCHAR(512),
  state INTEGER,
  signature INTEGER,
  senderid VARCHAR(255),
  receiverid VARCHAR(255),
  headerfilename VARCHAR(512),
  senderhost VARCHAR(255),
  useragent VARCHAR(255),
  mdntext TEXT,
  dispositionstate VARCHAR(255),
  FOREIGN KEY(relatedmessageid) REFERENCES messages(messageid)
);

CREATE INDEX idx_mdn_initdate ON mdn(initdateutc);

-- Message log table
CREATE TABLE messagelog (
  id SERIAL PRIMARY KEY,
  messageid VARCHAR(255),
  timestamputc TIMESTAMP,
  loglevel INTEGER,
  details TEXT,
  FOREIGN KEY(messageid) REFERENCES messages(messageid)
);

CREATE INDEX idx_messagelog_messageid ON messagelog(messageid);

-- Payload table
CREATE TABLE payload (
  id SERIAL PRIMARY KEY,
  messageid VARCHAR(255),
  originalfilename VARCHAR(512),
  payloadfilename VARCHAR(512),
  contentid VARCHAR(255),
  contenttype VARCHAR(255),
  FOREIGN KEY(messageid) REFERENCES messages(messageid)
);

CREATE INDEX idx_payload_messageid ON payload(messageid);

-- Tracker message table (drop first if exists from previous run)
DROP TABLE IF EXISTS tracker_auth_failure CASCADE;
DROP TABLE IF EXISTS tracker_message CASCADE;

CREATE TABLE tracker_message (
  id SERIAL PRIMARY KEY,
  messageid VARCHAR(255) NOT NULL,
  tracker_id VARCHAR(255) UNIQUE NOT NULL,
  remote_addr VARCHAR(255),
  user_agent VARCHAR(512),
  content_type VARCHAR(255),
  content_size INTEGER NOT NULL,
  initdateutc TIMESTAMP NOT NULL,
  auth_status INTEGER DEFAULT 0,
  auth_user VARCHAR(255),
  rawfilename VARCHAR(512) NOT NULL,
  request_headers TEXT
);

CREATE INDEX idx_tracker_message_id ON tracker_message(messageid);
CREATE INDEX idx_tracker_tracker_id ON tracker_message(tracker_id);
CREATE INDEX idx_tracker_initdate ON tracker_message(initdateutc);
CREATE INDEX idx_tracker_auth_status ON tracker_message(auth_status);
CREATE INDEX idx_tracker_remote_addr ON tracker_message(remote_addr);

-- Authentication failure tracking table
CREATE TABLE tracker_auth_failure (
  id SERIAL PRIMARY KEY,
  remote_addr VARCHAR(255) NOT NULL,
  failure_time TIMESTAMP NOT NULL,
  user_agent VARCHAR(512),
  attempted_user VARCHAR(255)
);

CREATE INDEX idx_tracker_auth_failure_addr ON tracker_auth_failure(remote_addr);
CREATE INDEX idx_tracker_auth_failure_time ON tracker_auth_failure(failure_time);

-- Login authentication failure tracking table
DROP TABLE IF EXISTS login_auth_failure CASCADE;

CREATE TABLE login_auth_failure (
  id SERIAL PRIMARY KEY,
  remote_addr VARCHAR(255) NOT NULL,
  failure_time TIMESTAMP NOT NULL,
  attempted_user VARCHAR(255),
  source VARCHAR(50),
  user_agent VARCHAR(512)
);

CREATE INDEX idx_login_auth_failure_addr ON login_auth_failure(remote_addr);
CREATE INDEX idx_login_auth_failure_time ON login_auth_failure(failure_time);
CREATE INDEX idx_login_auth_failure_source ON login_auth_failure(source);

-- Statistics overview table
CREATE TABLE statisticoverview (
  relationshipid VARCHAR(255) PRIMARY KEY,
  localstationid VARCHAR(255),
  partnerid VARCHAR(255),
  sendmessagecount INTEGER,
  receivedmessagecount INTEGER,
  sendwithfailurecount INTEGER,
  receivedwithfailurecount INTEGER,
  resetdateutc TIMESTAMP
);

CREATE INDEX idx_statisticoverview_localstationid ON statisticoverview(localstationid);
CREATE INDEX idx_statisticoverview_partnerid ON statisticoverview(partnerid);

-- Statistics details table
CREATE TABLE statisticdetails (
  id SERIAL PRIMARY KEY,
  localstation VARCHAR(255),
  partner VARCHAR(255),
  mdndate BIGINT,
  messageid VARCHAR(255),
  direction INTEGER,
  messagestate INTEGER
);

CREATE INDEX idx_statisticdetails_localstation ON statisticdetails(localstation);
CREATE INDEX idx_statisticdetails_partner ON statisticdetails(partner);
CREATE INDEX idx_statisticdetails_mdndate ON statisticdetails(mdndate);
CREATE INDEX idx_statisticdetails_direction ON statisticdetails(direction);
CREATE INDEX idx_statisticdetails_messagestate ON statisticdetails(messagestate);
CREATE INDEX idx_statisticdetails_mixed ON statisticdetails(messagestate,localstation,partner,direction);

-- Server statistics table
CREATE TABLE serverstatistic (
  id SERIAL PRIMARY KEY,
  counter INTEGER DEFAULT 0 NOT NULL,
  serverid VARCHAR(255) NOT NULL,
  direction INTEGER DEFAULT 0 NOT NULL,
  sign INTEGER DEFAULT 0 NOT NULL,
  encrypt INTEGER DEFAULT 0 NOT NULL,
  msgcompression INTEGER DEFAULT 0 NOT NULL,
  lastgood BIGINT DEFAULT 0 NOT NULL
);

CREATE INDEX idx_serverstatistic_serverid ON serverstatistic(serverid);

-- CEM (Certificate Exchange Messaging) table
CREATE TABLE cem (
  id SERIAL PRIMARY KEY,
  initiatoras2id VARCHAR(255),
  receiveras2id VARCHAR(255),
  requestid VARCHAR(255),
  requestmessageid VARCHAR(255),
  responsemessageid VARCHAR(255),
  respondbydate BIGINT,
  requestmessageoriginated BIGINT,
  responsemessageoriginated BIGINT,
  category INTEGER,
  cemstate INTEGER,
  serialid VARCHAR(255),
  issuername VARCHAR(255),
  processed INTEGER DEFAULT 0 NOT NULL,
  processdate BIGINT,
  reasonforrejection TEXT
);

-- Send order table
CREATE TABLE sendorder (
  id SERIAL PRIMARY KEY,
  scheduletime BIGINT NOT NULL,
  nextexecutiontime BIGINT NOT NULL,
  sendorder BYTEA NOT NULL,
  orderstate INTEGER NOT NULL
);

-- Module lock table
CREATE TABLE modulelock (
  modulename VARCHAR(255) PRIMARY KEY,
  startlockmillis BIGINT NOT NULL,
  refreshlockmillies BIGINT NOT NULL,
  clientip VARCHAR(255) NOT NULL,
  clientid VARCHAR(255) NOT NULL,
  username VARCHAR(255) NOT NULL,
  clientpid VARCHAR(255)
);

CREATE INDEX idx_refreshlockmillies ON modulelock(refreshlockmillies);

-- Processing event queue table
CREATE TABLE processingeventqueue (
  messageid VARCHAR(255) PRIMARY KEY,
  mdnid VARCHAR(255),
  eventtype INTEGER NOT NULL,
  processtype INTEGER NOT NULL,
  initdate BIGINT NOT NULL,
  parameterlist VARCHAR(2048) NOT NULL
);

CREATE INDEX idx_processingeventqueue_initdate ON processingeventqueue(initdate);

-- High availability table
CREATE TABLE highavail (
  id SERIAL PRIMARY KEY,
  uniqueid VARCHAR(8) NOT NULL,
  localip VARCHAR(45) NOT NULL,
  publicip VARCHAR(45),
  cloudinstanceid VARCHAR(65),
  hostname VARCHAR(128) NOT NULL,
  clientcount INTEGER DEFAULT 0 NOT NULL,
  operationsystem VARCHAR(128) NOT NULL,
  productversion VARCHAR(128) NOT NULL,
  starttime BIGINT NOT NULL,
  lastupdatetime BIGINT NOT NULL
);

CREATE INDEX idx_highavail_uniqueid ON highavail(uniqueid);
CREATE INDEX idx_highavail_starttime ON highavail(starttime);
CREATE INDEX idx_highavail_lastupdatetime ON highavail(lastupdatetime);

INSERT INTO version VALUES(DEFAULT,0,'2025-05-23 09:47:07.680000','mend-as2 1.0');

