package com.eshop.tubonge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView RegisterButton;
    private EditText InputUser,InputPassword;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        linearLayout = (LinearLayout) findViewById(R.id.login_layout);
        InputUser = (EditText) findViewById(R.id.input_user);
        InputPassword = (EditText) findViewById(R.id.input_password);
        RegisterButton = (TextView) findViewById(R.id.btn_register);

        Firebase.setAndroidContext(this);

        Snackbar snackbar = Snackbar.make(linearLayout,"ALREADY A MEMBER ?",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("LOGIN", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
                finish();
            }
        });
        snackbar.show();

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = InputUser.getText().toString().trim();
                password = InputPassword.getText().toString();

                if (TextUtils.isEmpty(username)){
                    InputUser.setError("Field Required");
                }

                if (TextUtils.isEmpty(password)){
                    InputPassword.setError("Field Required");
                }

                if(!username.matches("[A-Za-z0-9]+")){
                    InputUser.setError("Only alphabet or numbers allowed");
                }
                if(username.length()<5){
                    InputUser.setError("Username should be at least 5 characters long");
                }
                if(password.length()<5){
                    InputPassword.setError("Password should be at least 5 characters long");
                } else {

                        final ProgressDialog pd = new ProgressDialog(Register.this);
                        pd.setMessage("Loading...");
                        pd.show();

                        String url = "https://tubonge-84f9a.firebaseio.com/users.json";

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
                            @Override
                            public void onResponse(String s) {
                                Firebase reference = new Firebase("https://tubonge-84f9a.firebaseio.com/users");

                                if(s.equals("0")) {
                                    reference.child(username).child("password").setValue(password);
                                    Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(Register.this, Login.class));
                                    finish();
                                }
                                else {
                                    try {
                                        JSONObject obj = new JSONObject(s);

                                        if (!obj.has(username)) {
                                            reference.child(username).child("password").setValue(password);
                                            Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_LONG).show();

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    onBackPressed();
                                                }
                                            }, 500);
                                        } else {
                                            Toast.makeText(Register.this, "Username already exists. Try another one", Toast.LENGTH_LONG).show();
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                pd.dismiss();
                            }

                        },new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                System.out.println("" + volleyError );
                                pd.dismiss();
                            }
                        });

                        RequestQueue rQueue = Volley.newRequestQueue(Register.this);
                        rQueue.add(request);
                    }
                }

            });
        }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(Register.this, Login.class));
        finish();
    }
}
