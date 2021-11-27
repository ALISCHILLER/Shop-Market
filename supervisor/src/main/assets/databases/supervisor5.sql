-- ----------------------------
-- Questionnaire: Alter SupervisorCustomer Table
-- ----------------------------
alter table SupervisorCustomer add column isActive BIT NULL;

alter table SupervisorCustomer add column isPenddingChange BIT NULL;

alter table SupervisorCustomer add column newStoreName TEXT NULL ;

alter table SupervisorCustomer add column newAddress TEXT NULL ;

alter table SupervisorCustomer add column newPhone TEXT NULL ;

alter table SupervisorCustomer add column newMobile TEXT NULL ;
alter table SupervisorCustomer add column newPostCode TEXT NULL ;
alter table SupervisorCustomer add column newCustomerLevelName TEXT NULL ;
alter table SupervisorCustomer add column newCustomerActivityName TEXT NULL ;
alter table SupervisorCustomer add column newCustomerCategoryName TEXT NULL ;
alter table SupervisorCustomer add column newNationalCode TEXT NULL ;
alter table SupervisorCustomer add column newEconomicCode TEXT NULL ;
alter table SupervisorCustomer add column newCityName TEXT NULL ;
alter table SupervisorCustomer add column PostCode TEXT NULL ;