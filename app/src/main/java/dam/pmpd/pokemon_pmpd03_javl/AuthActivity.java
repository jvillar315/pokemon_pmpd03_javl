package dam.pmpd.pokemon_pmpd03_javl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private FirebaseAuth mAuth;  // FirebaseAuth para email/contraseña y Google
    private GoogleSignInClient mGoogleSignInClient;  // Cliente de Google Sign-In
    private static final int RC_SIGN_IN = 9001;      // Código para onActivityResult

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        //  Inicializar FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        //  Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Referencias a los componentes de layout
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        SignInButton btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);

        // Listeners para cada acción
        // Acción: Iniciar sesión con email/contraseña
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AuthActivity.this,
                            "Por favor, completa todos los campos.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    iniciarSesion(email, password);
                }
            }
        });

        // Acción: Registrar usuario con email/contraseña
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(AuthActivity.this,
                            "Por favor, completa todos los campos.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    registrarUsuario(email, password);
                }
            }
        });

        // Acción: Iniciar sesión con Google
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
    }

    // Método para iniciar sesión con email/contraseña
    private void iniciarSesion(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(AuthActivity.this,
                                "Inicio de sesión exitoso: " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                        // Ir a la pantalla principal
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Error al iniciar sesión
                        Toast.makeText(AuthActivity.this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    //  Método para registrar usuario con email/contraseña
    private void registrarUsuario(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registro exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(AuthActivity.this,
                                "Usuario registrado: " + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                        // Ir a la pantalla principal (opcional)
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Error al registrar
                        Toast.makeText(AuthActivity.this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    //  Iniciar el flujo de Google Sign-In
    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //  Manejar el resultado de la actividad de Google Sign-In
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // Tarea de obtener cuenta Google
            com.google.android.gms.tasks.Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                // Autenticar con Firebase
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Toast.makeText(this,
                        "Fallo al iniciar con Google: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Autenticar con Firebase usando la cuenta de Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("AuthActivity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this,
                                "Bienvenido: " + (user != null ? user.getEmail() : ""),
                                Toast.LENGTH_SHORT).show();

                        // Ir a MainActivity
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Si falla
                        Toast.makeText(AuthActivity.this,
                                "Fallo al autenticar con Google",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
