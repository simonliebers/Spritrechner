package com.simonliebers.spritrechner.Webservice;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Station {
    private String id;
    private String name;
    private String brand;
    private String street;
    private String houseNumber;
    private long postCode;
    private String place;
    private OpeningTime[] openingTimes;
    private Object[] overrides;
    private boolean wholeDay;
    private boolean isOpen;
    private double dist;
    private double e5;
    private double e10;
    private double diesel;
    private double price;
    private double lat;
    private double lng;
    private Object state;

    @JsonProperty("id")
    public String getID() { return id; }
    @JsonProperty("id")
    public void setID(String value) { this.id = value; }

    @JsonProperty("name")
    public String getName() { return name; }
    @JsonProperty("name")
    public void setName(String value) { this.name = value; }

    @JsonProperty("brand")
    public String getBrand() { return brand; }
    @JsonProperty("brand")
    public void setBrand(String value) { this.brand = value; }

    @JsonProperty("street")
    public String getStreet() { return street; }
    @JsonProperty("street")
    public void setStreet(String value) { this.street = value; }

    @JsonProperty("houseNumber")
    public String getHouseNumber() { return houseNumber; }
    @JsonProperty("houseNumber")
    public void setHouseNumber(String value) { this.houseNumber = value; }

    @JsonProperty("postCode")
    public long getPostCode() { return postCode; }
    @JsonProperty("postCode")
    public void setPostCode(long value) { this.postCode = value; }

    @JsonProperty("place")
    public String getPlace() { return place; }
    @JsonProperty("place")
    public void setPlace(String value) { this.place = value; }

    @JsonProperty("openingTimes")
    public OpeningTime[] getOpeningTimes() { return openingTimes; }
    @JsonProperty("openingTimes")
    public void setOpeningTimes(OpeningTime[] value) { this.openingTimes = value; }

    @JsonProperty("overrides")
    public Object[] getOverrides() { return overrides; }
    @JsonProperty("overrides")
    public void setOverrides(Object[] value) { this.overrides = value; }

    @JsonProperty("wholeDay")
    public boolean getWholeDay() { return wholeDay; }
    @JsonProperty("wholeDay")
    public void setWholeDay(boolean value) { this.wholeDay = value; }

    @JsonProperty("isOpen")
    public boolean getIsOpen() { return isOpen; }
    @JsonProperty("isOpen")
    public void setIsOpen(boolean value) { this.isOpen = value; }

    @JsonProperty("dist")
    public double getDist() { return dist; }
    @JsonProperty("dist")
    public void setDist(double value) { this.dist = value; }

    @JsonProperty("e5")
    public double getE5() { return e5; }
    @JsonProperty("e5")
    public void setE5(double value) { this.e5 = value; }

    @JsonProperty("e10")
    public double getE10() { return e10; }
    @JsonProperty("e10")
    public void setE10(double value) { this.e10 = value; }

    @JsonProperty("diesel")
    public double getDiesel() { return diesel; }
    @JsonProperty("diesel")
    public void setDiesel(double value) { this.diesel = value; }

    @JsonProperty("price")
    public double getPrice() { return price; }
    @JsonProperty("price")
    public void setPrice(double value) { this.price = value; }

    @JsonProperty("lat")
    public double getLat() { return lat; }
    @JsonProperty("lat")
    public void setLat(double value) { this.lat = value; }

    @JsonProperty("lng")
    public double getLng() { return lng; }
    @JsonProperty("lng")
    public void setLng(double value) { this.lng = value; }

    @JsonProperty("state")
    public Object getState() { return state; }
    @JsonProperty("state")
    public void setState(Object value) { this.state = value; }
}
