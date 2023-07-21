package com.stivenvacacela.fibert_lit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stivenvacacela.fibert_lit.AgregarNota.Agregar_Nota;
import com.stivenvacacela.fibert_lit.ListarNotas.Listar_Notas;
import com.stivenvacacela.fibert_lit.NotasArchivadas.Notas_Archivadas;
import com.stivenvacacela.fibert_lit.Perfil.Perfil_Usuario;

public class MenuPrincipal extends AppCompatActivity {

    Button AgregarNotas, ListarNotas, CerrarSesion;
    //Button Archivados,Perfil,AcercaDe
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    TextView UidPrincipal, NombresPrincipal, CorreoPrincipal;
    ProgressBar progressBarDatos;

    DatabaseReference Usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Agenda Online");

        UidPrincipal = findViewById(R.id.UidPrincipal);
        NombresPrincipal = findViewById(R.id.NombresPrincipal);
        CorreoPrincipal = findViewById(R.id.CorreoPrincipal);
        progressBarDatos = findViewById(R.id.progressBarDatos);

        //base de dartos
        Usuarios = FirebaseDatabase.getInstance().getReference("Usuarios");

        //inicializar botones
        AgregarNotas = findViewById(R.id.AgregarNotas);
        ListarNotas = findViewById(R.id.ListarNotas);
        //Archivados = findViewById(R.id.Archivados);
        //Perfil = findViewById(R.id.Perfil);
        //AcercaDe = findViewById(R.id.AcercaDe);
        CerrarSesion = findViewById(R.id.CerrarSesion);

        //inicializacion de firebase_user
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        AgregarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Obtenemos la información de los TextView*/
                String uid_usuario = UidPrincipal.getText().toString();
                String correo_usuario = CorreoPrincipal.getText().toString();

                /*Pasamos datos a la siguiente actividad*/
                Intent intent = new Intent(MenuPrincipal.this, Agregar_Nota.class);
                intent.putExtra("Uid", uid_usuario);
                intent.putExtra("Correo", correo_usuario);
                startActivity(intent);

            }
        });

        ListarNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, Listar_Notas.class));
                Toast.makeText(MenuPrincipal.this, "Listar Notas", Toast.LENGTH_SHORT).show();
            }
        });

       /* Archivados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, Notas_Archivadas.class));
                Toast.makeText(MenuPrincipal.this, "Notas Archivadas", Toast.LENGTH_SHORT).show();
            }
        });
        */

       /* Perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuPrincipal.this, Perfil_Usuario.class));
                Toast.makeText(MenuPrincipal.this, "Perfil Usuario", Toast.LENGTH_SHORT).show();
            }
        });

         */

       /* AcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MenuPrincipal.this, "Acerca De", Toast.LENGTH_SHORT).show();
            }
        });
        */

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SalirAplicacion();
            }
        });
    }

    @Override
    protected void onStart() {
        ComprobarInicioSesion();
        super.onStart();
    }

    private void ComprobarInicioSesion(){
        if (user!=null){
            //El usuario ha iniciado sesión
            CargaDeDatos();
        }else {
            //Lo dirigirá al MainActivity
            startActivity(new Intent(MenuPrincipal.this,MainActivity.class));
            finish();
        }
    }

    private void CargaDeDatos(){
        Usuarios.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //Si el usuario existe
                if (snapshot.exists()){
                    //El progressbar se oculta
                    progressBarDatos.setVisibility(View.GONE); // cambiar a GONE(oculto) or VISIBLE
                    //Los TextView se muestran
                    UidPrincipal.setVisibility(View.GONE);     //cambiar a visible para ver Uid
                    NombresPrincipal.setVisibility(View.VISIBLE);
                    CorreoPrincipal.setVisibility(View.VISIBLE);

                    //Obtener los datos
                    String uid = ""+snapshot.child("uid").getValue();
                    String nombres = ""+snapshot.child("nombres").getValue();
                    String correo = ""+snapshot.child("correo").getValue();

                    //Setear los datos en los respectivos TextView
                    UidPrincipal.setText(uid);
                    NombresPrincipal.setText(nombres);
                    CorreoPrincipal.setText(correo);

                    //Habilitar los botones del menú
                    AgregarNotas.setEnabled(true);
                    ListarNotas.setEnabled(true);
                    //Archivados.setEnabled(true);
                    //Perfil.setEnabled(true);
                    //AcercaDe.setEnabled(true);
                    CerrarSesion.setEnabled(true);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SalirAplicacion() {
        firebaseAuth.signOut();
        startActivity(new Intent(MenuPrincipal.this, MainActivity.class));
        Toast.makeText(this, "Cerraste sesión exitosamente", Toast.LENGTH_SHORT).show();
    }
}