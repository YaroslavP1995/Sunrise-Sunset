package com.example.slavik.sunrise_sunset;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class Results {
    @SerializedName("results")
    @Expose
    private Results_ results;
    @SerializedName("status")
    @Expose
    private String status;

    public Results_ getResults() {
        return results;
    }

    public void setResults(Results_ results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
 }                                         