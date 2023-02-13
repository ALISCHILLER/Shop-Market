package com.varanegar.vaslibrary.model.call;

import com.varanegar.processor.annotations.Table;

/**
 * Created by s.foroughi on 07/02/2017.
 */

@Table
public class CustomerCallInvoiceModel extends OrderBaseModel {

    public CustomerCallOrderModel convertInvoiceToOrderModel() {
        CustomerCallOrderModel customerCallOrderModel = new CustomerCallOrderModel();
        customerCallOrderModel.UniqueId = UniqueId;
        customerCallOrderModel.CustomerCallUniqueId = this.CustomerCallUniqueId;
        customerCallOrderModel.RowNo = this.RowNo;
        customerCallOrderModel.CustomerUniqueId = this.CustomerUniqueId;
        customerCallOrderModel.DistBackOfficeId = this.DistBackOfficeId;
        customerCallOrderModel.ShipToPartyUniqueId =this.ShipToPartyUniqueId;
        customerCallOrderModel.ShipToPartyCode =this.ShipToPartyCode;
        customerCallOrderModel.DisType = DisType;
//        customerCallOrderModel.Comment = Comment;
        customerCallOrderModel.LocalPaperNo = LocalPaperNo;
        customerCallOrderModel.BackOfficeOrderNo = BackOfficeOrderNo;
        customerCallOrderModel.SaleDate = SaleDate;
        customerCallOrderModel.SalePDate = SalePDate;
        customerCallOrderModel.DocDate = DocDate;
        customerCallOrderModel.DocPDate = DocPDate;
        customerCallOrderModel.BackOfficeOrderId = BackOfficeOrderId;
        customerCallOrderModel.BackOfficeOrderTypeId = BackOfficeOrderTypeId;
        customerCallOrderModel.OrderPaymentTypeUniqueId = OrderPaymentTypeUniqueId;
        customerCallOrderModel.RoundOrderOtherDiscount = RoundOrderOtherDiscount;
        customerCallOrderModel.RoundOrderDis1 = RoundOrderDis1;
        customerCallOrderModel.RoundOrderDis2 = RoundOrderDis2;
        customerCallOrderModel.RoundOrderDis3 = RoundOrderDis3;
        customerCallOrderModel.RoundOrderTax = RoundOrderTax;
        customerCallOrderModel.RoundOrderCharge = RoundOrderCharge;
        customerCallOrderModel.RoundOrderAdd1 = RoundOrderAdd1;
        customerCallOrderModel.RoundOrderAdd2 = RoundOrderAdd2;
        customerCallOrderModel.BackOfficeInvoiceId = BackOfficeInvoiceId;
        customerCallOrderModel.BackOfficeInvoiceNo = BackOfficeInvoiceNo;
//        if(withInvoiceFileds) {
//            customerCallOrderModel.RoundInvoiceAmount = RoundInvoiceAmount;
//            customerCallOrderModel.RoundInvoiceOtherDiscount = RoundInvoiceOtherDiscount;
//            customerCallOrderModel.RoundInvoiceTax = RoundInvoiceTax;
//            customerCallOrderModel.RoundInvoiceCharge = RoundInvoiceCharge;
//            customerCallOrderModel.RoundInvoiceDis1 = RoundInvoiceDis1;
//            customerCallOrderModel.RoundInvoiceDis2 = RoundInvoiceDis2;
//            customerCallOrderModel.RoundInvoiceDis3 = RoundInvoiceDis3;
//            customerCallOrderModel.RoundInvoiceAdd1 = RoundInvoiceAdd1;
//            customerCallOrderModel.RoundInvoiceAdd2 = RoundInvoiceAdd2;
//        }
        customerCallOrderModel.InvoicePaymentTypeUniqueId = InvoicePaymentTypeUniqueId;
        customerCallOrderModel.IsPromotion = IsPromotion;
        customerCallOrderModel.PromotionUniqueId = PromotionUniqueId;
        customerCallOrderModel.StockDCCodeSDS = StockDCCodeSDS;
        customerCallOrderModel.SupervisorRefSDS = SupervisorRefSDS;
        customerCallOrderModel.SupervisorCodeSDS = SupervisorCodeSDS;
        customerCallOrderModel.DcCodeSDS = DcCodeSDS;
        customerCallOrderModel.SaleIdSDS = SaleIdSDS;
        customerCallOrderModel.SaleNoSDS = SaleNoSDS;
        customerCallOrderModel.DealerRefSDS = DealerRefSDS;
        customerCallOrderModel.DealerCodeSDS = DealerCodeSDS;
        customerCallOrderModel.OrderNoSDS = OrderNoSDS;
        customerCallOrderModel.AccYearSDS = AccYearSDS;
        customerCallOrderModel.DCRefSDS = DCRefSDS;
        customerCallOrderModel.SaleOfficeRefSDS = SaleOfficeRefSDS;
        customerCallOrderModel.StockDCRefSDS = StockDCRefSDS;
        customerCallOrderModel.CallActionStatusUniqueId = CallActionStatusUniqueId;
        customerCallOrderModel.SubSystemTypeUniqueId = SubSystemTypeUniqueId;
        customerCallOrderModel.OrderTypeUniqueId = OrderTypeUniqueId;
        customerCallOrderModel.PriceClassId = PriceClassId;
        customerCallOrderModel.DeliveryDate = DeliveryDate;
        customerCallOrderModel.StartTime = StartTime;
        customerCallOrderModel.EndTime = EndTime;
        customerCallOrderModel.IsInvoice = IsInvoice;
        customerCallOrderModel.DealerName = DealerName;
        customerCallOrderModel.DealerMobile = DealerMobile;
        customerCallOrderModel.CashDuration = CashDuration;
        customerCallOrderModel.CheckDuration = CheckDuration;
        customerCallOrderModel.PinCode = PinCode;
        customerCallOrderModel.PinCode2 = PinCode2;
        customerCallOrderModel.PinCode3 =PinCode3;
        customerCallOrderModel.PinCode4=PinCode4;
        customerCallOrderModel.TotalAmountNutCheque=TotalAmountNutCheque;
        customerCallOrderModel.TotalAmountNutCash =TotalAmountNutCash;
        customerCallOrderModel.TotalAmountNutImmediate =TotalAmountNutImmediate;
        customerCallOrderModel.ZTERM=ZTERM;

        return customerCallOrderModel;
    }
}
