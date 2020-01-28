package com.proyecto.rosyec;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        pDialog = new ProgressDialog(LoginActivity.this);

        pDialog.setCancelable(false);

        final View view = findViewById(android.R.id.content);

        final EditText correo = findViewById(R.id.edittext_email_log);
        final EditText contrasena = findViewById(R.id.edittext_password_log);

        Button log = findViewById(R.id.button_login);

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.setMessage("Iniciando Sesión ...");
                showDialog();
                String email = correo.getText().toString().trim();
                String password = contrasena.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()){
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        hideDialog();
                                        Log.d(TAG, "signInWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(LoginActivity.this,MenuPrincipalActivity.class));
                                        finish();
                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Snackbar.make(view,"Correo u contraseña incorrectos!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    }

                                }
                            });
                }else{
                    Snackbar.make(view,"Por favor ingresa tus datos!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });

        ImageView back = findViewById(R.id.back_button_log);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ActivityBienvenida.class));
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent ant = new Intent(LoginActivity.this, ActivityBienvenida.class);
            startActivity(ant);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void savePreferences(String name_valor, String valor) {
        SharedPreferences sharedPref = getSharedPreferences("Preferencias_Globales", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(name_valor, valor);
        editor.apply();
    }

    public String getPreferences(String valor) {
        SharedPreferences sharedPre = getSharedPreferences("Preferencias_Globales", Context.MODE_PRIVATE);
        String dato = sharedPre.getString(valor, "No hay dato");
        return dato;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
