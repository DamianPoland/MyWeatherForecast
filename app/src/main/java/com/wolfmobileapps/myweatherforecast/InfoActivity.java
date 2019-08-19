package com.wolfmobileapps.myweatherforecast;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.wallet.PaymentsClient;

import static com.wolfmobileapps.myweatherforecast.MainActivity.SHARED_PREFERENCES_NAME;


public class InfoActivity extends AppCompatActivity {
    private static final String TAG = "InfoActivity";

    private TextView textViewVersionName;
    private LinearLayout version;
    private LinearLayout faq;
    private LinearLayout source;
    private LinearLayout privacy;
    private LinearLayout info;
    private LinearLayout donations;

    private SharedPreferences shar;
    private SharedPreferences.Editor editor;

    // do płatności Goog;ePay
    private PaymentsClient mPaymentsClient;
    private View mGooglePayButton;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        version = findViewById(R.id.version);
        faq = findViewById(R.id.faq);
        source = findViewById(R.id.source);
        privacy = findViewById(R.id.privacy);
        info = findViewById(R.id.info);
        donations = findViewById(R.id.donations);

        //ustawienie górnej nazwy i strzałki do powrotu
        getSupportActionBar().setTitle(getResources().getString(R.string.info)); //ustawia nazwę na górze
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ustawia strzałkę

        //ustavienie zazwy wersji z gradle
        textViewVersionName = findViewById(R.id.textViewVersionName);
        textViewVersionName.setText(BuildConfig.VERSION_NAME);

        //tworzenie shared preferences
        shar = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);

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
        donations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        // do płatności GooglePay
//        mPaymentsClient =
//                Wallet.getPaymentsClient(
//                        this,
//                        new Wallet.WalletOptions.Builder()
//                                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
//                                .build());
//        possiblyShowGooglePayButton();
    }



    // tworzy alert dialog z podanego stringa tutułu i opisu
    private void createAlertDialog(String titule, String alertString) {

        AlertDialog.Builder builder = new AlertDialog.Builder(InfoActivity.this);
        builder.setTitle(titule);
        if (titule.equals(getResources().getString(R.string.infoApp))) {
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


    /*
    TODO
        1. trzeba wszystko co niżej i w onCreate co dotyczy płatności GooglePay odkomentować
        2. usunąć z XMLa z Linear Layout z id: donations wiersz z visibility: invisible
     */

    // wszystko co niżej to do płatności google pay

//    private void possiblyShowGooglePayButton() {
//        if (GooglePay.getIsReadyToPayRequest() == null) {
//            return;
//        }
//        JSONObject isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
//        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString());
//        if (request == null) {
//            return;
//        }
//        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
//        task.addOnCompleteListener(
//                new OnCompleteListener<Boolean>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Boolean> task) {
//                        try {
//                            boolean result = task.getResult(ApiException.class);
//                            if (result) {
//                                // show Google as a payment option
//                                mGooglePayButton = findViewById(R.id.googlepay);
//                                mGooglePayButton.setOnClickListener(
//                                        new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                requestPayment(view);
//                                            }
//                                        });
//                                mGooglePayButton.setVisibility(View.VISIBLE);
//                            }
//                        } catch (ApiException exception) {
//                            // handle developer errors
//                        }
//                    }
//                });
//    }
//
//    public void requestPayment(View view) {
//        if (GooglePay.getPaymentDataRequest() == null) {
//            return;
//        }
//        JSONObject paymentDataRequestJson = GooglePay.getPaymentDataRequest();
//        PaymentDataRequest request =
//                PaymentDataRequest.fromJson(paymentDataRequestJson.toString());
//        if (request != null) {
//            AutoResolveHelper.resolveTask(
//                    mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            // value passed in AutoResolveHelper
//            case LOAD_PAYMENT_DATA_REQUEST_CODE:
//                switch (resultCode) {
//                    case Activity.RESULT_OK:
//                        PaymentData paymentData = PaymentData.getFromIntent(data);
//                        String json = paymentData.toJson();
//                        // if using gateway tokenization, pass this token without modification
//                        JSONObject paymentMethodData = null;
//                        try {
//                            paymentMethodData = new JSONObject(json).getJSONObject("paymentMethodData");
//                            // Alert dialog po to że jeśli - If the gateway is set to "example", no payment information is returned - instead, the token will only consist of "examplePaymentMethodToken".
//                            if (paymentMethodData
//                                    .getJSONObject("tokenizationData")
//                                    .getString("type")
//                                    .equals("PAYMENT_GATEWAY")
//                                    && paymentMethodData
//                                    .getJSONObject("tokenizationData")
//                                    .getString("token")
//                                    .equals("examplePaymentMethodToken")) {
//                                AlertDialog alertDialog =
//                                        new AlertDialog.Builder(this)
//                                                .setTitle("Warning")
//                                                .setMessage("Gateway name set to \"example\" - please modify " + "Constants.java and replace it with your own gateway.")
//                                                .setPositiveButton("OK", null)
//                                                .create();
//                                alertDialog.show();
//                            }
//
//                            //log do tokena
//                            Log.d(TAG, "onActivityResult: paymentToken: " + paymentMethodData.getJSONObject("tokenizationData").getString("token"));
//
//                            //gdy się powiedzie opłata
//                            String billingName =
//                                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
//                            Log.d("BillingName", billingName);
//                            Toast.makeText(this, this.getResources().getString(R.string.successfully_received_payment), Toast.LENGTH_LONG)
//                                    .show();
//
//                            //gdy jest zapłacone i wszystko przejdzie pomyślnie to ustawi w shar na tru - inaczej jest false
//                            editor = shar.edit();//wywołany edytor do zmian
//                            editor.putBoolean(KEY_FOR_SHARED_PREF_GOOGLE_PAY, true);
//                            editor.apply();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        // Nothing to here normally - the user simply cancelled without selecting a payment method.
//                        break;
//                    case AutoResolveHelper.RESULT_ERROR:
//                        Status status = AutoResolveHelper.getStatusFromIntent(data);
//                        Log.d(TAG, "onActivityResult: RESULT_ERROR: " + status.getStatusMessage());
//                        Toast.makeText(this, "Unfortunately, Google Pay is not available on this phone.", Toast.LENGTH_SHORT).show();
//                        break;
//                    default:
//                        // Do nothing.
//                }
//                break;
//            default:
//                // Do nothing.
//        }
//    }

}
