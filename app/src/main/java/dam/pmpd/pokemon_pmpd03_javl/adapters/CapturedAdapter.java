package dam.pmpd.pokemon_pmpd03_javl.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dam.pmpd.pokemon_pmpd03_javl.R;
import dam.pmpd.pokemon_pmpd03_javl.models.CapturedPokemon;

/**
 * Muestra la lista de Pokémon capturados con su imagen y stats.
 */
public class CapturedAdapter extends RecyclerView.Adapter<CapturedAdapter.ViewHolder> {

    private final List<CapturedPokemon> capturedList;

    //  Declaramos la interfaz
    public interface OnItemClickListener {
        void onItemClick(CapturedPokemon pokemon);
    }

    //  Variable para guardar el listener
    private OnItemClickListener onItemClickListener;

    //  Método para asignar el listener desde fuera
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public CapturedAdapter(List<CapturedPokemon> list) {
        this.capturedList = list;
    }

    @NonNull
    @Override
    public CapturedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_captured_pokemon, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CapturedPokemon pokemon = capturedList.get(position);

        holder.tvName.setText(pokemon.getName());
        holder.tvTypes.setText(pokemon.getTypes().toString());

        String stats = "Peso: " + pokemon.getWeight() + " | Altura: " + pokemon.getHeight();
        holder.tvStats.setText(stats);

        // Cargar la imagen con Glide
        Glide.with(holder.itemView.getContext())
                .load(pokemon.getImageUrl())
                .into(holder.ivSprite);

        // 4) Listener de click en el ítem
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(pokemon);
            }
        });
    }

    @Override
    public int getItemCount() {
        return capturedList.size();
    }

    public void removeItem(int position) {
        capturedList.remove(position);
        notifyItemRemoved(position);
    }

    public CapturedPokemon getItem(int position) {
        return capturedList.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivSprite;
        TextView tvName, tvTypes, tvStats;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSprite = itemView.findViewById(R.id.ivPokemonSprite);
            tvName   = itemView.findViewById(R.id.tvPokemonName);
            tvTypes  = itemView.findViewById(R.id.tvPokemonTypes);
            tvStats  = itemView.findViewById(R.id.tvPokemonStats);
        }
    }
}
