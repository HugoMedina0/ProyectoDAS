package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    private EditText editTextUsuario;
    private EditText editTextContraseña;
    private Button buttonAceptar;
    Database dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Inicializar el objeto de base de datos
        dbHelper = new Database(this);

        // Asignar los campos de texto y botón de la interfaz a las variables correspondientes
        editTextUsuario = findViewById(R.id.editTextNombreUsuario);
        editTextContraseña = findViewById(R.id.editTextContraseñaRegistro);
        buttonAceptar = findViewById(R.id.aceptarButton);

        // Establecer un listener para el botón "Aceptar" que llamará al método registrarUsuario() al hacer clic
        buttonAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
    }

    // Método para registrar un nuevo usuario
    private void registrarUsuario() {
        // Obtener el nombre de usuario y la contraseña ingresados por el usuario
        String nombreUsuario = editTextUsuario.getText().toString().trim();
        String contrasena = editTextContraseña.getText().toString().trim();

        // Verificar si los campos están vacíos
        if (nombreUsuario.isEmpty() || contrasena.isEmpty()) {
            // Mostrar un mensaje de advertencia si algún campo está vacío
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si el nombre de usuario ya existe en la base de datos
        if (dbHelper.comprobarNombre(nombreUsuario)) {
            // Mostrar un mensaje de error si el nombre de usuario ya está en uso
            Toast.makeText(this, "El nombre de usuario ya existe", Toast.LENGTH_SHORT).show();
        } else {
            // Insertar el nuevo usuario en la base de datos
            boolean insertado = dbHelper.insertDataUser(nombreUsuario, contrasena);
            if (insertado) {
                // Mostrar un mensaje de éxito si el usuario se registra correctamente
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                // Redirigir a la actividad de inicio de sesión
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Finalizar la actividad actual para evitar volver a ella desde la actividad de inicio de sesión
            } else {
                // Mostrar un mensaje de error si ocurre algún problema durante el registro del usuario
                Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
