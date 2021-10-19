package com.varanegar.framework.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.varanegar.framework.base.VaranegarApplication;
import com.varanegar.framework.database.model.ModelProjection;
import com.varanegar.framework.database.querybuilder.Query;
import com.varanegar.framework.database.querybuilder.TableNotFoundException;
import com.varanegar.framework.util.Linq;
import com.varanegar.java.util.Currency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

/**
 * Created by atp on 8/10/2016.
 */
public class DbHandler extends SQLiteOpenHelper {

    public Context getContext() {
        return context;
    }

    private Context context;

    public SQLiteConnectionString getConnectionString() {
        return connectionString;
    }

    private SQLiteConnectionString connectionString;

    public DbHandler(Context context, SQLiteConnectionString connectionString) {
        super(context, connectionString.getName(), null, connectionString.getVersion());
        this.context = context;
        this.connectionString = connectionString;
    }

    public String getDatabasePath() {
        return context.getDatabasePath(VaranegarApplication.getInstance().getDbHandler().getConnectionString().getName()).getPath();
    }

    public String getDatabaseFolder() {
        return context.getDatabasePath(VaranegarApplication.getInstance().getDbHandler().getConnectionString().getName()).getParent();
    }

    protected List<DbUpgrade> getUpgrades() throws IOException, InvalidFormatException {
        List<DbUpgrade> dbUpgrades = new ArrayList<>();
        List<String> scriptFiles = listScripts(getContext(), connectionString.getName());
        for (String scriptFileName :
                scriptFiles) {
            DbUpgrade dbUpgrade = createDbUpgrade(getContext(), connectionString.getName(), scriptFileName);
            dbUpgrades.add(dbUpgrade);
        }
        return dbUpgrades;
    }

    @Override
    synchronized public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            List<DbUpgrade> dbUpgrades = getUpgrades();

            if (dbUpgrades != null && dbUpgrades.size() > 0) {
                Linq.sort(dbUpgrades, new Comparator<DbUpgrade>() {
                    @Override
                    public int compare(DbUpgrade o1, DbUpgrade o2) {
                        int o1Version = o1.getVersion();
                        int o2Version = o2.getVersion();
                        if (o1Version == o2Version)
                            return 0;
                        else
                            return o1Version > o2Version ? 1 : -1;
                    }
                });
                for (DbUpgrade dbUpgrade :
                        dbUpgrades) {
                    Timber.d("Executing Script " + dbUpgrade.getVersion());
                    dbUpgrade.onUpgrade(sqLiteDatabase);
                    Timber.d("script " + dbUpgrade.getVersion() + " executed successfully");
                }
            }

        } catch (Exception e) {
            String dbPath = getDatabasePath();
            File file = new File(dbPath);
            if (file.exists())
                file.delete();
            Timber.e(e, "Error in creating database");
        }
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        List<DbUpgrade> upgrades = null;
        try {
            upgrades = getUpgrades();
            if (upgrades != null && upgrades.size() > 0) {
                Linq.sort(upgrades, new Comparator<DbUpgrade>() {
                    @Override
                    public int compare(DbUpgrade o1, DbUpgrade o2) {
                        int o1Version = o1.getVersion();
                        int o2Version = o2.getVersion();
                        if (o1Version == o2Version)
                            return 0;
                        else
                            return o1Version > o2Version ? 1 : -1;
                    }
                });
                try {
                    for (DbUpgrade dbUpgrade :
                            upgrades) {
                        if (dbUpgrade.getVersion() > oldVersion && dbUpgrade.getVersion() <= newVersion && dbUpgrade.getVersion() != 1) {
                            Timber.d("Executing Script " + dbUpgrade.getVersion());
                            dbUpgrade.onUpgrade(db);
                        }
                    }

                } catch (Exception ex) {
                    Timber.wtf(ex, "Error in upgrading database");
                }
            }
        } catch (Exception e) {
            Timber.wtf(e, "Error in upgrading database");
        }
    }

    public synchronized void emptyAllTablesExcept(List<ModelProjection> tables) {
        List<String> allTables;
        allTables = getString("SELECT name FROM sqlite_master WHERE type='table'", null);

        for (final String table : allTables) {
            if (!Linq.exists(tables, new Linq.Criteria<ModelProjection>() {
                @Override
                public boolean run(ModelProjection item) {
                    return item.getName().equals(table);
                }
            })) {
                String dropQuery = "DELETE FROM " + table;
                execSql(dropQuery);
            }
        }
    }

    public synchronized void emptyAllTablesExcept(ModelProjection... tables) {
        List<ModelProjection> tableCollection = new ArrayList<>();
        Collections.addAll(tableCollection, tables);
        emptyAllTablesExcept(tableCollection);
    }

    public synchronized void emptyTables(boolean throwOnMissingTable, List<ModelProjection> tables) {
        for (final ModelProjection table : tables) {
            if (throwOnMissingTable) {
                String dropQuery = "DELETE FROM " + table.getName();
                execSql(dropQuery);
            } else {
                boolean tblExists = false;
                Cursor tblResult = null;
                try {
                    tblResult = rawQuery("SELECT count(*) FROM sqlite_master WHERE name = '" + table.getName() + "'", null);
                    if (tblResult != null) {
                        tblResult.moveToFirst();
                        int c = tblResult.getInt(0);
                        tblResult.close();
                        tblExists = c != 0;
                    }
                } finally {
                    if (tblResult != null && !tblResult.isClosed())
                        tblResult.close();
                }
                if (tblExists) {
                    String dropQuery = "DELETE FROM " + table.getName();
                    execSql(dropQuery);
                }
            }

        }
    }

    public synchronized void emptyTables(boolean throwOnMissingTable, ModelProjection... tables) {
        List<ModelProjection> tableCollection = new ArrayList<>();
        Collections.addAll(tableCollection, tables);
        emptyTables(throwOnMissingTable, tableCollection);
    }

    public synchronized void exportDb(String dest, ModelProjection... tables) {
        exportDb(dest, Arrays.asList(tables));
    }

    public synchronized void exportDb(String dest, List<ModelProjection> tables) {
        SQLiteConnectionString destConnectionString = new SQLiteConnectionString(dest);
        DbHandler desDbHandler = new DbHandler(getContext(), destConnectionString);
        desDbHandler.emptyTables(false, tables);
        DbAttachment attachment = new DbAttachment(connectionString.getName());
        for (ModelProjection table :
                tables) {
            attachment.addMapping(new TableMap(table, table).addAllColumns());
        }
        try {
            desDbHandler.attach(attachment, true);
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    public synchronized void importDb(String src, ModelProjection... tables) {
        importDb(src, Arrays.asList(tables));
    }

    public synchronized void importDb(String src, List<ModelProjection> tables) {
        emptyTables(false, tables);
        DbAttachment attachment = new DbAttachment(src);
        for (ModelProjection table :
                tables) {
            attachment.addMapping(new TableMap(table, table).addAllColumns());
        }
        try {
            attach(attachment, true);
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    /**
     * @param dstFolder directory to save file
     * @return full file path and file name
     * @throws IOException
     */
    public synchronized String exportDb(File dstFolder, @Nullable String filename) throws IOException {
        if (filename == null)
            filename = getDatabaseName();
        String dbPath = context.getDatabasePath(connectionString.getName()).getPath();
        InputStream localStream = new FileInputStream(dbPath);
        OutputStream externalStream = new FileOutputStream(dstFolder + "/" + filename);
        //Copy the database
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = localStream.read(buffer)) > 0) {
            externalStream.write(buffer, 0, bytesRead);
        }
        //FLUSH THE OUT STREAM
        externalStream.flush();
        //Close the streams
        externalStream.close();
        localStream.close();
        return dstFolder + "/" + filename;
    }

    @NonNull
    public List<String> getString(@NonNull String query, @Nullable String[] parameters) {
        List<String> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(cursor.getString(0));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<UUID> getUUID(@NonNull String query, @Nullable String[] parameters) {
        List<UUID> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(UUID.fromString(cursor.getString(0)));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<Integer> getInteger(@NonNull String query, @Nullable String[] parameters) {
        List<Integer> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(cursor.getInt(0));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<Double> getDouble(@NonNull String query, @Nullable String[] parameters) {
        List<Double> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(cursor.getDouble(0));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<Float> getFloat(@NonNull String query, @Nullable String[] parameters) {
        List<Float> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(cursor.getFloat(0));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<Short> getShort(@NonNull String query, @Nullable String[] parameters) {
        List<Short> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(cursor.getShort(0));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<Long> getLong(@NonNull String query, @Nullable String[] parameters) {
        List<Long> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(cursor.getLong(0));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<BigDecimal> getBigDecimal(@NonNull String query, @Nullable String[] parameters) {
        List<BigDecimal> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(BigDecimal.valueOf(cursor.getLong(0)));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<Currency> getCurrency(@NonNull String query, @Nullable String[] parameters) {
        List<Currency> items = new ArrayList<>();
        Cursor cursor = null;
        try {
            SQLiteDatabase db = getReadableDatabase();
            cursor = db.rawQuery(
                    query, parameters);
            int size = cursor.getCount();
            for (int i = 0; i < size; i++) {
                cursor.moveToPosition(i);
                if (cursor.isNull(0))
                    items.add(null);
                else
                    items.add(Currency.valueOf(cursor.getLong(0)));
            }
            return items;
        } catch (Exception ex) {
            Timber.e(ex);
            return items;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @NonNull
    public List<String> getString(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getString(query.build(), parameters);
    }

    @NonNull
    public List<UUID> getUUID(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getUUID(query.build(), parameters);
    }

    @NonNull
    public List<Integer> getInteger(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getInteger(query.build(), parameters);
    }

    @NonNull
    public List<Double> getDouble(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getDouble(query.build(), parameters);
    }

    @NonNull
    public List<Float> getFloat(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getFloat(query.build(), parameters);
    }

    @NonNull
    public List<Short> getShort(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getShort(query.build(), parameters);
    }

    @NonNull
    public List<Long> getLong(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getLong(query.build(), parameters);
    }

    @NonNull
    public List<BigDecimal> getBigDecimal(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getBigDecimal(query.build(), parameters);
    }

    @NonNull
    public List<Currency> getCurrency(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        return getCurrency(query.build(), parameters);
    }

    @Nullable
    public String getStringSingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<String> items = getString(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }

    @Nullable
    public UUID getUUIDSingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<UUID> items = getUUID(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }

    @Nullable
    public Integer getIntegerSingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<Integer> items = getInteger(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }

    @Nullable
    public Double getDoubleSingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<Double> items = getDouble(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }

    @Nullable
    public Float getFloatSingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<Float> items = getFloat(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }

    @Nullable
    public Short getShortSingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<Short> items = getShort(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }

    @Nullable
    public Long getLongSingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<Long> items = getLong(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }

    @Nullable
    public BigDecimal getBigDecimalSingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<BigDecimal> items = getBigDecimal(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }

    @Nullable
    public Currency getCurrencySingle(@NonNull Query query) {
        String[] parameters = null;
        if (query.build().contains("?"))
            parameters = query.buildStringParameters();
        List<Currency> items = getCurrency(query.build(), parameters);
        if (items.size() == 0)
            return null;
        else
            return items.get(0);
    }


    public void execSql(@NonNull String sqlQuery) throws NullPointerException {
        try {
            SQLiteDatabase database = getWritableDatabase();
            database.execSQL(sqlQuery);
        } catch (Exception ex) {
            Timber.wtf(ex);
            throw ex;
        }
    }

    public Cursor rawQuery(@NonNull String sqlQuery, @Nullable String[] selectionArgs) throws NullPointerException {
        try {
            SQLiteDatabase database = getWritableDatabase();
            return database.rawQuery(sqlQuery, selectionArgs);
        } catch (Exception ex) {
            Timber.wtf(ex);
            throw ex;
        }
    }

    private boolean transaction;

    public void beginTransaction() {
        transaction = true;
        SQLiteDatabase database = super.getWritableDatabase();
        database.beginTransaction();
    }

    public void setTransactionSuccessful() {
        getWritableDatabase().setTransactionSuccessful();
    }

    public void endTransaction() {
        transaction = false;
        getWritableDatabase().endTransaction();
    }

    @Override
    public synchronized void close() {
        if (!transaction) {
            super.close();
        }
    }


    public static List<String> listScripts(Context context, String dbname) throws IOException {
        String name = dbname;
        if (dbname.endsWith(".db")) {
            name = dbname.substring(0, dbname.length() - 3);
        }
        String[] allFiles = context.getAssets().list("databases");
        List<String> scripts = new ArrayList<>();
        for (String scriptName :
                allFiles) {
            if (scriptName.endsWith(".sql") && scriptName.startsWith(name))
                scripts.add(scriptName);
        }
        return scripts;
    }

    /**
     * For example for version 1.0.6.1 you have to provide a file named vndb1-0-6-1.sql in assets/databases folder
     *
     * @param filename
     * @return
     * @throws Exception returns IOException
     */
    protected static DbUpgrade createDbUpgrade(final Context context, String dbName, final String filename) throws InvalidFormatException {

        String code = filename.substring(dbName.length(), filename.length() - 4);
        try {
            int versionCode = Integer.valueOf(code);
            final int finalVersionCode = versionCode;
            return new DbUpgrade() {
                @Override
                public int getVersion() {
                    return finalVersionCode;
                }

                @Override
                public void onUpgrade(SQLiteDatabase db) {
                    try {
                        Timber.d("Script file name is " + filename);
                        executeScriptFile(context, db, filename);
                    } catch (Exception e) {
                        Timber.e("Error executing script " + filename, e);
                    }
                }
            };
        } catch (Exception ex) {
            throw new InvalidFormatException("Script file name is invalid");
        }
    }

    private static void executeScriptFile(Context context, SQLiteDatabase db, String filename) throws IOException, InvalidFormatException {
        List<String> script = readScriptFile(context, filename);
        Timber.d("Script " + filename + " was read successfully.");
        for (String sql :
                script) {
            Timber.d("Executing sql command: " + sql);
            if (sql.startsWith("@UNCHECKED")) {
                try {
                    db.execSQL(sql.replaceAll("@UNCHECKED",""));
                } catch (SQLException e) {
                    Timber.d("@UNCHECKED sql command");
                }
            } else
                db.execSQL(sql);
        }
        Timber.d("Executing " + filename + " finished successfully.");
    }

    public static List<String> readScriptFile(Context context, String scriptName) throws IOException, InvalidFormatException {
        List<String> commands = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("databases/" + scriptName)));

            String mLine;
            boolean isComment = false;
            StringBuilder command = new StringBuilder();
            while ((mLine = reader.readLine()) != null) {
                //process line
                if (!mLine.isEmpty() && !mLine.startsWith("--")) {
                    if (mLine.startsWith("/*")) {
                        isComment = true;
                    }
                    if (!isComment) {
                        int end = mLine.indexOf(";");
                        if (end != -1 && end != mLine.length() - 1)
                            throw new InvalidFormatException("Only one sql command in a line is supported! Please move the second command ( or comment ) to the next line or remove all white spaces after ; ");
                        command.append(mLine);
                        if (!mLine.endsWith(System.getProperty("line.separator")))
                            command.append(System.getProperty("line.separator"));
                        if (end == mLine.length() - 1) {
                            commands.add(command.toString());
                            command.delete(0, command.length());
                        }

                    }
                    if (mLine.endsWith("*/")) {
                        isComment = false;
                    }
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        }
        return commands;
    }

    public synchronized void attach(DbAttachment attachment, boolean createMissingTable) throws SQLiteException, IOException {
        String dbName = attachment.getDbName();
        List<TableMap> maps = attachment.getMapping();
        File dbFile = context.getDatabasePath(dbName);
        if (!dbFile.exists())
            throw new FileNotFoundException(dbName + " not found");

        if (dbName.contains("."))
            dbName = dbName.substring(0, dbName.indexOf("."));
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor0 = null;
        Cursor cursor1 = null;
        Cursor cursor2 = null;
        Cursor cursor3 = null;
        Cursor cursor4 = null;
        Cursor cursor5 = null;
        try {
            db.execSQL("attach database '" + dbFile.getPath() + "' as " + dbName + ";");
            for (TableMap tblMap :
                    maps) {

                boolean srcTblExists = false;
                cursor0 = db.rawQuery("SELECT count(*) FROM " + dbName + ".sqlite_master WHERE type = 'table' and name = '" + tblMap.getSrc() + "'", null);
                if (cursor0 != null) {
                    cursor0.moveToFirst();
                    int c = cursor0.getInt(0);
                    srcTblExists = c != 0;
                    cursor0.close();
                }

                if (!srcTblExists) {
                    if (tblMap.throwIfSourceMissed())
                        throw new TableNotFoundException(connectionString.getName(), tblMap.getSrc());
                    else
                        continue;
                }

                boolean dstTblExists = false;
                cursor1 = db.rawQuery("SELECT count(*) FROM sqlite_master WHERE name = '" + tblMap.getDest() + "'", null);
                if (cursor1 != null) {
                    cursor1.moveToFirst();
                    int c = cursor1.getInt(0);
                    dstTblExists = c != 0;
                    cursor1.close();
                }

                if (!dstTblExists) {
                    if (createMissingTable) {
                        cursor2 = db.rawQuery("SELECT sql FROM " + dbName + ".sqlite_master WHERE name = '" + tblMap.getSrc() + "'", null);
                        if (cursor2 != null) {
                            cursor2.moveToFirst();
                            String sql = cursor2.getString(0);
                            cursor2.close();
                            sql = sql.replaceFirst(tblMap.getSrc(), tblMap.getDest());
                            db.execSQL(sql);
                        }
                    } else if (tblMap.throwIfDestMissed())
                        throw new TableNotFoundException(connectionString.getName(), tblMap.getSrc());
                    else
                        continue;
                }

                if (tblMap.getColumnsCount() == 0) {
                    cursor3 = getReadableDatabase().rawQuery("PRAGMA " + dbName + ".table_info(" + tblMap.getSrc() + ")", null);
                    StringBuilder columns = new StringBuilder();
                    if (cursor3 != null) {
                        cursor3.moveToFirst();
                        boolean firstRow = true;
                        int size = cursor3.getCount();
                        for (int i = 0; i < size; i++) {
                            cursor3.moveToPosition(i);
                            String columnName = cursor3.getString(cursor3.getColumnIndex("name"));
                            if (!tblMap.isColumnIgnored(columnName)) {
                                if (firstRow)
                                    columns.append(columnName);
                                else
                                    columns.append(",").append(columnName);
                            }
                            firstRow = false;
                        }
                        cursor3.close();
                    }
                    StringBuilder sql = new StringBuilder();
                    sql.append("INSERT INTO '")
                            .append(tblMap.getDest()).append("' (")
                            .append(columns).append(")")
                            .append(" SELECT ").append(columns).append(" FROM ")
                            .append(dbName).append(".").append(tblMap.getSrc());
                    String raw = sql.toString();
                    db.execSQL(raw);
                    try {
                        cursor4 = db.rawQuery("select changes();", null);
                        if (cursor4 != null) {
                            cursor4.moveToFirst();
                            int c = cursor4.getInt(0);
                        }
                    } catch (Exception ignored) {
                        Timber.e(ignored);
                    } finally {
                        if (cursor4 != null)
                            cursor4.close();
                    }
                } else {
                    StringBuilder sql = new StringBuilder();
                    sql.append("INSERT INTO '").append(tblMap.getDest()).append("' (");
                    StringBuilder values = new StringBuilder();
                    if (tblMap.getColumns().size() > 1)
                        for (int i = 0; i < tblMap.getColumns().size() - 1; i++) {
                            ColumnMap column = tblMap.getColumns().get(i);
                            if (!tblMap.isColumnIgnored(column.getSrc())) {
                                sql.append("'").append(column.getDest()).append("' , ");
                                values.append(column.getSrc()).append(",");
                            }
                        }
                    ColumnMap field = tblMap.getColumns().get(tblMap.getColumnsCount() - 1);
                    sql.append("'").append(field.getDest()).append("') ");
                    values.append(field.getSrc());
                    sql.append(" SELECT ").append(values.toString()).append(" FROM ").append(dbName).append(".").append(tblMap.getSrc());
                    String raw = sql.toString();
                    db.execSQL(raw);
                    try {
                        cursor5 = db.rawQuery("select changes();", null);
                        if (cursor5 != null) {
                            cursor5.moveToFirst();
                            int c = cursor5.getInt(0);
                        }
                    } catch (Exception ignored) {
                        Timber.e(ignored);
                    } finally {
                        if (cursor5 != null)
                            cursor5.close();
                    }
                }
            }
        } catch (Exception ex) {
            Timber.e(ex, "attaching or inserting from attached db failed");
            throw ex;
        } finally {
            db.execSQL("DETACH DATABASE " + dbName + ";");
            if (cursor0 != null && !cursor0.isClosed())
                cursor0.close();
            if (cursor1 != null && !cursor1.isClosed())
                cursor1.close();
            if (cursor2 != null && !cursor2.isClosed())
                cursor2.close();
            if (cursor3 != null && !cursor3.isClosed())
                cursor3.close();
            if (cursor4 != null && !cursor4.isClosed())
                cursor4.close();
            if (cursor5 != null && !cursor5.isClosed())
                cursor5.close();
        }
    }

    public Set<String> getListOfColumns(String tblName) {
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery("PRAGMA table_info(" + tblName + ")", null);
            Set<String> columns = new HashSet<>();
            if (cursor != null) {
                cursor.moveToFirst();
                int size = cursor.getCount();
                if (size == 0)
                    throw new TableNotFoundException(connectionString.getName(), tblName);
                for (int i = 0; i < size; i++) {
                    cursor.moveToPosition(i);
                    String columnName = cursor.getString(cursor.getColumnIndex("name"));
                    columns.add(columnName);
                }
                cursor.close();
            }
            return columns;
        } catch (Exception ex) {
            Timber.e(ex);
            return new HashSet<>();
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
        }
    }
}