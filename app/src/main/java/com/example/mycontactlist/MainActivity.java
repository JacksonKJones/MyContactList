package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.controls.templates.ToggleTemplate;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.SaveDateListener {
    public static final int PERMISSION_REQUEST_PHONE = 102;
    ContactDataSource ds = new ContactDataSource(this);
    ImageButton ibList, ibMap, ibSettings, cameraButton;
    EditText editPhone, editCell;
    final int PERMISSION_REQUEST_CAMERA = 103;
    final int CAMERA_REQUEST = 1888;
    private Contact currentContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListButton();
        initMapButton();
        initSettingsButton();

        initToggleButton();
        initSaveButton();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            initContact(extras.getInt("contactid"));
        }
        else {
            currentContact = new Contact();
        }
        setForEditing(false);
        initChangeDateButton();
        initTextChangedEvents();
        initCallFunction();
        initImageButton();

        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Contact> contacts = new ArrayList<Contact>();
                try {
                    ds.open();
                    contacts = ds.getContacts();
                    ds.close();
                    if (contacts.size() > 0) {
                        Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {

                    }
                }catch(Exception e ){

                }
            }
        });
    }

    private void initContact(int id) {
        ContactDataSource ds = new ContactDataSource(MainActivity.this);
        try {
            ds.open();
            currentContact = ds.getSpecificContact(id);
            ds.close();
        }
        catch (Exception e) {
            Toast.makeText(this, "Load Contact Failed", Toast.LENGTH_LONG).show();
        }
        EditText editName = findViewById(R.id.editName);
        EditText editAddress = findViewById(R.id.editAddress);
        EditText editCity = findViewById(R.id.editCity);
        EditText editState = findViewById(R.id.editState);
        EditText editZipCode = findViewById(R.id.editZipcode);
        EditText editPhone = findViewById(R.id.editHome);
        EditText editCell = findViewById(R.id.editCell);
        EditText editEmail = findViewById(R.id.editEMail);
        TextView birthDay = findViewById(R.id.textBirthday);

        editName.setText(currentContact.getContactName());
        editAddress.setText(currentContact.getStreetAddress());
        editCity.setText(currentContact.getCity());
        editState.setText(currentContact.getState());
        editZipCode.setText(currentContact.getZipCode());
        editPhone.setText(currentContact.getPhoneNumber());
        editCell.setText(currentContact.getCellNumber());
        editEmail.setText(currentContact.geteMail());
        birthDay.setText(DateFormat.format("MM/dd/yyyy", currentContact.getBirthday().getTimeInMillis()).toString());

        ImageButton picture = (ImageButton) findViewById(R.id.imageContact);
        if(currentContact.getPicture() != null){
            picture.setImageBitmap(currentContact.getPicture());
        }
        else{
            picture.setImageResource(R.drawable.ic_launcher_foreground);
        }

    }


    private void initListButton() {
        ibList = findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton() {
        ibMap = findViewById(R.id.imageButtonMap);
        ibMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactMapActivity.class);
                if (currentContact.getContactID() == -1) {
                    Toast.makeText(getBaseContext(), "Contact must be saved before it can be mapped", Toast.LENGTH_LONG).show();
                } else {
                    intent.putExtra(ContactDBHelper.CONTACT_ID, currentContact.getContactID());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSaveButton() {
        Button saveButton = findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean wasSuccessful;

                hideKeyboard();

                ContactDataSource ds = new ContactDataSource(MainActivity.this);
                try {
                    ds.open();

                    if (currentContact.getContactID() == -1) {
                        wasSuccessful = ds.insertContact(currentContact);
                        if (wasSuccessful) {
                            int newId = ds.getLastContactID();
                            currentContact.setContactID(newId);
                        }
                    } else {
                        wasSuccessful = ds.updateContact(currentContact);
                    }
                    ds.close();
                }
                catch (Exception e) {
                    wasSuccessful = false;
                }

                if (wasSuccessful) {
                    ToggleButton editToggle = findViewById(R.id.toggleButton);
                    editToggle.toggle();
                    setForEditing(false);
                }
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        EditText editName = findViewById(R.id.editName);
        imm.hideSoftInputFromWindow(editName.getWindowToken(), 0);
        EditText editAddress = findViewById(R.id.editAddress);
        imm.hideSoftInputFromWindow(editAddress.getWindowToken(), 0);
        EditText editCity = findViewById(R.id.editCity);
        imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);
        EditText editState = findViewById(R.id.editState);
        imm.hideSoftInputFromWindow(editState.getWindowToken(), 0);
        EditText editZipcode = findViewById(R.id.editZipcode);
        imm.hideSoftInputFromWindow(editZipcode.getWindowToken(), 0);
        EditText editHome = findViewById(R.id.editHome);
        imm.hideSoftInputFromWindow(editHome.getWindowToken(), 0);
        EditText editCell = findViewById(R.id.editCell);
        imm.hideSoftInputFromWindow(editCell.getWindowToken(), 0);
        EditText editEMail = findViewById(R.id.editEMail);
        imm.hideSoftInputFromWindow(editEMail.getWindowToken(), 0);
    }

    private void initSettingsButton() {
        ibSettings = findViewById(R.id.imageButtonSettings);
        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ContactSettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initToggleButton() {
        final ToggleButton editToggle = findViewById(R.id.toggleButton);
        editToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setForEditing(editToggle.isChecked());
            }
        });
    }

    private void setForEditing(boolean enabled) {
        EditText editName = findViewById(R.id.editName);
        EditText editAddress = findViewById(R.id.editAddress);
        EditText editCity = findViewById(R.id.editCity);
        EditText editState = findViewById(R.id.editState);
        EditText editZipCode = findViewById(R.id.editZipcode);
        EditText editPhone = findViewById(R.id.editHome);
        EditText editCell = findViewById(R.id.editCell);
        EditText editEmail = findViewById(R.id.editEMail);
        Button buttonChange = findViewById(R.id.buttonBirthday);
        Button buttonSave = findViewById(R.id.buttonSave);

        editName.setEnabled(enabled);
        editAddress.setEnabled(enabled);
        editCity.setEnabled(enabled);
        editState.setEnabled(enabled);
        editZipCode.setEnabled(enabled);
        editPhone.setEnabled(enabled);
        editCell.setEnabled(enabled);
        editEmail.setEnabled(enabled);
        buttonChange.setEnabled(enabled);
        buttonSave.setEnabled(enabled);

        if (enabled) {
            editName.requestFocus();
        } else {
            ScrollView s = findViewById(R.id.scrollView);
            s.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    @Override
    public void didFinishDatePickerDialog(Calendar selectedTime) {
        TextView birthDay = findViewById(R.id.textBirthday);
        birthDay.setText(DateFormat.format("MM/dd/yyy", selectedTime));
        currentContact.setBirthday(selectedTime);
    }

    private void initChangeDateButton() {
        Button changeDate = findViewById(R.id.buttonBirthday);
        changeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                DatePickerDialog datePickerDialog = new DatePickerDialog();
                datePickerDialog.show(fm, "DatePick");
            }
        });
    }

    private void textChangedEvents(EditText input) {
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                currentContact.setContactName(input.getText().toString());
            }
        });
    }

    private void initTextChangedEvents() {
        final EditText etContactName = findViewById(R.id.editName);
        textChangedEvents(etContactName);

        final EditText etStreetAddress = findViewById(R.id.editAddress);
        textChangedEvents(etStreetAddress);

        final EditText etCity = findViewById(R.id.editCity);
        textChangedEvents(etCity);

        final EditText etState = findViewById(R.id.editState);
        textChangedEvents(etState);

        final EditText etZipCode = findViewById(R.id.editZipcode);
        textChangedEvents(etZipCode);

        final EditText etHomeNumber = findViewById(R.id.editHome);
        textChangedEvents(etHomeNumber);

        final EditText etCellNumber = findViewById(R.id.editCell);
        textChangedEvents(etCellNumber);
        etCellNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        final EditText etEMail = findViewById(R.id.editEMail);
        textChangedEvents(etEMail);
        etHomeNumber.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    private void checkPhonePermission(String phone){
        if(Build.VERSION.SDK_INT>=23){
            if(ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        android.Manifest.permission.CALL_PHONE)){
                    Snackbar.make(findViewById(R.id.activity_main),
                            "MyContactList requires this permission to place a call from the app.",
                            Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{
                                            android.Manifest.permission.CALL_PHONE
                                    },PERMISSION_REQUEST_PHONE
                            );
                        }
                    }).show();
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CALL_PHONE},PERMISSION_REQUEST_PHONE);
                }
            }else{
                callContact(phone);
            }
        }else {
            callContact(phone);
        }
    }

    private void initCallFunction() {
        editPhone = findViewById(R.id.editHome);
        editPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                checkPhonePermission(currentContact.getPhoneNumber());
                return false;
            }
        });
    }

    private void callContact(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else {
            startActivity(intent);
        }
    }

    private void initImageButton() {
        cameraButton = findViewById(R.id.imageContact);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 23){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    ) {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(
                                MainActivity.this,Manifest.permission.CAMERA
                        )){
                            Snackbar.make(findViewById(R.id.activity_main),"The app needs permission to take pictures. ",
                                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                            Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);

                                }
                            }).show();
                        }else{
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},PERMISSION_REQUEST_CAMERA);
                        }
                    }
                    else {
                        takePhoto();
                    }
                }else {
                    takePhoto();
                }
            }
        });
    }

    private void takePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Bitmap scaledPhoto = Bitmap.createScaledBitmap(photo, 144, 144, true);
                ImageButton imageContact = (ImageButton) findViewById(R.id.imageContact);
                imageContact.setImageBitmap(scaledPhoto);
                currentContact.setPicture(scaledPhoto);
            }
        }
    }

}