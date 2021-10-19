package com.varanegar.vaslibrary.manager.emphaticitems;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

public class EmphaticPackageCheckResult {
    private final String error;
    private final HashMap<UUID, BigDecimal> qtys;
    private final String warning;

    public EmphaticPackageCheckResult(String error, String warning, HashMap<UUID, BigDecimal> qtys) {
        this.error = error;
        this.warning = warning;
        this.qtys = qtys;
    }

    public String getError() {
        return error;
    }

    public HashMap<UUID, BigDecimal> getQtys() {
        return qtys;
    }

    public String getWarning() {
        return warning;
    }
}
