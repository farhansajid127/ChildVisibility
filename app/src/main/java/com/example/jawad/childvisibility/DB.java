package com.example.jawad.childvisibility;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.jawad.childvisibility.Model.ChilData;

public class DB extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "childvissibility";
   private static final String TABLE_NAME = "childemail";
    private static final String DUA_NOTIFY_TABLE = "dua_notification";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_Email = "email";
    public DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DUADETAIL_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_Email + " TEXT"+")";
        db.execSQL(CREATE_DUADETAIL_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + DUA_NOTIFY_TABLE);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new contact
     public boolean addNewEmail(ChilData duaData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_Email, duaData.getEmail());
         if(db.insert(TABLE_NAME, null, values)>0){
              db.close();
             return true;
         }else {
              db.close();
             return false;
         }
    }

    public List<ChilData> getAllChild() {
        List<ChilData> duaDataList = new ArrayList<ChilData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     if (cursor.moveToFirst()) {
            do {
                ChilData duaData = new ChilData();
                duaData.setID(Integer.parseInt(cursor.getString(0)));
                duaData.setEmail(cursor.getString(1));
                duaDataList.add(duaData);
            } while (cursor.moveToNext());
        }
        return duaDataList;
    }
    public void deleteDua(ChilData duaData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_Email + " = ?",
                new String[] { String.valueOf(duaData.getEmail()) });
        db.close();
    }


    // Getting contacts Count
    public int getDuaCount() {
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        return cursor.getCount();
    }

}