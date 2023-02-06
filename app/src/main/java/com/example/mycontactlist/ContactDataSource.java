package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.sql.SQLException;
import java.util.ArrayList;

public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ArrayList<String> getContactName() {
        ArrayList<String> contactNames = new ArrayList<>();
        try {
            String query = "Select contactname from contact";
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                contactNames.add(cursor.getString(0));
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e) {
            contactNames = new ArrayList<String>();
        }
        return contactNames;
    }

    public ContactDataSource(Context context) {
        dbHelper = new ContactDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertContact(Contact c) {
        boolean didSucceed = false;
        try {
            ContentValues initialValues = new ContentValues();

            initialValues.put(ContactDBHelper.NAME, c.getContactName());
            initialValues.put(ContactDBHelper.ADDRESS, c.getStreetAddress());
            initialValues.put(ContactDBHelper.CITY, c.getCity());
            initialValues.put(ContactDBHelper.STATE, c.getState());
            initialValues.put(ContactDBHelper.ZIPCODE, c.getZipCode());
            initialValues.put(ContactDBHelper.PHONENUMBER, c.getPhoneNumber());
            initialValues.put(ContactDBHelper.CELLNUMBER, c.getCellNumber());
            initialValues.put(ContactDBHelper.EMAIL, c.geteMail());
            initialValues.put(ContactDBHelper.BIRTHDAY, String.valueOf(c.getBirthday().getTimeInMillis()));

            didSucceed = database.insert(ContactDBHelper.CONTACT_TABLE, null, initialValues) > 0;
        }
        catch (Exception e) {

        }
        return didSucceed;
    }

    public boolean updateContact(Contact c) {
        boolean didSucceed = false;
        try {
            Long rowID = (long) c.getContactID();
            ContentValues updateValues = new ContentValues();

            updateValues.put(ContactDBHelper.NAME, c.getContactName());
            updateValues.put(ContactDBHelper.ADDRESS, c.getStreetAddress());
            updateValues.put(ContactDBHelper.CITY, c.getCity());
            updateValues.put(ContactDBHelper.STATE, c.getState());
            updateValues.put(ContactDBHelper.ZIPCODE, c.getZipCode());
            updateValues.put(ContactDBHelper.PHONENUMBER, c.getPhoneNumber());
            updateValues.put(ContactDBHelper.CELLNUMBER, c.getCellNumber());
            updateValues.put(ContactDBHelper.EMAIL, c.geteMail());
            updateValues.put(ContactDBHelper.BIRTHDAY, String.valueOf(c.getBirthday().getTimeInMillis()));

            didSucceed = database.update(ContactDBHelper.CONTACT_TABLE, updateValues, "id=" + rowID, null) > 0;
        }
        catch (Exception e) {

        }
        return didSucceed;
    }

    public int getLastContactID() {
        int lastId;
        try {
            String query = "Select MAX(_id) from " + ContactDBHelper.CONTACT_TABLE;
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();
        }
        catch (Exception e) {
            lastId = -1;
        }
        return lastId;
    }
}
