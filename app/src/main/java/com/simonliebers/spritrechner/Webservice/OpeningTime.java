package com.simonliebers.spritrechner.Webservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OpeningTime {
    private String text;
    private String start;
    private String end;

    @JsonProperty("text")
    public String getText() { return text; }
    @JsonProperty("text")
    public void setText(String value) { this.text = value; }

    @JsonProperty("start")
    public String getStart() { return start; }
    @JsonProperty("start")
    public void setStart(String value) { this.start = value; }

    @JsonProperty("end")
    public String getEnd() { return end; }
    @JsonProperty("end")
    public void setEnd(String value) { this.end = value; }
}
