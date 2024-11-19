package com.example.primerparcial.presentacion.reporte;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.primerparcial.R;
import com.example.primerparcial.negocio.reporte.NReporte;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class PReporte extends AppCompatActivity {
    private NReporte nReporte;
    private EditText etFechaInicio, etFechaFin;
    private TextView tvResultado;
    private Button btnGenerar, btnVolverAtras, btnGuardarPDF;
    private Spinner spinnerTipoReporte, spinnerCliente;
    private String tipoReporteSeleccionado, nombreClienteSeleccionado;
    private Integer idClienteSeleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);

        nReporte = new NReporte(this);

        // Inicializar vistas
        etFechaInicio = findViewById(R.id.etFechaInicio);
        etFechaFin = findViewById(R.id.etFechaFin);
        tvResultado = findViewById(R.id.tvResultado);
        btnGenerar = findViewById(R.id.btnGenerar);
        btnVolverAtras = findViewById(R.id.btnVolverAtras);
        btnGuardarPDF = findViewById(R.id.btnGuardarPDF);
        spinnerTipoReporte = findViewById(R.id.spinnerTipoReporte);
        spinnerCliente = findViewById(R.id.spinnerCliente);

        // Configurar Spinner de tipo de reporte
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipo_reportes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoReporte.setAdapter(adapter);
        spinnerTipoReporte.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tipoReporteSeleccionado = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tipoReporteSeleccionado = "Ordenes";
            }
        });

        // Configurar Spinner de clientes
        List<Map<String, String>> clientes = nReporte.obtenerClientes();
        List<String> nombresClientes = new ArrayList<>();
        nombresClientes.add("Seleccione un cliente (opcional)");

        for (Map<String, String> cliente : clientes) {
            nombresClientes.add(cliente.get("nombre"));
        }

        ArrayAdapter<String> clienteAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, nombresClientes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    // Estilo gris para el primer elemento
                    textView.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    // Estilo normal para otros elementos
                    textView.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    // Estilo gris para el primer elemento en el desplegable
                    textView.setTextColor(getResources().getColor(R.color.gray));
                } else {
                    // Estilo normal para otros elementos
                    textView.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }
        };
        clienteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCliente.setAdapter(clienteAdapter);

        spinnerCliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    idClienteSeleccionado = null;
                    nombreClienteSeleccionado = null;
                } else {
                    idClienteSeleccionado = Integer.parseInt(clientes.get(position - 1).get("id")); // Ajustar índice
                    nombreClienteSeleccionado = clientes.get(position - 1).get("nombre");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                idClienteSeleccionado = null;
                nombreClienteSeleccionado = null;
            }
        });

        // Configurar DatePickers para fechas
        configurarDatePicker(etFechaInicio);
        configurarDatePicker(etFechaFin);

        btnGenerar.setOnClickListener(v -> generarReporte());
        btnVolverAtras.setOnClickListener(v -> finish());
        btnGuardarPDF.setOnClickListener(v -> generarYAbrirPDF());
    }

    private void configurarDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                String fechaSeleccionada = String.format("%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                editText.setText(fechaSeleccionada);
            }, year, month, day);
            datePickerDialog.show();
        });
    }

    private void generarReporte() {
        String fechaInicio = etFechaInicio.getText().toString();
        String fechaFin = etFechaFin.getText().toString();

        fechaInicio = convertirFechaAFormatoSQL(fechaInicio);
        fechaFin = convertirFechaAFormatoSQL(fechaFin);

        String resultado = "";

        switch (tipoReporteSeleccionado) {
            case "Ordenes":
                resultado = nReporte.generarReporteTotalOrdenes(fechaInicio, fechaFin, idClienteSeleccionado, nombreClienteSeleccionado);
                break;
            case "Categoria":
                resultado = nReporte.generarReporteTotalPorCategoria(fechaInicio, fechaFin, idClienteSeleccionado, nombreClienteSeleccionado);
                break;
            case "Producto":
                resultado = nReporte.generarReporteTotalPorProducto(fechaInicio, fechaFin, idClienteSeleccionado, nombreClienteSeleccionado);
                break;
        }

        tvResultado.setText(resultado);
    }

    private String convertirFechaAFormatoSQL(String fecha) {
        String[] partes = fecha.split("/");
        return partes[2] + "-" + partes[1] + "-" + partes[0];
    }

    private void generarYAbrirPDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        String texto = tvResultado.getText().toString();
        int x = 10;
        int y = 25;
        int lineHeight = 20;
        int pageHeight = 842;
        int bottomMargin = 50;
        int limitY = pageHeight - bottomMargin;

        for (String line : texto.split("\n")) {
            if (y + lineHeight > limitY) {
                // Terminar la página actual y crear una nueva
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 25; // Reiniciar la posición Y para la nueva página
            }
            canvas.drawText(line, x, y, paint);
            y += lineHeight;
        }

        pdfDocument.finishPage(page);

        try {
            File pdfFile = new File(getExternalFilesDir(null), "reporte_temporal.pdf");
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(outputStream);
            outputStream.close();
            abrirPDF(FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al generar PDF", Toast.LENGTH_SHORT).show();
        }

        pdfDocument.close();
    }

    private void abrirPDF(Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo abrir el archivo PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
