package com.varanegar.supervisor.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class StatusConfigModel {
        private List<String> dealersId = null;

        private List<String> statuses = null;

        private String startDate;
        private String endDate;

        public List<String> getDealersId() {
            return dealersId;
        }

        public void setDealersId(List<String> dealersId) {
            this.dealersId = dealersId;
        }

        public List<String> getStatuses() {
            return statuses;
        }

        public void setStatuses(List<String> statuses) {
            this.statuses = statuses;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }


}
