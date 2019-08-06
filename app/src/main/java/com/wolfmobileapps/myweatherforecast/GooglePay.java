package com.wolfmobileapps.myweatherforecast;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Optional;

public class GooglePay {
    private static final String TAG = "GooglePay";

    /**
     * Create a Google Pay API base request object with properties used in all requests
     *
     * @return Google Pay API base request object
     * @throws JSONException
     */
    private static JSONObject getBaseRequest() throws JSONException {
        return new JSONObject()
                .put("apiVersion", 2)
                .put("apiVersionMinor", 0);
    }

    /**
     * TODO: check with your gateway on the parameters to pass
     */
    private static JSONObject getTokenizationSpecification() throws JSONException {
        JSONObject tokenizationSpecification = new JSONObject();
        tokenizationSpecification.put("type", "PAYMENT_GATEWAY");
        tokenizationSpecification.put(
                "parameters",
                new JSONObject()
                        .put("gateway", "example")
                        .put("gatewayMerchantId", "17171587414917648235"));

        return tokenizationSpecification;
    }

    /**
     * Card networks supported by your app and your gateway
     *
     * <p>TODO: confirm card networks supported by your app and gateway
     *
     * @return allowed card networks
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
     */
    private static JSONArray getAllowedCardNetworks() {
        return new JSONArray()
                .put("AMEX")
                .put("DISCOVER")
                .put("JCB")
                .put("MASTERCARD")
                .put("VISA");
    }

    /**
     * Card authentication methods supported by your app and your gateway
     *
     * <p>TODO: confirm your processor supports Android device tokens on your supported card networks
     *
     * @return allowed card authentication methods
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#CardParameters">CardParameters</a>
     */
    private static JSONArray getAllowedCardAuthMethods() {
        return new JSONArray()
                .put("PAN_ONLY")
                .put("CRYPTOGRAM_3DS");
    }

    /**
     * Describe your app's support for the CARD payment method
     *
     * <p>The provided properties are applicable to both an IsReadyToPayRequest and a
     * PaymentDataRequest
     *
     * @return a CARD PaymentMethod object describing accepted cards
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getBaseCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = new JSONObject();
        cardPaymentMethod.put("type", "CARD");
        cardPaymentMethod.put(
                "parameters",
                new JSONObject()
                        .put("allowedAuthMethods", GooglePay.getAllowedCardAuthMethods())
                        .put("allowedCardNetworks", GooglePay.getAllowedCardNetworks()));

        return cardPaymentMethod;
    }

    /**
     * Describe the expected returned payment data for the CARD payment method
     *
     * @return a CARD PaymentMethod describing accepted cards and optional fields
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentMethod">PaymentMethod</a>
     */
    private static JSONObject getCardPaymentMethod() throws JSONException {
        JSONObject cardPaymentMethod = GooglePay.getBaseCardPaymentMethod();
        cardPaymentMethod.put("tokenizationSpecification", GooglePay.getTokenizationSpecification());

        return cardPaymentMethod;
    }

    /**
     * Provide Google Pay API with a payment amount, currency, and amount status
     *
     * @return information about the requested payment
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#TransactionInfo">TransactionInfo</a>
     */
    private static JSONObject getTransactionInfo() throws JSONException {
        JSONObject transactionInfo = new JSONObject();
        transactionInfo.put("totalPrice", "1");
        transactionInfo.put("totalPriceStatus", "FINAL");
        transactionInfo.put("currencyCode", "USD");

        return transactionInfo;
    }

    /**
     * Information about the merchant requesting payment information
     *
     * @return information about the merchant
     * @throws JSONException
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#MerchantInfo">MerchantInfo</a>
     */
    private static JSONObject getMerchantInfo() throws JSONException {
        return new JSONObject()
                .put("merchantName", "Wolf Mobile Apps - Forecast Weather");
    }

    /**
     * An object describing accepted forms of payment by your app, used to determine a viewer's
     * readiness to pay
     *
     * @return API version and payment methods supported by the app
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#IsReadyToPayRequest">IsReadyToPayRequest</a>
     */
    public static JSONObject getIsReadyToPayRequest() {
        try {
            JSONObject isReadyToPayRequest = GooglePay.getBaseRequest();
            isReadyToPayRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(getBaseCardPaymentMethod()));
            return isReadyToPayRequest;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * An object describing information requested in a Google Pay payment sheet
     *
     * @return payment data expected by your app
     * @see <a
     * href="https://developers.google.com/pay/api/android/reference/object#PaymentDataRequest">PaymentDataRequest</a>
     */
    public static JSONObject getPaymentDataRequest() {
        try {
            JSONObject paymentDataRequest = GooglePay.getBaseRequest();
            paymentDataRequest.put(
                    "allowedPaymentMethods", new JSONArray().put(GooglePay.getCardPaymentMethod()));
            paymentDataRequest.put("transactionInfo", GooglePay.getTransactionInfo());
            paymentDataRequest.put("merchantInfo", GooglePay.getMerchantInfo());
            return paymentDataRequest;
        } catch (JSONException e) {
            return null;
        }
    }
}