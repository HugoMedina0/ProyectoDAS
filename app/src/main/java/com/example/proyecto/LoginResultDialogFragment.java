package com.example.proyecto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class LoginResultDialogFragment extends DialogFragment {

    // Variable para almacenar el mensaje del diálogo
    private String message;

    // Método estático para crear una nueva instancia del fragmento de diálogo
    public static LoginResultDialogFragment newInstance(String message) {
        LoginResultDialogFragment fragment = new LoginResultDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }

    // Método para crear el diálogo
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Obtener el mensaje del argumento pasado
        if (getArguments() != null) {
            message = getArguments().getString("message");
        }

        // Crear un AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Configurar el mensaje del diálogo y el botón positivo
        builder.setMessage(message)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Cierra el diálogo
                        dismiss();
                    }
                });

        // Crear y devolver el diálogo
        return builder.create();
    }
}
