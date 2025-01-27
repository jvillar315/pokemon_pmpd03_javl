package dam.pmpd.pokemon_pmpd03_javl.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import dam.pmpd.pokemon_pmpd03_javl.AuthActivity;
import dam.pmpd.pokemon_pmpd03_javl.R;

public class SettingsFragment extends Fragment {

    private Switch switchAllowDelete;
    private Button btnLanguage, btnAbout, btnSignOut;

    private SharedPreferences preferences;
    private static final String PREF_NAME = "MyPrefs";          // Nombre del archivo de preferencias
    private static final String KEY_ALLOW_DELETE = "allowDelete"; // Clave booleana
    private static final String KEY_LANGUAGE = "appLanguage";     // Clave del idioma

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        //  Inicializar SharedPreferences
        preferences = requireContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        //  Referencias a los componentes
        switchAllowDelete = view.findViewById(R.id.switchAllowDelete);
        btnLanguage       = view.findViewById(R.id.btnLanguage);
        btnAbout          = view.findViewById(R.id.btnAbout);
        btnSignOut        = view.findViewById(R.id.btnSignOut);

        //  Cargar estado guardado del switch
        boolean allowDelete = preferences.getBoolean(KEY_ALLOW_DELETE, false);
        switchAllowDelete.setChecked(allowDelete);

        // Listener para guardar la nueva preferencia
        switchAllowDelete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(KEY_ALLOW_DELETE, isChecked);
            editor.apply(); // o commit()
        });

        //  Botón para cambiar idioma
        btnLanguage.setOnClickListener(v -> {
            // Alternar idioma de ejemplo (es/en).
            // Podrías hacerlo con un diálogo o spinner, etc.
            String currentLang = preferences.getString(KEY_LANGUAGE, "es");
            String newLang = currentLang.equals("es") ? "en" : "es";

            setLocale(newLang);
        });

        //  Botón “Acerca de”
        btnAbout.setOnClickListener(v -> showAboutDialog());

        //  Botón cerrar sesión
        btnSignOut.setOnClickListener(v -> {
            // Cerrar sesión de Firebase
            FirebaseAuth.getInstance().signOut();

            // Regresar a AuthActivity
            Intent intent = new Intent(requireContext(), AuthActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        return view;
    }

    // Metodo para cambiar y guardar el idioma
    private void setLocale(String languageCode) {
        // Guardar preferencia
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();

        // Cambiar la configuración del Locale
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);

        // Actualizar la configuración
        requireActivity().getResources().updateConfiguration(
                config,
                requireActivity().getResources().getDisplayMetrics()
        );

        // Recargar la Activity o Fragment para aplicar el idioma
        requireActivity().recreate();
    }

    // “Acerca de”
    private void showAboutDialog() {
        String versionName = "1.0";
        String message = "Desarrollador: Jose Antonio Villalar\nVersión: " + versionName;

        new AlertDialog.Builder(requireContext())
                .setTitle("Acerca de")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
