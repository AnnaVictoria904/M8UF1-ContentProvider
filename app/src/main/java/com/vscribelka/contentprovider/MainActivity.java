package com.vscribelka.contentprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final static int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    ListView listView;
    ArrayList<String> arrayList;
    ArrayList<String> contacteId;
    ArrayAdapter<String> arrayAdapter;
    Button inserirContacte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        inserirContacte = findViewById(R.id.inserirContacte);
        arrayList = new ArrayList<String>();
        contacteId = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_list_adapter, R.id.contactName, arrayList);
        listView.setAdapter(arrayAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS
            }, PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contacteID = contacteId.get(position);
                Intent i = new Intent(MainActivity.this, ContacteDetalls.class);
                i.putExtra("idContacte", contacteID);
                startActivity(i);
                finish();
            }
        });

        inserirContacte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, InserirContacte.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenirContactes();
            } else {
                Toast.makeText(this, "Permís denegat!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void obtenirContactes() {
        //Les columnes que volem obtenir per a cada element.
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };

        //Condició: volem obtenir totes les files (per això és null).
        String where = null;
        String[] whereArgs = null;

        //Ordre: que estiguin ordenats de forma ascendent.
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor c = getContentResolver().query
                (
                        ContactsContract.Contacts.CONTENT_URI,
                        projection, // Columnes per obtenir de cada fila
                        where, // Criteri de selecció
                        whereArgs, // Criteri de selecció
                        sortOrder // Ordre
                );

        while (c.moveToNext()) {
            @SuppressLint("Range") String nomContacte = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            @SuppressLint("Range") String idContacte = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            contacteId.add(idContacte);
            arrayList.add(nomContacte);
        }
        arrayAdapter.notifyDataSetChanged();
        c.close();
    }

    @Override
    public void onBackPressed() {
        // Desactiva el botó d'anar enrere
    }
}