package com.example.primerparcial.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appDB.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableClientes);
        db.execSQL(createTableCategorias);
        db.execSQL(CrearTablaPosiciones);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clientes");
        db.execSQL("DROP TABLE IF EXISTS categorias");
        db.execSQL("DROP TABLE IF EXISTS posiciones");
        onCreate(db);
    }

    private static final String createTableClientes = "CREATE TABLE clientes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "nroTelefono TEXT NOT NULL, " +
            "imagenPath TEXT)";

    private static final String createTableCategorias = "CREATE TABLE categorias (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL)";

    private static final String CrearTablaPosiciones = "CREATE TABLE posiciones (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "nombre TEXT NOT NULL, " +
            "urlMapa  TEXT, " +
            "referencia TEXT, " +
            "idCliente INTEGER , " +
            "FOREIGN KEY(idCliente) REFERENCES clientes(id)" +
            ");";

}