-- Check all tables
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC';

-- Check keydata table structure
SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'KEYDATA';

-- Check keydata table content
SELECT purpose, storagetype, lastchanged, securityprovider, LENGTH(storagedata) as data_size FROM keydata;
