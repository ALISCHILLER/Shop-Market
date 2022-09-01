package com.varanegar.vaslibrary.manager.emphaticitems;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

public class EmphaticPackageCheckResult {
    private String error;
    private final HashMap<UUID, BigDecimal> qtys;
    private String warning;

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

    public void setError(String error) {
        this.error = error;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}
