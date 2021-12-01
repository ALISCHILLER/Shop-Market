package com.varanegar.vaslibrary.ui.report;

import android.content.Context;
import android.os.Build;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.HelperMethods;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.util.recycler.BaseRecyclerAdapter;
import com.varanegar.framework.util.recycler.BaseRecyclerView;
import com.varanegar.framework.util.recycler.BaseViewHolder;
import com.varanegar.framework.util.recycler.ItemContextView;
import com.varanegar.framework.util.report.CustomViewHolder;
import com.varanegar.framework.util.report.ReportColumns;
import com.varanegar.framework.util.report.SimpleReportAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.ReturnReasonManager;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.sysconfigmanager.BackOfficeType;
import com.varanegar.vaslibrary.manager.sysconfigmanager.ConfigKey;
import com.varanegar.vaslibrary.manager.sysconfigmanager.SysConfigManager;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnAfterDiscountView;
import com.varanegar.vaslibrary.model.customercallreturnview.CustomerCallReturnAfterDiscountViewModel;
import com.varanegar.vaslibrary.model.returnReason.ReturnReasonModel;
import com.varanegar.vaslibrary.model.returnType.ReturnType;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.user.UserModel;
import com.varanegar.vaslibrary.ui.calculator.BaseUnit;
import com.varanegar.vaslibrary.ui.calculator.DiscreteUnit;
import com.varanegar.vaslibrary.ui.calculator.returncalculator.ReturnCalculatorItem;
import com.varanegar.vaslibrary.ui.qtyview.QtyView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by atp on 4/7/2017.
 */

public class ReturnReportAfterDiscountAdapter extends SimpleReportAdapter<CustomerCallReturnAfterDiscountViewModel> {
    private final boolean withRef;
    private boolean returnTypeEnabled;
    private BackOfficeType backOfficeType;
    private boolean isFromRequest;

    public ReturnReportAfterDiscountAdapter(MainVaranegarActivity activity, boolean withRef, BackOfficeType backOfficeType, boolean isFromRequest) {
        super(activity, CustomerCallReturnAfterDiscountViewModel.class);
        this.withRef = withRef;
        this.backOfficeType = backOfficeType;
        this.isFromRequest=isFromRequest;
    }

    @Override
    protected ItemContextView<CustomerCallReturnAfterDiscountViewModel> onCreateContextView() {
        return new ReturnReportAdapterContextView(getAdapter(), getActivity());
    }

    @Override
    public void bind(ReportColumns columns, CustomerCallReturnAfterDiscountViewModel entity) {
        bindRowNumber(columns);
        columns.add(bind(entity, CustomerCallReturnAfterDiscountView.ProductCode, activity.getString(R.string.product_code)).setSortable().setFrizzed());
        columns.add(bind(entity, CustomerCallReturnAfterDiscountView.ProductName, activity.getString(R.string.product_name)).setSortable().setWeight(2));
        columns.add(bind(entity, CustomerCallReturnAfterDiscountView.TotalReturnQty, activity.getString(R.string.returned_qty_label)).setCustomViewHolder(new CustomViewHolder<CustomerCallReturnAfterDiscountViewModel>() {
            @Override
            public void onBind(View view, CustomerCallReturnAfterDiscountViewModel entity) {
                String[] allQtys = entity.Qty.split("\\|");
                String[] allUnitNames = entity.UnitName.split("\\|");
                String[] unitNames = allUnitNames[0].split(":");
                List<BaseUnit> units = new ArrayList<>();
                for (int i = 0; i < unitNames.length; i++) {
                    BaseUnit unit = new BaseUnit();
                    unit.Name = unitNames[i];
                    for (int j = 0; j < allQtys.length; j++) {
                        String[] reasonQtys = allQtys[j].split(":");
                        if (reasonQtys.length > i)
                            unit.value += Double.parseDouble(reasonQtys[i]);
                    }
                    units.add(unit);
                }
                new QtyView().build((LinearLayout) view, units);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                return new LinearLayout(inflater.getContext());
            }
        }).setWeight(1.5f));
        columns.add(bind(entity, CustomerCallReturnAfterDiscountView.TotalReturnQty, activity.getString(R.string.total_qty)).setSortable().setWeight(1).calcTotal());
        if (withRef) {
            columns.add(bind(entity, CustomerCallReturnAfterDiscountView.IsPromoLine, activity.getString(R.string.prize)).setCustomViewHolder(new CustomViewHolder<CustomerCallReturnAfterDiscountViewModel>() {

                @Override
                public void onBind(View view, CustomerCallReturnAfterDiscountViewModel entity) {
                    if (entity.IsPromoLine)
                        view.findViewById(R.id.is_promo_image_view).setVisibility(View.VISIBLE);
                    else
                        view.findViewById(R.id.is_promo_image_view).setVisibility(View.INVISIBLE);
                }

                @Override
                public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.promotion_row_indicate_layout, parent, false);
                }
            }));
        }
        columns.add(bind(entity, CustomerCallReturnAfterDiscountView.TotalRequestAmount, activity.getString(R.string.return_amount)).setSortable().setWeight(1).calcTotal());

        if (withRef) {
            columns.add(bind(entity, CustomerCallReturnAfterDiscountView.TotalRequestNetAmount, activity.getString(R.string.sell_return_net_amount)).setSortable().setWeight(1.5f).calcTotal());

            if (isFromRequest){
                columns.add(bind(entity, CustomerCallReturnAfterDiscountView.ReturnRequestBackOfficeNo, activity.getString(R.string.invoice_no)).setSortable());
            }else {
                columns.add(bind(entity, CustomerCallReturnAfterDiscountView.SaleNo, activity.getString(R.string.invoice_no)).setSortable());
            }
            columns.add(bind(entity, CustomerCallReturnAfterDiscountView.InvoiceQty, activity.getString(R.string.invoice_qty_label)));
        }

        if (backOfficeType == BackOfficeType.ThirdParty) {
            columns.add(bind(entity, CustomerCallReturnAfterDiscountView.Comment, activity.getString(R.string.request_no)).setSortable().setWeight(1.5f));
            columns.add(bind(entity, CustomerCallReturnAfterDiscountView.ReturnReasonName, activity.getString(R.string.return_reason)).setWeight(2.5f));
            columns.add(bind(entity, CustomerCallReturnAfterDiscountView.EditReasonName, activity.getString(R.string.edit_reason)).setWeight(2.5f));
        }
    }

    class ReturnReportAdapterContextView extends ItemContextView<CustomerCallReturnAfterDiscountViewModel> {

        private ArrayList<ReturnCalculatorItem> returnCalculatorItemList = new ArrayList<>();

        public ReturnReportAdapterContextView(BaseRecyclerAdapter<CustomerCallReturnAfterDiscountViewModel> adapter, Context context) {
            super(adapter, context);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, int position) {
            View view = inflater.inflate(R.layout.return_report_adapter_context_view, viewGroup, false);

            SysConfigManager sysConfigManager = new SysConfigManager(getContext());
            SysConfigModel AllowReturnDamagedProductSys = sysConfigManager.read(ConfigKey.AllowReturnDamagedProduct, SysConfigManager.cloud);
            SysConfigModel AllowReturnIntactProductSys = sysConfigManager.read(ConfigKey.AllowReturnIntactProduct, SysConfigManager.cloud);
            if (SysConfigManager.compare(AllowReturnDamagedProductSys, false) && SysConfigManager.compare(AllowReturnIntactProductSys, false))
                returnTypeEnabled = false;

            if (!returnTypeEnabled)
                view.findViewById(R.id.return_type_header_text_view).setVisibility(View.GONE);

            CustomerCallReturnAfterDiscountViewModel returnViewModel = adapter.get(position);
            ((TextView) view.findViewById(R.id.product_name_text_view)).setText(returnViewModel.ProductName);
            ((TextView) view.findViewById(R.id.total_return_text_view)).setText(HelperMethods.bigDecimalToString(returnViewModel.TotalReturnQty));
            if (returnViewModel.Comment == null)
                ((TextView) view.findViewById(R.id.comment_text_view)).setText(" --- ");
            else
                ((TextView) view.findViewById(R.id.comment_text_view)).setText(returnViewModel.Comment);

            UserManager userManager = new UserManager(getContext());
            if (returnViewModel.DealerUniqueId != null) {
                UserModel userModel = userManager.getItem(returnViewModel.DealerUniqueId);
                if (userModel == null)
                    ((TextView) view.findViewById(R.id.dealer_name_text_view)).setText(R.string.unKnown);
                else
                    ((TextView) view.findViewById(R.id.dealer_name_text_view)).setText(userModel.UserName);
            } else
                ((TextView) view.findViewById(R.id.dealer_name_text_view)).setText(R.string.unKnown);

            String[] returnTypeIds = returnViewModel.ReturnProductTypeId.split(":");
            String[] returnReasonIds = returnViewModel.ReturnReasonId.split(":");
            ReturnReasonManager returnReasonManager = new ReturnReasonManager(getActivity());
            List<ReturnReasonModel> reasonModels = returnReasonManager.getAll();
            String[] allQtys = returnViewModel.Qty.split("\\|");
            String[] allUnitIds = returnViewModel.ProductUnitId.split("\\|");
            String[] allUnitNames = returnViewModel.UnitName.split("\\|");
            String[] allConvertFactors = returnViewModel.ConvertFactor.split("\\|");
            for (int i = 0; i < returnReasonIds.length; i++) {
                UUID returnTypeId = UUID.fromString(returnTypeIds[i]);
                final UUID returnReasonId = UUID.fromString(returnReasonIds[i]);
                ReturnReasonModel reason = Linq.findFirst(reasonModels, new Linq.Criteria<ReturnReasonModel>() {
                    @Override
                    public boolean run(ReturnReasonModel item) {
                        return item.UniqueId.equals(returnReasonId);
                    }
                });
                ReturnCalculatorItem returnCalculatorItem = new ReturnCalculatorItem(getActivity(), returnTypeId, ReturnType.getName(getActivity(), returnTypeId), returnReasonId, reason.ReturnReasonName);
                String[] reasonQtyUnits = allQtys[i].split(":");
                String[] reasonUnits = allUnitIds[i].split(":");
                String[] reasonUnitNames = allUnitNames[i].split(":");
                String[] reasonConvertFactors = allConvertFactors[i].split(":");
                for (int j = 0; j < reasonQtyUnits.length; j++) {
                    DiscreteUnit discreteUnit = new DiscreteUnit();
                    discreteUnit.ConvertFactor = Double.parseDouble(reasonConvertFactors[j]);
                    discreteUnit.Name = reasonUnitNames[j];
                    discreteUnit.value = Double.parseDouble(reasonQtyUnits[j]);
                    discreteUnit.ProductUnitId = UUID.fromString(reasonUnits[j]);
                    returnCalculatorItem.getDiscreteUnits().add(discreteUnit);
                }
                returnCalculatorItemList.add(returnCalculatorItem);
            }

            BaseRecyclerAdapter<ReturnCalculatorItem> adapter = new BaseRecyclerAdapter<ReturnCalculatorItem>(getActivity(), returnCalculatorItemList) {
                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.return_item_row_layout, parent, false);
                    return new CalcItemViewHolder(view, this, getActivity());
                }
            };

            BaseRecyclerView recyclerView = (BaseRecyclerView) view.findViewById(R.id.return_report_recycler_view);
            recyclerView.setAdapter(adapter);

            TextView returnTypeTextView = (TextView) view.findViewById(R.id.return_type_text_view);
            View invoiceLayout = view.findViewById(R.id.invoice_layout);
            if (returnViewModel.InvoiceId == null) {
                invoiceLayout.setVisibility(View.GONE);
                returnTypeTextView.setText(R.string.return_without_reference);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    returnTypeTextView.setBackgroundColor(getActivity().getColor(R.color.green));
                } else {
                    returnTypeTextView.setBackgroundColor(getActivity().getResources().getColor(R.color.green));
                }
            } else {
                invoiceLayout.setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.invoice_no_text_view)).setText(returnViewModel.SaleNo);
                ((TextView) view.findViewById(R.id.invoice_qty_text_view)).setText(HelperMethods.bigDecimalToString(returnViewModel.InvoiceQty));
                returnTypeTextView.setText(R.string.return_with_reference);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    returnTypeTextView.setBackgroundColor(getActivity().getColor(R.color.red));
                } else {
                    returnTypeTextView.setBackgroundColor(getActivity().getResources().getColor(R.color.red));
                }
            }

            return view;
        }

        class CalcItemViewHolder extends BaseViewHolder<ReturnCalculatorItem> {

            private final TextView returnTypeTextView;
            private final TextView returnReasonTextView;
            private final LinearLayout qtyLayout;
            private final TextView returnQtyTextView;

            public CalcItemViewHolder(View itemView, BaseRecyclerAdapter<ReturnCalculatorItem> recyclerAdapter, Context context) {
                super(itemView, recyclerAdapter, context);
                returnReasonTextView = (TextView) itemView.findViewById(R.id.return_reason_text_view);
                returnTypeTextView = (TextView) itemView.findViewById(R.id.return_type_text_view);
                if (!returnTypeEnabled)
                    returnTypeTextView.setVisibility(View.GONE);
                returnQtyTextView = (TextView) itemView.findViewById(R.id.return_qty_text_view);
                qtyLayout = (LinearLayout) itemView.findViewById(R.id.return_qty_layout);
            }

            @Override
            public void bindView(int position) {
                ReturnCalculatorItem returnCalculatorItem = recyclerAdapter.get(position);
                returnReasonTextView.setText(returnCalculatorItem.reasonName);
                returnTypeTextView.setText(returnCalculatorItem.typeName);
                returnQtyTextView.setText(HelperMethods.bigDecimalToString(returnCalculatorItem.getTotalQty()));
                List<BaseUnit> baseUnits = new ArrayList<>();
                for (DiscreteUnit unit :
                        returnCalculatorItem.getDiscreteUnits()) {
                    BaseUnit baseUnit = new BaseUnit();
                    baseUnit.value = unit.value;
                    baseUnit.Name = unit.Name;
                    baseUnit.ProductUnitId = unit.ProductUnitId;
                    baseUnits.add(baseUnit);
                }
                new QtyView().build(qtyLayout, baseUnits);
            }
        }
    }
}
