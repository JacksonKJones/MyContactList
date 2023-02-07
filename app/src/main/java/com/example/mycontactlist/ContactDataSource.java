package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

public class ContactDataSource {

    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

    public ArrayList<Contact> getContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        try {
            String query = "SELECT * FROM CONTACT_TABLE";
            Cursor cursor = database.rawQuery(query, null);

            Contact newContact;
            cursor.moveToFirst();
            while(!cursor.isAfterLast()) {
                newContact = new Contact();
                newContact.setContactID(cursor.getInt(0));
                newContact.setContactName(cursor.getString(1));
                newContact.setStreetAddress(cursor.getString(2));
                newContact.setCity(cursor.getString(3));
                newContact.setState(cursor.getString(4));
                newContact.setZipCode(cursor.getString(5));
                newContact.setPhoneNumber(cursor.getString(6));
                newContact.setCellNumber(cursor.getString(7));
                newContact.seteMail(cursor.getString(8));
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
                newContact.setBirthday(calendar);
                contacts.add(newContact);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e) {
            contacts = new ArrayList<Contact>();
        }
        return contacts;
    }

    public ArrayList<String> getContactName() {
        ArrayList<String> contactNames = new ArrayList<>();
        try {
            String query = "Select COLUMN_NAME from CONTACT_TABLE";
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
            String query = "Select MAX(COLUMN_CONTACT_ID) from " + ContactDBHelper.CONTACT_TABLE;
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
