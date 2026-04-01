INSERT INTO notification (mailhost, mailhostport, notificationemailaddress, notifycertexpire, notifytransactionerror,
  notifycem, notifysystemfailure, notifypostprocessing, replyto,
  usesmtpauth, smtpauthuser, smtpauthpass, notifyresend, security,
  maxnotificationspermin, notifyconnectionproblem, notifyclientserver, usesmtpoauth2, smtpoauth2id)
VALUES ('smtp.office365.com', 587, 'julian.xu@aliyun.com', 1, 1, 0, 1, 1, 'julian.xu@aliyun.com', 1, 'julian.xu@aliyun.com', 'password!', 1, 1, 2, 1, 0, 0, 0);

-- INSERT INTO BLOCKS VALUES(0, 2147483647, 0)
INSERT INTO VERSION
VALUES(
    0,
    67,
    '2025-05-23 09:47:07.544000',
    'mendelson opensource AS2 1.1 build 63: initial installation'
);
