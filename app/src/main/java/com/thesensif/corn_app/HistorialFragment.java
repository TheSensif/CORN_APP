package com.thesensif.corn_app;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.thesensif.corn_app.databinding.ActivityMainBinding;
import com.thesensif.corn_app.databinding.FragmentHistorialBinding;

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
    private FragmentHistorialBinding binding;
    private ListView listview;
    private ArrayList<String> transactio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHistorialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ListView listView = binding.recordsView;
        balance = binding.historialAmount;
        transactio = new ArrayList<String>();
        try {
            JSONObject obj = new JSONObject("{}");
            obj.put("session_token", LoginActivity.session_token);
            UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/get_record_transactions", obj.toString(), (response) -> {
                try {
                    JSONObject obj2 = new JSONObject(response);
                    System.out.println(response);
                    balance.setText(obj2.getString("balance"));
                    String id = obj2.getString("id");
                    JSONArray transactions = obj2.getJSONArray("transactions");
                    for (int i = 0; i < transactions.length(); i++) {
                        JSONObject transaction = transactions.getJSONObject(i);
                        String amount = transaction.getString("amount");
                        String originPhoneNumber = transaction.getString("originPhoneNumber");
                        String origen = transaction.getString("origin");
                        String destino = transaction.getString("destination");
                        String acepted = transaction.getString("accepted");
                        String tiempo = transaction.getString("timeFinish");
                        if(transaction.getInt("accepted")==1) {
                            //orige paga y al destino le suman
                            if (transaction.getString("originPhoneNumber").equals(MainActivity.telephon)) {
                                transactio.add("Origen: " + transaction.getString("originPhoneNumber").toString() + "\nDesti: " + transaction.getString("destinationPhoneNumber").toString() + "\nQuantitat: " + "-"+transaction.getString("amount").toString() + "\nData: "+ dateFormat(transaction.getString("timeFinish"))+"\nAcceptat");
                            } else {
                                transactio.add("Origen: " + transaction.getString("originPhoneNumber").toString() + "\nDesti: " + transaction.getString("destinationPhoneNumber").toString() + "\nQuantitat: " + "+"+transaction.getString("amount").toString() + "\nData: "+ dateFormat(transaction.getString("timeFinish"))+"\nAcceptat");
                            }
                        }
                        else{
                            transactio.add("Origen: " + "\nDesti: " + transaction.getString("destinationPhoneNumber").toString() + "\nQuantitat: " + transaction.getString("amount").toString()+ "\nData: "+ dateFormat(transaction.getString("timeFinish"))+"\nCancel·lat");

                        }
                        System.out.println(transactio);
                    }
                    System.out.println("------");
                    System.out.println(transactio);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, transactio);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listView.setAdapter(adapter);
                        }
                    });

                } catch (JSONException e) {
                    System.out.println();
                }
            });
        } catch (JSONException e) {
            System.out.println();
        }
    }

    private String dateFormat(String date){
        String newDateFormat = "";
        newDateFormat = date.replace('T', ' ');
        newDateFormat = newDateFormat.replace(".000Z", "");
        return newDateFormat;
    }
}