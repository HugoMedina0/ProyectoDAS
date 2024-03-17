package com.example.proyecto;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class RecetasActivity extends AppCompatActivity {

    // Constantes para solicitar permisos y para el código de solicitud de subir receta
    private static final int REQUEST_CODE_SUBIR_RECETA = 123;
    private static final int PERMISSION_REQUEST_CODE = 456;

    // Variables para RecyclerView, adaptador de recetas y base de datos
    private RecyclerView recyclerView;
    private RecetasAdapter adapter;
    private Database dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recetas);

        // Inicialización de variables y configuración de la barra de herramientas
        recyclerView = findViewById(R.id.recyclerView);
        dbHelper = new Database(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Verificar si se tienen permisos para acceder a la galería
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen permisos, solicitarlos al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            // Si se tienen permisos, cargar las recetas
            cargarRecetas();
        }
    }

    // Método para crear el menú de opciones en la barra de herramientas
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Método para manejar las acciones del menú de opciones
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.subirReceta) {
            // Si se selecciona la opción de subir receta, iniciar la actividad de subir receta
            Intent intent = new Intent(this, SubirRecetaActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SUBIR_RECETA);
            return true;
        } else if (id == R.id.exportar) {
            // Si se selecciona la opción de exportar, llamar al método para exportar recetas a un archivo de texto
            exportarRecetasAFicheroTexto();
            return true;
        }
        return true;
    }

    // Método para cargar las recetas desde la base de datos y configurar el RecyclerView
    public void cargarRecetas() {
        // Obtener todas las recetas de la base de datos
        Cursor cursor = dbHelper.getAllRecetas();

        // Configurar RecyclerView con el adaptador y el layout manager
        adapter = new RecetasAdapter(this, cursor, dbHelper);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    // Método para manejar el resultado de la actividad de subir receta
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SUBIR_RECETA) {
            if (resultCode == RESULT_OK) {
                // Si la actividad de subir receta devuelve un resultado OK, recargar las recetas
                Log.d("RecetasActivity", "onActivityResult: Result OK, reloading recipes...");
                cargarRecetas();
            }
        }
    }

    // Método para manejar la respuesta a la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el usuario concede el permiso, cargar recetas
                cargarRecetas();
            } else {
                // Si el usuario niega el permiso, mostrar un mensaje de error o manejarlo según tu lógica
                Log.d("RecetasActivity", "Permiso denegado para acceder a la galería");
                Toast.makeText(this, "Permiso denegado para acceder a la galería", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para exportar las recetas a un archivo de texto en el almacenamiento externo
    private void exportarRecetasAFicheroTexto() {
        // Verificar si el almacenamiento externo está disponible para escritura
        if (isExternalStorageWritable()) {
            try {
                // Obtener todas las recetas de la base de datos
                Cursor cursor = dbHelper.getAllRecetas();

                // Crear un ContentValues para almacenar la información del archivo
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "recetas.txt");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

                // Utilizar MediaStore para crear una solicitud de escritura
                Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL), contentValues);

                // Abrir un OutputStream para escribir en el archivo
                OutputStream outputStream = getContentResolver().openOutputStream(uri);

                // Escribir las recetas en el archivo de texto
                if (outputStream != null && cursor != null && cursor.moveToFirst()) {
                    do {
                        @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
                        @SuppressLint("Range") String descripcion = cursor.getString(cursor.getColumnIndex("Descripcion"));
                        String receta = "Nombre: " + nombre + "\nDescripción: " + descripcion + "\n\n";
                        outputStream.write(receta.getBytes());
                    } while (cursor.moveToNext());
                    cursor.close();
                    outputStream.close();
                    Toast.makeText(this, "Recetas exportadas correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No se pudieron exportar las recetas", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al exportar las recetas", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "El almacenamiento externo no está disponible para escribir", Toast.LENGTH_SHORT).show();
        }
    }

    // Método auxiliar para verificar si el almacenamiento externo está disponible para escritura
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
