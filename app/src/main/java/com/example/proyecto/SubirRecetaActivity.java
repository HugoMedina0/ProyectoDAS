package com.example.proyecto;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SubirRecetaActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private EditText editTextNombre;
    private EditText editTextDescripcion;
    private Button buttonSubir;
    private Uri imageUri;
    private Database dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_receta);

        // Inicializar la instancia de la base de datos
        dbHelper = new Database(this);

        // Asignar los elementos de la interfaz a las variables correspondientes
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        buttonSubir = findViewById(R.id.buttonSubir);

        // Configurar un OnClickListener para el botón "Subir"
        buttonSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();
            }
        });

        // Configurar un OnClickListener para el botón "Agregar Receta"
        Button buttonAgregarReceta = findViewById(R.id.buttonAgregarReceta);
        buttonAgregarReceta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarReceta();
            }
        });
    }

    // Método para abrir la galería de imágenes
    private void abrirGaleria() {
        // Verificar si se tienen permisos para acceder a la galería
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen permisos, solicitarlos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST_CODE);
        } else {
            // Si se tienen permisos, abrir la galería
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        }
    }

    // Método que se ejecuta después de seleccionar una imagen de la galería
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            // Obtener la URI de la imagen seleccionada
            imageUri = data.getData();
            // Convertir la URI en un Bitmap
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // Convertir el bitmap a URI y almacenarlo internamente
                imageUri = getImageUriFromBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Establecer el Bitmap en el ImageView
            if (bitmap != null) {
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    // Método para convertir un Bitmap en un URI y almacenarlo internamente
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    // Método para agregar una nueva receta
    private void agregarReceta() {
        // Obtener el nombre y la descripción de la receta ingresados por el usuario
        String nombreReceta = editTextNombre.getText().toString().trim();
        String descripcionReceta = editTextDescripcion.getText().toString().trim();

        // Obtener la URI de la imagen seleccionada
        String uriString = imageUri.toString();

        // Insertar la nueva receta en la base de datos
        boolean insertado = dbHelper.insertDataRecipe(nombreReceta, descripcionReceta, uriString);

        // Verificar si se han completado todos los campos
        if (nombreReceta.isEmpty() || descripcionReceta.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar un mensaje de éxito o error según el resultado de la inserción en la base de datos
        if (insertado) {
            Toast.makeText(this, "Receta agregada correctamente", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK); // Establecer el resultado como OK antes de finalizar la actividad
            finish(); // Cerrar la actividad después de agregar la receta
        } else {
            Toast.makeText(this, "Error al agregar la receta", Toast.LENGTH_SHORT).show();
        }
    }

    // Método que se llama cuando se solicitan permisos al usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el usuario concede el permiso, abrir la galería
                Log.d("SubirRecetasActivity", "onActivityResult: Result received from SubirRecetaActivity");
                abrirGaleria();
            } else {
                // Si el usuario niega el permiso, mostrar un mensaje de error
                Toast.makeText(this, "Permiso denegado para acceder al almacenamiento externo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
