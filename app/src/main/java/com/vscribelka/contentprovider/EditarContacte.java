package com.vscribelka.contentprovider;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class EditarContacte extends AppCompatActivity {
    ImageButton editImg;
    EditText editName, editPhone, editEmail;
    Button save;
    // Uri fotoUri;
    String contactId;
    private ActivityResultLauncher<String> mGetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_contacte);

        editName = findViewById(R.id.editarNom);
        editPhone = findViewById(R.id.editarTelefon);
        editEmail = findViewById(R.id.editarEmail);
        save = findViewById(R.id.guardarCanvis);
        editImg = findViewById(R.id.editarFoto);

        editName.setText(getIntent().getStringExtra("nomContacte"));
        editPhone.setText(getIntent().getStringExtra("mobilContacte"));
        editEmail.setText(getIntent().getStringExtra("emailContacte"));
        contactId = getIntent().getStringExtra("idContacte");

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
            @SuppressLint("Range") String foto = c.getString(c.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
            if (foto != null && !foto.isEmpty()) {
                Picasso.get().load(foto).into(editImg);
            }
        }

        c.close();

        /*

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
                imageUri -> {
                    if (imageUri != null) {
                        fotoUri = imageUri;
                        getIMGSize(imageUri);
                        editImg.setForeground(null);
                        editImg.setImageURI(imageUri);
                    }
                });
        editImg.setOnClickListener(v -> mGetContent.launch("image/*"));

         */

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString();
                String newPhone = editPhone.getText().toString();
                String newEmail = editEmail.getText().toString();
                // String newPhoto = editImg.toString();

                // Crear una nova operació de l'actualització del nom
                ContentProviderOperation updateName = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, newName)
                        .build();

                // Crear una nova operació de l'actualització del número de telèfon
                ContentProviderOperation updatePhone = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{contactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhone)
                        .build();

                // Crear una nova operació de l'actualització de l'adreça de correu electrònic
                ContentProviderOperation updateEmail = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{contactId, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE})
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, newEmail)
                        .build();

                /* No deixa editar la foto...

                ContentProviderOperation updatePhoto = ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                        .withSelection(ContactsContract.Data.CONTACT_ID + "=? AND " + ContactsContract.Data.MIMETYPE + "=?",
                                new String[]{contactId, ContactsContract.CommonDataKinds.Photo.PHOTO_URI})
                        .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO_URI, newPhoto)
                        .build();

                 */

                // Afegir les operacions a la llista
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();
                ops.add(updateName);
                ops.add(updatePhone);
                ops.add(updateEmail);
                // ops.add(updatePhoto);

                try {
                    // Executar les operacions en una sola transacció
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(EditarContacte.this, "S'ha desat les dades!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(EditarContacte.this, "Error en desar les dades", Toast.LENGTH_SHORT).show();
                }

                // Torna a l'activitat anterior o realitza altres accions necessàries
                startActivity(new Intent(EditarContacte.this, MainActivity.class));
                finish();
            }
        });

    }

    /*

    private void getIMGSize(Uri uri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(new File(uri.getPath()).getAbsolutePath(), options);
    }

     */
}