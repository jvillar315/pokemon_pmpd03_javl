package dam.pmpd.pokemon_pmpd03_javl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

import dam.pmpd.pokemon_pmpd03_javl.models.CapturedPokemon;

public class CapturedDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POKEMON = "extra_pokemon";

    private ImageView ivSprite;
    private TextView tvName, tvInfo, tvTypes;
    private Button btnDelete;

    // Preferencias
    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_ALLOW_DELETE = "allowDelete";

    private CapturedPokemon currentPokemon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captured_detail);

        // Referencias UI
        ivSprite = findViewById(R.id.ivSpriteDetail);
        tvName   = findViewById(R.id.tvNameDetail);
        tvTypes  = findViewById(R.id.tvTypesDetail);
        tvInfo   = findViewById(R.id.tvInfoDetail);
        btnDelete= findViewById(R.id.btnDeletePokemon);

        // Recuperar el Pokémon
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(EXTRA_POKEMON)) {
            Toast.makeText(this, "No se encontró el Pokémon", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentPokemon = (CapturedPokemon) intent.getSerializableExtra(EXTRA_POKEMON);
        if (currentPokemon == null) {
            Toast.makeText(this, "No se encontró el Pokémon (null)", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Rellenar la UI con los datos
        loadPokemonInfo(currentPokemon);

        // Configurar el botón de eliminar
        btnDelete.setOnClickListener(v -> tryDeletePokemon());
    }

    /**
     * Carga en pantalla los datos del Pokémon.
     */
    private void loadPokemonInfo(CapturedPokemon pokemon) {
        tvName.setText(pokemon.getName());
        tvTypes.setText(pokemon.getTypes().toString());

        String info = String.format(Locale.getDefault(),
                "Nº de Pokédex: %d\nPeso: %.2f\nAltura: %.2f",
                pokemon.getIndex(),
                pokemon.getWeight(),
                pokemon.getHeight());
        tvInfo.setText(info);

        Glide.with(this)
                .load(pokemon.getImageUrl())
                .into(ivSprite);
    }

    /**
     * Verifica la preferencia de borrado y, si está activa, pide confirmación.
     */
    private void tryDeletePokemon() {
        // Leer preferencia
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean allowDelete = prefs.getBoolean(KEY_ALLOW_DELETE, false);

        if (!allowDelete) {
            Toast.makeText(this, "La eliminación está desactivada en Ajustes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Confirmación con diálogo
        new AlertDialog.Builder(this)
                .setTitle("Eliminar " + currentPokemon.getName())
                .setMessage("¿Seguro que quieres eliminar este Pokémon capturado?")
                .setPositiveButton("Eliminar", (dialog, which) -> deletePokemon())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina el Pokémon de Firestore.
     */
    private void deletePokemon() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "No se detecta usuario logueado", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .collection("capturedPokemon")
                .document(String.valueOf(currentPokemon.getIndex()))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, currentPokemon.getName() + " eliminado", Toast.LENGTH_SHORT).show();
                    finish(); // Cerrar la Activity
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Método para lanzar Activity con un Pokémon capturado.
     */
    public static void start(CapturedPokemon pokemon, AppCompatActivity activity) {
        Intent i = new Intent(activity, CapturedDetailActivity.class);
        i.putExtra(EXTRA_POKEMON, pokemon);
        activity.startActivity(i);
    }
}
