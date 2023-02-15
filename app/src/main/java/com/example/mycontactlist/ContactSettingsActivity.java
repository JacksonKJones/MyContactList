package com.example.mycontactlist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ContactSettingsActivity extends AppCompatActivity {
    public static final String ContactList_Preferences = "ContactList_Preferences";
    public static final String sortFieldKey = "sortField";
    public static final String sortOrderKey = "sortOrderField";

    ImageButton ibList, ibMap, ibSettings;
    RadioButton rbName, rbCity, rbBirthday, rbAscending, rbDescending;
    RadioGroup rgSortBy, rgSortOrder;
    private String sortBy, sortOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_settings);

        initListButton();
        initMapButton();
        initSettingsButton();

        initSettings();
        initSortByClick();
        initSortOrderClick();
    }

    private void initSettings() {
        rgSortBy = findViewById(R.id.radioGroupSortBy);
        rgSortOrder = findViewById(R.id.radioGroupSortOrder);
        rbAscending = findViewById(R.id.radioAscending);
        rbDescending = findViewById(R.id.radioDescending);
        rbName = findViewById(R.id.radioName);
        rbCity = findViewById(R.id.radioCity);
        rbBirthday = findViewById(R.id.radioBirthday);

        sortBy = getSharedPreferences(ContactList_Preferences, Context.MODE_PRIVATE).getString(sortFieldKey, ContactDBHelper.NAME);
        sortOrder = getSharedPreferences(ContactList_Preferences, Context.MODE_PRIVATE).getString(sortOrderKey, "ASC");

        if (sortBy.equalsIgnoreCase(ContactDBHelper.NAME)) {
            rbName.setChecked(true);
        } else if (sortBy.equalsIgnoreCase(ContactDBHelper.CITY)) {
            rbCity.setChecked(true);
        } else {
            rbBirthday.setChecked(true);
        }

        if (sortOrder.equalsIgnoreCase("ASC")) {
            rbAscending.setChecked(true);
        } else {
            rbDescending.setChecked(true);
        }
    }

    private void initSortByClick() {
        rgSortBy.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rbName.isChecked()) {
                    getSharedPreferences(ContactList_Preferences, Context.MODE_PRIVATE).edit().putString(sortFieldKey, ContactDBHelper.NAME).apply();
                } else if (rbCity.isChecked()) {
                    getSharedPreferences(ContactList_Preferences, Context.MODE_PRIVATE).edit().putString(sortFieldKey, ContactDBHelper.CITY).apply();
                } else {
                    getSharedPreferences(ContactList_Preferences, Context.MODE_PRIVATE).edit().putString(sortFieldKey, ContactDBHelper.BIRTHDAY).apply();
                }
            }
        });
    }

    private void initSortOrderClick() {
        rgSortOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rbAscending.isChecked()) {
                    getSharedPreferences(ContactList_Preferences, Context.MODE_PRIVATE).edit().putString(sortOrderKey, "ASC").apply();
                } else {
                    getSharedPreferences(ContactList_Preferences, Context.MODE_PRIVATE).edit().putString(sortOrderKey, "DESC").apply();
                }
            }
        });
    }

    private void initListButton () {
        ibList = findViewById(R.id.imageButtonList);
        ibList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initMapButton () {
        ibMap = findViewById(R.id.imageButtonMap);
        ibMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactSettingsActivity.this, ContactMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private void initSettingsButton () {
        ibSettings = findViewById(R.id.imageButtonSettings);
        ibSettings.setEnabled(false);

    }
}