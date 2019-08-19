package com.wolfmobileapps.myweatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "StartActivity";

    //do reklam
    private InterstitialAd mInterstitialAd;
    public static final String advertisementIntedidtialID = "ca-app-pub-1490567689734833/3701270854"; //id reklamy na cały ekran
    private boolean shouldLoadAds; // żeby reklamy nie pokazywały się po wyłaczeniu aplikacji - tylko do intestitialAds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // do pokazania reklamy na cały ekran ( ładowanie reklam na cały ekran też jest dodane w przyciskach menu)
        MobileAds.initialize(this); //inicjalizacja reklam potrzebna tylko raz
        mInterstitialAd = new InterstitialAd(this); // instancja do danej reklamy
        mInterstitialAd.setAdUnitId(advertisementIntedidtialID); //wpisać iD danej reklamy czyli identyfikator jednostki reklamowej wzięty zz AdMOB
        mInterstitialAd.loadAd(new AdRequest.Builder().build()); // ładuje reklamę to chwile potrwa więc odrazu może nie pokazać bo nie bęszie załadowana
        mInterstitialAd.setAdListener(new AdListener() {// dodaje listenera do pokazywania reklam jak np się załaduje reklama i mozna ustawić też inne rzeczy że się wyświetla ale są bez sensu
            @Override
            public void onAdLoaded() {
                if (shouldLoadAds) { // żeby reklamy nie pokazywały się po wyłaczeniu aplikacji - tylko do intestitialAds
                    mInterstitialAd.show(); //pokazuje reklamę
                }
                startActivity(new Intent(StartActivity.this, MainActivity.class)); //ładuje activity jeśli załaduje reklamę
            }
            @Override
            public void onAdLeftApplication() { //jeśli error to 3 to nie ma zasobów reklamowych
                shouldLoadAds = false; // żeby reklamy nie pokazywały się po wyłaczeniu aplikacji - tylko do intestitialAds
            }

            @Override
            public void onAdFailedToLoad(int errorCode) { //jeśli error to 3 to nie ma zasobów reklamowych
                Log.d(TAG, "onAdFailedToLoad reklama na cały ekran: __________ errorCode: " + errorCode);
                startActivity(new Intent(StartActivity.this, MainActivity.class)); //ładuje activity jeśli nie ma reklamy

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        shouldLoadAds = true; // żeby reklamy nie pokazywały się po wyłaczeniu aplikacji - tylko do intestitialAds
    }
    @Override
    protected void onPause() {
        shouldLoadAds = false; // żeby reklamy nie pokazywały się po wyłaczeniu aplikacji - tylko do intestitialAds
        super.onPause();
    }
}
