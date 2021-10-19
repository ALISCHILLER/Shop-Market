package com.varanegar.vaslibrary.manager.locationmanager.viewmodel;

import java.lang.reflect.Type;
import java.util.UUID;

/**
 * Created by A.Torabi on 6/11/2018.
 */

public class EventTypeId {
    public static final UUID LackOfOrder = UUID.fromString("E8ED4D92-CBC0-40F1-A732-53CFF4C91AC5");
    public static final UUID LackOfVisit = UUID.fromString("E68F7931-2189-4000-B173-D02719720923");
    public static final UUID BatteryLow = UUID.fromString("d68b5868-4cfe-47d8-8f36-1f4bb73beec5");
    public static final UUID DeviceReport = UUID.fromString("81ed18b6-ebd8-4348-97dd-3443a8cec30e");
    public static final UUID GpsOff = UUID.fromString("27F67EDA-9A75-467C-A2EC-AD6D73DBAD0D");
    public static final UUID GpsOn = UUID.fromString("C09CD4B1-45D9-4EE4-933F-3D11D8A8616E");
    public static final UUID OpenApp = UUID.fromString("33B2106D-F9AE-46FE-8C8D-D19C3CAA93C5");
    public static final UUID Order = UUID.fromString("E780728B-9BAC-4E86-AFE0-9886AD101128");
    public static final UUID EditOrder = UUID.fromString("5d90730c-2218-4992-a9fe-62b3e7d31726");
    public static final UUID SendTour = UUID.fromString("69120FD8-2889-4A61-ADF3-79BD392FB4C4");
    public static final UUID StartTour = UUID.fromString("66F98B69-0CF4-49C3-897B-E8E87DB6E7E6");
    public static final UUID EnterVisitDay = UUID.fromString("208C91CB-92DF-44D9-A096-63196F878ECA");
    public static final UUID ExitVisitDay = UUID.fromString("3987BAF4-BEFE-4B0D-B3E9-FCCB3B54E990");
    public static final UUID EnterRegion = UUID.fromString("8F3AD929-5078-4CF7-BFC9-23B0C618E0E9");
    public static final UUID ExitRegion = UUID.fromString("93CB3196-EC22-402D-80F7-6BF2D4DE82F9");
    public static final UUID EnterCompany = UUID.fromString("AFB68462-3CA6-4CFB-9046-B66C742432B8");
    public static final UUID ExitCompany = UUID.fromString("55D1A587-F234-4C43-86B9-915FD6BB8E3B");
    public static final UUID Wait = UUID.fromString("3A50BF2E-6933-4BFA-A0BC-6E645FBFBD01");
    public static final UUID WifiOff = UUID.fromString("f8e19bae-fd99-447e-a484-aef0f0a07951");
    public static final UUID WifiOn = UUID.fromString("2bf2e7c3-a750-4e13-a43c-7f03341cc6fa");
    public static final UUID MobileDataOff = UUID.fromString("5eb2bb25-807e-472e-bb52-dff14199b1c4");
    public static final UUID MobileDataOn = UUID.fromString("8b38efdb-93cd-4fb1-afd1-41683d4873a8");
    public static final UUID SummaryTour = UUID.fromString("1b1007cb-b4ff-4d9b-9a3e-93a2297a3d2d");
    public static final UUID DeleteOperation = UUID.fromString("d6538f6c-6cc5-4d75-8195-a4466b0b9e17");

    public static Type getClass(UUID pointType) {
        if (pointType.equals(LackOfOrder))
            return LackOfOrderLocationViewModel.class;
        else if (pointType.equals(LackOfVisit))
            return LackOfVisitLocationViewModel.class;
        else if (pointType.equals(BatteryLow))
            return BatteryLowLocationViewModel.class;
        else if (pointType.equals(DeviceReport))
            return DeviceReportLocationViewModel.class;
        else if (pointType.equals(GpsOff))
            return GpsProviderOffLocationViewModel.class;
        else if (pointType.equals(GpsOn))
            return GpsProviderOnLocationViewModel.class;
        else if (pointType.equals(OpenApp))
            return OpenApplicationLocationViewModel.class;
        else if (pointType.equals(Order))
            return OrderLocationViewModel.class;
        else if (pointType.equals(EditOrder))
            return EditOrderLocationViewModel.class;
        else if (pointType.equals(SendTour))
            return SendTourLocationViewModel.class;
        else if (pointType.equals(StartTour))
            return StartTourLocationViewModel.class;
        else if (pointType.equals(EnterVisitDay) ||
                pointType.equals(ExitVisitDay) ||
                pointType.equals(EnterCompany) ||
                pointType.equals(ExitCompany) ||
                pointType.equals(EnterRegion) ||
                pointType.equals(ExitRegion))
            return TransitionEventLocationViewModel.class;
        else if (pointType.equals(Wait))
            return WaitLocationViewModel.class;
        else if (pointType.equals(WifiOn))
            return WifiOnLocationViewModel.class;
        else if (pointType.equals(WifiOff))
            return WifiOffLocationViewModel.class;
        else if (pointType.equals(MobileDataOn))
            return MobileDataOnLocationViewModel.class;
        else if (pointType.equals(MobileDataOff))
            return MobileDataOffLocationViewModel.class;
        else if (pointType.equals(SummaryTour))
            return SummaryTourLocationViewModel.class;
        else
            return DeleteOperationLocationViewModel.class;
    }
}
