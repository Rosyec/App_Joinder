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
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private FirebaseAuth mAuth;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        pDialog = new ProgressDialog(RegisterActivity.this);

        pDialog.setCancelable(false);

        final View view = findViewById(android.R.id.content);

        final EditText nombre = findViewById(R.id.edittext_name_reg);
        final EditText apellido = findViewById(R.id.edittext_lastname_reg);
        final EditText correo = findViewById(R.id.edittext_email_reg);
        final EditText telefono = findViewById(R.id.edittext_number_phone_reg);
        final EditText contrasena = findViewById(R.id.edittext_password_reg);
        final EditText contrasena_confirm = findViewById(R.id.edittext_password_confirm_reg);

        final RadioButton doc = findViewById(R.id.radiobutton_docente);
        final RadioButton pad = findViewById(R.id.radiobutton_padre);

        Button reg = findViewById(R.id.button_register);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog.setMessage("Registrando cuenta, espere ...");
                showDialog();
                final String name = nombre.getText().toString().trim();
                final String lastname = apellido.getText().toString().trim();
                final String email = correo.getText().toString().trim();
                final String number_phone = telefono.getText().toString().trim();
                String password = contrasena.getText().toString().trim();
                String password_confirm = contrasena_confirm.getText().toString().trim();

                if (doc.isChecked()){
                    savePreferences("type_user","1");
                }else if (pad.isChecked()){
                    savePreferences("type_user","2");
                }

                if (!password.equalsIgnoreCase(password_confirm)){
                    Snackbar.make(view,"Las contraseñas no coinciden!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    return;
                }

                if (!name.isEmpty() && !lastname.isEmpty() && !email.isEmpty() && !number_phone.isEmpty() && !password.isEmpty()){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        hideDialog();
                                        try {
                                            Log.d(TAG, "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference myRef = database.getReference("users");
                                            String type_user = getPreferences("type_user");
                                            myRef.child(user.getUid()).child("Name").setValue(name);
                                            myRef.child(user.getUid()).child("Email").setValue(email
                                            );
                                            myRef.child(user.getUid()).child("LastName").setValue(lastname);
                                            myRef.child(user.getUid()).child("Number_Phone").setValue(number_phone);
                                            myRef.child(user.getUid()).child("Type_User").setValue(type_user);
                                            startActivity(new Intent(RegisterActivity.this, ActivityBienvenida.class));
                                            finish();
                                        }catch (NullPointerException e){
                                            Log.e(TAG,"Datos Nulos en Registro: ",e);
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Snackbar.make(view,"Ya existe un usuario con este correo!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                    }

                                }
                            });
                }else{
                    Snackbar.make(view,"Por favor ingresa tus datos", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }
        });


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent ant = new Intent(RegisterActivity.this, ActivityBienvenida.class);
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
