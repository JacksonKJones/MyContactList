package com.example.mycontactlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter {
    private ArrayList<Contact> contactData;
    private static View.OnClickListener mOnItemClickListener;
    private boolean isDeleting;
    private Context parentContext;

    public ContactAdapter (ArrayList<Contact> arrayList, Context context) {
        contactData = arrayList;
        parentContext = context;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewContact;
        public TextView textPhone;
        public TextView textAddress;
        public TextView textCityStateZipCode;
        public Button deleteButton;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContact = itemView.findViewById(R.id.textViewName);
            textPhone = itemView.findViewById(R.id.textPhoneNumber);
            textAddress = itemView.findViewById(R.id.streetAddressTextCell);
            textCityStateZipCode = itemView.findViewById(R.id.cityStateZipcodeTextCell);
            deleteButton = itemView.findViewById(R.id.buttonDeleteContact);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }

        public TextView getContactTextView() {
            return textViewContact;
        }

        public TextView getPhoneTextView() {
            return textPhone;
        }

        public TextView getTextAddress() {
            return textAddress;
        }

        public TextView getTextCityStateZipCode() {
            return textCityStateZipCode;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }
    }

    public static void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_item_view, parent, false);
        return new ContactViewHolder(v);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ContactViewHolder cvh = (ContactViewHolder) holder;

        String currentAddress = contactData.get(position).getStreetAddress();
        String currentCityStateZipCode = contactData.get(position).getCity() +
                ", " + contactData.get(position).getState() +
                ", " + contactData.get(position).getZipCode();

        cvh.getContactTextView().setText(contactData.get(position).getContactName());
        if (ifPositionEven(position)) {
            cvh.getContactTextView().setTextColor(Color.RED);
        } else {
            cvh.getContactTextView().setTextColor(Color.BLUE);
        }
        cvh.getPhoneTextView().setText(contactData.get(position).getPhoneNumber());
        cvh.getTextAddress().setText(currentAddress);
        cvh.getTextCityStateZipCode().setText(currentCityStateZipCode);

        if (isDeleting) {
            cvh.getDeleteButton().setVisibility(View.VISIBLE);
            cvh.getDeleteButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(position);
                }
            });
        }
        else {
            cvh.getDeleteButton().setVisibility(View.INVISIBLE);
        }
    }
    public void setDelete(boolean b) {
        isDeleting = b;
    }

    private void deleteItem(int position) {
        Contact contact = contactData.get(position);
        ContactDataSource ds = new ContactDataSource(parentContext);
        try {
            ds.open();
            boolean didDelete = ds.deleteContact(contact.getContactID());
            ds.close();
            if (didDelete) {
                contactData.remove(position);
                notifyDataSetChanged();
            }
            else {
                Toast.makeText(parentContext, "Delete Failed!", Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean ifPositionEven(int position) {
        return (position % 2 == 0);
    }


    @Override
    public int getItemCount() {
        return contactData.size();
    }
}
