package com.example.primerparcial.datos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "primerparcial.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(crearTablaClientes);
        db.execSQL(crearTablaCategorias);
        db.execSQL(crearTablaPosiciones);
        db.execSQL(crearTablaProductos);
        db.execSQL(crearTablaRepartidores);
        db.execSQL(crearTablaOrdenes);
        db.execSQL(crearTablaDetalleOrden);
        db.execSQL(crearTablaCatalogos);
        db.execSQL(crearTablaCatalogoProducto);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS clientes");
        db.execSQL("DROP TABLE IF EXISTS categorias");
        db.execSQL("DROP TABLE IF EXISTS posiciones");
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS repartidores");
        db.execSQL("DROP TABLE IF EXISTS ordenes");
        db.execSQL("DROP TABLE IF EXISTS detalle_orden");
        db.execSQL("DROP TABLE IF EXISTS catalogos");
        db.execSQL("DROP TABLE IF EXISTS catalogo_producto");
        onCreate(db);
    }

    private static final String crearTablaClientes = "CREATE TABLE clientes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "nroTelefono TEXT NOT NULL, " +
            "imagenPath TEXT)";

    private static final String crearTablaCategorias = "CREATE TABLE categorias (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL)";

    private static final String crearTablaPosiciones = "CREATE TABLE posiciones (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "nombre TEXT NOT NULL, " +
            "urlMapa  TEXT, " +
            "referencia TEXT, " +
            "idCliente INTEGER NOT NULL, " +
            "FOREIGN KEY(idCliente) REFERENCES clientes(id) ON DELETE CASCADE ON UPDATE CASCADE" +
            ");";

    private static final String crearTablaProductos = "CREATE TABLE productos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "descripcion TEXT NOT NULL, " +
            "precio REAL NOT NULL, " +
            "imagenPath TEXT, " +
            "stock INTEGER NOT NULL," +
            "idCategoria INTEGER NOT NULL, " +
            "FOREIGN KEY(idCategoria) REFERENCES categorias(id) ON DELETE CASCADE ON UPDATE CASCADE" +
            ");";

    private static final String crearTablaRepartidores = "CREATE TABLE repartidores (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "nroTelefono TEXT NOT NULL)";

    private static final String crearTablaOrdenes = "CREATE TABLE ordenes (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "estado TEXT NOT NULL, " +
            "total REAL NOT NULL, " +
            "idCliente INTEGER NOT NULL, " +
            "idRepartidor INTEGER NOT NULL, " +
            "FOREIGN KEY(idCliente) REFERENCES clientes(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
            "FOREIGN KEY(idRepartidor) REFERENCES repartidores(id) ON DELETE CASCADE ON UPDATE CASCADE);";

    private static final String crearTablaDetalleOrden = "CREATE TABLE detalleOrden (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "cantidad INTEGER NOT NULL, " +
            "precio REAL NOT NULL, " +
            "idOrden INTEGER NOT NULL, " +
            "idProducto INTEGER NOT NULL, " +
            "FOREIGN KEY(idOrden) REFERENCES ordenes(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
            "FOREIGN KEY(idProducto) REFERENCES productos(id) ON DELETE CASCADE ON UPDATE CASCADE);";

    private static final String crearTablaCatalogos = "CREATE TABLE catalogos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "descripcion TEXT NOT NULL);";

    private static final String crearTablaCatalogoProducto = "CREATE TABLE catalogoProducto (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nota TEXT, " +
            "idCatalogo INTEGER NOT NULL, " +
            "idProducto INTEGER NOT NULL, " +
            "FOREIGN KEY(idCatalogo) REFERENCES catalogos(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
            "FOREIGN KEY(idProducto) REFERENCES productos(id) ON DELETE CASCADE ON UPDATE CASCADE);";

}