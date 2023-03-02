package com.thesensif.corn_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.thesensif.corn_app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    public static String telephon;
    public static String name;
    public static String surname;
    public static String email;

    public static String session_token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new InicioFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case R.id.Inici:
                    replaceFragment(new InicioFragment());
                    break;
                case R.id.Cobrament:
                    replaceFragment(new CobramentFragment());
                    break;
                case R.id.Escanejar:
                    replaceFragment(new EscanejarFragment());
                    break;
                case R.id.Historial:
                    replaceFragment(new HistorialFragment());
                    break;
                case R.id.Perfil:
                    replaceFragment(new PerfilFragment());
                    break;
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }

}