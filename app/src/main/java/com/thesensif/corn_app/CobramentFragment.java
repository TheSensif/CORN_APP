package com.thesensif.corn_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CobramentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CobramentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String token = "";
    private ImageView qrImage;
    private Button generateTokenButton;

    public CobramentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CobramentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CobramentFragment newInstance(String param1, String param2) {
        CobramentFragment fragment = new CobramentFragment();
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
        View v = inflater.inflate(R.layout.fragment_cobrament, container, false);
        Bitmap bitmap;

        qrImage = v.findViewById(R.id.imageQr);
        generateTokenButton = v.findViewById(R.id.buttonGenerateToken);


        generateTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDB();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    private void generateQE(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap;
                // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
                QRGEncoder qrgEncoder = new QRGEncoder(token, null, QRGContents.Type.TEXT, 120);
                qrgEncoder.setColorBlack(Color.BLACK);
                qrgEncoder.setColorWhite(Color.WHITE);
                try {
                    // Getting QR-Code as Bitmap
                    bitmap = qrgEncoder.getBitmap(0);
                    // Setting Bitmap to ImageView
                    qrImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,320,320,false));
                } catch (Exception e) {
                    System.out.println("error");
                }
            }
    });
    }


    private void checkDB(){
        final Activity activity = getActivity();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Introduir quantitat");

                final EditText input = new EditText(getActivity());

                input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Generar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cantidad = input.getText().toString();
                        try {
                            JSONObject obj = new JSONObject("{}");

                            obj.put("user_id", LoginActivity.session_token);
                            obj.put("amount", cantidad);
                            UtilsHTTP.sendPOST("https://cornapi-production-5680.up.railway.app:443/api/setup_payment", obj.toString(), (response) -> {
                                try {
                                    JSONObject obj2 = new JSONObject(response);
                                    if (obj2.getString("status").equals("OK")) {
                                        token = obj2.getString("transaction_token");
                                        generateQE();
                                    } else if (obj2.getString("status").equals("ERROR")) {
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
                                                AlertDialog dialog2 = builder.create();
                                                dialog2.show();
                                            }
                                        });
                                    }
                                } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("CANCEL·LAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();
            }
        });
    }


}