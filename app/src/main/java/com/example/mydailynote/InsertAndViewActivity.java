package com.example.mydailynote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class InsertAndViewActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_STORAGE = 100;
    int eventId = 0;
    EditText fillFileName, fillCatatan;
    Button btnSimpan;
    String filename = "";
    String tempCatatan = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_view);

        ActionBar actionBar = getSupportActionBar();

        fillFileName = findViewById(R.id.namaFile);
        fillCatatan = findViewById(R.id.isiCatatan);
        btnSimpan = findViewById(R.id.buttonSimpan);

        btnSimpan.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        
        if (extras != null) {
            filename = extras.getString("filename");
            fillFileName.setText(filename);
            actionBar.setTitle("Ubah Catatan");
        } else {
            actionBar.setTitle("Tambah Catatan");
        }

        eventId = 1;

        if (Build.VERSION.SDK_INT >= 23) {
            readFile();
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonSimpan) {
            eventId = 2;
            if (!tempCatatan.equals(fillCatatan.getText().toString())) {
                if (Build.VERSION.SDK_INT >= 23) {
                    showDialogConfirmStorage();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (eventId == 1) {
                        readFile();
                    } else {
                        showDialogConfirmStorage();
                    }
                }
            break;
        }
    }

    private void readFile() {
        String path = getFilesDir().toString() + "/kominfo.proyek1";
        File file = new File(path, fillFileName.getText().toString());

        if (file.exists()) {
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String line = bufferedReader.readLine();

                while (line != null) {
                    text.append(line);
                    line = bufferedReader.readLine();
                }

                bufferedReader.close();
            } catch (IOException ex) {
                System.out.println(ex);
            }

            tempCatatan = text.toString();
            fillCatatan.setText(text.toString());
        }
    }

    private void createdAndEdited() {
        String path = getFilesDir().toString() + "/kominfo.proyek1";
        File parent = new File(path);

        if (parent.exists()) {
            File file = new File(path, fillFileName.getText().toString());
            FileOutputStream outputStream = null;

            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                streamWriter.append(fillCatatan.getText());
                streamWriter.flush();
                streamWriter.close();
                outputStream.flush();
                outputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            parent.mkdir();

            File file = new File(path, fillFileName.getText().toString());
            FileOutputStream outputStream = null;

            try {
                file.createNewFile();
                outputStream = new FileOutputStream(file, false);
                outputStream.write(fillCatatan.getText().toString().getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        onBackPressed();
    }

    private void showDialogConfirmStorage() {
        new AlertDialog.Builder(this)
                .setTitle("Simpan Catatan ?")
                .setMessage("Apakah Anda Yakin Menyimpan Catatan " + filename + " ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        createdAndEdited();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public void onBackPressed() {
        if (!tempCatatan.equals(fillCatatan.getText().toString())) {
            showDialogConfirmStorage();
        }
        super.onBackPressed();
    }
}