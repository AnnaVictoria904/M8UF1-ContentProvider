package com.vscribelka.contentprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ContacteDetalls extends AppCompatActivity {
    ImageView img;
    TextView contactName, contactPhone, contactEmail;
    Button editContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacte_detalls);

        img = findViewById(R.id.contactPhoto);
        contactName = findViewById(R.id.nomContacte);
        contactPhone = findViewById(R.id.telefonContacte);
        contactEmail = findViewById(R.id.emailContacte);
        editContact = findViewById(R.id.editarContacte);

        String contactId = getIntent().getStringExtra("idContacte");

        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER,
                ContactsContract.Contacts.PHOTO_URI
        };

        Cursor c = getContentResolver().query
                (
                        ContactsContract.Contacts.CONTENT_URI,
                        projection,
                        ContactsContract.Contacts._ID + "= '" + contactId + "'",
                        null,
                        null
                );

        if (c != null && c.moveToFirst()) {
            @SuppressLint("Range") String nom = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            @SuppressLint("Range") String telefon = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            @SuppressLint("Range") String foto = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));

            contactName.setText(nom);

            if (foto != null && !foto.isEmpty()) {
                Picasso.get().load(foto).into(img);
            }

            if (Integer.parseInt(telefon) > 0) {
                Cursor telefons = getContentResolver().query
                        (
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                                null,
                                null
                        );

                if (telefons != null && telefons.moveToFirst()) {
                    @SuppressLint("Range") String mobil = telefons.getString(telefons.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contactPhone.setText(mobil);
                }
                telefons.close();
            }

            Cursor emails = getContentResolver().query
                    (
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                            null,
                            null
                    );

            if (emails != null && emails.moveToFirst()) {
                @SuppressLint("Range") String email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                contactEmail.setText(email);
            }

            emails.close();
        }
        c.close();

        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContacteDetalls.this, EditarContacte.class);
                i.putExtra("idContacte", contactId);
                i.putExtra("nomContacte", contactName.getText());
                i.putExtra("mobilContacte", contactPhone.getText());
                i.putExtra("emailContacte", contactEmail.getText());
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ContacteDetalls.this, MainActivity.class));
        finish();
    }
}