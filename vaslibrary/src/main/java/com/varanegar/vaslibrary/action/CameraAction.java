package com.varanegar.vaslibrary.action;

import androidx.annotation.Nullable;

import com.varanegar.framework.base.MainVaranegarActivity;
import com.varanegar.framework.util.fragment.extendedlist.ActionsAdapter;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerManager;
import com.varanegar.vaslibrary.manager.picture.PictureCustomerViewManager;
import com.varanegar.vaslibrary.manager.picture.PictureTemplateManager;
import com.varanegar.vaslibrary.model.customercall.TaskPriorityModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerModel;
import com.varanegar.vaslibrary.ui.fragment.picturecustomer.CameraFragment;

import java.util.List;
import java.util.UUID;

/**
 * Created by A.Torabi on 7/4/2017.
 */

public class CameraAction extends CheckDistanceAction {

    @Nullable
    @Override
    public UUID getTaskUniqueId() {
        return UUID.fromString("46BBCC77-5E7E-4213-9347-AF9BA7D3097A");
    }

    public CameraAction(MainVaranegarActivity activity, ActionsAdapter adapter, UUID selectedId) {
        super(activity, adapter, selectedId);
        icon = R.drawable.ic_camera_alt_black_24dp;
    }

    @Override
    public String getName() {
        return getActivity().getString(R.string.customer_picture);
    }

    @Override
    @Nullable
    public String isEnabled() {
        String error = super.isEnabled();
        if (error != null)
            return error;

        if (getCallManager().isDataSent(getCalls(), null))
            return getActivity().getString(R.string.customer_operation_is_sent_already);

        if (getCallManager().isLackOfVisit(getCalls()))
            return getActivity().getString(R.string.customer_is_not_visited);
        TaskPriorityModel taskPriorityModel = getTaskPriorities().get(getTaskUniqueId());
        if (((VasActionsAdapter) getAdapter()).checkPriorities() && taskPriorityModel != null && !taskPriorityModel.IsEnabled && isMandatory() == null)
            return getActivity().getString(R.string.the_action_is_disabled_for_you);
        return null;
    }

    @Override
    public boolean isDone() {
        CustomerCallManager callManager = new CustomerCallManager(getActivity());
        return callManager.hasCameraCall(getCalls());
    }

    @Nullable
    @Override
    protected String isMandatory() {
        if (getCallManager().isLackOfVisit(getCalls()))
            return null;
        return new PictureCustomerViewManager(getActivity()).checkMandatoryPicture(getSelectedId(), getCalls());
    }


    @Override
    public void run() {
        PictureTemplateManager pictureTemplateManager = new PictureTemplateManager(getActivity());
        try {
            pictureTemplateManager.calculateCustomerPictures(getSelectedId(), getCalls());
            PictureCustomerManager pictureCustomerManager = new PictureCustomerManager(getActivity());
            List<PictureCustomerModel> subjects = pictureCustomerManager.getCustomerSubjects(getSelectedId());
            if (subjects.size() == 0)
                getActivity().showSnackBar(R.string.no_subject_to_take_picture, MainVaranegarActivity.Duration.Short);
            else {
                CameraFragment fragment = new CameraFragment();
                fragment.setCustomerId(getSelectedId());
                getActivity().pushFragment(fragment);
            }
        } catch (Exception ex) {
            getActivity().showSnackBar(R.string.calculating_pictures_failed, MainVaranegarActivity.Duration.Short);
        }

    }

}
