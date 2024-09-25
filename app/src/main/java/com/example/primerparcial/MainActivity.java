package com.example.primerparcial;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.primerparcial.datos.DBHelper;
import com.example.primerparcial.presentacion.categoria.PCategoria;
import com.example.primerparcial.presentacion.cliente.PCliente;
import com.example.primerparcial.presentacion.producto.PProducto;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarBaseDeDatos();

        Button btnClientes = findViewById(R.id.btnCliente);
        btnClientes.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PCliente.class);
            startActivity(intent);
        });

        Button btnCategoria = findViewById(R.id.btnCategoria);
        btnCategoria.setOnClickListener(v -> {
             Intent intent = new Intent(MainActivity.this, PCategoria.class);
             startActivity(intent);
        });

        Button btnProducto = findViewById(R.id.btnProducto);
        btnProducto.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PProducto.class);
            startActivity(intent);
        });

        Button btnOrden = findViewById(R.id.btnOrden);
        Button btnCatalogo = findViewById(R.id.btnCatalogo);

        // Aún no hacen nada, pero se implementarán en el futuro
    }

    private void inicializarBaseDeDatos() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
    }
}
