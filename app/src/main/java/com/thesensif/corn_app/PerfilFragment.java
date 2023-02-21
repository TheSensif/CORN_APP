package com.thesensif.corn_app;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
        EditText email = v.findViewById(R.id.editTextTextEmailAddress2);

        syncButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = new JSONObject("{}");
                    obj.put("name", name.getText());
                    obj.put("lastName", surname.getText());
                    obj.put("email", email.getText());
                    obj.put("phoneNumber", telefon.getText());
                    obj.put("balance", 100);

                   UtilsHTTP.sendPOST("https://server-production-cc78.up.railway.app:443/api/signup", obj.toString(), (response) -> {
                        try {
                            JSONObject obj2 = new JSONObject(response);
                            System.out.println(obj2.getString("status"));
                            System.out.println(obj2.getString("result"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return  v;
    }
}