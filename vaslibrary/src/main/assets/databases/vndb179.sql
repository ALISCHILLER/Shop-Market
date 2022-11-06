--create CheckCustomerCredit
CREATE TABLE "CheckCustomerCredit"(
 "UniqueId" TEXT NOT NULL COLLATE NOCASE,
 "customerBackOfficeCode" TEXT,
 "CustomerCreditLimit" REAL,
 "customerUsedCredit" REAL,
 "customerRemainCredit" REAL,
 PRIMARY KEY ("UniqueId" ASC)
 );

 alter table Customer add column IsZarShopCustomer TEXT;

 alter table NewsData_ add column IsRead BIT;