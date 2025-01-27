package dam.pmpd.pokemon_pmpd03_javl.models;

import java.io.Serializable;
import java.util.List;
/**
 * Representa el Pokemon capturado que guardaremos en Firestore.
 */
public class CapturedPokemon implements Serializable {

    private String name;
    private int index;
    private String imageUrl;
    private List<String> types;
    private double weight;
    private double height;

    public CapturedPokemon() {
    }

    public CapturedPokemon(String name, int index, String imageUrl,
                           List<String> types, double weight, double height) {
        this.name = name;
        this.index = index;
        this.imageUrl = imageUrl;
        this.types = types;
        this.weight = weight;
        this.height = height;
    }

    // Getters & Setters

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<String> getTypes() {
        return types;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
