package dam.pmpd.pokemon_pmpd03_javl.network;

import dam.pmpd.pokemon_pmpd03_javl.models.PokemonResponse;
import dam.pmpd.pokemon_pmpd03_javl.models.PokemonDetailResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Declara las llamadas a la PokeAPI.
 */
public interface PokemonApiService {

    // Lista de Pokemon
    @GET("pokemon")
    Call<PokemonResponse> getPokemonList(
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    // Detalle de un Pokemon (por nombre o id)
    @GET("pokemon/{nameOrId}")
    Call<PokemonDetailResponse> getPokemonDetails(@Path("nameOrId") String nameOrId);
}
