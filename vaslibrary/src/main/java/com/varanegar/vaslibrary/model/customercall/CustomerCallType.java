package com.varanegar.vaslibrary.model.customercall;

/**
 * Created by A.Torabi on 9/27/2017.
 */

public enum CustomerCallType {
    SaveOrderRequest,
    SaveReturnRequestWithRef,
    SaveReturnRequestWithoutRef,
    LackOfVisit,
    LackOfOrder,
    Camera,
    EditCustomer,
    CustomerInventory,
    Questionnaire,
    ChangeLocation,
    SendData,
    Payment,
    CancelInvoice,
    CompleteLackOfDelivery,
    CompleteReturnDelivery,
    OrderDelivered,
    OrderPartiallyDelivered,
    OrderLackOfDelivery,
    OrderReturn,
    IncompleteOperation
}
