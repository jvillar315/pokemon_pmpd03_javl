package dam.pmpd.pokemon_pmpd03_javl.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import dam.pmpd.pokemon_pmpd03_javl.R;
import dam.pmpd.pokemon_pmpd03_javl.adapters.CapturedAdapter;
import dam.pmpd.pokemon_pmpd03_javl.CapturedDetailActivity;
import dam.pmpd.pokemon_pmpd03_javl.models.CapturedPokemon;

public class PokemonCapturedFragment extends Fragment {

    private RecyclerView rvCaptured;
    private CapturedAdapter adapter;
    private List<CapturedPokemon> capturedList = new ArrayList<>();

    private static final String PREF_NAME = "MyPrefs";
    private static final String KEY_ALLOW_DELETE = "allowDelete";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_captured, container, false);

        rvCaptured = view.findViewById(R.id.rvCaptured);
        rvCaptured.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CapturedAdapter(capturedList);
        rvCaptured.setAdapter(adapter);

        //  Al hacer click en un Pokemon, abrimos la Activity de detalle
        adapter.setOnItemClickListener(pokemon -> {

            if (requireActivity() instanceof AppCompatActivity) {
                CapturedDetailActivity.start(pokemon, (AppCompatActivity) requireActivity());
            }
        });

        //  Cargar de Firestore
        loadCapturedFromFirestore();

        // Habilitar swipe
        enableSwipeToDelete();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCapturedFromFirestore();
    }

    private void loadCapturedFromFirestore() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(user.getUid())
                .collection("capturedPokemon")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    capturedList.clear();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        CapturedPokemon p = doc.toObject(CapturedPokemon.class);
                        if (p != null) {
                            capturedList.add(p);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Error al cargar capturados: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void enableSwipeToDelete() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        CapturedPokemon p = adapter.getItem(position);

                        // Verificar preferencia "allowDelete"
                        SharedPreferences prefs = requireContext().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                        boolean allowDelete = prefs.getBoolean(KEY_ALLOW_DELETE, false);

                        if (!allowDelete) {
                            Toast.makeText(getContext(),
                                    "Eliminar desactivado en Ajustes", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(position);
                            return;
                        }

                        // Borrar en Firestore
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null) {
                            Toast.makeText(getContext(), "No user found", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemChanged(position);
                            return;
                        }
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("users")
                                .document(user.getUid())
                                .collection("capturedPokemon")
                                .document(String.valueOf(p.getIndex()))
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    // Quitar del adapter
                                    adapter.removeItem(position);
                                    Toast.makeText(getContext(),
                                            p.getName() + " eliminado", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(),
                                            "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    adapter.notifyItemChanged(position);
                                });
                    }
                }
        );
        itemTouchHelper.attachToRecyclerView(rvCaptured);
    }
}
