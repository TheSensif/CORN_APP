package com.thesensif.corn_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EscanejarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EscanejarFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CodeScanner mCodeScanner;

    public EscanejarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EscanejarFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EscanejarFragment newInstance(String param1, String param2) {
        EscanejarFragment fragment = new EscanejarFragment();
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

        requestPermissionLauncher.launch(Manifest.permission.CAMERA);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_escanejar, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, result.getText(), Toast.LENGTH_SHORT).show();
                        System.out.println("RESULT: " + result.getText());
                        try {
                            JSONObject obj = new JSONObject("{}");
                            obj.put("user_id", MainActivity.telephon);
                            obj.put("transaction_token", result.getText());
                            UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/start_payment", obj.toString(), (response) -> {
                                try {
                                    JSONObject obj2 = new JSONObject(response);
                                    if (obj2.getString("status").equals("OK")) {
                                        // Add the buttons
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                    builder.setTitle("Confirmacion transaccion")
                                                            .setMessage("Apruebas hacer la transaccion con un importe de: " + obj2.getString("amount"))
                                                            .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    try {
                                                                        JSONObject obj3 = new JSONObject("{}");
                                                                        obj3.put("user_id", MainActivity.telephon);
                                                                        obj3.put("transaction_token", result.getText());
                                                                        obj3.put("accept", true);
                                                                        obj3.put("amount", obj2.getString("amount"));

                                                                        UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/finish_payment", obj3.toString(), (response) -> {
                                                                            try {
                                                                                JSONObject obj4 = new JSONObject(response);
                                                                                if (obj4.getString("status").equals("OK")) {
                                                                                    Handler handler = new Handler(Looper.getMainLooper());
                                                                                    handler.post(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                                            builder.setTitle("Transaccion aceptada")
                                                                                                    .setMessage("La transaccion ha sido aceptada")
                                                                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                                        public void onClick(DialogInterface dialog, int id) {

                                                                                                        }
                                                                                                    });
                                                                                            AlertDialog dialog2 = builder.create();
                                                                                            dialog2.show();
                                                                                        }
                                                                                    });
                                                                                } else if (obj4.getString("status").equals("ERROR")) {
                                                                                    Handler handler = new Handler(Looper.getMainLooper());
                                                                                    handler.post(new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                                            try {
                                                                                                builder.setTitle("Error")
                                                                                                        .setMessage(obj4.getString("message"))
                                                                                                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                                                                            public void onClick(DialogInterface dialog, int id) {
                                                                                                                // User clicked OK button
                                                                                                            }
                                                                                                        });
                                                                                            } catch (JSONException e) {
                                                                                                e.printStackTrace();
                                                                                            }
                                                                                            AlertDialog dialog2 = builder.create();
                                                                                            dialog2.show();
                                                                                        }
                                                                                    });
                                                                                }
                                                                            } catch (JSONException e) {
                                                                                throw new RuntimeException(e);
                                                                            }
                                                                        });
                                                                    } catch (JSONException e) {
                                                                        throw new RuntimeException(e);
                                                                    }
                                                                }
                                                            });
                                                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            try {
                                                                JSONObject obj3 = new JSONObject("{}");
                                                                obj3.put("user_id", MainActivity.telephon);
                                                                obj3.put("transaction_token", result.getText());
                                                                obj3.put("accept", false);
                                                                obj3.put("amount", obj2.getString("amount"));

                                                                UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/finish_payment", obj3.toString(), (response) -> {
                                                                    try {
                                                                        JSONObject obj4 = new JSONObject(response);
                                                                        if (obj4.getString("status").equals("OK")) {
                                                                            Handler handler = new Handler(Looper.getMainLooper());
                                                                            handler.post(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                                    builder.setTitle("Transaccion rechazada")
                                                                                            .setMessage("La transaccion ha sido rechazada")
                                                                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                                public void onClick(DialogInterface dialog, int id) {

                                                                                                }
                                                                                            });
                                                                                    AlertDialog dialog2 = builder.create();
                                                                                    dialog2.show();
                                                                                }
                                                                            });
                                                                        } else if (obj4.getString("status").equals("ERROR")) {
                                                                            Handler handler = new Handler(Looper.getMainLooper());
                                                                            handler.post(new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                                                    try {
                                                                                        builder.setTitle("Error")
                                                                                                .setMessage(obj4.getString("message"))
                                                                                                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                                                        // User clicked OK button
                                                                                                    }
                                                                                                });
                                                                                    } catch (JSONException e) {
                                                                                        e.printStackTrace();
                                                                                    }
                                                                                    AlertDialog dialog2 = builder.create();
                                                                                    dialog2.show();
                                                                                }
                                                                            });
                                                                        }
                                                                    } catch (JSONException e) {
                                                                        throw new RuntimeException(e);
                                                                    }
                                                                });
                                                            } catch (JSONException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        }
                                                    });
                                                    AlertDialog dialog = builder.create();
                                                    dialog.show();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    } else if (obj2.getString("status").equals("ERROR")){
                                        Handler handler = new Handler(Looper.getMainLooper());
                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                                                try {
                                                    builder.setTitle("Error")
                                                            .setMessage(obj2.getString("message"))
                                                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int id) {
                                                                    // User clicked OK button
                                                                }
                                                            });
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                AlertDialog dialog = builder.create();
                                                dialog.show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    //PERMISOS
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    System.out.println("Persmisos");
                } else {
                    System.out.println("No persmisos");
                }
            });

}