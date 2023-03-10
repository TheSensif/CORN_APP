package com.thesensif.corn_app;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thesensif.corn_app.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistorialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistorialFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistorialFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistorialFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistorialFragment newInstance(String param1, String param2) {
        HistorialFragment fragment = new HistorialFragment();
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
    TextView balance;
    ActivityMainBinding binding;
    // Model = Taula de records: utilitzem ArrayList
    ArrayList<Record> records;

    // ArrayAdapter serà l'intermediari amb la ListView
    ArrayAdapter<Record> adapter;

    Handler mainHandler = new Handler();
    ProgressDialog progressDialog;

    class Record {
        public String amount;
        public String id;
        public String originPhoneNumber;
        public String origen;
        public String destino;
        public String accepted;
        public String tiempo;

        public Record(String _id, String _amount, String _origen, String _originPhoneNumber, String _destino, String _accepted, String _tiempo ) {
            id = _id;
            amount = _amount;
            origen = _origen;
            originPhoneNumber = _originPhoneNumber;
            destino = _destino;
            accepted = _accepted;
            tiempo = _tiempo;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = inflater.inflate(R.layout.fragment_historial, binding.getRoot(), false);
        balance = v.findViewById(R.id.historialAmount);
        new fetchData().start();
        records = new ArrayList<Record>();

        adapter = new ArrayAdapter<Record>(getActivity(), R.layout.list_item, records )
        {
            @Override
            public View getView(int pos, View convertView, ViewGroup container)
            {
                // getView ens construeix el layout i hi "pinta" els valors de l'element en la posició pos
                if( convertView==null ) {
                    // inicialitzem l'element la View amb el seu layout
                    convertView = getLayoutInflater().inflate(R.layout.list_item, container, false);
                }
                // "Pintem" valors (també quan es refresca)
                ((TextView) convertView.findViewById(R.id.tiempo)).setText(dateFormat(getItem(pos).tiempo));

                if (getItem(pos).originPhoneNumber.equals("null")) {
                    ((TextView) convertView.findViewById(R.id.itemTelefono)).setText("");
                } else {
                    ((TextView) convertView.findViewById(R.id.itemTelefono)).setText(getItem(pos).originPhoneNumber);
                }

                if (getItem(pos).accepted.equals("1")) {
                    ((TextView) convertView.findViewById(R.id.estado)).setText("Aceptada");
                }
                if (getItem(pos).accepted.equals("0")) {
                    ((TextView) convertView.findViewById(R.id.estado)).setText("Denegada");
                }
                if (getItem(pos).origen.equals(getItem(pos).id)) {
                    ((TextView) convertView.findViewById(R.id.amount)).setText("-"+getItem(pos).amount);
                }
                if (getItem(pos).destino.equals(getItem(pos).id)) {
                    ((TextView) convertView.findViewById(R.id.amount)).setText("+"+getItem(pos).amount);
                }


                return convertView;
            }

        };

        ListView lv = (ListView) v.findViewById(R.id.recordsView);
        lv.setAdapter(adapter);

        return v;
    }

    private void initializerHistorialList() {


    }

    class fetchData extends Thread {


        @Override
        public void run() {
            super.run();

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Cargan dades");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            });


            try {
                JSONObject obj = new JSONObject("{}");
                obj.put("session_token", LoginActivity.session_token);
                UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/get_record_transactions", obj.toString(), (response) -> {
                    try {
                        JSONObject obj2 = new JSONObject(response);
                        balance.setText(obj2.getString("balance"));
                        String id = obj2.getString("id");
                        JSONArray transactions = obj2.getJSONArray("transactions");
                        records.clear();
                        for (int i = 0; i < transactions.length(); i++) {
                            JSONObject transaction = transactions.getJSONObject(i);
                            String amount = transaction.getString("amount");
                            String originPhoneNumber = transaction.getString("originPhoneNumber");
                            String origen = transaction.getString("origin");
                            String destino = transaction.getString("destination");
                            String acepted = transaction.getString("accepted");
                            String tiempo = transaction.getString("timeFinish");
                            records.add(new Record(id,amount,origen,originPhoneNumber,destino,acepted,tiempo));
                        }
                    } catch (JSONException e) {
                        System.out.println();
                    }
                });
            } catch (JSONException e) {
                System.out.println();
            }

            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
    private String dateFormat(String date){
        String newDateFormat = "";
        newDateFormat = date.replace('T', ' ');
        newDateFormat = newDateFormat.replace(".000Z", "");
        return newDateFormat;
    }
}