package com.example.mycontactlist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.mycontactlist.Contact;
import com.example.mycontactlist.ContactDBHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ContactDataSource {
    private SQLiteDatabase database;
    private ContactDBHelper dbHelper;

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
            //Stores values from the current Contact.
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
            if(c.getPicture() != null ){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                c.getPicture().compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo = baos.toByteArray();
                initialValues.put(ContactDBHelper.CONTACT_PHOTO,photo);
            }
            //If a row was inserted return 1
            didSucceed = database.insert(ContactDBHelper.CONTACT_TABLE, null, initialValues) > 0;


        } catch (Exception e) {

        }
        return didSucceed;
    }

    public boolean updateContact(Contact c) {
        boolean didSucceed = false;
        try {
            //Retirives contact ID
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
            if(c.getPicture() != null ){
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                c.getPicture().compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] photo = baos.toByteArray();
                updateValues.put(ContactDBHelper.CONTACT_PHOTO,photo);
            }
            //If table is updated if the Contact ids are Identical.
            String whereClause  = ContactDBHelper.CONTACT_ID+"= ";
            didSucceed = database.update(ContactDBHelper.CONTACT_TABLE, updateValues, whereClause + rowID, null) > 0;
        } catch (Exception e) {

        }
        return didSucceed;
    }
    //If the database executed correctly return 1 else do nothing
    public boolean deleteContact(int contactId){
        boolean didDelete = false;
        try{
            System.out.println("Help");
            didDelete = database.delete(ContactDBHelper.CONTACT_TABLE,ContactDBHelper.CONTACT_ID+" = "+contactId,null)>0;
        }catch(Exception e ){

        }
        return didDelete;
    }


    public int getLastContactID() {
        int lastId;
        try {
            String query = "Select MAX(" + ContactDBHelper.CONTACT_ID + ") from " + ContactDBHelper.CONTACT_TABLE;
            Cursor cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
            cursor.close();

        } catch (Exception e) {
            //If no results LastID is -1
            lastId = -1;
        }
        return lastId;
    }

    //Return an array that gets everything
    public ArrayList<Contact> getContacts(){
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        try{
            String query = "SELECT * FROM "+ContactDBHelper.CONTACT_TABLE;
            Cursor cursor = database.rawQuery(query, null);

            Contact newContact;

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
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
        }catch(Exception e ){
            contacts = new ArrayList<Contact>();
        }
        return contacts;
    }
    //Return an array that gets everything but filtered
    public ArrayList<Contact> getContacts(String sortField, String sortOrder){
        ArrayList<Contact> contacts = new ArrayList<Contact>();
        try{
            String query = "SELECT * FROM "+ContactDBHelper.CONTACT_TABLE +" ORDER BY "+sortField+" "+sortOrder;
            Cursor cursor = database.rawQuery(query, null);

            Contact newContact;

            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
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
        }catch(Exception e ){
            contacts = new ArrayList<Contact>();
        }
        return contacts;
    }
    //Gets specific customer on giving ID
    public Contact getSpecificContact(int contactId) {
        Contact contact = new Contact();
        String query = "SELECT * FROM "+ContactDBHelper.CONTACT_TABLE+" WHERE " + ContactDBHelper.CONTACT_ID + "=" + contactId;
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst()){
            contact.setContactID(cursor.getInt(0));
            contact.setContactName(cursor.getString(1));
            contact.setStreetAddress(cursor.getString(2));
            contact.setCity(cursor.getString(3));
            contact.setState(cursor.getString(4));
            contact.setZipCode(cursor.getString(5));
            contact.setPhoneNumber(cursor.getString(6));
            contact.setCellNumber(cursor.getString(7));
            contact.seteMail(cursor.getString(8));
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(cursor.getString(9)));
            contact.setBirthday(calendar);
            //Gathers photo from DBS and set it to the contact.
            byte[] photo = cursor.getBlob(10);
            if(photo != null){
                ByteArrayInputStream imageStream = new ByteArrayInputStream(photo);
                Bitmap thePicture = BitmapFactory.decodeStream(imageStream);
                contact.setPicture(thePicture);
            }

            cursor.close();
        }
        return contact;
    }

}
