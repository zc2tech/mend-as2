-- MySQL/MariaDB Runtime Database Schema
-- Converted from PostgreSQL version

-- Version table
CREATE TABLE version (
  id INT AUTO_INCREMENT PRIMARY KEY,
  actualversion INT,
  updatedate TIMESTAMP NULL,
  updatecomment VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Messages table
CREATE TABLE messages (
  messageid VARCHAR(255) PRIMARY KEY,
  initdateutc TIMESTAMP NULL,
  senddateutc TIMESTAMP NULL,
  direction INT,
  rawfilename VARCHAR(512),
  state INT,
  signature INT,
  encryption INT,
  senderid VARCHAR(255),
  receiverid VARCHAR(255),
  syncmdn INT,
  headerfilename VARCHAR(512),
  rawdecryptedfilename VARCHAR(512),
  senderhost VARCHAR(255),
  useragent VARCHAR(255),
  contentmic VARCHAR(255),
  msgcompression INT DEFAULT 0 NOT NULL,
  messagetype INT DEFAULT 1 NOT NULL,
  asyncmdnurl VARCHAR(512),
  msgsubject VARCHAR(255),
  resendcounter INT DEFAULT 0 NOT NULL,
  userdefinedid VARCHAR(255),
  secureconnection INT DEFAULT 0 NOT NULL,
  owner_user_id INT DEFAULT 0 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_messages_initdate ON messages(initdateutc);
CREATE INDEX idx_messages_contentmic ON messages(contentmic);
CREATE INDEX idx_messages_state ON messages(state);
CREATE INDEX idx_messages_owner_user ON messages(owner_user_id);

-- MDN table
CREATE TABLE mdn (
  messageid VARCHAR(255) PRIMARY KEY,
  relatedmessageid VARCHAR(255),
  initdateutc TIMESTAMP NULL,
  direction INT,
  rawfilename VARCHAR(512),
  state INT,
  signature INT,
  senderid VARCHAR(255),
  receiverid VARCHAR(255),
  headerfilename VARCHAR(512),
  senderhost VARCHAR(255),
  useragent VARCHAR(255),
  mdntext TEXT,
  dispositionstate VARCHAR(255),
  FOREIGN KEY(relatedmessageid) REFERENCES messages(messageid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_mdn_initdate ON mdn(initdateutc);

-- Message log table
CREATE TABLE messagelog (
  id INT AUTO_INCREMENT PRIMARY KEY,
  messageid VARCHAR(255),
  timestamputc TIMESTAMP NULL,
  loglevel INT,
  details TEXT,
  FOREIGN KEY(messageid) REFERENCES messages(messageid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_messagelog_messageid ON messagelog(messageid);

-- Payload table
CREATE TABLE payload (
  id INT AUTO_INCREMENT PRIMARY KEY,
  messageid VARCHAR(255),
  originalfilename VARCHAR(512),
  payloadfilename VARCHAR(512),
  contentid VARCHAR(255),
  contenttype VARCHAR(255),
  payload_format VARCHAR(50),
  payload_doctype VARCHAR(255),
  FOREIGN KEY(messageid) REFERENCES messages(messageid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_payload_messageid ON payload(messageid);
CREATE INDEX idx_payload_format ON payload(payload_format);

-- Tracker message table
DROP TABLE IF EXISTS tracker_auth_failure;
DROP TABLE IF EXISTS tracker_message;

CREATE TABLE tracker_message (
  id INT AUTO_INCREMENT PRIMARY KEY,
  messageid VARCHAR(255) NOT NULL,
  tracker_id VARCHAR(255) UNIQUE NOT NULL,
  remote_addr VARCHAR(255),
  user_agent VARCHAR(512),
  content_type VARCHAR(255),
  content_size INT NOT NULL,
  initdateutc TIMESTAMP NOT NULL,
  auth_status INT DEFAULT 0,
  auth_user VARCHAR(255),
  rawfilename VARCHAR(512) NOT NULL,
  request_headers TEXT,
  payload_count INT DEFAULT 0,
  payload_format VARCHAR(50),
  payload_doctype VARCHAR(255),
  payload_details VARCHAR(512)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tracker_message_id ON tracker_message(messageid);
CREATE INDEX idx_tracker_tracker_id ON tracker_message(tracker_id);
CREATE INDEX idx_tracker_initdate ON tracker_message(initdateutc);
CREATE INDEX idx_tracker_auth_status ON tracker_message(auth_status);
CREATE INDEX idx_tracker_remote_addr ON tracker_message(remote_addr);

-- Authentication failure tracking table
CREATE TABLE tracker_auth_failure (
  id INT AUTO_INCREMENT PRIMARY KEY,
  remote_addr VARCHAR(255) NOT NULL,
  failure_time TIMESTAMP NOT NULL,
  user_agent VARCHAR(512),
  attempted_user VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tracker_auth_failure_addr ON tracker_auth_failure(remote_addr);
CREATE INDEX idx_tracker_auth_failure_time ON tracker_auth_failure(failure_time);

-- Login authentication failure tracking table
DROP TABLE IF EXISTS login_auth_failure;

CREATE TABLE login_auth_failure (
  id INT AUTO_INCREMENT PRIMARY KEY,
  remote_addr VARCHAR(255) NOT NULL,
  failure_time TIMESTAMP NOT NULL,
  attempted_user VARCHAR(255),
  source VARCHAR(50),
  user_agent VARCHAR(512)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_login_auth_failure_addr ON login_auth_failure(remote_addr);
CREATE INDEX idx_login_auth_failure_time ON login_auth_failure(failure_time);
CREATE INDEX idx_login_auth_failure_source ON login_auth_failure(source);

-- Statistics overview table
CREATE TABLE statisticoverview (
  relationshipid VARCHAR(255) PRIMARY KEY,
  localstationid VARCHAR(255),
  partnerid VARCHAR(255),
  sendmessagecount INT,
  receivedmessagecount INT,
  sendwithfailurecount INT,
  receivedwithfailurecount INT,
  resetdateutc TIMESTAMP NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_statisticoverview_localstationid ON statisticoverview(localstationid);
CREATE INDEX idx_statisticoverview_partnerid ON statisticoverview(partnerid);

-- Statistics details table
CREATE TABLE statisticdetails (
  id INT AUTO_INCREMENT PRIMARY KEY,
  localstation VARCHAR(255),
  partner VARCHAR(255),
  mdndate BIGINT,
  messageid VARCHAR(255),
  direction INT,
  messagestate INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_statisticdetails_localstation ON statisticdetails(localstation);
CREATE INDEX idx_statisticdetails_partner ON statisticdetails(partner);
CREATE INDEX idx_statisticdetails_mdndate ON statisticdetails(mdndate);
CREATE INDEX idx_statisticdetails_direction ON statisticdetails(direction);
CREATE INDEX idx_statisticdetails_messagestate ON statisticdetails(messagestate);
CREATE INDEX idx_statisticdetails_mixed ON statisticdetails(messagestate,localstation,partner,direction);

-- Server statistics table
CREATE TABLE serverstatistic (
  id INT AUTO_INCREMENT PRIMARY KEY,
  counter INT DEFAULT 0 NOT NULL,
  serverid VARCHAR(255) NOT NULL,
  direction INT DEFAULT 0 NOT NULL,
  sign INT DEFAULT 0 NOT NULL,
  encrypt INT DEFAULT 0 NOT NULL,
  msgcompression INT DEFAULT 0 NOT NULL,
  lastgood BIGINT DEFAULT 0 NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_serverstatistic_serverid ON serverstatistic(serverid);

-- CEM (Certificate Exchange Messaging) table
CREATE TABLE cem (
  id INT AUTO_INCREMENT PRIMARY KEY,
  initiatoras2id VARCHAR(255),
  receiveras2id VARCHAR(255),
  requestid VARCHAR(255),
  requestmessageid VARCHAR(255),
  responsemessageid VARCHAR(255),
  respondbydate BIGINT,
  requestmessageoriginated BIGINT,
  responsemessageoriginated BIGINT,
  category INT,
  cemstate INT,
  serialid VARCHAR(255),
  issuername VARCHAR(255),
  processed INT DEFAULT 0 NOT NULL,
  processdate BIGINT,
  reasonforrejection TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Send order table
CREATE TABLE sendorder (
  id INT AUTO_INCREMENT PRIMARY KEY,
  scheduletime BIGINT NOT NULL,
  nextexecutiontime BIGINT NOT NULL,
  sendorder LONGBLOB NOT NULL,
  orderstate INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Module lock table
CREATE TABLE modulelock (
  modulename VARCHAR(255) PRIMARY KEY,
  startlockmillis BIGINT NOT NULL,
  refreshlockmillies BIGINT NOT NULL,
  clientip VARCHAR(255) NOT NULL,
  clientid VARCHAR(255) NOT NULL,
  username VARCHAR(255) NOT NULL,
  clientpid VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_refreshlockmillies ON modulelock(refreshlockmillies);

-- Processing event queue table
CREATE TABLE processingeventqueue (
  messageid VARCHAR(255) PRIMARY KEY,
  mdnid VARCHAR(255),
  eventtype INT NOT NULL,
  processtype INT NOT NULL,
  initdate BIGINT NOT NULL,
  parameterlist VARCHAR(2048) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_processingeventqueue_initdate ON processingeventqueue(initdate);

-- High availability table
CREATE TABLE highavail (
  id INT AUTO_INCREMENT PRIMARY KEY,
  uniqueid VARCHAR(8) NOT NULL,
  localip VARCHAR(45) NOT NULL,
  publicip VARCHAR(45),
  cloudinstanceid VARCHAR(65),
  hostname VARCHAR(128) NOT NULL,
  clientcount INT DEFAULT 0 NOT NULL,
  operationsystem VARCHAR(128) NOT NULL,
  productversion VARCHAR(128) NOT NULL,
  starttime BIGINT NOT NULL,
  lastupdatetime BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_highavail_uniqueid ON highavail(uniqueid);
CREATE INDEX idx_highavail_starttime ON highavail(starttime);
CREATE INDEX idx_highavail_lastupdatetime ON highavail(lastupdatetime);

INSERT INTO version VALUES(NULL,0,'2025-05-23 09:47:07.680000','mend-as2 1.0');
