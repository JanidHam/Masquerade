package com.digma.masquerade.digma.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.digma.masquerade.digma.R;
import com.digma.masquerade.digma.util.PrefManager;

import java.util.Calendar;

public class TermsConditionActivity extends AppCompatActivity {

    public final String ACCEPT_TERMS = "accept_terms";

    Spinner spGenero;

    private Button btnNacimiento;

    private int year;
    private int month;
    private int day;

    static final int DATE_DIALOG_ID = 999;

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_condition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.title_activity_terms_condition);

        prefManager = new PrefManager(this);

        if (prefManager.getAcceptedTerms()) {
            this.startMainActivity();
        }


        //spGenero = (Spinner) findViewById(R.id.sp_genero);
        Button btnAcept = (Button) findViewById(R.id.btn_accept_terms);
        Button btnDeny = (Button) findViewById(R.id.btn_deny_terms);

        btnAcept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptTerms();
            }
        });
        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.generos_array, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spGenero.setAdapter(adapter);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        addListenerOnButton();
        */
    }

    /*
    public void addListenerOnButton() {

        btnNacimiento = (Button) findViewById(R.id.btn_nacimiento);

        btnNacimiento.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                showDialog(DATE_DIALOG_ID);

            }

        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

        }
    };

    private boolean acceptedTerms() {
        SharedPreferences settings = getPreferences(0);
        return settings.getBoolean(ACCEPT_TERMS, false);
    }
    */
    private boolean isValid() {
        CheckBox chkTerms = (CheckBox) findViewById(R.id.chk_accept);

        if (!chkTerms.isChecked()) {
            Toast toast = Toast.makeText(this, "Debe aceptar los terminos y condiciones.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }

        //spGenero.getSelectedItemPosition();

        return true;
    }

    private void acceptTerms() {
        if (!isValid()) return;

        prefManager.setAcceptTerms(true);

        this.startMainActivity();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, SocialActivity.class);
        startActivity(intent);
        finish();
    }
}
