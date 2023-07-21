package com.stivenvacacela.fibert_lit.AgregarNota;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
//import com.stivenvacacela.fibert_lit.MenuPrincipal;
import com.stivenvacacela.fibert_lit.Objetos.Nota;
import com.stivenvacacela.fibert_lit.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
public class Agregar_Nota extends AppCompatActivity {

    TextView Uid_Usuario, Correo_usuario, Fecha_hora_actual, Fecha, Estado;
    EditText Titulo, Descripcion;
    Button Btn_Calendario;

    int dia, mes , anio;

    DatabaseReference BD_Firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_nota);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        InicializarVariables();
        ObtenerDatos();
        Obtener_Fecha_Hora_Actual();

        //agregar un evento al boton calendario
        Btn_Calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendario = Calendar.getInstance();

                dia = calendario.get(Calendar.DAY_OF_MONTH);
                mes = calendario.get(Calendar.MONTH);
                anio = calendario.get(Calendar.YEAR);

                //se crea el objeto calendario 'datePickerDialog'

                // Fecha seleccionada, mostrar el formato
                DatePickerDialog datePickerDialog = new DatePickerDialog(Agregar_Nota.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int AnioSeleccionado, int MesSeleccionado, int DiaSeleccionado) {

                        //formatear los datos (dar forma )
                        String diaFormateado, mesFormateado;

                        //OBTENER DIA
                        if (DiaSeleccionado < 10){
                            diaFormateado = "0"+String.valueOf(DiaSeleccionado);
                            // Antes: 6/07/2023 -  Ahora 09/11/2022
                        }else {
                            diaFormateado = String.valueOf(DiaSeleccionado);
                            //Ejemplo 06/07/2023
                        }

                        //OBTENER EL MES
                        int Mes = MesSeleccionado + 1;

                        if (Mes < 10){
                            mesFormateado = "0"+String.valueOf(Mes);
                            // Antes: 09/7/2023 -  Ahora 09/07/2023
                        }else {
                            mesFormateado = String.valueOf(Mes);
                            //Ejemplo 13/10/2023 - 13/11/2023 - 13/12/2022

                        }

                        //Setear fecha en TextView
                        Fecha.setText(diaFormateado + "/" + mesFormateado + "/"+ AnioSeleccionado);

                    }
                }
                        ,anio,mes,dia);
                datePickerDialog.show();

            }
        });


    }

    private void InicializarVariables(){
        Uid_Usuario = findViewById(R.id.Uid_Usuario);
        Correo_usuario = findViewById(R.id.Correo_usuario);
        Fecha_hora_actual = findViewById(R.id.Fecha_hora_actual);
        Fecha = findViewById(R.id.Fecha);
        Estado = findViewById(R.id.Estado);

        Titulo = findViewById(R.id.Titulo);
        Descripcion = findViewById(R.id.Descripcion);
        Btn_Calendario = findViewById(R.id.Btn_Calendario);

        BD_Firebase = FirebaseDatabase.getInstance().getReference();
    }

    private void ObtenerDatos(){
        //recuperar los datos desde el MenuPrincipla
        String uid_recuperado = getIntent().getStringExtra("Uid");
        String correo_recuperado = getIntent().getStringExtra("Correo");

        //le asignamos los valores recuperados a los TextView
        Uid_Usuario.setText(uid_recuperado);
        Correo_usuario.setText(correo_recuperado);
    }

    private void Obtener_Fecha_Hora_Actual(){
        String Fecha_hora_registro = new SimpleDateFormat("dd-MM-yyyy/HH:mm:ss a",
                Locale.getDefault()).format(System.currentTimeMillis());
        //EJEMPLO: 06-07-2023/06:30:20 pm
        Fecha_hora_actual.setText(Fecha_hora_registro);
    }

    private void Agregar_Nota_1(){

        //Obtener los datos
        String uid_usuario = Uid_Usuario.getText().toString();
        String correo_usuario = Correo_usuario.getText().toString();
        String fecha_hora_actual = Fecha_hora_actual.getText().toString();
        String titulo = Titulo.getText().toString();
        String descripcion = Descripcion.getText().toString();
        String fecha = Fecha.getText().toString();
        String estado = Estado.getText().toString();

        //Validar datos
        if (!uid_usuario.equals("") && !correo_usuario.equals("") && !fecha_hora_actual.equals("") &&
                !titulo.equals("") && !descripcion.equals("") && ! fecha.equals("") && !estado.equals("")){

            Nota nota = new Nota(correo_usuario+"/"+fecha_hora_actual,
                    uid_usuario,
                    correo_usuario,
                    fecha_hora_actual,
                    titulo,
                    descripcion,
                    fecha,
                    estado);

            String Nota_usuario = BD_Firebase.push().getKey();
            //Establecer el nombre de la BD
            String Nombre_BD = "Notas_Publicadas";


            if (Nota_usuario != null) {
                BD_Firebase.child(Nombre_BD).child(Nota_usuario).setValue(nota);
                Toast.makeText(this, "Se ha agregado la nota exitosamente", Toast.LENGTH_SHORT).show();
                onBackPressed();

                // Mostrar la notificación
                showNotification(titulo, descripcion);
            } else {
                Toast.makeText(this, "Error al generar la clave de la nota", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            Toast.makeText(this, "Llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void showNotification(String titulo, String descripcion) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtils.showNotification(this, titulo, descripcion);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //especificar un menu de opciones dentro de la actividad
        //lo que perimite que se visualice cuando a app se ejecute
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_agregar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //agrega una accion al botos GuardarNota
        if (item.getItemId() ==R.id.Agregar_Nota_BD) {
            Agregar_Nota_1();
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean onSupportNavigateUp() {
        //método para que la flecha me lleva hacia atras
        onBackPressed();
        return super.onSupportNavigateUp();
}
}