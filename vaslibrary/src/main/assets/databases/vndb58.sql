----------------------------------
-- Add some indexes --------------
----------------------------------
DROP INDEX IF EXISTS "main"."ngtCustomerId";
CREATE INDEX ngtCustomerId ON CustomerCall(CustomerId);
DROP INDEX IF EXISTS "main"."ngtCustomerUniqueId";
CREATE INDEX ngtCustomerUniqueId ON CustomerCallOrder(CustomerUniqueId);
DROP INDEX IF EXISTS "main"."ngtOrderUniqueId";
CREATE INDEX ngtOrderUniqueId ON CustomerCallOrderLines(OrderUniqueId);
DROP INDEX IF EXISTS "main"."ngtOrderLineUniqueId";
CREATE INDEX ngtOrderLineUniqueId ON CustomerCallOrderLinesInvoiceQtyDetail(OrderLineUniqueId);
DROP INDEX IF EXISTS "main"."ngtOrderLineUniqueIdOrder";
CREATE INDEX ngtOrderLineUniqueIdOrder ON CustomerCallOrderLinesOrderQtyDetail(OrderLineUniqueId);
DROP INDEX IF EXISTS "main"."ngtCustomerUniqueIdCallReturn";
CREATE INDEX ngtCustomerUniqueIdCallReturn ON CustomerCallReturn(CustomerUniqueId);
DROP INDEX IF EXISTS "main"."ngtReturnUniqueId";
CREATE INDEX ngtReturnUniqueId ON CustomerCallReturnLines(ReturnUniqueId);
DROP INDEX IF EXISTS "main"."ngtReturnLineUniqueId";
CREATE INDEX ngtReturnLineUniqueId ON CustomerCallReturnLinesQtyDetail(ReturnLineUniqueId);