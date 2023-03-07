package com.thesensif.corn_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    public static String session_token = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        generaPref();
        if (!session_token.equals("")) {
            System.out.println(session_token);
            try {
                getProfile();
                //startActivity(new Intent(LoginActivity.this,MainActivity.class));
            } catch (JSONException e) {
                System.out.println();
            }
        }
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
                            if (obj2.getString("status").equals("OK")) {
                                session_token = obj2.getString("session_token");
                                System.out.println(session_token);
                                guardarPref();
                                dialog(obj2.getString("status"),obj2.getString("message"));
                            } else if (obj2.getString("status").equals("ERROR")) {
                                dialog(obj2.getString("status"),obj2.getString("message"));
                            }

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
                    alerta.setTitle("Iniciar Sessió");
                    alerta.setMessage(mesage);
                    alerta.setNegativeButton("Tancar" ,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                getProfile();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        }
                    });
                    alerta.show();
                } else if (status.equals("ERROR")) {
                    alerta.setTitle("Error de iniciar sessió");
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

    private void getProfile() throws JSONException {
        JSONObject obj = new JSONObject("{}");
        obj.put("session_token", session_token);
        UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/get_profile", obj.toString(), (response) -> {
            System.out.println(response);
            try {
                JSONObject obj2 = new JSONObject(response);
                if (obj2.getString("status").equals("OK")) {
                    MainActivity.name = obj2.getString("name");
                    MainActivity.surname = obj2.getString("surname");
                    MainActivity.email = obj2.getString("email");
                    MainActivity.telephon = obj2.getString("phone");
                    MainActivity.validation_status = obj2.getString("validation_status");
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                } else if (obj2.getString("status").equals("ERROR")) {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(LoginActivity.this);
                    alerta.setTitle("Error Token");
                    alerta.setMessage(obj2.getString("message"));
                    alerta.setNegativeButton("Tancar" ,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alerta.show();
                }
            } catch (JSONException e) {
                System.out.println();
            }
        });
    }

    private void generaPref() {
        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(
                this);
        session_token = datos.getString("session_token","");
    }

    private void guardarPref() {
        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(
                LoginActivity.this);
        SharedPreferences.Editor miEditor = datos.edit();
        miEditor.putString("session_token",session_token);
        miEditor.apply();
    }
}