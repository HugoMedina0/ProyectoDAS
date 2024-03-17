package com.example.proyecto;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class LoginActivity extends AppCompatActivity {

    // Constante para solicitar permisos
    private static final int PERMISSION_REQUEST_CODE = 1001;

    // Elementos de la interfaz de usuario
    EditText editTextUsuario;
    EditText editTextContrasena;

    // Instancia de la base de datos
    Database dbHelper;

    // Identificador del canal de notificación
    private static final String CHANNEL_ID = "channel_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar la base de datos
        dbHelper = new Database(this);
        dbHelper.DeleteAllRecetas();
        dbHelper.insertDataUser("root", "root");
        dbHelper.insertDataRecipe("Huevos al plato con verduras y jamón", "¡Con esta sencillísima receta de huevos al plato con verduras triunfarás seguro! Lleva pimientos, judías, cebolla, jamón serrano y el huevo por encima, que se hornea.", "huevosalplato");
        dbHelper.insertDataRecipe("Crema de calabaza (asada al horno) con almendras", "Hemos preparado una crema de calabaza con cebolleta, asándola primero en el horno. Lleva también semillas de sésamo y curry y por encima se añaden almendras fileteadas.", "cremadecalabaza");
        dbHelper.insertDataRecipe("Lubina al vapor con patatas y mojo verde", "Hoy te proponemos una lubina al vapor. Se acompaña con patatas y se riega con un mojo verde de ajos triturados, cilantro, perejil, aceite, vinagre y comino.", "lubinaalvapor");
        dbHelper.insertDataRecipe("Pavo con salsa de queso", "Hoy te proponemos una receta ligerita ideal para una cena rápida: unos solomillos de carne de pavo con una salsa de queso light y verduras de acompañamiento.", "pavoconsalsa");
        dbHelper.insertDataRecipe("Potaje de garbanzos", "Si quieres preparar un potaje de legumbres rápido, pero muy sabroso, toma nota de esta receta de garbanzos. Los agregamos cocidos y llevan un extra de sabor con el comino, la cúrcuma y el curry.", "garbanzos");

        // Inicializar los elementos de la interfaz
        editTextUsuario = findViewById(R.id.editTextUsuario);
        editTextContrasena = findViewById(R.id.editTextContraseña);

        // Configurar el OnClickListener para el botón de inicio de sesión
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = editTextUsuario.getText().toString();
                String contrasena = editTextContrasena.getText().toString();
                Log.d("LoginActivity", "Usuario: " + usuario + ", Contraseña: " + contrasena);

                // Comprobar la contraseña
                Boolean comprobar = dbHelper.comprobarContraseña(usuario, contrasena);
                if (comprobar) {
                    // Mostrar notificación de bienvenida
                    showWelcomeNotification(usuario);
                    // Iniciar la actividad de recetas
                    Intent intent = new Intent(getApplicationContext(), RecetasActivity.class);
                    startActivity(intent);
                } else {
                    // Mostrar diálogo de inicio de sesión incorrecto
                    showLoginResultDialog("Usuario o contraseña incorrectos");
                }
            }
        });

        // Configurar el OnClickListener para el botón de registro
        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de registro
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        // Crear el canal de notificación
        createNotificationChannel();
    }

    // Método para mostrar el diálogo de resultado del inicio de sesión
    private void showLoginResultDialog(String message) {
        LoginResultDialogFragment dialog = LoginResultDialogFragment.newInstance(message);
        dialog.show(getSupportFragmentManager(), "LoginResultDialogFragment");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Limpiar los campos de texto
        editTextUsuario.setText("");
        editTextContrasena.setText("");
    }

    // Método para crear un canal de notificación
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Configurar el canal de notificación
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // Registrar el canal en el NotificationManager
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // Método para mostrar la notificación de bienvenida
    private void showWelcomeNotification(String nombreUsuario) {
        // Configurar la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Bienvenido")
                .setContentText("¡Bienvenido de nuevo, "+ nombreUsuario + "!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Verificar si se tiene el permiso necesario para mostrar notificaciones
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // El permiso no ha sido concedido, solicitar permiso al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permiso concedido, mostrar la notificación
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, builder.build());
        }
    }
}
