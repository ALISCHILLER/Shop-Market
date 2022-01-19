package com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report;

import android.content.Context;

import com.unnamed.b.atv.model.TreeNode;
import com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model.ReturnCustomerModel;
import com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model.ReturnDealerModel;
import com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model.ReturnReasonModel;
import com.varanegar.vaslibrary.ui.report.report_new.orderReturn_report.model.ReturnReportFlat;


import java.util.ArrayList;
import java.util.List;

public class ReturnFlatGenerator {
    private final TreeNode root;
    private final Context mContext;
    private final List<ReturnDealerModel> result;

    public ReturnFlatGenerator(
            Context mContext,
            TreeNode root,
            List<ReturnDealerModel> result) {
        this.root = root;
        this.mContext = mContext;
        this.result = result;
    }

    private List<ReturnReportFlat> generateTreeData() {
        List<ReturnReportFlat> reports = new ArrayList<>();
        for (ReturnDealerModel report : result) {
            ReturnReportFlat rep = new ReturnReportFlat(
                    report.getDealerName(),
                    report.getDealerCode(),
                    report.getProductCountCa(),
                    "",
                    "",
                    "",
                    ""

            );
            List<ReturnReportFlat> customers = new ArrayList<>();
            for (ReturnCustomerModel customer : report.getCustomerModels()) {
                ReturnReportFlat cus = new ReturnReportFlat(
                        "",
                        "",
                        customer.getProductCountCa(),
                        customer.getCustomerName(),
                        customer.getCustomerCode(),
                        "",
                        ""
                );
                List<ReturnReportFlat> reasons = new ArrayList<>();
                for (ReturnReasonModel reason : customer.getReturnReasonModels()) {
                    ReturnReportFlat reas = new ReturnReportFlat(
                            "",
                            "",
                            reason.getProductCountCa(),
                            "",
                            "",
                            reason.getReason(),
                            reason.getReasonCode()
                    );
                    reasons.add(reas);
                }
                cus.setChilds(reasons);
                customers.add(cus);
            }
            rep.setChilds(customers);
            reports.add(rep);
        }
        return reports;
    }

    private void generateTreeFromData(List<ReturnReportFlat> treeData) {
        for (ReturnReportFlat level1 : treeData) {
            TreeNode l1 = new TreeNode(level1)
                    .setViewHolder(new ReturnTreeNodeHolder(mContext, treeData));

            for (ReturnReportFlat level2 : level1.getChilds()) {
                TreeNode l2 = new TreeNode(level2)
                        .setViewHolder(new ReturnTreeNodeHolder(mContext, null));

                for (ReturnReportFlat level3 : level2.getChilds()) {
                    TreeNode l3 = new TreeNode(level3)
                            .setViewHolder(new ReturnTreeNodeHolder(mContext, null));
                    l2.addChild(l3);
                }
                l1.addChild(l2);
            }
            root.addChild(l1);
        }
    }

    public void build() {
        generateTreeFromData(
                generateTreeData()
        );
    }

    public TreeNode getRoot() {
        return root;
    }
}
