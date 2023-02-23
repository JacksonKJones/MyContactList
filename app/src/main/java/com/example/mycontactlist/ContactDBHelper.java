package com.example.mycontactlist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class ContactDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mycontacts.db";
    private static final int DATABASE_VERSION = 2;
    public static final String CONTACT_TABLE = "CONTACT_TABLE";
    public static final String CONTACT_ID = "COLUMN_CONTACT_ID";
    public static final String NAME = "COLUMN_NAME";
    public static final String ADDRESS = "COLUMN_ADDRESS";
    public static final String CITY= "COLUMN_CITY";
    public static final String STATE = "COLUMN_STATE";
    public static final String ZIPCODE = "COLUMN_ZIPCODE";
    public static final String PHONENUMBER = "COLUMN_PHONENUMBER";
    public static final String CELLNUMBER = "COLUMN_CELLNUMBER";
    public static final String EMAIL = "COLUMN_EMAIL";
    public static final String BIRTHDAY = "COLUMN_BIRTHDAY";
    public static final String CONTACT_PHOTO = "COLUMN_CONTACT_PHOTO";


    private static final String CREATE_TABLE =
            "CREATE TABLE " + CONTACT_TABLE + " (" + CONTACT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NAME + " TEXT, " + ADDRESS + " TEXT, "
            + CITY + " TEXT, " + STATE + " TEXT, "
            + ZIPCODE + " TEXT, " + PHONENUMBER + " TEXT, "
            + CELLNUMBER + " TEXT, " + EMAIL + " TEXT, "
            + BIRTHDAY + " TEXT, " + CONTACT_PHOTO + " BLOB);";

    public ContactDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("ALTER TABLE " + CONTACT_TABLE + " ADD COLUMN " + CONTACT_PHOTO + " BLOB");
        } catch (Exception e) {

        }
    }
}
