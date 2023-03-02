package com.thesensif.corn_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        registerButton();
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        loginButton();
    }

    private void loginButton(){
        Button loginbuton = findViewById(R.id.login);
        loginbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("email", email.getText());
                    obj.put("password",password.getText());
                    UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/login", obj.toString(), (response) -> {
                        try {
                            JSONObject obj2 = new JSONObject(response);
                            System.out.println(obj2.getString("status"));
                            System.out.println(obj2.getString("message"));
                            System.out.println(obj2.getString("session_token"));

                            MainActivity.session_token = obj2.getString("session_token");

                            dialog(obj2.getString("status"),obj2.getString("message"));
                        } catch (JSONException e) {
                            System.out.println();
                        }
                    });
                } catch (JSONException e) {
                    System.out.println();
                }
            }
        });
    }

    private void registerButton() {
        Button registerButon = findViewById(R.id.register);
        registerButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
    }

    private void dialog(String status ,String mesage) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alerta = new AlertDialog.Builder(LoginActivity.this);
                if (status.equals("OK")) {
                    alerta.setTitle("Registre");
                    alerta.setMessage(mesage);
                    alerta.setNegativeButton("Tancar" ,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        }
                    });
                    alerta.show();
                } else if (status.equals("ERROR")) {
                    alerta.setTitle("Error de registre");
                    alerta.setMessage(mesage);
                    alerta.setNegativeButton("Tancar" ,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alerta.show();
                }

            }
        });
    }
}