package com.simonliebers.spritrechner.General;

import java.security.Key;

public class Constants {
    public static String MapBoxAccessToken = "pk.eyJ1Ijoic2l4b25sIiwiYSI6ImNrOG84YnN2cTA1cXMzZXQyamN1dXlrYmsifQ.bPFe7dzeSTuA_UZ2wCoBuQ";

    public static final String DataPrefs = "DataPrefs";
    public static final String GasKey = "GasKey";
    public static final String SortKey = "ConsumptionKey";

    public static enum Type{
        e5, e10, diesel, all;
    }

    public static enum Sort{
        price, dist;
    }
}
