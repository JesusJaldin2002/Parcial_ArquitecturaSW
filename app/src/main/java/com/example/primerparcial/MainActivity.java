package com.example.primerparcial;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.primerparcial.datos.DBHelper;
import com.example.primerparcial.presentacion.cliente.PCliente;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarBaseDeDatos();

        Button btnClientes = findViewById(R.id.btnCliente);
        btnClientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PCliente.class);
                startActivity(intent);
            }
        });

        Button btnRepartidores = findViewById(R.id.nuevoRepartidor);
        Button btnCategorias = findViewById(R.id.nuevaCategoria);
        Button btnProductos = findViewById(R.id.nuevoProducto);
        Button btnOrdenes = findViewById(R.id.nuevaOrden);

        // Aún no hacen nada, pero se implementarán en el futuro
    }

    private void inicializarBaseDeDatos() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
    }
}
