package com.varanegar.vaslibrary.webapi.customeroldinvoiceheader;

import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetailModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderModel;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleModel;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleVnLtModel;
import com.varanegar.vaslibrary.model.dissaleprizepackagesds.DisSalePrizePackageSDSModel;

import java.util.List;

/**
 * Created by A.Jafarzadeh on 8/19/2017.
 */

public class CustomerOldInvoicesViewModel {


    public List<CustomerOldInvoiceHeaderModel> headers;

    public List<CustomerOldInvoiceDetailModel> details;

    public List<CustomerOldInvoiceDisSaleModel> disSales;

    public List<CustomerOldInvoiceDisSaleVnLtModel> disSalesVnLt;

    public List<DisSalePrizePackageSDSModel> disSalePrizePackages;
}
