package com.stivenvacacela.fibert_lit.ListarNotas;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stivenvacacela.fibert_lit.ActualizarNota.Actualizar_Nota;
import com.stivenvacacela.fibert_lit.Objetos.Nota;
import com.stivenvacacela.fibert_lit.R;
import com.stivenvacacela.fibert_lit.ViewHolder.ViewHolder_Nota;

import org.jetbrains.annotations.NotNull;


public class Listar_Notas extends AppCompatActivity {

    RecyclerView recyclerviewNotas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference BASE_DE_DATOS;

    LinearLayoutManager linearLayoutManager;

    //Detector de eventos que suceden en la base de datos(eliminar, actualizar, cambiar las notas)
    FirebaseRecyclerAdapter<Nota, ViewHolder_Nota> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<Nota> options;

    Dialog dialog;

    //FirebaseAuth auth;
    //FirebaseUser user;

    private static final int NOTIFICATION_ID = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_notas);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Mis notas");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        recyclerviewNotas = findViewById(R.id.recyclerviewNotas);
        recyclerviewNotas.setHasFixedSize(true);

        //auth = FirebaseAuth.getInstance();
        //user = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        BASE_DE_DATOS = firebaseDatabase.getReference("Notas_Publicadas");
        dialog = new Dialog(Listar_Notas.this);
        ListarNotasUsuarios();

        // Crear el canal de notificación (solo para versiones de Android Oreo y posteriores)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notas_channel";
            String channelName = "Notas Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void ListarNotasUsuarios(){
        //Consulta
        //Query query = BASE_DE_DATOS.orderByChild("uid_usuario").equalTo(user.getUid());


        //options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(query, Nota.class).build();
        options = new FirebaseRecyclerOptions.Builder<Nota>().setQuery(BASE_DE_DATOS, Nota.class).build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Nota, ViewHolder_Nota>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder_Nota viewHolder_nota, int position, @NotNull Nota nota) {
                viewHolder_nota.SetearDatos(
                        getApplicationContext(),
                        nota.getId_nota(),
                        nota.getUid_usuario(),
                        nota.getCorreo_usuario(),
                        nota.getFecha_hora_actual(),
                        nota.getTitulo(),
                        nota.getDescripcion(),
                        nota.getFecha_nota(),
                        nota.getEstado()
                );

                if (!nota.getEstado().equalsIgnoreCase("Finalizado")) {
                    mostrarNotificacion(nota.getTitulo(), nota.getDescripcion());
                }
            }

                @Override
            public ViewHolder_Nota onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_nota,parent,false);
                ViewHolder_Nota viewHolder_nota = new ViewHolder_Nota(view);
                viewHolder_nota.setOnClickListener(new ViewHolder_Nota.ClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(Listar_Notas.this, "on item click", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {


                        //obtener los datos de la nota señeccionada
                        String id_nota = getItem(position).getId_nota();
                        String uid_usuario = getItem(position).getUid_usuario();
                        String correo_usuario = getItem(position).getCorreo_usuario();
                        String fecha_registro = getItem(position).getFecha_hora_actual();
                        String titulo = getItem(position).getTitulo();
                        String descripcion = getItem(position).getDescripcion();
                        String fecha_nota = getItem(position).getFecha_nota();
                        String estado = getItem(position).getEstado();

                        //Declarar las vistas
                        Button CD_Eliminar, CD_Actualizar;

                        //Realizar la conezxión con el diseño
                        dialog.setContentView(R.layout.dialogo_opciones);

                        //inicializar las vistas
                        CD_Eliminar = dialog.findViewById(R.id.CD_Eliminar);
                        CD_Actualizar = dialog.findViewById(R.id.CD_Actualizar);

                        CD_Eliminar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                EliminarNota(id_nota);
                                dialog.dismiss();

                                //Toast.makeText(Listar_Notas.this, "La nota ha sido eliminada", Toast.LENGTH_SHORT).show();
                            }
                        });

                        CD_Actualizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //startActivity(new Intent(Listar_Notas.this, Actualizar_Nota.class));
                                //intent permite enviar datos a la case seleccionada
                                Intent intent = new Intent(Listar_Notas.this,Actualizar_Nota.class);
                                intent.putExtra("id_nota",id_nota);
                                intent.putExtra("uid_usuario",uid_usuario);
                                intent.putExtra("correo_usuario",correo_usuario);
                                intent.putExtra("fecha_registro",fecha_registro);
                                intent.putExtra("titulo",titulo);
                                intent.putExtra("descripcion",descripcion);
                                intent.putExtra("fecha_nota",fecha_nota);
                                intent.putExtra("estado",estado);
                                startActivity(intent);

                                dialog.dismiss();
                                //Toast.makeText(Listar_Notas.this, "Actualizar Nota", Toast.LENGTH_SHORT).show();
                            }
                        });

                        //De esta manera se visualiza el cuadro de dialogo al presionar por cierta cantidad de tiempo
                        dialog.show();
                        //Toast.makeText(Listar_Notas.this, "on item long click", Toast.LENGTH_SHORT).show();
                    }
                });
                return viewHolder_nota;
            }
        };

        linearLayoutManager = new LinearLayoutManager(Listar_Notas.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setReverseLayout(true); //diseño se enliste del ultimo al primer registro
        linearLayoutManager.setStackFromEnd(true); //que inicie la lista desde la parte superior

        recyclerviewNotas.setLayoutManager(linearLayoutManager);
        recyclerviewNotas.setAdapter(firebaseRecyclerAdapter);

    }

    private void EliminarNota(String id_nota) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Listar_Notas.this);
        builder.setTitle("Eliminar Nota");
        builder.setMessage("¿Desea eliminar la Nota?");

        builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //ELIMINAR NOTA
                //si el id_nota que se encuantra en la lista es identica al id_nota que se encuantra en la base de datos
                Query query = BASE_DE_DATOS.orderByChild("id_nota").equalTo(id_nota);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds : snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(Listar_Notas.this, "Nota eliminada", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Listar_Notas.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });


        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(Listar_Notas.this, "Canceado por el usuario", Toast.LENGTH_SHORT).show();

            }
        });

        builder.create().show();

        // Cancelar la notificación persistente cuando se elimina una nota
        eliminarNotificacionPersistente();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter!=null){
            firebaseRecyclerAdapter.startListening();
        }
    }


    private void mostrarNotificacion(String titulo, String descripcion) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, Listar_Notas.class);
        PendingIntent pendingIntent;

        // Comprobar la versión de Android para establecer el flag de mutabilidad adecuado
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        // Construir la notificación
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, "notas_channel")
                    .setContentTitle(titulo)
                    .setContentText(descripcion)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true) // La notificación no se puede despejar deslizando
                    .build();
        } else {
            notification = new Notification.Builder(this)
                    .setContentTitle(titulo)
                    .setContentText(descripcion)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true) // La notificación no se puede despejar deslizando
                    .build();
        }

        // Mostrar la notificación persistente
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void eliminarNotificacionPersistente() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }




    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}