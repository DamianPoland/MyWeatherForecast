package com.wolfmobileapps.myweatherforecast;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {
    private static final String TAG = "InfoActivity";

    TextView textViewVersionName;
    private LinearLayout version;
    private LinearLayout faq;
    private LinearLayout source;
    private LinearLayout privacy;
    private LinearLayout info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        version = findViewById(R.id.version);
        faq = findViewById(R.id.faq);
        source = findViewById(R.id.source);
        privacy = findViewById(R.id.privacy);
        info = findViewById(R.id.info);

        //ustawienie górnej nazwy i strzałki do powrotu
        getSupportActionBar().setTitle(getResources().getString(R.string.info)); //ustawia nazwę na górze
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ustawia strzałkę

        //ustavienie zazwy wersji z gradle
        textViewVersionName = findViewById(R.id.textViewVersionName);
        textViewVersionName.setText(BuildConfig.VERSION_NAME);


        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = getResources().getString(R.string.version);
                String alertString = BuildConfig.VERSION_NAME;
                createAlertDialog(titule, alertString);
            }
        });

        faq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = getResources().getString(R.string.description);
                String alertString = getResources().getString(R.string.app_description);
                createAlertDialog(titule, alertString);
            }
        });

        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = getResources().getString(R.string.open_source_licenses);
                String alertString = getResources().getString(R.string.sourceDescription);
                createAlertDialog(titule, alertString);
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = getResources().getString(R.string.privacy_policy);
                String alertString = getResources().getString(R.string.privacy_policy_description);
                createAlertDialog(titule, alertString);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titule = getResources().getString(R.string.infoApp);
                String alertString = getResources().getString(R.string.infoAppDescription);
                createAlertDialog(titule, alertString);
            }
        });
    }

    // tworzy alert dialog z podanego stringa tutułu i opisu
    private void createAlertDialog (String titule, String alertString){

        AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
        builder.setTitle(titule);
        if (titule.equals(getResources().getString(R.string.infoApp))){
            builder.setIcon(R.drawable.wolf_icon);
        }
        builder.setMessage(alertString);
        builder.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //do something when click OK
            }
        }).create();
        builder.show();
    }
}
