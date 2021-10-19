package com.varanegar.framework.database;

import android.content.Context;

import com.varanegar.framework.util.Linq;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by atp on 12/10/2016.
 */
public class SQLiteConnectionString {

    public String getName() {
        return name;
    }

    private String name;

    public int getVersion() {
        return version;
    }

    private int version;

    public SQLiteConnectionString(Context context, final String name) throws IOException, ScriptNotFoundException {
        this.name = name;
        String n = name;
        if (name.endsWith(".db")) {
            n = name.substring(0, name.length() - 3);
        }
        List<String> scripts = DbHandler.listScripts(context, n);
        if (scripts.size() == 0)
            throw new ScriptNotFoundException();
        final String finalN = n;
        List<Integer> versions = Linq.map(scripts, new Linq.Map<String, Integer>() {
            @Override
            public Integer run(String item) {
                int code = Integer.valueOf(item.substring(finalN.length(), item.length() - 4));
                return code;
            }
        });
        Linq.sort(versions, new Comparator<Integer>() {
            @Override
            public int compare(Integer v1, Integer v2) {
                if (v1.equals(v2))
                    return 0;
                else
                    return v1 > v2 ? 1 : -1;
            }
        });
        int latest = versions.get(versions.size() - 1);
        this.version = latest;

    }

    public SQLiteConnectionString(String name, int version) {
        this.name = name;
        this.version = version;
    }

    public SQLiteConnectionString(String name) {
        this.name = name;
        this.version = 1;
    }
}
