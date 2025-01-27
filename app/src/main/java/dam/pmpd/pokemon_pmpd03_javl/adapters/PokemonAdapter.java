package dam.pmpd.pokemon_pmpd03_javl.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dam.pmpd.pokemon_pmpd03_javl.R;
import dam.pmpd.pokemon_pmpd03_javl.models.PokemonResult;

/**
 * Muestra la lista de pokémon desde la PokeAPI.
 * Permite un listener al hacer click para capturarlos.
 */
public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    private List<PokemonResult> pokemonList = new ArrayList<>();
    private OnPokemonClickListener onPokemonClickListener;
    private Set<Integer> capturedIds;

    public interface OnPokemonClickListener {
        void onPokemonClick(PokemonResult pokemon);
    }

    public void setOnPokemonClickListener(OnPokemonClickListener listener) {
        this.onPokemonClickListener = listener;
    }

    public void setPokemonList(List<PokemonResult> list) {
        this.pokemonList = list;
        notifyDataSetChanged();
    }

    public void setCapturedIds(Set<Integer> capturedIds) {
        this.capturedIds = capturedIds;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pokemon, parent, false);
        return new PokemonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        PokemonResult pokemon = pokemonList.get(position);
        holder.tvPokemonName.setText(pokemon.getName());

        // Determinar si está capturado
        int id = parseIdFromUrl(pokemon.getUrl());
        if (capturedIds != null && capturedIds.contains(id)) {
            // Cambia color de fondo
            holder.itemView.setBackgroundColor(Color.parseColor("#FFCCCC")); // rojizo
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (onPokemonClickListener != null) {
                onPokemonClickListener.onPokemonClick(pokemon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }

    private int parseIdFromUrl(String url) {

        String[] parts = url.split("/");
        String val = parts[parts.length - 1].isEmpty()
                ? parts[parts.length - 2]
                : parts[parts.length - 1];
        return Integer.parseInt(val);
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        TextView tvPokemonName;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPokemonName = itemView.findViewById(R.id.tvPokemonName);
        }
    }
}
