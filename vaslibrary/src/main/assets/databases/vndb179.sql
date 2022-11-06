--create CheckCustomerCredit
CREATE TABLE "CustomerCallOrderLines"
(
 UniqueId TEXT NOT NULL COLLATE NOCAS,
 customerBackOfficeCode TEXT,
 CustomerCreditLimit REAL,
 customerUsedCredit REAL,
 customerRemainCredit REAL,
 PRIMARY KEY (UniqueId ASC)
 );