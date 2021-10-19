package varanegar.com.discountcalculatorlib.dataadapter.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import org.simpleframework.xml.transform.InvalidFormatException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import varanegar.com.discountcalculatorlib.utility.GlobalVariables;

public class DiscountDbV3 extends SQLiteAssetHelper {
    private static final int DATABASE_VERSION = 47;
    private Context _context;

    public DiscountDbV3(Context context) {
        super(context, GlobalVariables.getDiscountDbName(), GlobalVariables.getDiscountDbPath(), null, DATABASE_VERSION);
        _context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        List<DbUpgrade> upgrades = null;
        try {
            upgrades = getUpgrades();
            if (upgrades != null && upgrades.size() > 0) {
                Collections.sort(upgrades, new Comparator<DbUpgrade>() {
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
                            dbUpgrade.onUpgrade(db);
                        }
                    }

                } catch (Exception ex) {
                    Log.wtf("Discount", "Upgrading Discount database failed");
                }
            }
        } catch (Exception e) {
            Log.wtf("Discount", "Upgrading Discount database failed");
        }
    }

    protected List<DbUpgrade> getUpgrades() throws IOException, InvalidFormatException {
        List<DbUpgrade> dbUpgrades = new ArrayList<>();
        List<String> scriptFiles = listScripts(_context,GlobalVariables.getDiscountDbShortName());
        for (String scriptFileName :
                scriptFiles) {
            DbUpgrade dbUpgrade = createDbUpgrade(scriptFileName);
            dbUpgrades.add(dbUpgrade);
        }
        return dbUpgrades;
    }

    protected DbUpgrade createDbUpgrade(final String filename) throws InvalidFormatException {

        String code = filename.substring(GlobalVariables.getDiscountDbShortName().length(), filename.length() - 4);
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
                        executeScriptFile(db, filename);
                    } catch (Exception e) {
                        Log.e("Discount","Unable to create discount  database");
                    }
                }
            };
        } catch (Exception ex) {
            throw new InvalidFormatException("Script file name is invalid");
        }
    }

    public static List<String> listScripts(Context context, String dbname) throws IOException {
        String[] allFiles = context.getAssets().list("databases");
        List<String> scripts = new ArrayList<>();
        for (String scriptName :
                allFiles) {
            if (scriptName.endsWith(".sql") && scriptName.startsWith(dbname))
                scripts.add(scriptName);
        }
        return scripts;
    }

    private void executeScriptFile(SQLiteDatabase db, String filename) throws IOException, InvalidFormatException {
        List<String> script = readScriptFile(filename);
        for (String sql : script) {
            db.execSQL(sql);
        }
    }

    public List<String> readScriptFile(String scriptName) throws IOException, InvalidFormatException {
        List<String> commands = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(_context.getAssets().open("databases/" + scriptName)));

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
                    Log.e("DiscountDbV3", e.getMessage());
                }
            }
            return commands;
        }
    }



}