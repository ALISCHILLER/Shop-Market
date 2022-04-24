package com.varanegar.vaslibrary.manager.updatemanager;

import android.content.Context;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.CountyManager;
import com.varanegar.vaslibrary.manager.CustExtraFieldManager;
import com.varanegar.vaslibrary.manager.CustomerBoGroupManager;
import com.varanegar.vaslibrary.manager.CustomerMainSubTypeManager;
import com.varanegar.vaslibrary.manager.CustomerOrderTypesManager;
import com.varanegar.vaslibrary.manager.CustomerOwnerTypeManager;
import com.varanegar.vaslibrary.manager.CustomerRemainPerLineManager;
import com.varanegar.vaslibrary.manager.DataForRegisterManager;
import com.varanegar.vaslibrary.manager.DealerPaymentTypeManager;
import com.varanegar.vaslibrary.manager.DisAccManager;
import com.varanegar.vaslibrary.manager.FreeReasonManager;
import com.varanegar.vaslibrary.manager.GoodsCustQuotasManager;
import com.varanegar.vaslibrary.manager.GoodsFixUnitManager;
import com.varanegar.vaslibrary.manager.GoodsNosaleManager;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.OnHandQtyManager;
import com.varanegar.vaslibrary.manager.PaymentOrderTypeManager;
import com.varanegar.vaslibrary.manager.PaymentUsancesManager;
import com.varanegar.vaslibrary.manager.PriceClassVnLiteManager;
import com.varanegar.vaslibrary.manager.PriceHistoryManager;
import com.varanegar.vaslibrary.manager.ProductBoGroupManager;
import com.varanegar.vaslibrary.manager.ProductGroupCatalogManager;
import com.varanegar.vaslibrary.manager.ProductGroupManager;
import com.varanegar.vaslibrary.manager.ProductMainSubTypeManager;
import com.varanegar.vaslibrary.manager.ProductManager;
import com.varanegar.vaslibrary.manager.RegionAreaPointManager;
import com.varanegar.vaslibrary.manager.ReturnReasonManager;
import com.varanegar.vaslibrary.manager.StateManager;
import com.varanegar.vaslibrary.manager.Target.TargetManager;
import com.varanegar.vaslibrary.manager.UnitManager;
import com.varanegar.vaslibrary.manager.ValidPayTypeManager;
import com.varanegar.vaslibrary.manager.VisitTemplatePathCustomerManager;
import com.varanegar.vaslibrary.manager.bank.BankManager;
import com.varanegar.vaslibrary.manager.c_shipToparty.CustomerShipToPartyManager;
import com.varanegar.vaslibrary.manager.catalogmanager.CatalogManager;
import com.varanegar.vaslibrary.manager.city.CityManager;
import com.varanegar.vaslibrary.manager.contractpricemanager.ContractPriceManager;
import com.varanegar.vaslibrary.manager.customer.CustomerActivityManager;
import com.varanegar.vaslibrary.manager.customer.CustomerBarcodeManager;
import com.varanegar.vaslibrary.manager.customer.CustomerCategoryManager;
import com.varanegar.vaslibrary.manager.customer.CustomerLevelManager;
import com.varanegar.vaslibrary.manager.customer.CustomerManager;
import com.varanegar.vaslibrary.manager.customercallmanager.TaskPriorityManager;
import com.varanegar.vaslibrary.manager.customercardex.CustomerCardexManager;
import com.varanegar.vaslibrary.manager.dealer.DealerManager;
import com.varanegar.vaslibrary.manager.discountmanager.DiscountVnLiteManager;
import com.varanegar.vaslibrary.manager.discountmanager.ProductTaxInfoManager;
import com.varanegar.vaslibrary.manager.dissaleprizepackagesdsmanager.DisSalePrizePackageSDSManager;
import com.varanegar.vaslibrary.manager.emphaticitems.EmphaticProductManager;
import com.varanegar.vaslibrary.manager.evcstatutemanager.EvcStatuteTemplateManager;
import com.varanegar.vaslibrary.manager.goodspackageitemmanager.GoodsPackageItemManager;
import com.varanegar.vaslibrary.manager.goodspackagemanager.GoodsPackageManager;
import com.varanegar.vaslibrary.manager.image.ImageManager;
import com.varanegar.vaslibrary.manager.image.LogoManager;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerHistoryManager;
import com.varanegar.vaslibrary.manager.picture.PictureSubjectManager;

import com.varanegar.vaslibrary.manager.picture.PictureSubjectZarManager;
import com.varanegar.vaslibrary.manager.picture.PictureTemplateManager;
import com.varanegar.vaslibrary.manager.productUnit.ProductUnitManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireHistoryManager;
import com.varanegar.vaslibrary.manager.questionnaire.QuestionnaireManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.OwnerKeysWrapper;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.UnknownBackOfficeException;
import com.varanegar.vaslibrary.manager.visitday.VisitDayManager;
import com.varanegar.vaslibrary.model.product.ProductModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandler;
import com.varanegar.vaslibrary.promotion.V3.DiscountInitializeHandlerV3;
import com.varanegar.vaslibrary.ui.fragment.news_fragment.NewsZarManager;

import java.util.List;

import timber.log.Timber;
import varanegar.com.discountcalculatorlib.DiscountCalculatorHandler;
import varanegar.com.vdmclient.VdmInitializer;

/**
 * Created by A.Torabi on 2/14/2018.
 */

public abstract class TourUpdateFlow extends UpdateFlow {
    public TourUpdateFlow(Context context) {
        super(context);
    }


    @Override
    @CallSuper
    protected void addAsyncTasks(List<TourAsyncTask> tasks, UpdateCall call) {
        try {
            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel simplePresale = sysConfigManager.read(ConfigKey.SimplePresale, SysConfigManager.cloud);
            boolean isSimpleMode = SysConfigManager.compare(simplePresale, true) || VaranegarApplication.is(VaranegarApplication.AppId.Contractor);
            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
            if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
                        sysConfigManager.syncPersonnelMetaData(call);
                    }

                    @Override
                    public String name() {
                        return "PersonnelMetaData";
                    }

                    @Override
                    public int group() {
                        return com.varanegar.vaslibrary.R.string.base_info;
                    }

                    @Override
                    public int queueId() {
                        return 0;
                    }
                });
            }

            if (!VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        DealerManager dealerManager = new DealerManager(getContext());
                        dealerManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "Dealer";
                    }

                    @Override
                    public int group() {
                        return R.string.base_info;
                    }

                    @Override
                    public int queueId() {
                        return 0;
                    }
                });
            }
            if (backOfficeType == BackOfficeType.Varanegar) {
                if (VaranegarApplication.is(VaranegarApplication.AppId.PreSales)) {
                    tasks.add(new TourAsyncTask() {
                        GoodsCustQuotasManager goodsCustQuotasManager = new GoodsCustQuotasManager(getContext());

                        @Override
                        public void run(UpdateCall call) {
                            goodsCustQuotasManager.sync(call);
                        }

                        @Override
                        public String name() {
                            return "GoodsCustQuotas";
                        }

                        @Override
                        public int group() {
                            return R.string.product_info;
                        }

                        @Override
                        public int queueId() {
                            return 4;
                        }

                        @Override
                        public void cancel() {
                            if (goodsCustQuotasManager != null)
                                goodsCustQuotasManager.cancelSync();
                        }
                    });
                }
                if (!VaranegarApplication.is(VaranegarApplication.AppId.Dist))
                    tasks.add(new TourAsyncTask() {
                        CustomerRemainPerLineManager customerRemainPerLineManager = new CustomerRemainPerLineManager(getContext());

                        @Override
                        public void run(UpdateCall call) {
                            customerRemainPerLineManager.sync(null, call);
                        }

                        @Override
                        public String name() {
                            return "CustomerRemainPerLine";
                        }

                        @Override
                        public int group() {
                            return R.string.customer_info;
                        }

                        @Override
                        public int queueId() {
                            return 0;
                        }

                        @Override
                        public void cancel() {
                            if (customerRemainPerLineManager != null)
                                customerRemainPerLineManager.cancelSync();
                        }
                    });

//                tasks.add(new TourAsyncTask() {
//                    DiscountSDSManager discountSDSManager = new DiscountSDSManager(getContext());
//
//                    @Override
//                    public void run(UpdateCall call) {
//                        discountSDSManager.sync(call);
//                    }
//
//                    @Override
//                    public String name() {
//                        return "DiscountSDS";
//                    }
//
//                    @Override
//                    public int group() {
//                        return R.string.discounts_and_awards;
//                    }
//
//                    @Override
//                    public int queueId() {
//                        return 1;
//                    }
//
//                    @Override
//                    public void cancel() {
//                        if (discountSDSManager != null)
//                            discountSDSManager.cancelSync();
//                    }
//                });
//                tasks.add(new TourAsyncTask() {
//                    DiscountConditionManager discountConditionManager = new DiscountConditionManager(getContext());
//
//                    @Override
//                    public void run(UpdateCall call) {
//                        discountConditionManager.sync(call);
//                    }
//
//                    @Override
//                    public String name() {
//                        return "DiscountCondition";
//                    }
//
//                    @Override
//                    public int group() {
//                        return R.string.discounts_and_awards;
//                    }
//
//                    @Override
//                    public int queueId() {
//                        return 1;
//                    }
//
//                    @Override
//                    public void cancel() {
//                        if (discountConditionManager != null)
//                            discountConditionManager.cancelSync();
//                    }
//                });
                tasks.add(new TourAsyncTask() {
                    GoodsPackageManager manager = new GoodsPackageManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        manager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "GoodsPackage";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 2;
                    }

                    @Override
                    public void cancel() {
                        if (manager != null)
                            manager.cancelSync();
                    }
                });
                tasks.add(new TourAsyncTask() {
                    GoodsPackageItemManager manager = new GoodsPackageItemManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        manager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "GoodsPackageItem";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 3;
                    }

                    @Override
                    public void cancel() {
                        if (manager != null)
                            manager.cancelSync();
                    }
                });
                tasks.add(new TourAsyncTask() {
                    DisSalePrizePackageSDSManager manager = new DisSalePrizePackageSDSManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        manager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "DisSalePrizePackageSDS";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 3;
                    }

                    @Override
                    public void cancel() {
                        if (manager != null)
                            manager.cancelSync();
                    }
                });
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        CustomerMainSubTypeManager customerMainSubTypeManager = new CustomerMainSubTypeManager(getContext());
                        customerMainSubTypeManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "CustomerMainSubType";
                    }

                    @Override
                    public int group() {
                        return R.string.customer_info;
                    }

                    @Override
                    public int queueId() {
                        return 3;
                    }
                });
//                tasks.add(new TourAsyncTask() {
//                    DiscountGoodManager discountGoodManager = new DiscountGoodManager(getContext());
//
//                    @Override
//                    public void run(UpdateCall call) {
//                        discountGoodManager.sync(call);
//                    }
//
//                    @Override
//                    public String name() {
//                        return "DiscountGood";
//                    }
//
//                    @Override
//                    public int group() {
//                        return R.string.product_info;
//                    }
//
//                    @Override
//                    public int queueId() {
//                        return 1;
//                    }
//
//                    @Override
//                    public void cancel() {
//                        if (discountGoodManager!= null)
//                        discountGoodManager.cancel();
//                    }
//                });
                tasks.add(new TourAsyncTask() {
                    PaymentUsancesManager paymentUsancesManager = new PaymentUsancesManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        paymentUsancesManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "PaymentUsances";
                    }

                    @Override
                    public int group() {
                        return R.string.price_info;
                    }

                    @Override
                    public int queueId() {
                        return 2;
                    }

                    @Override
                    public void cancel() {
                        if (paymentUsancesManager != null)
                            paymentUsancesManager.cancel();
                    }
                });
//                tasks.add(new TourAsyncTask() {
//                    RetSaleHdrManager retSaleHdrManager = new RetSaleHdrManager(getContext());
//
//                    @Override
//                    public void run(UpdateCall call) {
//                        retSaleHdrManager.sync(call);
//                    }
//
//                    @Override
//                    public String name() {
//                        return "RetSaleHdr";
//                    }
//
//                    @Override
//                    public int group() {
//                        return R.string.discounts_and_awards;
//                    }
//
//                    @Override
//                    public int queueId() {
//                        return 3;
//                    }
//
//                    @Override
//                    public void cancel() {
//                        retSaleHdrManager.cancel();
//                    }
//                });
//                tasks.add(new TourAsyncTask() {
//                    RetSaleItemManager retSaleItemManager = new RetSaleItemManager(getContext());
//
//                    @Override
//                    public void run(UpdateCall call) {
//                        retSaleItemManager.sync(call);
//                    }
//
//                    @Override
//                    public String name() {
//                        return "RetSaleItem";
//                    }
//
//                    @Override
//                    public int group() {
//                        return R.string.discounts_and_awards;
//                    }
//
//                    @Override
//                    public int queueId() {
//                        return 4;
//                    }
//
//                    @Override
//                    public void cancel() {
//                        retSaleItemManager.cancel();
//                    }
//                });
                tasks.add(new TourAsyncTask() {
                    GoodsNosaleManager goodsNosaleManager = new GoodsNosaleManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        goodsNosaleManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "GoodsNosale";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 3;
                    }

                    @Override
                    public void cancel() {
                        if (goodsNosaleManager != null)
                            goodsNosaleManager.cancel();
                    }
                });
                tasks.add(new TourAsyncTask() {
                    GoodsFixUnitManager goodsFixUnitManager = new GoodsFixUnitManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        goodsFixUnitManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "GoodsFixUnit";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 2;
                    }

                    @Override
                    public void cancel() {
                        if (goodsFixUnitManager != null)
                            goodsFixUnitManager.cancel();
                    }
                });
                tasks.add(new TourAsyncTask() {
                    DisAccManager disAccManager = new DisAccManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        disAccManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "DisAcc";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 1;
                    }

                    @Override
                    public void cancel() {
                        if (disAccManager != null)
                            disAccManager.cancel();
                    }
                });
                tasks.add(new TourAsyncTask() {
                    CustExtraFieldManager custExtraFieldManager = new CustExtraFieldManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        custExtraFieldManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "CustExtraField";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 2;
                    }

                    @Override
                    public void cancel() {
                        if (custExtraFieldManager != null)
                            custExtraFieldManager.cancel();
                    }
                });
                tasks.add(new TourAsyncTask() {
                    CustomerBoGroupManager customerBoGroupManager = new CustomerBoGroupManager(getContext());

                    @Override
                    public void run(UpdateCall call) {
                        customerBoGroupManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "CustomerBoGroup";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 2;
                    }

                    @Override
                    public void cancel() {
                        if (customerBoGroupManager != null)
                            customerBoGroupManager.cancel();
                    }
                });
            }
            if (backOfficeType == BackOfficeType.Rastak) {
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        DiscountVnLiteManager discountVnLiteManager = new DiscountVnLiteManager(getContext());
                        discountVnLiteManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "DiscountVnLite";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 1;
                    }
                });
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        ProductTaxInfoManager productTaxInfoManager = new ProductTaxInfoManager(getContext());
                        productTaxInfoManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "ProductTaxInfo";
                    }

                    @Override
                    public int group() {
                        return R.string.discounts_and_awards;
                    }

                    @Override
                    public int queueId() {
                        return 1;
                    }
                });
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        PriceClassVnLiteManager priceClassVnLiteManager = new PriceClassVnLiteManager(getContext());
                        priceClassVnLiteManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "PriceClass";
                    }

                    @Override
                    public int group() {
                        return R.string.price_info;
                    }

                    @Override
                    public int queueId() {
                        return 2;
                    }
                });
            }
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    TaskPriorityManager taskPriorityManager = new TaskPriorityManager(getContext());
                    taskPriorityManager.sync(call);
                }

                @Override
                public String name() {
                    return "TaskPriority";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    FreeReasonManager manager = new FreeReasonManager(getContext());
                    manager.sync(call);
                }

                @Override
                public String name() {
                    return "FreeReasons";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CustomerActivityManager manager = new CustomerActivityManager(getContext());
                    manager.sync(call);
                }

                @Override
                public String name() {
                    return "CustomerActivity";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CustomerLevelManager manager = new CustomerLevelManager(getContext());
                    manager.sync(call);
                }

                @Override
                public String name() {
                    return "CustomerLevel";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CustomerCategoryManager manager = new CustomerCategoryManager(getContext());
                    manager.sync(call);
                }

                @Override
                public String name() {
                    return "CustomerCategory";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });

            tasks.add(new TourAsyncTask() {
                CustomerShipToPartyManager shipToPartyManager = new
                        CustomerShipToPartyManager(getContext());
                @Override
                public void run(UpdateCall call) {
                    shipToPartyManager.sync(call, true);
                }

                @Override
                public String name() {
                    return "shipToPartyManager";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }

                @Override
                public void cancel() {
                    if (shipToPartyManager != null)
                        shipToPartyManager.cancelSync();
                }
            });
            tasks.add(new TourAsyncTask() {
                CustomerManager customerManager = new CustomerManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    customerManager.sync(call, true);
                }

                @Override
                public String name() {
                    return "Customer";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }

                @Override
                public void cancel() {
                    if (customerManager != null)
                        customerManager.cancelSync();
                }
            });


            tasks.add(new TourAsyncTask() {
                ProductGroupManager productGroupManager = new ProductGroupManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    productGroupManager.sync(call);
                }

                @Override
                public String name() {
                    return "ProductGroup";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }

                @Override
                public void cancel() {
                    if (productGroupManager != null)
                        productGroupManager.cancelSync();
                }
            });
            tasks.add(new TourAsyncTask() {
                ProductBoGroupManager productBoGroupManager = new ProductBoGroupManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    productBoGroupManager.sync(call);
                }

                @Override
                public String name() {
                    return "ProductBoGroup";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }

                @Override
                public void cancel() {
                    if (productBoGroupManager != null)
                        productBoGroupManager.cancelSync();
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    UnitManager unitManager = new UnitManager(getContext());
                    unitManager.sync(call);
                }

                @Override
                public String name() {
                    return "Unit";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });
            tasks.add(new TourAsyncTask() {
                VisitTemplatePathCustomerManager visitTemplatePathCustomerManager = new VisitTemplatePathCustomerManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    visitTemplatePathCustomerManager.sync(null, call);
                }

                @Override
                public String name() {
                    return "VisitTemplatePathCustomer";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }

                @Override
                public void cancel() {
                    if (visitTemplatePathCustomerManager != null)
                        visitTemplatePathCustomerManager.cancelSync();
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    ReturnReasonManager returnReasonManager = new ReturnReasonManager(getContext());
                    returnReasonManager.sync(call);
                }

                @Override
                public String name() {
                    return "ReturnReason";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });

                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        NewsZarManager newsZarManager = new NewsZarManager(getContext());
                        newsZarManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "Questionnaire";
                    }

                    @Override
                    public int group() {
                        return R.string.questionnairie_info;
                    }

                    @Override
                    public int queueId() {
                        return 1;
                    }
                });
            if (!isSimpleMode)
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        QuestionnaireManager questionnaireManager = new QuestionnaireManager(getContext());
                        questionnaireManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "Questionnaire";
                    }

                    @Override
                    public int group() {
                        return R.string.questionnairie_info;
                    }

                    @Override
                    public int queueId() {
                        return 2;
                    }
                });


            tasks.add(new TourAsyncTask() {
                ProductManager productManager = new ProductManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    productManager.sync(false, call);
                }

                @Override
                public String name() {
                    return "Product";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }

                @Override
                public void cancel() {
                    if (productManager != null)
                        productManager.cancelSync();
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    NoSaleReasonManager noSaleReasonManager = new NoSaleReasonManager(getContext());
                    noSaleReasonManager.sync(call);
                }

                @Override
                public String name() {
                    return "NoSaleReason";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }
            });
            tasks.add(new TourAsyncTask() {
                ContractPriceManager contractPriceManager = new ContractPriceManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    contractPriceManager.sync(call);
                }

                @Override
                public String name() {
                    return "ContractPrice";
                }

                @Override
                public int group() {
                    return R.string.price_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }

                @Override
                public void cancel() {
                    if (contractPriceManager != null)
                        contractPriceManager.cancelSync();
                }
            });
            tasks.add(new TourAsyncTask() {
                PriceHistoryManager priceHistoryManager = new PriceHistoryManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    priceHistoryManager.sync(call);
                }

                @Override
                public String name() {
                    return "PriceHistory";
                }

                @Override
                public int group() {
                    return R.string.price_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }

                @Override
                public void cancel() {
                    if (priceHistoryManager != null)
                        priceHistoryManager.cancelSync();
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CustomerOrderTypesManager customerOrderTypesManager = new CustomerOrderTypesManager(getContext());
                    customerOrderTypesManager.sync(call);
                }

                @Override
                public String name() {
                    return "CustomerOrderTypes";
                }

                @Override
                public int group() {
                    return R.string.price_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    ProductMainSubTypeManager productMainSubTypeManager = new ProductMainSubTypeManager(getContext());
                    productMainSubTypeManager.sync(call);
                }

                @Override
                public String name() {
                    return "ProductMainSubType";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    VisitDayManager visitDayManager = new VisitDayManager(getContext());
                    visitDayManager.sync(call);
                }

                @Override
                public String name() {
                    return "VisitDay";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }
            });
//            tasks.add(new SimpleTourAsyncTask() {
//                @Override
//                public void run(UpdateCall call) {
//                    DiscountItemCountManager discountItemCountManager = new DiscountItemCountManager(getContext());
//                    discountItemCountManager.sync(call);
//                }
//
//                @Override
//                public String name() {
//                    return "DiscountItemCount";
//                }
//
//                @Override
//                public int group() {
//                    return R.string.discounts_and_awards;
//                }
//
//                @Override
//                public int queueId() {
//                    return 2;
//                }
//            });
            if (!isSimpleMode)
//                tasks.add(new SimpleTourAsyncTask() {
//                    @Override
//                    public void run(UpdateCall call) {
//                        PictureSubjectManager manager = new PictureSubjectManager(getContext());
//                        manager.sync(call);
//                    }
//
//                    @Override
//                    public String name() {
//                        return "PictureSubject";
//                    }
//
//                    @Override
//                    public int group() {
//                        return R.string.customer_picture_subjects;
//                    }
//
//                    @Override
//                    public int queueId() {
//                        return 3;
//                    }
//                });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    PictureSubjectZarManager manager = new PictureSubjectZarManager(getContext());
                    manager.sync(call);
                }

                @Override
                public String name() {
                    return "PictureSubject";
                }

                @Override
                public int group() {
                    return R.string.customer_picture_subjects;
                }

                @Override
                public int queueId() {
                    return 3;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    PaymentOrderTypeManager paymentOrderTypeManager = new PaymentOrderTypeManager(getContext());
                    paymentOrderTypeManager.sync(call);
                }

                @Override
                public String name() {
                    return "PaymentOrderType";
                }

                @Override
                public int group() {
                    return R.string.price_info;
                }

                @Override
                public int queueId() {
                    return 2;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CatalogManager catalogManager = new CatalogManager(getContext());
                    catalogManager.sync(call);
                }

                @Override
                public String name() {
                    return "Catalog";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 3;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    EvcStatuteTemplateManager evcStatuteTemplateManager = new EvcStatuteTemplateManager(getContext());
                    evcStatuteTemplateManager.sync(call);
                }

                @Override
                public String name() {
                    return "EvcStatuteTemplate";
                }

                @Override
                public int group() {
                    return R.string.discounts_and_awards;
                }

                @Override
                public int queueId() {
                    return 3;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    EmphaticProductManager manager = new EmphaticProductManager(getContext());
                    manager.sync(call);
                }

                @Override
                public String name() {
                    return "EmphaticProduct";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 4;
                }
            });
            if (!isSimpleMode)
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        PictureTemplateManager manager = new PictureTemplateManager(getContext());
                        manager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "PictureTemplate";
                    }

                    @Override
                    public int group() {
                        return R.string.customer_picture_subjects;
                    }

                    @Override
                    public int queueId() {
                        return 4;
                    }
                });
            if (!isSimpleMode)
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        QuestionnaireHistoryManager questionnaireHistoryManager = new QuestionnaireHistoryManager(getContext());
                        questionnaireHistoryManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "QuestionnaireHistory";
                    }

                    @Override
                    public int group() {
                        return R.string.questionnairie_info;
                    }

                    @Override
                    public int queueId() {
                        return 4;
                    }
                });
            if (!isSimpleMode)
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        PictureCustomerHistoryManager manager = new PictureCustomerHistoryManager(getContext());
                        manager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "PictureCustomerHistory";
                    }

                    @Override
                    public int group() {
                        return R.string.customer_picture_subjects;
                    }

                    @Override
                    public int queueId() {
                        return 4;
                    }
                });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    ProductUnitManager productUnitManager = new ProductUnitManager(getContext());
                    productUnitManager.sync(false, call);
                }

                @Override
                public String name() {
                    return "ProductUnit";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 4;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    ProductGroupCatalogManager manager = new ProductGroupCatalogManager(getContext());
                    manager.sync(call);
                }

                @Override
                public String name() {
                    return "ProductGroupCatalog";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 4;
                }
            });
            tasks.add(new TourAsyncTask() {
                ImageManager imageManager = new ImageManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    imageManager.sync(call);
                }

                @Override
                public String name() {
                    return "Image";
                }

                @Override
                public int group() {
                    return R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 4;
                }

                @Override
                public void cancel() {
                    if (imageManager != null)
                        imageManager.cancelSync();
                }
            });

            tasks.add(new TourAsyncTask() {
                CustomerCardexManager customerCardexManager = new CustomerCardexManager(getContext());

                @Override
                public void run(UpdateCall call) {
                    customerCardexManager.sync(null, null, null, call);
                }

                @Override
                public String name() {
                    return "CustomerCardex";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 4;
                }

                @Override
                public void cancel() {
                    if (customerCardexManager != null)
                        customerCardexManager.cancelSync();
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    TargetManager targetManager = new TargetManager(getContext());
                    targetManager.sync(call);
                }

                @Override
                public String name() {
                    return "Target";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 4;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    OnHandQtyManager onHandQtyManager = new OnHandQtyManager(getContext());
                    onHandQtyManager.sync(call, false);
                }

                @Override
                public String name() {
                    return "OnHandQty";
                }

                @Override
                public int group() {
                    return com.varanegar.vaslibrary.R.string.product_info;
                }

                @Override
                public int queueId() {
                    return 4;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    ValidPayTypeManager validPayTypeManager = new ValidPayTypeManager(getContext());
                    validPayTypeManager.sync(call);
                }

                @Override
                public String name() {
                    return "ValiPayType";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    RegionAreaPointManager regionAreaPointManager = new RegionAreaPointManager(getContext());
                    regionAreaPointManager.sync(call);
                }

                @Override
                public String name() {
                    return "RegionAreaPoint";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CityManager cityManager = new CityManager(getContext());
                    cityManager.sync(call);
                }

                @Override
                public String name() {
                    return "Cities";
                }

                @Override
                public int group() {
                    return com.varanegar.vaslibrary.R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    BankManager bankManager = new BankManager(getContext());
                    bankManager.sync(call);
                }

                @Override
                public String name() {
                    return "Banks";
                }

                @Override
                public int group() {
                    return com.varanegar.vaslibrary.R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    StateManager stateManager = new StateManager(getContext());
                    stateManager.sync(call);
                }

                @Override
                public String name() {
                    return "states";
                }

                @Override
                public int group() {
                    return com.varanegar.vaslibrary.R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CustomerOwnerTypeManager customerOwnerTypeManager = new CustomerOwnerTypeManager(getContext());
                    customerOwnerTypeManager.sync(call);
                }

                @Override
                public String name() {
                    return "customer owner typr";
                }

                @Override
                public int group() {
                    return com.varanegar.vaslibrary.R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    CountyManager countyManager = new CountyManager(getContext());
                    countyManager.sync(call);
                }

                @Override
                public String name() {
                    return "county";
                }

                @Override
                public int group() {
                    return com.varanegar.vaslibrary.R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 0;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    DealerPaymentTypeManager dealerPaymentTypeManager = new DealerPaymentTypeManager(getContext());
                    dealerPaymentTypeManager.sync(call);
                }

                @Override
                public String name() {
                    return "DealerPaymentType";
                }

                @Override
                public int group() {
                    return R.string.price_info;
                }

                @Override
                public int queueId() {
                    return 3;
                }
            });
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    LogoManager logoManager = new LogoManager(getContext());
                    logoManager.sync(call);
                }

                @Override
                public String name() {
                    return "Logo";
                }

                @Override
                public int group() {
                    return R.string.base_info;
                }

                @Override
                public int queueId() {
                    return 4;
                }
            });
            SysConfigModel checkBarcode = sysConfigManager.read(ConfigKey.CheckBarcode, SysConfigManager.cloud);
            if (SysConfigManager.compare(checkBarcode, true))
                tasks.add(new SimpleTourAsyncTask() {
                    @Override
                    public void run(UpdateCall call) {
                        CustomerBarcodeManager customerBarcodeManager = new CustomerBarcodeManager(getContext());
                        customerBarcodeManager.sync(call);
                    }

                    @Override
                    public String name() {
                        return "CustomerBarcode";
                    }

                    @Override
                    public int group() {
                        return R.string.customer_info;
                    }

                    @Override
                    public int queueId() {
                        return 1;
                    }
                });
        } catch (UnknownBackOfficeException e) {
            call.failure(getContext().getString(R.string.back_office_type_is_uknown));
        }
        SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        OwnerKeysWrapper ownerKeysWrapper = sysConfigManager.readOwnerKeys();
        if (ownerKeysWrapper.DataOwnerKey != null && ownerKeysWrapper.isZarMakaron()) {
            tasks.add(new SimpleTourAsyncTask() {
                @Override
                public void run(UpdateCall call) {
                    DataForRegisterManager dataForRegisterManager = new DataForRegisterManager(getContext());
                    dataForRegisterManager.sync(call);
                }

                @Override
                public String name() {
                    return "DataForRegister";
                }

                @Override
                public int group() {
                    return R.string.customer_info;
                }

                @Override
                public int queueId() {
                    return 1;
                }
            });
        }

    }

    public void getTourAndInitPromotionDb(@NonNull final UpdateCall call) {
        new CustomerManager(getContext()).clearCache();
        final SysConfigManager sysConfigManager = new SysConfigManager(getContext());
        sysConfigManager.sync(new UpdateCall() {
            @Override
            protected void onSuccess() {
                start(new UpdateCall() {
                    @Override
                    protected void onFinish() {

                    }

                    @Override
                    protected void onSuccess() {
                        try {
                            if (VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                                ProductManager productManager = new ProductManager(getContext());
                                List<ProductModel> productModels = productManager.getAll();
                                for (ProductModel product :
                                        productModels) {
                                    product.IsForSale = true;
                                }
                                productManager.update(productModels);
                            }
                            DiscountInitializeHandler disc = DiscountInitializeHandlerV3.getDiscountHandler(getContext());
                            DiscountCalculatorHandler.init(disc, 0, null);
                            BackOfficeType backOfficeType = sysConfigManager.getBackOfficeType();
                            SysConfigModel newDiscountConfig = sysConfigManager.read(ConfigKey.NewDiscount, SysConfigManager.cloud);
                            if (backOfficeType == BackOfficeType.ThirdParty || newDiscountConfig == null || SysConfigManager.compare(newDiscountConfig, false)) {
                                try {
                                    DiscountCalculatorHandler.fillInitData(disc);
                                    call.success();
                                } catch (Exception ex) {
                                    call.onFailure(getContext().getString(R.string.init_discount_failed));
                                }
                            } else {
                                DiscountCalculatorHandler.fillInitData(disc, new VdmInitializer.InitCallback() {
                                    @Override
                                    public void onSuccess(String s) {
                                        call.success();
                                    }

                                    @Override
                                    public void onFailure(String s) {
                                        call.failure(s);
                                    }
                                });
                            }


                        } catch (Exception ex) {
                            Timber.e(ex);
                            call.failure(getContext().getString(R.string.init_discount_failed));
                        } finally {
                            sysConfigManager.syncAdvanceDealerCredit(null);
                        }
                    }

                    @Override
                    protected void onFailure(String error) {
                        call.failure(error);
                    }
                });

            }

            @Override
            protected void onFailure(String error) {
                call.failure(error);
            }
        });
    }

}
