package com.varanegar.vaslibrary.manager;

import android.content.Context;

import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.vaslibrary.model.profileView.ProfileView;
import com.varanegar.vaslibrary.model.profileView.ProfileViewModel;
import com.varanegar.vaslibrary.model.profileView.ProfileViewModelRepository;

/**
 * Created by s.foroughi on 18/03/2017.
 */

public class ProfileViewManager extends BaseManager<ProfileViewModel> {
    public ProfileViewManager(Context context) {
        super(context, new ProfileViewModelRepository());
    }

    public ProfileViewModel getItem(String UniqueId) {
        Query q = new Query();
        q.from(ProfileView.ProfileViewTbl).whereAnd(Criteria.equals(ProfileView.UniqueId, UniqueId));
        return getRepository().getItem(q);
    }

    public static Query getAll() {
        Query query = new Query();
        query.from(ProfileView.ProfileViewTbl);
        return query;
    }


}