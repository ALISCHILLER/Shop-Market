--- CREATE TABLE DealerCommissionDataModel
alter table DealerCommissionData add column waferTarget  INT;
alter table DealerCommissionData add column cakeTarget  INT;
alter table DealerCommissionData add column biscuitTarget  INT;
alter table DealerCommissionData add column oilTarget  INT;
alter table DealerCommissionData add column waferSales  INT;
alter table DealerCommissionData add column cakeSales  INT;
alter table DealerCommissionData add column biscuitSales  INT;
alter table DealerCommissionData add column oilSales  INT;
alter table DealerCommissionData add column waferPayment  INT;
alter table DealerCommissionData add column cakePayment  INT;
alter table DealerCommissionData add column biscuitPayment  INT;
alter table DealerCommissionData add column oilPayment  INT;
alter table DealerCommissionData add column waferAchive  FLOAT;
alter table DealerCommissionData add column cakeAchive  FLOAT;
alter table DealerCommissionData add column biscuitAchive  FLOAT;
alter table DealerCommissionData add column oilAchive  FLOAT;
-----------------------------------
alter table DealerCommissionData add column oilAchive_Frying  FLOAT;
alter table DealerCommissionData add column oilAchive_Mix  FLOAT;
alter table DealerCommissionData add column oilAchive_Corn  FLOAT;

alter table DealerCommissionData add column coverageRatePaymentIOL  INT;
alter table DealerCommissionData add column coverageRatePaymentCON  INT;

alter table DealerCommissionData add column finalPayment_OIL  INT;
alter table DealerCommissionData add column finalPayment_CON  INT;

alter table DealerCommissionData add column oilPayment_Corn  INT;
alter table DealerCommissionData add column oilPayment_Mix  INT;
alter table DealerCommissionData add column oilPayment_Frying  INT;

alter table DealerCommissionData add column oilSales_Corn  INT;
alter table DealerCommissionData add column oilSales_Mix  INT;
alter table DealerCommissionData add column oilSales_Frying  INT;

alter table DealerCommissionData add column oilTarget_Corn  INT;
alter table DealerCommissionData add column oilTarget_Mix  INT;
alter table DealerCommissionData add column oilTarget_Frying  INT;


