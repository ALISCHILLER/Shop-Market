package com.varanegar.vaslibrary.base;

import androidx.annotation.Nullable;

import java.util.Locale;
import java.util.UUID;

/**
 * Created by A.Torabi on 2/12/2018.
 */
public class LocalModel {
    private final UUID id;
    private final String name;
    private Locale local;

    private LocalModel(@Nullable Locale locale, UUID id, String name) {
        this.local = locale;
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Locale getLocal(@Nullable UUID id) {
        if (id == null)
            return Persian.local;
        if (id.equals(Persian.id))
            return Persian.local;
        else if (id.equals(English.id))
            return English.local;
        else if (id.equals(Arabic.id))
            return Arabic.local;
        else if (id.equals(Kurdish.id))
            return Kurdish.local;
        else if (id.equals(French.id))
            return French.local;
        else if (id.equals(Turkish.id))
            return Turkish.local;
        else
            return Persian.local;
    }

    public Locale getLocal() {
        if (id == null)
            return Persian.local;
        if (id.equals(Persian.id))
            return Persian.local;
        else if (id.equals(English.id))
            return English.local;
        else if (id.equals(Arabic.id))
            return Arabic.local;
        else if (id.equals(Kurdish.id))
            return Kurdish.local;
        else if (id.equals(French.id))
            return French.local;
        else if (id.equals(Turkish.id))
            return Turkish.local;
        else
            return Persian.local;
    }

    public static final LocalModel Persian = new LocalModel(new Locale("fa", "ir"), UUID.fromString("E612912E-9A2C-4B69-B2FF-90731873683D"), "Persian");
    public static final LocalModel English = new LocalModel(Locale.US, UUID.fromString("7824F541-7F5D-4CEB-80DB-6EC87A821BF6"), "English");
    public static final LocalModel Arabic = new LocalModel(new Locale("ar"), UUID.fromString("AEFE7E1F-1892-475B-BEAF-2B9F462933DD"), "Arabic");
    public static final LocalModel Kurdish = new LocalModel(new Locale("ku"), UUID.fromString("24434B7D-1AB0-4549-B722-05DFE7434F01"), "Kurdish");
    public static final LocalModel French = new LocalModel(new Locale("fr"), UUID.fromString("A3211DFE-9209-4F42-B60F-CF6E93AF813C"), "French");
    public static final LocalModel Turkish = new LocalModel(new Locale("tr"), UUID.fromString("C57EAF6F-F217-4473-A114-8A80F3C73B3F"), "Turkish");
}
