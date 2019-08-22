package com.eshop.tubonge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class Login extends AppCompatActivity {
    private LinearLayout linearLayout;
    private TextView LoginButton;
    private EditText InputUser,InputPassword;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        linearLayout = (LinearLayout) findViewById(R.id.login_layout);
        InputUser = (EditText) findViewById(R.id.input_username);
        InputPassword = (EditText) findViewById(R.id.input_password);
        LoginButton = (TextView) findViewById(R.id.btn_login);

        Firebase.setAndroidContext(this);

        Snackbar snackbar = Snackbar.make(linearLayout,"NOT REGISTERED ?",Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("REGISTER NOW", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Register.class));
                finish();
            }
        });
        snackbar.show();


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = InputUser.getText().toString().trim();
                password = InputPassword.getText().toString();

                if (TextUtils.isEmpty(username)){
                    InputUser.setError("Field Required");
                }

                if (TextUtils.isEmpty(password)){
                    InputPassword.setError("Field Required");
                }

                else {

                    String url = "https://tubonge-84f9a.firebaseio.com/users.json";
                        final ProgressDialog pd = new ProgressDialog(Login.this);
                        pd.setMessage("Loading....");
                        pd.show();

                        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                if(response.equals("0")){
                                    Toast.makeText(Login.this, "User not found. Please Register", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    try {
                                        JSONObject obj = new JSONObject(response);

                                        if(!obj.has(username)){
                                            Toast.makeText(Login.this, "User not found", Toast.LENGTH_LONG).show();
                                        }
                                        else if(obj.getJSONObject(username).getString("password").equals(password)){
                                            UserDetails.username = username;
                                            UserDetails.password = password;
                                            startActivity(new Intent(Login.this, Users.class));
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(Login.this, "Incorrect password.Please try again.", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                pd.dismiss();
                            }
                        }, new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                System.out.println("" + volleyError);
                                pd.dismiss();
                            }
                    });

                        RequestQueue rQueue = Volley.newRequestQueue(Login.this);
                        rQueue.add(request);

                }

            }

        });
    }


}
