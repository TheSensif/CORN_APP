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

public class RegisterActivity extends AppCompatActivity {
    EditText email, name, surname, phone, password, compPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginButton();
        email = findViewById(R.id.registerEmail);
        name = findViewById(R.id.registerPersonName);
        surname = findViewById(R.id.registerPersonSurname);
        phone = findViewById(R.id.registerPhone);
        password = findViewById(R.id.registerPassword);
        compPassword = findViewById(R.id.registerRepeatPassword);
        try {
            registerButton();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    private void loginButton() {
        Button registerButon = findViewById(R.id.loginRegister);
        registerButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
    }

    private void registerButton() throws JSONException {
        Button registerButton = findViewById(R.id.registerRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("email", email.getText());
                    obj.put("name",name.getText());
                    obj.put("surname",surname.getText());
                    obj.put("phone",phone.getText());
                    obj.put("password",password.getText());
                    String value = compContrasenya(password.getText().toString(),compPassword.getText().toString());
                    if (value.equals("true")) {
                        UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/signup", obj.toString(), (response) -> {
                            try {
                                JSONObject obj2 = new JSONObject(response);
                                if (obj2.getString("status").equals("OK")) {
                                    LoginActivity.session_token = obj2.getString("session_token");
                                    guardarPref();
                                    dialog(obj2.getString("status"),obj2.getString("message"));
                                } else if (obj2.getString("status").equals("ERROR")) {
                                    dialog(obj2.getString("status"),obj2.getString("message"));

                                }
                            } catch (JSONException e) {
                                System.out.println();
                            }
                        });
                    } else {
                        dialog("ERROR","Les contrasenyes no són iguals.");
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private String compContrasenya(String contrasenya, String repContrasenya) {
        if (contrasenya.equals(repContrasenya)) {
            return "true";
        }
        return "false";
    }

    private void dialog(String status ,String mesage) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alerta = new AlertDialog.Builder(RegisterActivity.this);
                if (status.equals("OK")) {
                    alerta.setTitle("Registre");
                    alerta.setMessage(mesage);
                    alerta.setNegativeButton("Tancar" ,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.name = name.getText().toString();
                            MainActivity.surname = surname.getText().toString();
                            MainActivity.email = email.getText().toString();
                            MainActivity.telephon = phone.getText().toString();
                            MainActivity.validation_status = "NO_VERFICAT";
                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
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

    private void guardarPref() {
        SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(
                RegisterActivity.this);
        SharedPreferences.Editor miEditor = datos.edit();
        miEditor.putString("session_token",LoginActivity.session_token);
        miEditor.apply();
    }
}