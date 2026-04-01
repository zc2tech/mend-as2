CREATE TABLE version(
    id SERIAL PRIMARY KEY,
    actualversion INTEGER,
    updatedate TIMESTAMP,
    updatecomment VARCHAR(255)
);

CREATE TABLE oauth2(
    id SERIAL PRIMARY KEY,
    authendpoint VARCHAR(1024) NOT NULL,
    authscope VARCHAR(1024) NOT NULL,
    tokenendpoint VARCHAR(1024) NOT NULL,
    clientsecret VARCHAR(1024) NOT NULL,
    clientid VARCHAR(1024) NOT NULL,
    authcode BYTEA,
    accesstoken BYTEA,
    refreshtoken BYTEA,
    accesstokenuntil BIGINT DEFAULT 0 NOT NULL,
    username VARCHAR(256),
    customparamauth VARCHAR(1024),
    pkce INTEGER DEFAULT 0 NOT NULL,
    pkceverifier VARCHAR(128) DEFAULT '_new' NOT NULL
);

CREATE TABLE partner(
    id SERIAL PRIMARY KEY,
    as2ident VARCHAR(255),
    partnername VARCHAR(255),
    islocal INTEGER,
    sign INTEGER,
    encrypt INTEGER,
    email VARCHAR(255),
    url VARCHAR(255),
    mdnurl VARCHAR(255),
    msgsubject VARCHAR(255),
    contenttype VARCHAR(255),
    syncmdn INTEGER,
    pollignorelist VARCHAR(1024),
    pollinterval INTEGER,
    msgcompression INTEGER,
    signedmdn INTEGER,
    usehttpauth INTEGER,
    httpauthuser VARCHAR(256),
    httpauthpass VARCHAR(256),
    usehttpauthasyncmdn INTEGER,
    httpauthuserasnymdn VARCHAR(256),
    httpauthpassasnymdn VARCHAR(256),
    keeporiginalfilenameonreceipt INTEGER,
    partnercomment TEXT,
    notifysend INTEGER,
    notifyreceive INTEGER,
    notifysendreceive INTEGER,
    notifysendenabled INTEGER,
    notifyreceiveenabled INTEGER,
    notifysendreceiveenabled INTEGER,
    contenttransferencoding INTEGER,
    httpversion VARCHAR(3) DEFAULT '1.1' NOT NULL,
    maxpollfiles INTEGER DEFAULT 100 NOT NULL,
    partnercontact TEXT,
    partneraddress TEXT,
    algidentprotatt INTEGER DEFAULT 1 NOT NULL,
    enabledirpoll INTEGER DEFAULT 1 NOT NULL,
    useoauth2message INTEGER DEFAULT 0 NOT NULL,
    useoauth2mdn INTEGER DEFAULT 0 NOT NULL,
    oauth2idmessage INTEGER,
    oauth2idmdn INTEGER,
    overwritelocalsecurity INTEGER DEFAULT 0 NOT NULL,
    FOREIGN KEY(oauth2idmessage) REFERENCES oauth2(id),
    FOREIGN KEY(oauth2idmdn) REFERENCES oauth2(id)
);


CREATE INDEX idx_partner_islocal ON partner(islocal);
CREATE INDEX idx_partner_as2ident ON partner(as2ident);
CREATE TABLE partnerevent(
    id SERIAL PRIMARY KEY,
    partnerid INTEGER,
    useonreceipt INTEGER DEFAULT 0 NOT NULL,
    useonsenderror INTEGER DEFAULT 0 NOT NULL,
    useonsendsuccess INTEGER DEFAULT 0 NOT NULL,
    typeonreceipt INTEGER DEFAULT 1 NOT NULL,
    typeonsenderror INTEGER DEFAULT 1 NOT NULL,
    typeonsendsuccess INTEGER DEFAULT 1 NOT NULL,
    parameteronreceipt VARCHAR(2048),
    parameteronsenderror VARCHAR(2048),
    parameteronsendsuccess VARCHAR(2048)
);
CREATE TABLE httpheader(
    id SERIAL PRIMARY KEY,
    partnerid INTEGER,
    headerkey VARCHAR(255),
    headervalue VARCHAR(255)
    -- ,FOREIGN KEY(partnerid) REFERENCES partner(id)
);
CREATE TABLE certificates(
    id SERIAL PRIMARY KEY,
    partnerid INTEGER,
    fingerprintsha1 VARCHAR(255),
    category INTEGER
);
CREATE TABLE partnersystem(
    id SERIAL PRIMARY KEY,
    partnerid INTEGER,
    as2version VARCHAR(10),
    productname VARCHAR(255),
    msgcompression INTEGER DEFAULT 0 NOT NULL,
    ma INTEGER DEFAULT 0 NOT NULL,
    cem INTEGER DEFAULT 0 NOT NULL
);
CREATE TABLE serversettings(
    id SERIAL PRIMARY KEY,
    vkey VARCHAR(256) NOT NULL,
    vvalue VARCHAR(256) NOT NULL
);

CREATE INDEX idx_serversettings_vkey ON serversettings(vkey);

CREATE TABLE notification(
    id SERIAL PRIMARY KEY,
     mailhost VARCHAR(255),
  mailhostport INTEGER,
  notificationemailaddress VARCHAR(255),
  notifycertexpire INTEGER,
  notifytransactionerror INTEGER,
  notifycem INTEGER,
  notifysystemfailure INTEGER,
  replyto VARCHAR(255),
  usesmtpauth INTEGER,
  smtpauthuser VARCHAR(255),
  smtpauthpass VARCHAR(255),
  notifyresend INTEGER,
  security INTEGER,
  maxnotificationspermin INTEGER,
  notifyconnectionproblem INTEGER,
  notifypostprocessing INTEGER,
  usesmtpoauth2 INTEGER,
  smtpoauth2id INTEGER,
  notifyclientserver INTEGER
);



