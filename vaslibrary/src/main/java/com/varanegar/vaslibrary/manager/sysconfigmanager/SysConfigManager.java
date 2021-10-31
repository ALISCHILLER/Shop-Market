package com.varanegar.vaslibrary.manager.sysconfigmanager;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.BaseManager;
import com.varanegar.framework.database.DbException;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.criteria.Criteria;
import com.varanegar.framework.network.listeners.ApiError;
import com.varanegar.framework.network.listeners.WebCallBack;
import com.varanegar.framework.util.component.UserDialogPreferences;
import com.varanegar.framework.validation.ValidationException;
import com.varanegar.java.util.Currency;
import com.varanegar.vaslibrary.R;
import com.varanegar.vaslibrary.manager.UserManager;
import com.varanegar.vaslibrary.manager.updatemanager.UpdateCall;
import com.varanegar.vaslibrary.model.sysconfig.SysConfig;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModel;
import com.varanegar.vaslibrary.model.sysconfig.SysConfigModelRepository;
import com.varanegar.vaslibrary.webapi.WebApiErrorBody;
import com.varanegar.vaslibrary.webapi.advancedealercreditcontrol.AdvanceDealerCreditControlApi;
import com.varanegar.vaslibrary.webapi.personnel.PersonnelMetaDataApi;
import com.varanegar.vaslibrary.webapi.personnel.PersonnelMetaDataViewModel;
import com.varanegar.vaslibrary.webapi.sysconfig.SysConfigApi;
import com.varanegar.vaslibrary.webapi.tracking.TrackingApi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

import okhttp3.Request;
import retrofit2.Call;
import timber.log.Timber;

/**
 * Created by atp on 12/18/2016.
 */
public class SysConfigManager extends BaseManager<SysConfigModel> {
    public static UUID cloud = UUID.fromString("6e134a3e-57f4-4d21-8417-58287589fad3");
    public static UUID local = UUID.fromString("f0eeaf18-4a85-4250-bf91-c278def9e8ff");
    public static UUID base = UUID.fromString("80D0CBB4-17E7-4662-B559-37409C1387DF");


    private static final HashMap<String, SysConfigModel> cache = new HashMap<>();

    public SysConfigManager(Context context) {
        super(context, new SysConfigModelRepository());
    }

    @Nullable
    public static String getValue(@Nullable SysConfigModel sysConfigModel) {
        if (sysConfigModel == null)
            return null;
        return sysConfigModel.Value;
    }

    @Nullable
    public static UUID getUUIDValue(@Nullable SysConfigModel sysConfigModel) {
        if (sysConfigModel == null)
            return null;
        if (sysConfigModel.Value == null || sysConfigModel.Value.isEmpty())
            return null;
        try {
            return UUID.fromString(sysConfigModel.Value);
        } catch (Exception ex) {
            Timber.e(ex);
            return null;
        }
    }

    public static int getIntValue(@Nullable SysConfigModel sysConfigModel, int defaultValue) {
        if (sysConfigModel == null)
            return defaultValue;
        try {
            return Integer.parseInt(sysConfigModel.Value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static String getStringValue(@Nullable SysConfigModel sysConfigModel, String defaultValue) {
        if (sysConfigModel == null)
            return defaultValue;
        try {
            return sysConfigModel.Value;
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Nullable
    public static Integer getIntegerValue(@Nullable SysConfigModel sysConfigModel, @Nullable Integer defaultValue) {
        if (sysConfigModel == null)
            return defaultValue;
        try {
            return Integer.parseInt(sysConfigModel.Value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    @Nullable
    public static Currency getCurrencyValue(@Nullable SysConfigModel sysConfigModel, @Nullable Currency defaultValue) {
        if (sysConfigModel == null)
            return defaultValue;
        try {
            return Currency.parse(sysConfigModel.Value);
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static boolean getBooleanValue(@Nullable SysConfigModel sysConfigModel, boolean defaultValue) {
        if (sysConfigModel == null)
            return defaultValue;
        try {
            return sysConfigModel.Value.equals("True");
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    /*
    Sample input "18:20:30"
     */
    public static Date getDateFromTime(@Nullable SysConfigModel sysConfigModel, Date defaultValue) {
        if (sysConfigModel != null && sysConfigModel.Value != null && !sysConfigModel.Value.isEmpty()) {
            String[] splits = sysConfigModel.Value.split(":");
            if (splits.length == 3) {
                int hour = Integer.parseInt(splits[0]);
                int minute = Integer.parseInt(splits[1]);
                int second = Integer.parseInt(splits[2]);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, second);
                Date date = cal.getTime();
                return date;
            }
        }
        return defaultValue;
    }

    public static boolean compare(@Nullable SysConfigModel sysConfigModel, @NonNull String value) {
        return sysConfigModel != null && sysConfigModel.Value.equalsIgnoreCase(value);
    }

    public static boolean compare(@Nullable SysConfigModel sysConfigModel, boolean value) {
        if (sysConfigModel == null)
            return false;
        else {
            boolean val = Boolean.parseBoolean(sysConfigModel.Value);
            return val == value;
        }
    }

    public static boolean compare(@Nullable SysConfigModel sysConfigModel, @NonNull UUID value) {
        return sysConfigModel != null && sysConfigModel.Value.equalsIgnoreCase(value.toString());
    }

    public void syncPersonnelMetaData(@NonNull final UpdateCall call) {
        String userId = UserManager.readFromFile(getContext()).UniqueId.toString();
        PersonnelMetaDataApi personnelMetaDataApi = new PersonnelMetaDataApi(getContext());
        personnelMetaDataApi.runWebRequest(personnelMetaDataApi.getPersonalMetaData(userId, VaranegarApplication.getInstance().getAppId().toString()), new WebCallBack<PersonnelMetaDataViewModel>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(PersonnelMetaDataViewModel result, Request request) {
                if (result != null) {
                    List<SysConfigModel> sysConfigModels = new ArrayList<>();

                    SysConfigModel sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("1d34ad07-49f8-4fb5-84d1-dae6e13bb34d");
                    sysConfigModel.Name = ConfigKey.DcRef.getKey();
                    sysConfigModel.Value = String.valueOf(result.DcRef);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("9944da5a-16b9-417a-ad3c-441de0ca0bd7");
                    sysConfigModel.Name = ConfigKey.DcCode.getKey();
                    sysConfigModel.Value = String.valueOf(result.DcCode);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("da93d775-1b8a-455b-ac23-7a4b724f9665");
                    sysConfigModel.Name = ConfigKey.SaleOfficeRef.getKey();
                    sysConfigModel.Value = String.valueOf(result.SaleOfficeRef);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("3fdb4c75-01d0-4f1c-82f8-d1dfc75b12ce");
                    sysConfigModel.Name = ConfigKey.SaleOfficeId.getKey();
                    sysConfigModel.Value = String.valueOf(result.SaleOfficeId);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("c6914d3a-b86a-4662-94e1-4e20d4510e37");
                    sysConfigModel.Name = ConfigKey.StockDCRef.getKey();
                    sysConfigModel.Value = String.valueOf(result.StockDcRef);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("c5224dab-a2bb-4123-8459-b0426e991f6e");
                    sysConfigModel.Name = ConfigKey.StockDCCode.getKey();
                    sysConfigModel.Value = String.valueOf(result.StockDcCode);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("f4f5035d-541b-4f4e-8823-88161693fb06");
                    sysConfigModel.Name = ConfigKey.DealerCode.getKey();
                    sysConfigModel.Value = String.valueOf(result.DealerCode);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("ca63819e-ae31-498a-b65d-3d3f46ea4a94");
                    sysConfigModel.Name = ConfigKey.DealerRef.getKey();
                    sysConfigModel.Value = String.valueOf(result.DealerRef);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("a4fc0e82-b0b3-4f1a-97c7-3967aa55ac49");
                    sysConfigModel.Name = ConfigKey.SupervisorRef.getKey();
                    sysConfigModel.Value = String.valueOf(result.SupervisorRef);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("527999e0-ba2a-4224-9e4a-7a6fe34c1e16");
                    sysConfigModel.Name = ConfigKey.SupervisorCode.getKey();
                    sysConfigModel.Value = String.valueOf(result.SupervisorCode);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("0C41742F-B8D2-449E-902D-63FC10A8612A");
                    sysConfigModel.Name = ConfigKey.PrizeCalcType.getKey();
                    sysConfigModel.Value = String.valueOf(result.PrizeCalcType);
                    sysConfigModels.add(sysConfigModel);

                    sysConfigModel = new SysConfigModel();
                    sysConfigModel.Scope = SysConfigManager.cloud;
                    sysConfigModel.UniqueId = UUID.fromString("8b1396c5-2302-40a8-8ae6-8eddab2e6248");
                    sysConfigModel.Name = ConfigKey.Mobile.getKey();
                    sysConfigModel.Value = result.Mobile;
                    sysConfigModels.add(sysConfigModel);
                    try {
                        insertOrUpdate(sysConfigModels);
                        cache.clear();
                        Timber.i("List of personnel metadata count updated");
                        call.success();
                    } catch (ValidationException e) {
                        Timber.e(e.getMessage());
                        call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e.getMessage());
                        call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    Timber.i("List of personnel meta data count was empty");
                    call.success();
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                call.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                call.failure(getContext().getString(R.string.network_error));
            }
        });

    }

    public void syncAdvanceDealerCredit(@Nullable final UpdateCall call) {
        AdvanceDealerCreditControlApi advanceDealerCreditControlApi = new AdvanceDealerCreditControlApi(getContext());
        advanceDealerCreditControlApi.runWebRequest(advanceDealerCreditControlApi.getAdvanceDealerCreditsControl(UserManager.readFromFile(getContext()).UniqueId.toString()), new WebCallBack<SysConfigModel>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(SysConfigModel result, Request request) {
                if (result != null) {
                    try {
                        insertOrUpdate(result);
                        Timber.i("AdvancedCreditControl is inserted");
                        cache.clear();
                        if (call != null)
                            call.success();
                    } catch (ValidationException e) {
                        Timber.e(e.getMessage());
                        if (call != null)
                            call.failure(getContext().getString(R.string.data_validation_failed));
                    } catch (DbException e) {
                        Timber.e(e.getMessage());
                        if (call != null)
                            call.failure(getContext().getString(R.string.data_error));
                    }
                } else {
                    if (call != null)
                        call.failure(getContext().getString(R.string.empty_settings_downloaded));
                    Timber.wtf("Sys config list was empty!");
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                if (call != null) {
                    call.failure(err);
                }
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e(t);
                if (call != null)
                    call.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public void sync(@NonNull final UpdateCall call) {
        SysConfigApi sysConfigApi = new SysConfigApi(getContext());
        SysConfigModel sysConfigModel = read(ConfigKey.SettingsId, SysConfigManager.local);
        if ((sysConfigModel != null && sysConfigModel.Value != null) || VaranegarApplication.is(VaranegarApplication.AppId.Supervisor)) {
            Call<List<SysConfigModel>> api;
            if (VaranegarApplication.is(VaranegarApplication.AppId.Supervisor))
                api = sysConfigApi.getSupervisorConfigs();
            else
                api = sysConfigApi.getAll(sysConfigModel.Value);

            sysConfigApi.runWebRequest(api, new WebCallBack<List<SysConfigModel>>() {
                @Override
                protected void onFinish() {

                }

                @Override
                protected void onSuccess(List<SysConfigModel> result, Request request) {
                    if (result.size() > 0) {
                        try {
                            sync(result);
                            cache.clear();
                            TrackingApi.setOwnerKeysWrapper(readOwnerKeys());
                            SysConfigModel sysConfigModel = read(ConfigKey.UserDialogPreference, cloud);
                            UserDialogPreferences.setPreferenceAvailability(getContext(), compare(sysConfigModel, true));
                            Timber.i("SysConfig list updated");
                            call.success();
                        } catch (ValidationException e) {
                            Timber.e(e.getMessage());
                            call.failure(getContext().getString(R.string.data_validation_failed));
                        } catch (DbException e) {
                            Timber.e(e.getMessage());
                            call.failure(getContext().getString(R.string.data_error));
                        }
                    } else {
                        call.failure(getContext().getString(R.string.empty_settings_downloaded));
                        Timber.wtf("Sys config list was empty!");
                    }

                }

                @Override
                protected void onApiFailure(ApiError error, Request request) {
                    String err = WebApiErrorBody.log(error, getContext());
                    call.failure(err);
                }

                @Override
                protected void onNetworkFailure(Throwable t, Request request) {
                    Timber.e(t);
                    call.failure(getContext().getString(R.string.network_error));
                }
            });
        } else {
            Timber.e("Settings Id Not found");
            call.failure(getContext().getString(R.string.settings_id_not_found));
        }
    }

    public void getInitialSettings(final UpdateCall updateCall) {
        final SysConfigApi sysConfigApi = new SysConfigApi(getContext());
        sysConfigApi.runWebRequest(sysConfigApi.getLanguage(), new WebCallBack<List<SysConfigModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<SysConfigModel> result, Request request) {
                try {
                    insertOrUpdate(result);
                    cache.clear();
                    updateCall.success();
                } catch (ValidationException e) {
                    Timber.e(e);
                    updateCall.failure(getContext().getString(R.string.data_validation_failed));
                } catch (DbException e) {
                    updateCall.failure(getContext().getString(R.string.data_error));
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                updateCall.failure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e("network error on getownerkeys");
                updateCall.failure(getContext().getString(R.string.network_error));
            }
        });
    }

    public void save(@NonNull ConfigKey key, @NonNull String value, @NonNull UUID scope) throws ValidationException, DbException {
        SysConfigModel config = read(key, scope);
        if (config == null) {
            config = new SysConfigModel();
            config.UniqueId = UUID.randomUUID();
            config.Name = key.getKey();
            config.Scope = scope;
        }
        config.Value = value;
        insertOrUpdate(config);
        String cacheKey = key.getKey() + "_" + scope;
        cache.put(cacheKey, config);
    }

    @Nullable
    public SysConfigModel read(ConfigKey key, UUID scope) {
        String cacheKey = key.getKey() + "_" + scope;
        if (cache.containsKey(cacheKey)) {
            SysConfigModel sysConfigModel = cache.get(cacheKey);
            return sysConfigModel;
        }
        Query query = new Query();
        query.from(SysConfig.SysConfigTbl).whereAnd(Criteria.equals(SysConfig.Name, key.getKey())
                .and(Criteria.equals(SysConfig.Scope, scope.toString())));
        SysConfigModel sysConfigModel = getItem(query);
        cache.put(cacheKey, sysConfigModel);
        return sysConfigModel;
    }

    @NonNull
    public ConfigMap read(UUID scope) {
        List<SysConfigModel> sysConfigModels = getItems(new Query().from(SysConfig.SysConfigTbl)
                .whereAnd(Criteria.equals(SysConfig.Scope, scope)));
        ConfigMap configMap = new ConfigMap();
        for (SysConfigModel sysConfigModel :
                sysConfigModels) {
            String cacheKey = sysConfigModel.Name + "_" + scope;
            configMap.put(sysConfigModel);
            cache.put(cacheKey, sysConfigModel);
        }
        return configMap;
    }

    @NonNull
    public BackOfficeType getBackOfficeType() throws UnknownBackOfficeException {
        SysConfigModel sysConfigModel = read(ConfigKey.BackOfficeType, cloud);
        if (sysConfigModel == null || sysConfigModel.Value == null || sysConfigModel.Value.isEmpty())
            throw new UnknownBackOfficeException();
        else {
            if (sysConfigModel.Value.equalsIgnoreCase(BackOfficeType.Varanegar.getName()))
                return BackOfficeType.Varanegar;
            else if (sysConfigModel.Value.equalsIgnoreCase(BackOfficeType.Rastak.getName()))
                return BackOfficeType.Rastak;
            else if (sysConfigModel.Value.equalsIgnoreCase(BackOfficeType.ThirdParty.getName()))
                return BackOfficeType.ThirdParty;
            else
                throw new UnknownBackOfficeException();
        }
    }

    @NonNull
    public OwnerKeysWrapper readOwnerKeys() throws OwnerKeysNotFoundException {
        SysConfigModel ownerKeys = read(ConfigKey.OwnerKeys, SysConfigManager.base);
        if (ownerKeys == null || ownerKeys.Value == null || ownerKeys.Value.isEmpty())

            throw new OwnerKeysNotFoundException();
        else {
            StringTokenizer st = new StringTokenizer(ownerKeys.Value, ":");
            String ownerKey = st.nextToken();
            String dataOwnerKey = st.nextToken();
            String dataOwnerCenterKey = st.nextToken();
            if (ownerKey == null || dataOwnerKey == null || dataOwnerCenterKey == null)
                throw new OwnerKeysNotFoundException();
            OwnerKeysWrapper result = new OwnerKeysWrapper();
            result.OwnerKey = ownerKey;
            result.DataOwnerKey = dataOwnerKey;
            result.DataOwnerCenterKey = dataOwnerCenterKey;
            return result;
        }
    }

    public void delete(ConfigKey key) throws DbException {
        delete(Criteria.equals(SysConfig.Name, key.getKey()));
        cache.clear();
    }


    public void getOwnerKeys(@NonNull final IOwnerKeyResponse response) {
        final SysConfigApi sysConfigApi = new SysConfigApi(getContext());
        sysConfigApi.runWebRequest(sysConfigApi.getOwnerKeys(), new WebCallBack<List<CenterSysConfigModel>>() {
            @Override
            protected void onFinish() {

            }

            @Override
            protected void onSuccess(List<CenterSysConfigModel> result, Request request) {
                if (result != null && result.size() > 0) {
                    response.onSuccess(result);
                } else {
                    Timber.e("owner keys not found");
                    response.onFailure("owner keys not found");
                }
            }

            @Override
            protected void onApiFailure(ApiError error, Request request) {
                String err = WebApiErrorBody.log(error, getContext());
                response.onFailure(err);
            }

            @Override
            protected void onNetworkFailure(Throwable t, Request request) {
                Timber.e("network error on getownerkeys");
                response.onFailure(getContext().getString(R.string.network_error));
            }
        });
    }

    public static boolean hasTracking(Context context) {
        SysConfigManager manager = new SysConfigManager(context);
        if (VaranegarApplication.is(VaranegarApplication.AppId.Supervisor)) {
            SysConfigModel sysConfigModel = manager.read(ConfigKey.SupervisorGrs, cloud);
            return compare(sysConfigModel, "1");
        } else {
            SysConfigModel sysConfigModel = manager.read(ConfigKey.EnableGPS, cloud);
            return compare(sysConfigModel, true);
        }
    }

    public static boolean isMultipleSendActive(Context context) {
        SysConfigManager manager = new SysConfigManager(context);
        SysConfigModel sysConfigModel = manager.read(ConfigKey.CheckSingleSendOperation, cloud);
        return SysConfigManager.compare(sysConfigModel, true);
    }

    public void setOwnerKeys(CenterSysConfigModel centerSysConfigModel) throws ValidationException, DbException {
        delete(Criteria.equals(SysConfig.Name, "OwnerKeys"));
        cache.clear();
        SysConfigModel sysConfigModel = new SysConfigModel();
        sysConfigModel.UniqueId = centerSysConfigModel.UniqueId;
        sysConfigModel.Name = "OwnerKeys";
        sysConfigModel.Value = centerSysConfigModel.Value;
        sysConfigModel.Scope = centerSysConfigModel.Scope;
        insertOrUpdate(sysConfigModel);
    }

    public interface IOwnerKeyResponse {
        void onSuccess(List<CenterSysConfigModel> ownerKeys);

        void onFailure(String error);
    }
}
