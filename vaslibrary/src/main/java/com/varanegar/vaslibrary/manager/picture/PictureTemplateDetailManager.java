package com.varanegar.vaslibrary.manager.picture;

import android.content.Context;
import androidx.annotation.NonNull;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.database.querybuilder.projection.Projection;
import com.varanegar.framework.util.Linq;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateDetail;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateDetailModel;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateDetailModelRepository;
import com.varanegar.vaslibrary.model.picturesubject.PictureTemplateHeaderModel;

import java.util.List;

/**
 * Created by A.Torabi on 10/18/2017.
 */

public class PictureTemplateDetailManager extends BaseManager<PictureTemplateDetailModel> {
    public PictureTemplateDetailManager(@NonNull Context context) {
        super(context, new PictureTemplateDetailModelRepository());
    }

    @NonNull
    public List<PictureTemplateDetailModel> getCorrespondingDetails(@NonNull List<PictureTemplateHeaderModel> templateHeaders) {
        List<String> templateIds = Linq.map(templateHeaders, new Linq.Map<PictureTemplateHeaderModel, String>() {
            @Override
            public String run(PictureTemplateHeaderModel item) {
                return item.UniqueId.toString();
            }
        });
        Query query = new Query();
        query.select(
                Projection.max(PictureTemplateDetail.DemandType).as("DemandType"),
                Projection.max(PictureTemplateDetail.PictureTemplateUniqueId).as("PictureTemplateUniqueId"),
                Projection.max(PictureTemplateDetail.DemandTypeUniqueId).as("DemandTypeUniqueId"),
                Projection.max(PictureTemplateDetail.PictureSubjectUniqueId).as("PictureSubjectUniqueId"),
                Projection.max(PictureTemplateDetail.UniqueId).as("UniqueId")
        );
        query.from(PictureTemplateDetail.PictureTemplateDetailTbl)
                .whereAnd(Criteria.in(PictureTemplateDetail.PictureTemplateUniqueId, templateIds))
                .groupBy(PictureTemplateDetail.PictureSubjectUniqueId);
        List<PictureTemplateDetailModel> result = getItems(query);
        return result;
    }
}
