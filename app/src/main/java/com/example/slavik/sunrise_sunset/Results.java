package com.example.slavik.sunrise_sunset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class Results {
    @SerializedName("results")
    @Expose
    private SunsetSanrise results;
    @SerializedName("status")
    @Expose
    private String status;

    public SunsetSanrise getResults() {
        return results;
    }

    public void setResults(SunsetSanrise results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
 }                                         