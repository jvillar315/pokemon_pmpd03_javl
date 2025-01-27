package dam.pmpd.pokemon_pmpd03_javl.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapea la respuesta JSON al pedir
 * https://pokeapi.co/api/v2/pokemon/{id_o_nombre}
 */
public class PokemonDetailResponse {

    private int id;
    private String name;
    private int height;
    private int weight;
    private Sprites sprites;
    private List<TypeSlot> types;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public List<TypeSlot> getTypes() {
        return types;
    }


    public String getImageUrl() {
        if (sprites != null) {
            return sprites.getFrontDefault();
        }
        return null;
    }

    /**
     * Convierte los tipos a un List<String>
     */
    public List<String> getTypeNames() {
        List<String> result = new ArrayList<>();
        if (types != null) {
            for (TypeSlot slot : types) {
                if (slot != null && slot.getType() != null) {
                    result.add(slot.getType().getName());
                }
            }
        }
        return result;
    }

    // Clases internas para mapear subcampos del JSON

    public static class Sprites {
        @SerializedName("front_default")
        private String frontDefault;

        public String getFrontDefault() {
            return frontDefault;
        }
    }

    public static class TypeSlot {
        private int slot;
        private TypeInfo type;

        public int getSlot() {
            return slot;
        }

        public TypeInfo getType() {
            return type;
        }
    }

    public static class TypeInfo {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
}