--create CheckCustomerCredit
CREATE TABLE "CheckCustomerCredit"
(
 UniqueId TEXT NOT NULL COLLATE NOCAS,
 customerBackOfficeCode TEXT,
 CustomerCreditLimit REAL,
 customerUsedCredit REAL,
 customerRemainCredit REAL,
 PRIMARY KEY (UniqueId ASC)
 );