package com.example.proyecto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    // Nombre de la base de datos
    public static final String Dbname = "Database.db";

    // Constructor de la clase Database
    public Database(Context context) {
        super(context, Dbname, null, 1);
    }

    // Método llamado cuando se crea la base de datos por primera vez
    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        // Crear tabla de usuarios
        MyDB.execSQL("CREATE TABLE Usuarios (Nombre VARCHAR(255) PRIMARY KEY, Password VARCHAR(255))");
        // Crear tabla de recetas
        MyDB.execSQL("CREATE TABLE Recetas (Id INTEGER PRIMARY KEY AUTOINCREMENT, Nombre VARCHAR(255), Descripcion TEXT, Imagen INTEGER)");
        // Eliminar todos los datos de la tabla de recetas
        MyDB.execSQL("DELETE FROM Recetas");
        // Eliminar todos los datos de la tabla de usuarios
        MyDB.execSQL("DELETE FROM Usuarios");
    }

    // Método llamado cuando se actualiza la base de datos
    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int oldVersion, int newVersion) {
        // Eliminar tablas existentes si es necesario
        MyDB.execSQL("DROP TABLE IF EXISTS Usuarios");
        MyDB.execSQL("DROP TABLE IF EXISTS Recetas");
        // Crear tablas nuevamente
        onCreate(MyDB);
    }

    // Método para insertar un nuevo usuario en la tabla Usuarios
    public Boolean insertDataUser(String nombre, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Nombre", nombre);
        contentValues.put("Password", password);

        long resultado =  MyDB.insert("Usuarios", null, contentValues);
        return resultado != -1; // Devuelve true si la inserción fue exitosa, false en caso contrario
    }

    // Método para insertar una nueva receta en la tabla Recetas
    public Boolean insertDataRecipe(String nombre, String descripcion, String imagen) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("Nombre", nombre);
        contentValues.put("Descripcion", descripcion);
        contentValues.put("Imagen", imagen);
        long resultado = MyDB.insert("Recetas", null, contentValues);
        return resultado != -1; // Devuelve true si la inserción fue exitosa, false en caso contrario
    }

    // Método para comprobar si un nombre de usuario ya existe en la tabla Usuarios
    public Boolean comprobarNombre(String nombre){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM Usuarios WHERE Nombre =?", new String[] {nombre});
        return cursor.getCount() > 0; // Devuelve true si el nombre de usuario existe, false en caso contrario
    }

    // Método para comprobar si un nombre de usuario y contraseña coinciden en la tabla Usuarios
    public Boolean comprobarContraseña(String nombre, String password){
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM Usuarios WHERE Nombre = ? AND Password =?", new String[] {nombre, password});
        return cursor.getCount() > 0; // Devuelve true si el nombre de usuario y contraseña coinciden, false en caso contrario
    }

    // Método para obtener todas las recetas de la tabla Recetas
    public Cursor getAllRecetas() {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        return MyDB.rawQuery("SELECT * FROM Recetas", null); // Devuelve un cursor con todas las recetas
    }

    // Método para eliminar todas las recetas de la tabla Recetas
    public void DeleteAllRecetas() {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        MyDB.execSQL("DELETE FROM Recetas"); // Elimina todas las recetas
    }
}
