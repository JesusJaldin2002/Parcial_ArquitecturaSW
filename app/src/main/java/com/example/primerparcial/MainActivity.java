package com.example.primerparcial;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.primerparcial.datos.DBHelper;
import com.example.primerparcial.presentacion.catalogo.PCatalogo;
import com.example.primerparcial.presentacion.categoria.PCategoria;
import com.example.primerparcial.presentacion.cliente.PCliente;
import com.example.primerparcial.presentacion.orden.POrden;
import com.example.primerparcial.presentacion.producto.PProducto;
import com.example.primerparcial.presentacion.repartidor.PRepartidor;
import com.example.primerparcial.presentacion.reporte.PReporte;

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
        btnOrden.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, POrden.class);
            startActivity(intent);
        });

        Button btnCatalogo = findViewById(R.id.btnCatalogo);
        btnCatalogo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PCatalogo.class);
            startActivity(intent);
        });

        Button btnRepartidor = findViewById(R.id.btnRepartidor);
        btnRepartidor.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PRepartidor.class);
            startActivity(intent);
        });

        Button btnReporte = findViewById(R.id.btnReporte);
        btnReporte.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PReporte.class);
            startActivity(intent);
        });

        // Aún no hacen nada, pero se implementarán en el futuro
    }

    private void inicializarBaseDeDatos() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        dbHelper.insertarDatosIniciales();
    }
}
