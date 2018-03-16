package com.ambuja.clubapp.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ambuja.clubapp.R;

import static android.view.Gravity.CENTER;

public class AskLoginActvity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_login_actvity);
        InitUI();

    }

    private void InitUI() {


        openDialog();

    }


    private void openDialog() {

        final Dialog dialog;
        dialog = new Dialog(AskLoginActvity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                exitFromApp();
            }
        });

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(CENTER);

        ImageView loginButtonIV, cancelIV, registerButtonIV;

        dialog.setContentView(R.layout.dialog_ask_login);


        loginButtonIV = (ImageView) dialog.findViewById(R.id.loginButtonIV);
        cancelIV = (ImageView) dialog.findViewById(R.id.cancelIV);
        registerButtonIV = (ImageView) dialog.findViewById(R.id.registerButtonIV);

        loginButtonIV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AskLoginActvity.this, SignInActivity.class);
                startActivity(intent);


            }
        });

        cancelIV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                exitFromApp();
                dialog.cancel();


            }
        });

        registerButtonIV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                Intent intent = new Intent(AskLoginActvity.this, SignUpActivity.class);
                startActivity(intent);


            }
        });
        dialog.show();


    }

}
