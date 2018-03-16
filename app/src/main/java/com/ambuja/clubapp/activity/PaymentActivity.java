package com.ambuja.clubapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.utils.Constants;


public class PaymentActivity extends BaseActivity {

    private String paymentUrl;
    private WebView paymentWV;
    private TextView exitPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


    }

    @Override
    protected void onResume() {
        super.onResume();


        initUI();

        Constants.userId = store.getString("userId");

       /* Constants.userId = store.getString("userId");

        Constants.clubId = store.getString("clubId");

        if (Constants.clubId != null && !Constants.clubId.equalsIgnoreCase("")) {

            initUI();

        } else {

            Intent intent = new Intent(this, ClubListActivity.class);
            startActivity(intent);

        }
*/

    }

    private void initUI() {

        paymentUrl = "http://neotiahospitality.com/api/payment/index.php?user_id=" + Constants.userId;

        log(paymentUrl);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.payments));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        paymentWV = (WebView) findViewById(R.id.paymentWV);
        paymentWV.getSettings().setJavaScriptEnabled(true);
        paymentWV.getSettings().setDomStorageEnabled(true);
        paymentWV.getSettings().setAppCacheEnabled(true);
        paymentWV.getSettings().setDatabaseEnabled(true);
        paymentWV.setWebViewClient(new MyWebViewClient());
        paymentWV.loadUrl(paymentUrl);

        exitPayment = (TextView) findViewById(R.id.exitPayment);

        exitPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }


    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public class MyWebViewClient extends WebViewClient {

        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub

            view.loadUrl(url);

            return true;

        }

    }

    private class MyWebViewClientChrome extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            // loaderlayout.setVisibility(View.GONE);

            // progressBar1.setVisibility(View.VISIBLE);

            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            //  loaderlayout.setVisibility(View.VISIBLE);
//
            super.onPageStarted(view, url, favicon);
        }

    }


    public boolean canGoBack() {
        return paymentWV.canGoBack();
    }

    public void goBack() {
        paymentWV.goBack();
    }

    @Override
    public void onBackPressed() {
        onPressingBack();
    }

    /* On Pressing Back  Giving Alert...
     **/
    private void onPressingBack() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("Warning");

        // Setting Dialog Message
        alertDialog.setMessage("Do you cancel this transaction?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                //startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }
}
