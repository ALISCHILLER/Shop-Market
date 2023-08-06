package com.varanegar.vaslibrary.manager.picture;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.util.Linq;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.vaslibrary.manager.NoSaleReasonManager;
import com.varanegar.vaslibrary.manager.customercallmanager.CustomerCallManager;
import com.varanegar.vaslibrary.model.customercall.CustomerCallModel;
import com.varanegar.vaslibrary.model.noSaleReason.NoSaleReasonModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomer;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureCustomerModelRepository;
import com.varanegar.vaslibrary.model.picturesubject.PictureDemandType;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateDetailModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by A.Torabi on 10/18/2017.
 */

public class PictureCustomerManager extends BaseManager<PictureCustomerModel> {
    public PictureCustomerManager(@NonNull Context context) {
        super(context, new PictureCustomerModelRepository());
    }

    public List<PictureCustomerModel> getCustomerSubjects(UUID customerId) {
        Query query = new Query()
                .from(PictureCustomer.PictureCustomerTbl)
                .whereAnd(Criteria.equals(PictureCustomer.CustomerId, customerId.toString()));
        return getItems(query);
    }

    private void deleteSubjects(UUID customerId) throws DbException {
        delete(Criteria.equals(PictureCustomer.CustomerId, customerId.toString()));
    }

    private PictureCustomerModel getItem(UUID customerId, UUID pictureSubjectUniqueId) {
        Query query = new Query()
                .from(PictureCustomer.PictureCustomerTbl)
                .whereAnd(Criteria.equals(PictureCustomer.CustomerId, customerId.toString())
                        .and(Criteria.equals(PictureCustomer.PictureSubjectId, pictureSubjectUniqueId.toString())));
        return getItem(query);
    }


    public void savePictureTemplatesZar(final UUID customerId, final List<PictureSubjectZarModel> pictures, @Nullable List<CustomerCallModel> customerCalls)throws ValidationException, DbException{
        if (customerCalls == null)
            customerCalls = new ArrayList<>();
        // fetch all picture subjects that have been already calculated for this customer
        final List<PictureCustomerModel> existingPictures = getCustomerSubjects(customerId);

        deleteSubjects(customerId);


        CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
        boolean b =customerCallManager.isNeedImage(customerCalls);
        if (b){
            for (CustomerCallModel callModel:
                    customerCalls) {
                if (callModel.ExtraField1!=null && !callModel.ExtraField1.isEmpty()) {
                    NoSaleReasonModel model = new NoSaleReasonManager(getContext())
                            .getItemUniqueId(UUID.fromString(callModel.ExtraField1));
                    PictureCustomerModel pictureCustomerModel = new PictureCustomerModel();
                    pictureCustomerModel.UniqueId = UUID.randomUUID();
                    pictureCustomerModel.CustomerId = customerId;
                    pictureCustomerModel.Title = model.NoSaleReasonName;
                    pictureCustomerModel.PictureSubjectId = UUID.fromString("63CDD545-7256-4CE1-8465-DF86090E5DAA");
                    pictureCustomerModel.DemandTypeUniqueId = UUID.fromString("ece7d3e4-acdd-4e92-bf85-207b79e703b2");
                    pictureCustomerModel.DemandType = PictureDemandType.Mandatory;
                    insertOrUpdate(pictureCustomerModel);
                }
            }
            if(pictures == null )
                return;
        }





        // iterate over templates and insert(update) customer picture subjects
        for (final PictureSubjectZarModel p :
                pictures) {
            PictureCustomerModel pictureCustomerModel = Linq.findFirst(existingPictures,
                    new Linq.Criteria<PictureCustomerModel>() {
                @Override
                public boolean run(PictureCustomerModel item) {
                    return item.PictureSubjectId.equals(p.UniqueId);
                }
            });
            if (pictureCustomerModel == null) {
                pictureCustomerModel = new PictureCustomerModel();
                pictureCustomerModel.UniqueId = UUID.randomUUID();
                pictureCustomerModel.CustomerId = customerId;
                pictureCustomerModel.PictureSubjectId = p.subjectUniqueId;
                pictureCustomerModel.Title = p.subjectTitle;
            }

            pictureCustomerModel.DemandTypeUniqueId = p.demandTypeUniqueId;
            if (p.demandTypeUniqueId.equals(PictureDemandTypeId.Mandatory) || p.demandTypeUniqueId.equals(PictureDemandTypeId.JustOnce))
                pictureCustomerModel.DemandType = PictureDemandType.Mandatory;
            else if (p.demandTypeUniqueId.equals(PictureDemandTypeId.Optional))
                pictureCustomerModel.DemandType = PictureDemandType.Optional;
            else if (p.demandTypeUniqueId.equals(PictureDemandTypeId.NoSaleMandatory)) {

                if(!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    if (customerCallManager.isNeedImage(customerCalls))
                        pictureCustomerModel.DemandType = PictureDemandType.Mandatory;
                }
            } else
                pictureCustomerModel.DemandType = PictureDemandType.Optional;


            // insert or update item
            insertOrUpdate(pictureCustomerModel);
        }
        Timber.i("Picture subjects calculated for customer");

    }

    public void savePictureTemplates(final UUID customerId, final List<PictureTemplateDetailModel> pictures, @Nullable List<CustomerCallModel> customerCalls) throws ValidationException, DbException {
        if (customerCalls == null)
            customerCalls = new ArrayList<>();
        // fetch all picture subjects that have been already calculated for this customer
        final List<PictureCustomerModel> existingPictures = getCustomerSubjects(customerId);
        // delete all these pictures for the custoFmer
        deleteSubjects(customerId);
        // iterate over templates and insert(update) customer picture subjects
        for (final PictureTemplateDetailModel p :
                pictures) {
            // Maybe this is second time that visitor enters the camera action therefore,
            // find if this subject is already calculated for customer.
            // We do this because we do not want to lose pictures that have been taken before
            PictureCustomerModel pictureCustomerModel = Linq.findFirst(existingPictures, new Linq.Criteria<PictureCustomerModel>() {
                @Override
                public boolean run(PictureCustomerModel item) {
                    return item.PictureSubjectId.equals(p.PictureSubjectUniqueId);
                }
            });
            // If this is a new subject for this customer assign a new unique id
            if (pictureCustomerModel == null) {
                pictureCustomerModel = new PictureCustomerModel();
                pictureCustomerModel.UniqueId = UUID.randomUUID();
                pictureCustomerModel.CustomerId = customerId;
                pictureCustomerModel.PictureSubjectId = p.PictureSubjectUniqueId;
            }
            pictureCustomerModel.DemandTypeUniqueId = p.DemandTypeUniqueId;
            if (p.DemandTypeUniqueId.equals(PictureDemandTypeId.Mandatory) || p.DemandTypeUniqueId.equals(PictureDemandTypeId.JustOnce))
                pictureCustomerModel.DemandType = PictureDemandType.Mandatory;
            else if (p.DemandTypeUniqueId.equals(PictureDemandTypeId.Optional))
                pictureCustomerModel.DemandType = PictureDemandType.Optional;
            else if (p.DemandTypeUniqueId.equals(PictureDemandTypeId.NoSaleMandatory)) {
                CustomerCallManager customerCallManager = new CustomerCallManager(getContext());
                if(!VaranegarApplication.is(VaranegarApplication.AppId.Dist)) {
                    if (customerCallManager.isNeedImage(customerCalls))
                        pictureCustomerModel.DemandType = PictureDemandType.Mandatory;
                }
            } else
                pictureCustomerModel.DemandType = PictureDemandType.Optional;
            // insert or update item
            insertOrUpdate(pictureCustomerModel);
        }
        Timber.i("Picture subjects calculated for customer");
    }

    public void deletePicturesAndFiles(final UUID customerId) throws DbException {
        deleteSubjects(customerId);
        PictureFileManager fileManager = new PictureFileManager(getContext());
        fileManager.deleteFiles(customerId);
        new CustomerCallManager(getContext()).removeCameraCall(customerId);
    }

    public void deleteLackOfOrderPictures(final UUID customerId) throws DbException {
        List<PictureCustomerModel> all = getItems(new Query().from(PictureCustomer.PictureCustomerTbl)
                .whereAnd(Criteria.equals(PictureCustomer.CustomerId, customerId.toString()).and(Criteria.notIsNull(PictureCustomer.FileId))));
        List<PictureCustomerModel> list = Linq.findAll(all, new Linq.Criteria<PictureCustomerModel>() {
            @Override
            public boolean run(PictureCustomerModel item) {
                return item.DemandTypeUniqueId.equals(PictureDemandTypeId.NoSaleMandatory);
            }
        });
        PictureFileManager fileManager = new PictureFileManager(getContext());
        for (PictureCustomerModel picture :
                list) {
            delete(Criteria.equals(PictureCustomer.UniqueId, picture.UniqueId.toString()));
            fileManager.deleteFile(picture.FileId);
        }
        if (list.size() == all.size())
            new CustomerCallManager(getContext()).removeCameraCall(customerId);
    }

    public PictureCustomerModel getPicture(UUID customerId, UUID pictureSubjectId) {
        Query query = new Query();
        query.from(PictureCustomer.PictureCustomerTbl)
                .whereAnd(Criteria.equals(PictureCustomer.CustomerId, customerId.toString())
                        .and(Criteria.equals(PictureCustomer.PictureSubjectId, pictureSubjectId.toString())));
        return getItem(query);
    }
}
