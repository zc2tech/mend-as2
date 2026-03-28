CREATE TABLE config (
  key VARCHAR(255) PRIMARY KEY,
  value VARCHAR(255)
);

CREATE TABLE certificate (
  cert_id VARCHAR(255) PRIMARY KEY,
  alias VARCHAR(255),
  type VARCHAR(255),
  purpose INTEGER,
  expiration BIGINT
);

CREATE TABLE partner (
  partner_id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255),
  as2_id VARCHAR(255),
  email VARCHAR(255),
  url VARCHAR(255),
  is_local INTEGER
);

CREATE TABLE message (
  message_id VARCHAR(255) PRIMARY KEY,
  sender VARCHAR(255),
  receiver VARCHAR(255),
  direction INTEGER,
  state INTEGER,
  timestamp BIGINT,
  payload BYTEA
);

CREATE TABLE mdn (
  mdn_id VARCHAR(255) PRIMARY KEY,
  message_id VARCHAR(255),
  timestamp BIGINT,
  disposition VARCHAR(255),
  details VARCHAR(255)
);

CREATE TABLE eventlog (
  event_id SERIAL PRIMARY KEY,
  timestamp BIGINT,
  severity INTEGER,
  source VARCHAR(255),
  message VARCHAR(1024)
);

CREATE TABLE version (
  actualversion INTEGER,
  updatedate TIMESTAMP,
  updatecomment VARCHAR(255)
);

CREATE TABLE keydata (
  purpose INTEGER PRIMARY KEY,
  storagedata BYTEA,
  storagetype INTEGER,
  lastchanged BIGINT,
  securityprovider VARCHAR(255)
);