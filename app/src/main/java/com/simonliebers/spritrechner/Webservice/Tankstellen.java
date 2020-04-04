package com.simonliebers.spritrechner.Webservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.simonliebers.spritrechner.Webservice.Station;

public class Tankstellen {
    private boolean ok;
    private String license;
    private String data;
    private String status;
    private Station[] stations;

    @JsonProperty("ok")
    public boolean getOk() { return ok; }
    @JsonProperty("ok")
    public void setOk(boolean value) { this.ok = value; }

    @JsonProperty("license")
    public String getLicense() { return license; }
    @JsonProperty("license")
    public void setLicense(String value) { this.license = value; }

    @JsonProperty("data")
    public String getData() { return data; }
    @JsonProperty("data")
    public void setData(String value) { this.data = value; }

    @JsonProperty("status")
    public String getStatus() { return status; }
    @JsonProperty("status")
    public void setStatus(String value) { this.status = value; }

    @JsonProperty("stations")
    public Station[] getStations() { return stations; }
    @JsonProperty("stations")
    public void setStations(Station[] value) { this.stations = value; }
}
