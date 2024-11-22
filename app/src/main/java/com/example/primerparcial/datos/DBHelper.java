package com.example.primerparcial.datos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "primerparcial.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public SQLiteDatabase getDatabase() {
        return this.getWritableDatabase();
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
            "idRepartidor INTEGER, " +
            "FOREIGN KEY(idCliente) REFERENCES clientes(id), " +
            "FOREIGN KEY(idRepartidor) REFERENCES repartidores(id));";

    private static final String crearTablaDetalleOrden = "CREATE TABLE detalleOrden (" +
            "idOrden INTEGER NOT NULL, " +
            "idProducto INTEGER NOT NULL, " +
            "cantidad INTEGER NOT NULL, " +
            "precio REAL NOT NULL, " +
            "PRIMARY KEY(idOrden, idProducto), " +
            "FOREIGN KEY(idOrden) REFERENCES ordenes(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
            "FOREIGN KEY(idProducto) REFERENCES productos(id) ON DELETE CASCADE ON UPDATE CASCADE);";

    private static final String crearTablaCatalogos = "CREATE TABLE catalogos (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "nombre TEXT NOT NULL, " +
            "fecha TEXT NOT NULL, " +
            "descripcion TEXT NOT NULL);";

    private static final String crearTablaCatalogoProducto = "CREATE TABLE catalogoProducto (" +
            "idCatalogo INTEGER NOT NULL, " +
            "idProducto INTEGER NOT NULL, " +
            "nota TEXT, " +
            "PRIMARY KEY(idCatalogo, idProducto), " +
            "FOREIGN KEY(idCatalogo) REFERENCES catalogos(id) ON DELETE CASCADE ON UPDATE CASCADE, " +
            "FOREIGN KEY(idProducto) REFERENCES productos(id) ON DELETE CASCADE ON UPDATE CASCADE);";


    public void insertarDatosIniciales() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar si ya hay datos en la tabla clientes
        boolean datosClientesExistentes = hayDatosEnTabla(db, "clientes");
        boolean datosCategoriasExistentes = hayDatosEnTabla(db, "categorias");
        boolean datosProductosExistentes = hayDatosEnTabla(db, "productos");
        boolean datosRepartidoresExistentes = hayDatosEnTabla(db, "repartidores");
        boolean datosPosicionesExistentes = hayDatosEnTabla(db, "posiciones");


        if (!datosClientesExistentes) {
            // Insertar datos de ejemplo para clientes con imágenes
            db.execSQL("INSERT INTO clientes " +
                    "(nombre, nroTelefono, imagenPath) VALUES ('Rodrigo Jaldin', '73982631', 'android.resource://com.example.primerparcial/drawable/cliente1')");
            db.execSQL("INSERT INTO clientes " +
                    "(nombre, nroTelefono, imagenPath) VALUES ('Jesus Jaldin', '71396966', 'android.resource://com.example.primerparcial/drawable/cliente2')");
        }

        if (!datosCategoriasExistentes) {
            // Insertar datos de ejemplo para categorías
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Camisas')");
            db.execSQL("INSERT INTO categorias (nombre) VALUES ('Zapatos')");
        }

        if (!datosProductosExistentes) {
            // Insertar datos de ejemplo para productos en la categoría 'Camisas'
            db.execSQL("INSERT INTO productos " +
                    "(nombre, descripcion, precio, imagenPath, stock, idCategoria) " +
                    "VALUES ('Camisa 1', 'Descripción Camisa 1', 15.0, 'android.resource://com.example.primerparcial/drawable/camisa1', 50, 1)");
            db.execSQL("INSERT INTO productos " +
                    "(nombre, descripcion, precio, imagenPath, stock, idCategoria) " +
                    "VALUES ('Camisa 2', 'Descripción Camisa 2', 18.0, 'android.resource://com.example.primerparcial/drawable/camisa2', 60, 1)");
            db.execSQL("INSERT INTO productos " +
                    "(nombre, descripcion, precio, imagenPath, stock, idCategoria) " +
                    "VALUES ('Camisa 3', 'Descripción Camisa 3', 20.0, 'android.resource://com.example.primerparcial/drawable/camisa3', 40, 1)");
            db.execSQL("INSERT INTO productos " +
                    "(nombre, descripcion, precio, imagenPath, stock, idCategoria) " +
                    "VALUES ('Camisa 4', 'Descripción Camisa 4', 22.0, 'android.resource://com.example.primerparcial/drawable/camisa4', 30, 1)");

            // Insertar datos de ejemplo para productos en la categoría 'Zapatos'
            db.execSQL("INSERT INTO productos (nombre, descripcion, precio, imagenPath, stock, idCategoria) VALUES ('Zapato 1', 'Descripción Zapato 1', 25.0, 'android.resource://com.example.primerparcial/drawable/zapato1', 10, 2)");
            db.execSQL("INSERT INTO productos (nombre, descripcion, precio, imagenPath, stock, idCategoria) VALUES ('Zapato 2', 'Descripción Zapato 1', 45.0, 'android.resource://com.example.primerparcial/drawable/zapato2', 15, 2)");
            db.execSQL("INSERT INTO productos (nombre, descripcion, precio, imagenPath, stock, idCategoria) VALUES ('Zapato 3', 'Descripción Zapato 1', 75.0, 'android.resource://com.example.primerparcial/drawable/zapato3', 10, 2)");
            db.execSQL("INSERT INTO productos (nombre, descripcion, precio, imagenPath, stock, idCategoria) VALUES ('Zapato 4', 'Descripción Zapato 1', 125.0, 'android.resource://com.example.primerparcial/drawable/zapato4', 10, 2)");

        }

        if (!datosRepartidoresExistentes) {
            // Insertar datos de ejemplo para repartidores
            db.execSQL("INSERT INTO repartidores (nombre, nroTelefono) VALUES ('Milton Jaldin', '71396966')");
            db.execSQL("INSERT INTO repartidores (nombre, nroTelefono) VALUES ('Sarely Columba', '63588838')");
        }

        if (!datosPosicionesExistentes) {
            // Insertar posiciones para los clientes
            Cursor cursorClientes = db.rawQuery("SELECT id FROM clientes", null);
            if (cursorClientes.moveToFirst()) {
                do {
                    int idCliente = cursorClientes.getInt(0);

                    // Insertar una posición para cada cliente
                    db.execSQL("INSERT INTO posiciones (nombre, urlMapa, referencia, idCliente) VALUES " +
                            "('Ubicación Cliente " + idCliente + "', 'http://maps.google.com/?q=-17.3935,-66.1456', 'Referencia Cliente " + idCliente + "', " + idCliente + ")");
                } while (cursorClientes.moveToNext());
            }
            cursorClientes.close();
        }

    }

    private boolean hayDatosEnTabla(SQLiteDatabase db, String nombreTabla) {
        boolean existe = false;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + nombreTabla, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                existe = cursor.getInt(0) > 0;
            }
            cursor.close();
        }
        return existe;
    }
}

