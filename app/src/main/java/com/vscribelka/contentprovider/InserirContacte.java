package com.vscribelka.contentprovider;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

public class InserirContacte extends AppCompatActivity {
    ImageButton insertImg;
    EditText insertName, insertPhone, insertEmail;
    Button save, cancel;

    /* No funciona...

    Uri fotoUri;

    private final ActivityResultLauncher<Intent> mGetContent =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        // Obtenir la URI de la imatge seleccionada
                        if (result.getData() != null) {
                            fotoUri = result.getData().getData();
                            // Mostre la imatge seleccionada
                            insertImg.setImageURI(fotoUri);
                        }
                    }
                }
            });

     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inserir_contacte);

        insertImg = findViewById(R.id.inserirFoto);
        insertName = findViewById(R.id.inserirNom);
        insertPhone = findViewById(R.id.inserirTelefon);
        insertEmail = findViewById(R.id.inserirEmail);
        save = findViewById(R.id.guardarContacte);
        cancel = findViewById(R.id.cancelar);

        // Acció del botó "Guardar Contacte"
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = insertName.getText().toString();
                String phone = insertPhone.getText().toString();
                String email = insertEmail.getText().toString();

                // Crear una nova operació d'inserció de contacte
                ArrayList<ContentProviderOperation> ops = new ArrayList<>();

                // Inserir l'operació d'inserció de nom
                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                // Inserir l'operació d'inserció del nom del contacte
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                        .build());

                // Inserir l'operació d'inserció del número de telèfon
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone)
                        .build());

                // Inserir l'operació d'inserció de l'adreça de correu electrònic
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
                        .build());

                /* Tampoc funciona...

                // Afegir l'operació d'inserció de la foto
                if (fotoUri != null) {
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Photo.PHOTO, fotoUri.toString())
                            .build());
                }

                 */

                try {
                    // Executar les operacions en una sola transacció
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    Toast.makeText(InserirContacte.this, "Contacte guardat amb èxit", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(InserirContacte.this, "Error en guardar el contacte", Toast.LENGTH_SHORT).show();
                }

                // Tornar a l'activitat anterior o realitzar altres accions necessàries
                startActivity(new Intent(InserirContacte.this, MainActivity.class));
                finish();
            }
        });

        // Acció del botó "Cancelar"
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tornar a l'activitat anterior o realitzar altres accions necessàries
                finish();
            }
        });

        /* Inútil, ja que no desa la foto insertada per l'usuari

        // Acció de l'ImageButton per seleccionar la foto
        insertImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mGetContent.launch(intent);
            }
        });

         */
    }
}