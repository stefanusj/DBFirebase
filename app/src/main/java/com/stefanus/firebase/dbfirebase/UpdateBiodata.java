package com.stefanus.firebase.dbfirebase;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class UpdateBiodata extends AppCompatActivity {

    private Calendar calendar;
    private String selected;

    private EditText etNama, etTtl, etAlamat;
    private RadioButton rbPria, rbWanita;

    private DatabaseReference root;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biodata);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        calendar = Calendar.getInstance();

        selected = getIntent().getStringExtra(BiodataModel.TAG_NAMA);

        firebaseDatabase = FirebaseDatabase.getInstance();
        root = firebaseDatabase.getReference().getRoot().child(selected);

        etNama = (EditText) findViewById(R.id.etNama);
        etTtl = (EditText) findViewById(R.id.etTtl);
        rbPria = (RadioButton) findViewById(R.id.rbPria);
        rbWanita = (RadioButton) findViewById(R.id.rbWanita);
        etAlamat = (EditText) findViewById(R.id.etAlamat);

        root.addValueEventListener(valueData);

        etTtl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(UpdateBiodata.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_biodata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.actionSave:
                simpanData();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setData(DataSnapshot dataSnapshot) {
        Iterator i = dataSnapshot.getChildren().iterator();
        while (i.hasNext()) {
            String alamat = String.valueOf(((DataSnapshot) i.next()).getValue());
            String jk = String.valueOf(((DataSnapshot) i.next()).getValue());
            String nama = String.valueOf(((DataSnapshot) i.next()).getValue());
            String ttl = String.valueOf(((DataSnapshot) i.next()).getValue());
            etNama.setText(nama);
            etTtl.setText(ttl);
            if (jk.equals("Laki-laki")) rbPria.setChecked(true);
            else if (jk.equals("Perempuan")) rbWanita.setChecked(true);
            etAlamat.setText(alamat);
        }
    }

    private void simpanData() {
        String nama = etNama.getText().toString();
        String ttl = etTtl.getText().toString();
        String jk = "";
        if (rbPria.isChecked()) jk = "Laki-laki";
        else if (rbWanita.isChecked()) jk = "Perempuan";
        String alamat = etAlamat.getText().toString();
        if (nama.equals("") || ttl.equals("") || jk.equals("") || alamat.equals(""))
            Toast.makeText(UpdateBiodata.this, R.string.prompt_3, Toast.LENGTH_SHORT).show();
        else {

            BiodataModel model = new BiodataModel();
            model.setNama(nama)
                    .setTtl(ttl)
                    .setJk(jk)
                    .setAlamat(alamat);

            root.removeValue();
            firebaseDatabase.getReference().child(nama).setValue(model);

            finish();
        }
    }

    private void updateLabel() {
        String myFormat = "dd/MMM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etTtl.setText(sdf.format(calendar.getTime()));
    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }
    };

    private ValueEventListener valueData = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setData(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("Database Error", databaseError.toString());
        }
    };
}
