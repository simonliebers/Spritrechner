package com.simonliebers.spritrechner.General.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GasDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "gas_table";
    private static final String COL1 = "ID";
    private static final String COL2 = "station";
    private static final String COL3 = "timestamp";
    private  static final  String COL4 = "amount";
    private  static final  String COL5 = "price";



    public GasDatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 + " TEXT DEFAULT '', "
                + COL3 + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + COL4 + " TEXT DEFAULT '0', "
                + COL5 + " TEXT DEFAULT '0')";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String stationuid, String amount, String price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, stationuid);
        contentValues.put(COL4, amount);
        contentValues.put(COL5, price);

        Log.d(TAG, "addData: Tanked " + amount + " liters for a price of " + price + "€ on the station " + stationuid + " (" + TABLE_NAME + ")");

        long result = db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Returns all the data from database
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getDataById(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + " = '" + id + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor getDataByStationUID(String uid){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + COL2 + " = '" + uid + "'";
        Cursor data = db.rawQuery(query, null);
        return data;
    }

}
