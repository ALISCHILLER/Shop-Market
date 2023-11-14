package com.varanegar.vaslibrary.manager.oldinvoicemanager;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.DbAttachment;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.TableMap;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.datetime.DateFormat;
import com.varanegar.framework.util.datetime.DateHelper;
import com.varanegar.framework.util.prefs.Preferences;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.base.BackupManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.customeroldinvoicedissale.CustomerOldInvoiceDisSaleManager;
import com.varanegar.vaslibrary.manager.customeroldinvoicedissale.CustomerOldInvoiceDisSaleVnLtManager;
import com.varanegar.vaslibrary.manager.dissaleprizepackagesdsmanager.DisSalePrizePackageSDSManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.tourmanager.TourManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateManager;
import com.varanegar.vaslibrary.model.UpdateKey;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetail;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetailModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceDetailTempModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeader;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderModel;
import com.varanegar.vaslibrary.model.customeroldInvoice.CustomerOldInvoiceHeaderTempModel;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSale;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleModel;
import com.varanegar.vaslibrary.model.customeroldinvoicedissalemodel.CustomerOldInvoiceDisSaleVnLtModel;
import com.varanegar.vaslibrary.model.discoungood.DiscountGood;
import com.varanegar.vaslibrary.model.discountSDS.DiscountCondition;
import com.varanegar.vaslibrary.model.discountSDS.DiscountItemCount;
import com.varanegar.vaslibrary.model.discountSDS.DiscountSDS;
import com.varanegar.vaslibrary.model.dissaleprizepackagesds.DisSalePrizePackageSDS;
import com.varanegar.vaslibrary.model.dissaleprizepackagesds.DisSalePrizePackageSDSModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.tour.TourModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.customeroldinvoiceheader.CustomerOldInvoiceHeaderApi;
import com.varanegar.vaslibrary.webapi.customeroldinvoiceheader.CustomerOldInvoicesViewModel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import timber.log.Timber;
import varanegar.com.vdmclient.call.DiscountGoods;

/**
 * Created by A.Torabi on 11/20/2017.
 */

public class OldInvoiceManager {
    private final UserModel userModel;
    private final TourModel tourModel;
    private final SysConfigManager sysConfigManager;
    private final String dateString;
    private final UpdateManager updateManager;
    private Context context;
    private SysConfigModel sysConfigModel;

    public OldInvoiceManager(Context context) {
        this.context = context;
        TourManager tourManager = new TourManager(context);
        userModel = UserManager.readFromFile(context);
        tourModel = tourManager.loadTour();
        sysConfigManager = new SysConfigManager(context);
        sysConfigModel = sysConfigManager.read(ConfigKey.SettingsId, SysConfigManager.local);
        updateManager = new UpdateManager(context);
        Date date = updateManager.getLog(UpdateKey.CustomerOldInvoice);
        dateString = DateHelper.toString(date, DateFormat.MicrosoftDateTime, Locale.US);
    }

    public void sync(final Boolean firstTime, final boolean onlyDiscount,
                     @Nullable Date startDate, @Nullable Date endDate,
                     @Nullable UUID customerId, final UpdateCall call) {
        Preferences preferences = new Preferences(context);
        if (customerId == null) {
            preferences.clearPreferences(Preferences.InvoiceSelection);
        } else {
            preferences.remove(Preferences.InvoiceSelection, customerId, null, null);
        }
        final CustomerOldInvoiceHeaderApi customerOldInvoiceHeaderApi = new CustomerOldInvoiceHeaderApi(context);
        if (customerId == null) {
            VaranegarApplication.getInstance().getDbHandler().emptyTables(true,
                    DisSalePrizePackageSDS.DisSalePrizePackageSDSTbl,
                    CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl,
                    CustomerOldInvoiceDisSale.CustomerOldInvoiceDisSaleTbl,
                    CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl);
            try {
                BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                if (backOfficeType == BackOfficeType.Varanegar) {
                    customerOldInvoiceHeaderApi.runWebRequest(customerOldInvoiceHeaderApi
                            .invoiceSQLite(tourModel.UniqueId.toString(),
                                    userModel.UniqueId.toString(), dateString,
                                    sysConfigModel.Value, !onlyDiscount,
                                    VaranegarApplication.getInstance().getAppId()),
                            new WebCallBack<ResponseBody>() {
                        @Override
                        protected void onFinish() {

                        }

                        @Override
                        protected void onSuccess(ResponseBody result, Request request) {
                            try {
                                Timber.i("old invoice zip file downloaded.");
                                customerOldInvoiceHeaderApi.saveResponseBodyToPrivateFolder(result, "oldinvoice.zip");
                                Timber.i("old invoice zip file saved");
                                unzip();
                                Timber.i("old invoice zip file extracted");
                                if (firstTime) {
                                    DbAttachment attachment = new DbAttachment("oldinvoice.db");
                                    VaranegarApplication.getInstance().getDbHandler().emptyTables(true,
                                            DiscountSDS.DiscountSDSTbl,
                                            DiscountCondition.DiscountConditionTbl,
                                            DiscountItemCount.DiscountItemCountTbl,
                                            DiscountGood.DiscountGoodTbl);
                                    Timber.i("discount tables was deleted");
                                    attachment.addMapping(new TableMap("DiscountSDS", DiscountSDS.DiscountSDSTbl)
                                            .addColumn("UniqueId", DiscountSDS.UniqueId)
                                            .addColumn("DisGroup", DiscountSDS.DisGroup)
                                            .addColumn("Priority", DiscountSDS.Priority)
                                            .addColumn("Code", DiscountSDS.Code)
                                            .addColumn("DisType", DiscountSDS.DisType)
                                            .addColumn("PrizeType", DiscountSDS.PrizeType)
                                            .addColumn("StartDate", DiscountSDS.StartDate)
                                            .addColumn("EndDate", DiscountSDS.EndDate)
                                            .addColumn("MinQty", DiscountSDS.MinQty)
                                            .addColumn("MaxQty", DiscountSDS.MaxQty)
                                            .addColumn("QtyUnit", DiscountSDS.QtyUnit)
                                            .addColumn("MinAmount", DiscountSDS.MinAmount)
                                            .addColumn("MaxAmount", DiscountSDS.MaxAmount)
                                            .addColumn("PrizeQty", DiscountSDS.PrizeQty)
                                            .addColumn("PrizeRef", DiscountSDS.PrizeRef)
                                            .addColumn("PrizeStep", DiscountSDS.PrizeStep)
                                            .addColumn("PrizeUnit", DiscountSDS.PrizeUnit)
                                            .addColumn("DisPerc", DiscountSDS.DisPerc)
                                            .addColumn("DisPrice", DiscountSDS.DisPrice)
                                            .addColumn("GoodsRef", DiscountSDS.GoodsRef)
                                            .addColumn("DCRef", DiscountSDS.DCRef)
    //                                        .addColumn("CustActRef", DiscountSDS.CustActRef)
    //                                        .addColumn("CustCtgrRef", DiscountSDS.CustCtgrRef)
    //                                        .addColumn("StateRef", DiscountSDS.StateRef)
    //                                        .addColumn("AreaRef", DiscountSDS.AreaRef)
                                            .addColumn("GoodsCtgrRef", DiscountSDS.GoodsCtgrRef)
    //                                        .addColumn("CustRef", DiscountSDS.CustRef)
                                            .addColumn("DisAccRef", DiscountSDS.DisAccRef)
    //                                        .addColumn("PayType", DiscountSDS.PayType)
    //                                        .addColumn("OrderType", DiscountSDS.OrderType)
                                            .addColumn("SupPerc", DiscountSDS.SupPerc)
                                            .addColumn("AddPerc", DiscountSDS.AddPerc)
    //                                        .addColumn("SaleOfficeRef", DiscountSDS.SaleOfficeRef)
                                            .addColumn("Comment", DiscountSDS.Comment)
                                            .addColumn("ApplyInGroup", DiscountSDS.ApplyInGroup)
                                            .addColumn("CalcPriority", DiscountSDS.CalcPriority)
                                            .addColumn("CalcMethod", DiscountSDS.CalcMethod)
    //                                        .addColumn("CustLevelRef", DiscountSDS.CustLevelRef)
                                            .addColumn("GoodsGroupRef", DiscountSDS.GoodsGroupRef)
                                            .addColumn("ManufacturerRef", DiscountSDS.ManufacturerRef)
    //                                        .addColumn("SaleZoneRef", DiscountSDS.SaleZoneRef)
                                            .addColumn("MainTypeRef", DiscountSDS.MainTypeRef)
                                            .addColumn("SubTypeRef", DiscountSDS.SubTypeRef)
                                            .addColumn("BrandRef", DiscountSDS.BrandRef)
                                            .addColumn("MinWeight", DiscountSDS.MinWeight)
                                            .addColumn("MaxWeight", DiscountSDS.MaxWeight)
                                            .addColumn("UserRef", DiscountSDS.UserRef)
                                            .addColumn("ModifiedDate", DiscountSDS.ModifiedDate)
                                            .addColumn("PrizeIncluded", DiscountSDS.PrizeIncluded)
                                            .addColumn("ModifiedDateBeforeSend", DiscountSDS.ModifiedDateBeforeSend)
                                            .addColumn("UserRefBeforeSend", DiscountSDS.UserRefBeforeSend)
                                            .addColumn("MinRowsCount", DiscountSDS.MinRowsCount)
                                            .addColumn("MinCustChequeRetQty", DiscountSDS.MinCustChequeRetQty)
                                            .addColumn("MaxCustChequeRetQty", DiscountSDS.MaxCustChequeRetQty)
                                            .addColumn("MinCustRemAmount", DiscountSDS.MinCustRemAmount)
                                            .addColumn("MaxCustRemAmount", DiscountSDS.MaxCustRemAmount)
                                            .addColumn("MaxRowsCount", DiscountSDS.MaxRowsCount)
                                            .addColumn("IsActive", DiscountSDS.IsActive)
    //                                        .addColumn("OrderNo", DiscountSDS.OrderNo)
                                            .addColumn("PrizeStepType", DiscountSDS.PrizeStepType)
    //                                        .addColumn("IsPrize", DiscountSDS.IsPrize)
                                            .addColumn("SqlCondition", DiscountSDS.SqlCondition)
                                            .addColumn("PrizePackageRef", DiscountSDS.PrizePackageRef)
    //                                        .addColumn("DetailIsActive", DiscountSDS.DetailIsActive)
    //                                        .addColumn("DetailPriority", DiscountSDS.DetailPriority)
    //                                        .addColumn("PromotionDetailCustomerGroupId", DiscountSDS.PromotionDetailCustomerGroupId)
    //                                        .addColumn("PromotionDetailId", DiscountSDS.PromotionDetailId)
    //                                        .addColumn("PromotionDetailCustomerId", DiscountSDS.PromotionDetailCustomerId)
    //                                        .addColumn("ReduceOfQty", DiscountSDS.ReduceOfQty)
                                            .addColumn("HasAdvanceCondition", DiscountSDS.HasAdvanceCondition)
                                            .addColumn("BackOfficeId", DiscountSDS.BackOfficeId)
                                            .addColumn("TotalMinAmount", DiscountSDS.TotalMinAmount)
                                            .addColumn("TotalMaxAmount", DiscountSDS.TotalMaxAmount)
                                            .addColumn("TotalMinRowCount", DiscountSDS.TotalMinRowCount)
                                            .addColumn("TotalMaxRowCount", DiscountSDS.TotalMaxRowCount)
                                            .addColumn("MixCondition", DiscountSDS.MixCondition)
                                            .addColumn("PrizeStepUnit", DiscountSDS.PrizeStepUnit)
                                            .addColumn("TotalDiscount", DiscountSDS.TotalDiscount)
                                            .addColumn("CustRefList", DiscountSDS.CustRefList)
                                            .addColumn("DiscountAreaRefList", DiscountSDS.DiscountAreaRefList)
                                            .addColumn("DiscountCustActRefList", DiscountSDS.DiscountCustActRefList)
                                            .addColumn("DiscountCustCtgrRefList", DiscountSDS.DiscountCustCtgrRefList)
                                            .addColumn("DiscountCustGroupRefList", DiscountSDS.DiscountCustGroupRefList)
                                            .addColumn("DiscountCustLevelRefList", DiscountSDS.DiscountCustLevelRefList)
                                            .addColumn("DiscountDcRefList", DiscountSDS.DiscountDcRefList)
                                            .addColumn("DiscountGoodRefList", DiscountSDS.DiscountGoodRefList)
                                            .addColumn("DiscountMainCustTypeRefList", DiscountSDS.DiscountMainCustTypeRefList)
                                            .addColumn("DiscountOrderNoList", DiscountSDS.DiscountOrderNoList)
                                            .addColumn("DiscountOrderRefList", DiscountSDS.DiscountOrderRefList)
                                            .addColumn("DiscountOrderTypeList", DiscountSDS.DiscountOrderTypeList)
                                            .addColumn("DiscountPaymentUsanceRefList", DiscountSDS.DiscountPaymentUsanceRefList)
                                            .addColumn("DiscountPayTypeList", DiscountSDS.DiscountPayTypeList)
                                            .addColumn("DiscountSaleOfficeRefList", DiscountSDS.DiscountSaleOfficeRefList)
                                            .addColumn("DiscountSaleZoneRefList", DiscountSDS.DiscountSaleZoneRefList)
                                            .addColumn("DiscountStateRefList", DiscountSDS.DiscountStateRefList)
                                            .addColumn("DiscountSubCustTypeRefList", DiscountSDS.DiscountSubCustTypeRefList)
                                            .addColumn("IsComplexCondition", DiscountSDS.IsComplexCondition)
    //                                        .addColumn("PrizeSelectionList", DiscountSDS.PrizeSelectionList)
                                            .addColumn("IsSelfPrize", DiscountSDS.IsSelfPrize)
                                            .addColumn("DiscountPreventSaveOrder", DiscountSDS.DiscountPreventSaveOrder)
                                            .addColumn("DiscountPreventSaveSale", DiscountSDS.DiscountPreventSaveSale)
                                            .addColumn("DiscountPreventSaveFollowVoucher", DiscountSDS.DiscountPreventSaveFollowVoucher)
                                            .addColumn("DiscountPreventSaveTablet", DiscountSDS.DiscountPreventSaveTablet)
                                            .addColumn("DiscountPreventMessage", DiscountSDS.DiscountPreventMessage)
                                            .addColumn("DiscountTypeRef", DiscountSDS.DiscountTypeRef)
                                            .addColumn("PreventSaleResultDesc", DiscountSDS.PreventSaleResultDesc));
                                    attachment.addMapping(new TableMap("DiscountCondition", DiscountCondition.DiscountConditionTbl)
                                            .addColumn("DiscountRef", DiscountCondition.DiscountRef)
                                            .addColumn("DCRef", DiscountCondition.DCRef)
                                            .addColumn("CustCtgrRef", DiscountCondition.CustCtgrRef)
                                            .addColumn("CustActRef", DiscountCondition.CustActRef)
                                            .addColumn("CustLevelRef", DiscountCondition.CustLevelRef)
                                            .addColumn("PayType", DiscountCondition.PayType)
                                            .addColumn("PaymentUsanceRef", DiscountCondition.PaymentUsanceRef)
                                            .addColumn("OrderType", DiscountCondition.OrderType)
                                            .addColumn("SaleOfficeRef", DiscountCondition.SaleOfficeRef)
                                            .addColumn("CustGroupRef", DiscountCondition.CustGroupRef)
                                            .addColumn("CustRef", DiscountCondition.CustRef)
                                            .addColumn("OrderNo", DiscountCondition.OrderNo)
                                            .addColumn("StateRef", DiscountCondition.StateRef)
                                            .addColumn("AreaRef", DiscountCondition.AreaRef)
                                            .addColumn("SaleZoneRef", DiscountCondition.SaleZoneRef)
                                            .addColumn("MainCustTypeRef", DiscountCondition.MainCustTypeRef)
                                            .addColumn("SubCustTypeRef", DiscountCondition.SubCustTypeRef)
                                            .addColumn("UniqueId", DiscountCondition.UniqueId));
                                    attachment.addMapping(new TableMap("DiscountItemCount", DiscountItemCount.DiscountItemCountTbl)
                                            .addColumn("GoodsRef", DiscountItemCount.GoodsRef)
                                            .addColumn("DisRef", DiscountItemCount.DisRef)
                                            .addColumn("UniqueId", DiscountItemCount.UniqueId));
                                    attachment.addMapping(new TableMap("DiscountGoodsSDS", DiscountGood.DiscountGoodTbl)
                                            .addColumn("id", DiscountGood.UniqueId)
                                            .addColumn("GoodsRef", DiscountGood.GoodsRef)
                                            .addColumn("DisRef", DiscountGood.DiscountRef));
                                    Timber.i("start inserting discount database");
                                    VaranegarApplication.getInstance().getDbHandler().attach(attachment, false);
                                    Timber.i("discount database attached and inserted successfully");
                                }
                            } catch (SQLiteException e) {
                                Timber.e(e);
                                call.error(context.getString(R.string.error_saving_request));
                                return;
                            } catch (FileNotFoundException e) {
                                Timber.e(e);
                                call.error(context.getString(R.string.error_saving_request));
                                return;
                            } catch (IOException e) {
                                Timber.e(e);
                                call.error(context.getString(R.string.error_saving_request));
                                return;
                            } catch (Exception e) {
                                Timber.e(e);
                                call.error(context.getString(R.string.error_saving_request));
                                return;
                            }
                            try {
                                if (!onlyDiscount) {
                                    DbAttachment attachment = new DbAttachment("oldinvoice.db");
                                    VaranegarApplication.getInstance().getDbHandler().emptyTables(true,
                                            CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl,
                                            CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl,
                                            CustomerOldInvoiceDisSale.CustomerOldInvoiceDisSaleTbl,
                                            DisSalePrizePackageSDS.DisSalePrizePackageSDSTbl);
                                    Timber.i("old invoice tables was deleted");
                                    attachment.addMapping(
                                            new TableMap("CustomerOldInvoiceHeader", CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl)
                                                    .addColumn("SaleRef", CustomerOldInvoiceHeader.SaleRef)
                                                    .addColumn("UniqueId", CustomerOldInvoiceHeader.UniqueId)
                                                    .addColumn("SaleNo", CustomerOldInvoiceHeader.SaleNo)
                                                    .addColumn("SaleVocherNo", CustomerOldInvoiceHeader.SaleVocherNo)
                                                    .addColumn("SalePDate", CustomerOldInvoiceHeader.SalePDate)
                                                    .addColumn("StockId", CustomerOldInvoiceHeader.StockId)
                                                    .addColumn("StockBackOfficeId", CustomerOldInvoiceHeader.StockBackOfficeId)
                                                    .addColumn("PaymentTypeOrderUniqueId", CustomerOldInvoiceHeader.PaymentTypeOrderUniqueId)
                                                    .addColumn("CustomerId", CustomerOldInvoiceHeader.CustomerId)
                                                    .addColumn("CustRef", CustomerOldInvoiceHeader.CustRef)
                                                    .addColumn("DealerId", CustomerOldInvoiceHeader.DealerId)
                                                    .addColumn("TotalAmount", CustomerOldInvoiceHeader.TotalAmount)
                                                    .addColumn("PayAmount", CustomerOldInvoiceHeader.PayAmount)
                                                    .addColumn("CashAmount", CustomerOldInvoiceHeader.CashAmount)
                                                    .addColumn("ChequeAmount", CustomerOldInvoiceHeader.ChequeAmount)
                                                    .addColumn("GoodsGroupTreeXML", CustomerOldInvoiceHeader.GoodsGroupTreeXML)
                                                    .addColumn("GoodsDetailXML", CustomerOldInvoiceHeader.GoodsDetailXML)
                                                    .addColumn("GoodsMainSubTypeDetailXML", CustomerOldInvoiceHeader.GoodsMainSubTypeDetailXML)
                                                    .addColumn("CustCtgrRef", CustomerOldInvoiceHeader.CustCtgrRef)
                                                    .addColumn("CustActRef", CustomerOldInvoiceHeader.CustActRef)
                                                    .addColumn("CustLevelRef", CustomerOldInvoiceHeader.CustLevelRef)
                                                    .addColumn("OrderType", CustomerOldInvoiceHeader.OrderType)
                                                    .addColumn("BuyTypeRef", CustomerOldInvoiceHeader.BuyTypeRef)
                                                    .addColumn("DisType", CustomerOldInvoiceHeader.DisType)
                                                    .addColumn("OrderRef", CustomerOldInvoiceHeader.OrderRef)
                                                    .addColumn("OrderNo", CustomerOldInvoiceHeader.OrderNo)
                                                    .addColumn("DealerName", CustomerOldInvoiceHeader.DealerName)
                                    );
                                    attachment.addMapping(new TableMap("CustomerOldInvoiceDetail", CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl));
                                    attachment.addMapping(new TableMap("CustomerOldInvoiceDisSale", CustomerOldInvoiceDisSale.CustomerOldInvoiceDisSaleTbl));
                                    attachment.addMapping(new TableMap("DisSalePrizePackageSDS", DisSalePrizePackageSDS.DisSalePrizePackageSDSTbl));
                                    Timber.i("start inserting old invoice database");
                                    VaranegarApplication.getInstance().getDbHandler().attach(attachment, false);
                                    Timber.i("old invoice database attached and inserted successfully");
                                }
                                BackupManager.exportData(context, true);
                                updateManager.addLog(UpdateKey.CustomerOldInvoice);
                                String filename = getResponse().headers().get("Content-Disposition");
                                if (filename == null) {
                                    call.success();
                                    return;
                                }
                                filename = filename.substring(filename.indexOf("attachment; filename=") + 21);
                                filename = filename.replace(".gz", "");
                                customerOldInvoiceHeaderApi.runWebRequest(customerOldInvoiceHeaderApi.deleteSQLite(filename), new WebCallBack<ResponseBody>() {
                                    @Override
                                    protected void onFinish() {
                                        call.success();
                                    }

                                    @Override
                                    protected void onSuccess(ResponseBody result, Request request) {

                                    }

                                    @Override
                                    protected void onApiFailure(ApiError error, Request request) {

                                    }

                                    @Override
                                    protected void onNetworkFailure(Throwable t, Request request) {

                                    }
                                });
                            } catch (SQLiteException e) {
                                Timber.e(e);
                                call.failure(context.getString(R.string.error_saving_request));
                            } catch (FileNotFoundException e) {
                                Timber.e(e);
                                call.failure(context.getString(R.string.error_saving_request));
                            } catch (IOException e) {
                                Timber.e(e);
                                call.failure(context.getString(R.string.error_saving_request));
                            } catch (Exception e) {
                                Timber.e(e);
                                call.failure(context.getString(R.string.error_saving_request));
                            }
                        }

                        @Override
                        protected void onApiFailure(ApiError error, Request request) {
                            String err = WebApiErrorBody.log(error, context);
                            if (firstTime)
                                call.error(err);
                            else
                                call.failure(err);
                        }

                        @Override
                        protected void onNetworkFailure(Throwable t, Request request) {
                            Timber.e(t, request.url().toString());
                            if (firstTime)
                                call.error(context.getString(R.string.network_error));
                            else
                                call.failure(context.getString(R.string.network_error));
                        }
                    });
                } else {
                    syncCustomerOldInvoices(null, null, null, call);
                }
            } catch (UnknownBackOfficeException e) {
                Timber.e(e);
                call.failure(context.getString(R.string.back_office_type_is_uknown));
            }
        } else {
            syncCustomerOldInvoices(customerId, startDate, endDate, call);
        }
    }

    public void syncCustomerOldInvoices(@Nullable final UUID customerId, @Nullable final Date startDate, @Nullable Date endDate, @NonNull final UpdateCall call) {
        final CustomerOldInvoiceHeaderApi customerOldInvoiceHeaderApi = new CustomerOldInvoiceHeaderApi(context);
        VaranegarApplication.getInstance().resetElapsedTime("resetting timer");
        VaranegarApplication.getInstance().printElapsedTime("Start old invoice");
        Call<CustomerOldInvoicesViewModel> service;
        if (customerId != null)
            service = customerOldInvoiceHeaderApi.invoice(tourModel.UniqueId.toString(), userModel.UniqueId.toString(), dateString, sysConfigModel.Value, customerId.toString(), DateHelper.toString(startDate, DateFormat.MicrosoftDateTime, Locale.US), DateHelper.toString(endDate, DateFormat.MicrosoftDateTime, Locale.US));
        else
            service = customerOldInvoiceHeaderApi.invoice(tourModel.UniqueId.toString(), userModel.UniqueId.toString(), dateString, sysConfigModel.Value);
        customerOldInvoiceHeaderApi.runWebRequest(service, new WebCallBack<CustomerOldInvoicesViewModel>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(final CustomerOldInvoicesViewModel result, final Request request) {
                VaranegarApplication.getInstance().printElapsedTime("old invoice downloaded and deserialized");
                if (result.headers != null)
                    Timber.d("Headers: " + result.headers.size());
                else {
                    Timber.d("Headers: 0");
                    result.headers = new ArrayList<>();
                }
                if (result.details != null)
                    Timber.d("Details: " + result.details.size());
                else {
                    Timber.d("Details: 0");
                    result.details = new ArrayList<>();
                }
                if (result.disSalePrizePackages != null)
                    Timber.d("disSalePrizePackages: " + result.disSalePrizePackages.size());
                else {
                    Timber.d("disSalePrizePackages: 0");
                    result.disSalePrizePackages = new ArrayList<>();
                }
                if (result.disSales != null)
                    Timber.d("disSales: " + result.disSales.size());
                else {
                    Timber.d("disSales: 0");
                    result.disSales = new ArrayList<>();
                }

                if (result.disSalesVnLt != null)
                    Timber.d("disSalesVnLt: " + result.disSalesVnLt.size());
                else {
                    Timber.d("disSalesVnLt: 0");
                    result.disSalesVnLt = new ArrayList<>();
                }

                List<CustomerOldInvoiceHeaderModel> customerOldInvoiceHeaders = result.headers;
                if (startDate != null) {
                    CustomerOldInvoiceHeaderTempManager customerOldInvoiceHeaderTempManager = new CustomerOldInvoiceHeaderTempManager(context);
                    List<CustomerOldInvoiceHeaderTempModel> customerOldInvoiceHeaderTempModels = new ArrayList<>();
                    for (CustomerOldInvoiceHeaderModel customerOldInvoiceHeaderModel : customerOldInvoiceHeaders) {
                        CustomerOldInvoiceHeaderTempModel customerOldInvoiceHeaderTempModel = customerOldInvoiceHeaderModel.convert();
                        customerOldInvoiceHeaderTempModels.add(customerOldInvoiceHeaderTempModel);
                    }
                    try {
                        customerOldInvoiceHeaderTempManager.deleteAll();
                        long affectedRows;
                        if (customerOldInvoiceHeaders.size() > 0)
                            affectedRows = customerOldInvoiceHeaderTempManager.insert(customerOldInvoiceHeaderTempModels);
                        else {
                            CustomerOldInvoiceHeaderTempModel customerOldInvoiceHeaderTempModel = new CustomerOldInvoiceHeaderTempModel();
                            customerOldInvoiceHeaderTempModel.UniqueId = UUID.fromString("00000000-0000-0000-0000-000000000000");
                            affectedRows = customerOldInvoiceHeaderTempManager.insert(customerOldInvoiceHeaderTempModel);
                        }
                        VaranegarApplication.getInstance().printElapsedTime("old invoice headers temp saved");
                        Timber.d("Number of rows inserted = " + affectedRows);
                        List<CustomerOldInvoiceDetailModel> customerOldInvoiceDetails = result.details;
                        CustomerOldInvoiceDetailTempManager customerOldInvoiceDetailTempManager = new CustomerOldInvoiceDetailTempManager(context);
                        List<CustomerOldInvoiceDetailTempModel> customerOldInvoiceDetailTempModels = new ArrayList<>();
                        for (CustomerOldInvoiceDetailModel customerOldInvoiceDetailModel : customerOldInvoiceDetails) {
                            CustomerOldInvoiceDetailTempModel customerOldInvoiceDetailTempModel = customerOldInvoiceDetailModel.convert();
                            customerOldInvoiceDetailTempModels.add(customerOldInvoiceDetailTempModel);
                        }
                        customerOldInvoiceDetailTempManager.deleteAll();
                        if (customerOldInvoiceHeaders.size() > 0)
                            affectedRows = customerOldInvoiceDetailTempManager.insert(customerOldInvoiceDetailTempModels);
                        else {
                            CustomerOldInvoiceDetailTempModel customerOldInvoiceDetailTempModel = new CustomerOldInvoiceDetailTempModel();
                            customerOldInvoiceDetailTempModel.UniqueId = UUID.fromString("00000000-0000-0000-0000-000000000000");
                            customerOldInvoiceDetailTempModel.ProductId = UUID.fromString("00000000-0000-0000-0000-000000000000");
                            customerOldInvoiceDetailTempManager.insert(customerOldInvoiceDetailTempModel);
                        }
                        VaranegarApplication.getInstance().printElapsedTime("old invoice detailes temp saved");
                        Timber.d("Number of rows inserted = " + affectedRows);
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(context.getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(context.getString(R.string.data_error));
                    }
                } else if (customerOldInvoiceHeaders.size() > 0) {
                    VaranegarApplication.getInstance().getDbHandler().emptyTables(true,
                            DisSalePrizePackageSDS.DisSalePrizePackageSDSTbl,
                            CustomerOldInvoiceDisSale.CustomerOldInvoiceDisSaleTbl,
                            CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl,
                            CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl);
                    CustomerOldInvoiceHeaderManager customerOldInvoiceHeaderManager = new CustomerOldInvoiceHeaderManager(context);
                    try {
                        long affectedRows = customerOldInvoiceHeaderManager.insert(customerOldInvoiceHeaders);
                        VaranegarApplication.getInstance().printElapsedTime("old invoice headers saved");
                        Timber.d("Number of rows inserted = " + affectedRows);
                        List<CustomerOldInvoiceDetailModel> customerOldInvoiceDetails = result.details;
                        if (customerOldInvoiceDetails.size() > 0) {
                            final CustomerOldInvoiceDetailManager customerOldInvoiceDetailManager = new CustomerOldInvoiceDetailManager(context);
                            affectedRows = customerOldInvoiceDetailManager.insert(customerOldInvoiceDetails);
                            VaranegarApplication.getInstance().printElapsedTime("old invoice detailes saved");
                            Timber.d("Number of rows inserted = " + affectedRows);
                        }
                        insertCustomerOldInvoiceDisSaleModels(result);
                        new UpdateManager(context).addLog(UpdateKey.CustomerOldInvoice);
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e);
                        call.failure(context.getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e);
                        call.failure(context.getString(R.string.data_error));
                    }

                } else {
                    VaranegarApplication.getInstance().getDbHandler().emptyTables(true,
                            DisSalePrizePackageSDS.DisSalePrizePackageSDSTbl,
                            CustomerOldInvoiceDisSale.CustomerOldInvoiceDisSaleTbl,
                            CustomerOldInvoiceDetail.CustomerOldInvoiceDetailTbl,
                            CustomerOldInvoiceHeader.CustomerOldInvoiceHeaderTbl);
                    updateManager.addLog(UpdateKey.CustomerOldInvoice);
                    call.success();
                    Timber.i("List of old invoice headers was empty");
                }
            }


            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, context);
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t, "Failed to download customer old invoice");
                call.failure(context.getString(R.string.network_error));
            }
        });
    }

    private void insertDisSalePrizePackageSDSModels(final CustomerOldInvoicesViewModel result) throws ValidationException, DbException {
        List<DisSalePrizePackageSDSModel> disSalePrizePackageSDSModels = result.disSalePrizePackages;
        if (disSalePrizePackageSDSModels.size() > 0) {
            DisSalePrizePackageSDSManager disSalePrizePackageSDSManager = new DisSalePrizePackageSDSManager(context);
            long affectedRows = disSalePrizePackageSDSManager.insert(disSalePrizePackageSDSModels);
            VaranegarApplication.getInstance().printElapsedTime("old invoice disSalePrizePackages saved");
            Timber.d("Number of rows inserted = " + affectedRows);
        }
    }

    private void insertCustomerOldInvoiceDisSaleModels(final CustomerOldInvoicesViewModel result) throws ValidationException, DbException {
        List<CustomerOldInvoiceDisSaleModel> customerOldInvoiceDisSaleModels = result.disSales;
        if (customerOldInvoiceDisSaleModels.size() > 0) {
            CustomerOldInvoiceDisSaleManager customerOldInvoiceDisSaleManager = new CustomerOldInvoiceDisSaleManager(context);
            long affectedRows = customerOldInvoiceDisSaleManager.insert(customerOldInvoiceDisSaleModels);
            VaranegarApplication.getInstance().printElapsedTime("old invoice disSales saved");
            Timber.d("Number of rows inserted = " + affectedRows);
            insertCustomerOldInvoiceDisSaleVnLtModels(result);
        } else {
            insertCustomerOldInvoiceDisSaleVnLtModels(result);
        }
    }

    private void insertCustomerOldInvoiceDisSaleVnLtModels(final CustomerOldInvoicesViewModel result) throws ValidationException, DbException {
        List<CustomerOldInvoiceDisSaleVnLtModel> customerOldInvoiceDisSaleVnLtModels = result.disSalesVnLt;
        if (customerOldInvoiceDisSaleVnLtModels.size() > 0) {
            CustomerOldInvoiceDisSaleVnLtManager customerOldInvoiceDisSaleVnLtManager = new CustomerOldInvoiceDisSaleVnLtManager(context);
            long affectedRows = customerOldInvoiceDisSaleVnLtManager.insert(customerOldInvoiceDisSaleVnLtModels);
            VaranegarApplication.getInstance().printElapsedTime("old invoice disSalesVnLt saved");
            Timber.d("Number of rows inserted = " + affectedRows);
            insertDisSalePrizePackageSDSModels(result);
        } else {
            insertDisSalePrizePackageSDSModels(result);
        }
    }

    private void unzip() throws IOException {
        FileInputStream fin = context.openFileInput("oldinvoice.zip");
        GZIPInputStream zin = new GZIPInputStream(fin);
        int BUFFER = 1024;
        byte data[] = new byte[BUFFER];
        String path = context.getDatabasePath("oldinvoice.db").getPath();
        FileOutputStream fout = new FileOutputStream(path);
        int n;
        while ((n = zin.read(data, 0, BUFFER)) > -1) {
            fout.write(data, 0, n);
        }
        fout.close();
        zin.close();
    }
}
