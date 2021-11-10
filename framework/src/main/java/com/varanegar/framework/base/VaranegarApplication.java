package com.varanegar.framework.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.database.DbHandler;

import java.util.Date;
import java.util.UUID;

import timber.log.Timber;


/**
 * Created by atp on 12/10/2016.
 */


/**
 * AppId اپلیکیشن های
 *
 */
public class VaranegarApplication {

    public UUID getAppId() {
        return appId;
    }

    public UUID getLocalAppId() {
        return localAppId;
    }

    private final UUID appId;
    private final UUID localAppId;

    public UUID getGrsAppId() {
        return grsAppId;
    }

    private final UUID grsAppId;
    private DbHandler dbHandler;
    private DataHolder dataHolder;
    private static VaranegarApplication instance;


    public DbHandler getDbHandler() {
        return dbHandler;
    }

    private long lastTime;

    public void resetElapsedTime(String tag) {
        lastTime = new Date().getTime();
        Timber.d(tag + " Timer started");
    }

    public void printElapsedTime(String tag) {
        long now = new Date().getTime();
        long elapsed = now - lastTime;
        lastTime = now;
        if (elapsed < 1000)
            Timber.d(tag + ": elapsed " + String.valueOf(elapsed) + " milliseconds");
        else
            Timber.d(tag + ": elapsed " + String.valueOf((double) elapsed / 1000) + " seconds");
    }

    public static void Init(DbHandler dbHandler, @Nullable AppId appId, @Nullable GRSAppId grsAppId) {
        if (instance == null)
            instance = new VaranegarApplication(dbHandler, appId == null ? null : appId.getId(), appId == null ? null : appId.getLocalId(), grsAppId == null ? null : grsAppId.getId());
        else
            throw new UnsupportedOperationException("Varanegar Application is already initialized.");
    }

    public static boolean isInitialized() {
        return instance != null;
    }

    public static VaranegarApplication getInstance() {
        if (instance != null)
            return instance;
        else
            throw new NullPointerException();

    }

    private VaranegarApplication(DbHandler dbHandler, @Nullable UUID appId, @Nullable UUID localAppId, @Nullable UUID grsAppId) {
        this.dbHandler = dbHandler;
        this.appId = appId;
        this.localAppId = localAppId;
        this.grsAppId = grsAppId;
        dataHolder = new DataHolder();
    }

    public <T> void save(@NonNull String id, @NonNull T object) {
        dataHolder.save(id, object);
    }

    @NonNull
    public <T> T retrieve(@NonNull String id, boolean remove) {
        return dataHolder.retrieve(id, remove);
    }

    @Nullable
    public <T> T tryRetrieve(@NonNull String id, boolean remove) {
        try {
            return dataHolder.retrieve(id, remove);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public void remove(@NonNull String id) {
        dataHolder.remove(id);
    }

    public static class AppId {
        private UUID id;
        private UUID localId;

        public AppId(String id, String localId) {
            this.id = UUID.fromString(id);
            this.localId = UUID.fromString(localId);
        }

        public static final AppId Supervisor = new AppId("1e67dfff-e23d-461f-83ea-ba0974a46c1d", "1e67dfff-e23d-461f-83ea-ba0974a46c1d");
        public static final AppId PreSales = new AppId("7571D2C6-44CE-4B62-A45E-42EC8B1C670E", "7571D2C6-44CE-4B62-A45E-42EC8B1C670E");
        public static final AppId Dist = new AppId("30691348-311A-4D00-B3D1-9D76FCD0ADF4", "30691348-311A-4D00-B3D1-9D76FCD0ADF4");
        public static final AppId HotSales = new AppId("4ED97478-0F6E-4044-9581-C71382DA608F", "4ED97478-0F6E-4044-9581-C71382DA608F");
        public static final AppId Contractor = new AppId("7571D2C6-44CE-4B62-A45E-42EC8B1C670E", "888ea4b1-87e7-47c6-a0df-a0ed1116a341");
        public static final AppId WarmSales = new AppId("54CE14F6-E171-481B-B53A-DC29396DFCBE", "54CE14F6-E171-481B-B53A-DC29396DFCBE");

        public UUID getId() {
            return id;
        }

        public UUID getLocalId() {
            return localId;
        }
    }

    public static boolean is(AppId appId) {
        if (VaranegarApplication.getInstance().getLocalAppId().equals(AppId.Contractor.localId) && appId.localId.equals(AppId.PreSales.localId))
            return true;
        else
            return appId.localId.equals(VaranegarApplication.getInstance().getLocalAppId());
    }


    public static class GRSAppId {
        private UUID id;

        private GRSAppId(String id) {
            this.id = UUID.fromString(id);
        }

        public static final GRSAppId Supervisor = new GRSAppId("c3cc739b-f92e-4250-a78f-095dbcc6cadd");
        public static final GRSAppId PreSales = new GRSAppId("8722D784-0814-4986-B065-17CBD4945306");
        public static final GRSAppId Dist = new GRSAppId("AF24B66E-069C-4EB6-926D-852664237251");
        public static final GRSAppId HotSales = new GRSAppId("9A5C443C-3E83-47DD-A986-E69ED134B43B");
        public static final GRSAppId WarmSales = new GRSAppId("54CE14F6-E171-481B-B53A-DC29396DFCBE");

        public UUID getId() {
            return id;
        }
    }
}