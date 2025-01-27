package dam.pmpd.pokemon_pmpd03_javl;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vincular la vista de navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Obtener el controlador de navegación del NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Configurar las pestañas que no necesitan retroceso
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.pokemonCapturedFragment,
                R.id.pokedexFragment,
                R.id.settingsFragment
        ).build();

        // Configurar la barra de acción y la navegación con el controlador
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}
