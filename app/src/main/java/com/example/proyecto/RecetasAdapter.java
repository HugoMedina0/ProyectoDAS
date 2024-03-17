package com.example.proyecto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class RecetasAdapter extends RecyclerView.Adapter<RecetasAdapter.RecetaViewHolder> {

    // Variables de clase
    private Cursor cursor;
    private Context context;
    private Database dbHelper;

    // Constructor de la clase RecetasAdapter
    public RecetasAdapter(Context context, Cursor cursor, Database dbHelper) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = dbHelper; // Inicializar el dbHelper
    }

    // Método para crear una nueva instancia de RecetaViewHolder
    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el diseño de la tarjeta de receta y devolver una nueva instancia de RecetaViewHolder
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cardview_receta, parent, false);
        return new RecetaViewHolder(view);
    }

    // Método para vincular los datos de una receta a la vista correspondiente en RecetaViewHolder
    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        // Verificar si el cursor puede moverse a la posición especificada
        if (!cursor.moveToPosition(position)) {
            return;
        }

        // Obtener los valores de las columnas de la receta
        @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex("Nombre"));
        @SuppressLint("Range") String descripcion = cursor.getString(cursor.getColumnIndex("Descripcion"));
        @SuppressLint("Range") String imagen = cursor.getString(cursor.getColumnIndex("Imagen")); // Obtener el ID de la imagen desde la base de datos

        // Establecer el nombre y la descripción de la receta en las vistas correspondientes
        holder.textViewNombreReceta.setText(nombre);
        holder.textViewDescripcionReceta.setText(descripcion);

        // Verificar si la receta tiene una URI de imagen o una ID de imagen
        if (imagen != null && imagen.startsWith("content://")) {
            // La receta tiene una URI de imagen, cargarla desde la URI
            holder.imageViewReceta.setImageURI(Uri.parse(imagen));
        } else {
            // La receta tiene una ID de imagen, cargar la imagen predeterminada utilizando la ID
            int drawableId = context.getResources().getIdentifier(imagen, "drawable", context.getPackageName());
            holder.imageViewReceta.setImageResource(drawableId);
        }
    }

    // Método para obtener la cantidad total de elementos en el conjunto de datos
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    // Método para intercambiar el cursor actual por uno nuevo
    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }

        cursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    // Clase estática para representar una vista de tarjeta de receta
    public static class RecetaViewHolder extends RecyclerView.ViewHolder {
        // Vistas de la tarjeta de receta
        ImageView imageViewReceta;
        TextView textViewNombreReceta, textViewDescripcionReceta;

        // Constructor de la clase RecetaViewHolder
        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar las vistas de la tarjeta de receta
            imageViewReceta = itemView.findViewById(R.id.imageViewReceta);
            textViewNombreReceta = itemView.findViewById(R.id.textViewNombreReceta);
            textViewDescripcionReceta = itemView.findViewById(R.id.textViewDescripcionReceta);
        }
    }

    // Método privado para actualizar la ventana con los datos actualizados
    private void actualizarVentana() {
        // Obtener un nuevo cursor con los datos actualizados de la base de datos
        Cursor nuevoCursor = dbHelper.getAllRecetas();

        // Actualizar el cursor del adaptador llamando al método swapCursor
        swapCursor(nuevoCursor);
    }
}
