package com.thesensif.corn_app;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_perfil, container, false);

        Button syncButon = v.findViewById(R.id.sync);
        EditText telefon = v.findViewById(R.id.editTextPhone);
        EditText name = v.findViewById(R.id.editTextTextPersonName);
        EditText surname = v.findViewById(R.id.editTextTextPersonSurname);
        EditText email = v.findViewById(R.id.editTextTextEmailAddress);
        name.setText(MainActivity.name);
        surname.setText(MainActivity.surname);
        email.setText(MainActivity.email);
        telefon.setText(MainActivity.telephon);

        syncButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("session_token", LoginActivity.session_token);

                   UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/logout", obj.toString(), (response) -> {
                       try {
                           JSONObject obj2 = new JSONObject(response);
                           if (obj2.getString("status").equals("OK")) {
                                LoginActivity.session_token = "";
                               SharedPreferences datos = PreferenceManager.getDefaultSharedPreferences(
                                       getActivity());
                               SharedPreferences.Editor miEditor = datos.edit();
                               miEditor.remove("session_token").apply();
                               dialog(obj2.getString("status"),obj2.getString("message"));
                           } else if (obj2.getString("status").equals("ERROR")) {
                               dialog(obj2.getString("status"),obj2.getString("message"));
                           }
                       } catch (JSONException e) {
                           System.out.println();
                       }

                   });


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });



        return  v;
    }
    private void dialog(String status ,String mesage) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alerta = new AlertDialog.Builder(getActivity());
                if (status.equals("OK")) {
                    alerta.setTitle("Tancar Sessió");
                    alerta.setMessage(mesage);
                    alerta.setNegativeButton("Tancar" ,new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getActivity(),LoginActivity.class));
                        }
                    });
                    alerta.show();
                } else if (status.equals("ERROR")) {
                    alerta.setTitle("Error de tancar sessió");
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