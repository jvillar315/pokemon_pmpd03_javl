package dam.pmpd.pokemon_pmpd03_javl.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dam.pmpd.pokemon_pmpd03_javl.R;
import dam.pmpd.pokemon_pmpd03_javl.adapters.PokemonAdapter;
import dam.pmpd.pokemon_pmpd03_javl.models.CapturedPokemon;
import dam.pmpd.pokemon_pmpd03_javl.models.PokemonDetailResponse;
import dam.pmpd.pokemon_pmpd03_javl.models.PokemonResponse;
import dam.pmpd.pokemon_pmpd03_javl.models.PokemonResult;
import dam.pmpd.pokemon_pmpd03_javl.network.PokemonApiService;
import dam.pmpd.pokemon_pmpd03_javl.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Muestra la lista de Pokemon obtenidos de la API.
 * Permite capturar un Pokemon al hacer click, llamando a la API de detalles y guardando en Firestore.
 * Destaca en la lista cuáles ya están capturados.
 */
public class PokedexFragment extends Fragment {

    private RecyclerView recyclerView;
    private PokemonAdapter adapter;

    // Un set con los ids de pokémon ya capturados (descargados de Firestore)
    private Set<Integer> capturedIds = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pokedex, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewPokedex);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PokemonAdapter();
        recyclerView.setAdapter(adapter);

        // Al hacer click en un Pokemon => capturarlo
        adapter.setOnPokemonClickListener(pokemon -> {
            String nameOrId = parseNameOrId(pokemon.getUrl());
            fetchPokemonDetails(nameOrId, new OnPokemonDetailsLoaded() {
                @Override
                public void onSuccess(CapturedPokemon captured) {
                    saveCapturedPokemonToFirestore(captured);
                }
                @Override
                public void onError(String errorMsg) {
                    Toast.makeText(getContext(), "Error detalles: " + errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Cargamos la lista inicial de Pokemon
        fetchPokemonList();

        // Cargamos de Firestore qué Pokemon están capturados
        loadCapturedIds();

        return view;
    }

    /**
     * Descarga la lista de Pokemon base (nombre, url) con offset=0, limit=50.
     */
    private void fetchPokemonList() {
        PokemonApiService apiService =
                RetrofitClient.getRetrofitInstance().create(PokemonApiService.class);

        Call<PokemonResponse> call = apiService.getPokemonList(0, 50);
        call.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonResponse> call,
                                   @NonNull Response<PokemonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PokemonResult> pokemonList = response.body().getResults();

                    adapter.setPokemonList(pokemonList);
                } else {
                    Toast.makeText(getContext(), "Error al obtener la lista", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Carga desde Firestore los IDs de los Pokemon que el usuario tiene capturados,
     * para poder resaltarlos en la lista de Pokedex.
     */
    private void loadCapturedIds() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .collection("capturedPokemon")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        CapturedPokemon p = doc.toObject(CapturedPokemon.class);
                        if (p != null) {
                            capturedIds.add(p.getIndex());
                        }
                    }
                    // Actualiza
                    adapter.setCapturedIds(capturedIds);
                });
    }

    /**
     * crea un CapturedPokemon.
     */
    private void fetchPokemonDetails(String nameOrId, OnPokemonDetailsLoaded listener) {
        PokemonApiService apiService =
                RetrofitClient.getRetrofitInstance().create(PokemonApiService.class);

        Call<PokemonDetailResponse> call = apiService.getPokemonDetails(nameOrId);
        call.enqueue(new Callback<PokemonDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<PokemonDetailResponse> call,
                                   @NonNull Response<PokemonDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PokemonDetailResponse detail = response.body();
                    CapturedPokemon captured = new CapturedPokemon(
                            detail.getName(),
                            detail.getId(),
                            detail.getImageUrl(),
                            detail.getTypeNames(),
                            detail.getWeight(),
                            detail.getHeight()
                    );
                    listener.onSuccess(captured);
                } else {
                    listener.onError("Respuesta no exitosa");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PokemonDetailResponse> call,
                                  @NonNull Throwable t) {
                listener.onError(t.getMessage());
            }
        });
    }

    /**
     * Guarda un CapturedPokemon en Firestore.
     */
    private void saveCapturedPokemonToFirestore(CapturedPokemon captured) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "No hay usuario logueado", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .collection("capturedPokemon")
                .document(String.valueOf(captured.getIndex()))
                .set(captured)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),
                            captured.getName() + " capturado y guardado!", Toast.LENGTH_SHORT).show();

                    // Añadir el id a capturedIds y notificar al adapter
                    capturedIds.add(captured.getIndex());
                    adapter.setCapturedIds(capturedIds);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al guardar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Extrae el id o nombre de "https://pokeapi.co/api/v2/pokemon/25/"
     */
    private String parseNameOrId(String url) {
        String[] parts = url.split("/");

        if (parts[parts.length - 1].isEmpty()) {
            return parts[parts.length - 2];
        } else {
            return parts[parts.length - 1];
        }
    }

    // Interfaz callback para obtener el detalle del Pokémon
    private interface OnPokemonDetailsLoaded {
        void onSuccess(CapturedPokemon captured);
        void onError(String errorMsg);
    }
}
