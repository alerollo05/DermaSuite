package it.uninsubria.dermasuite.model

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import it.uninsubria.dermasuite.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Funzione suspend per generare il PDF senza bloccare l'interfaccia utente (UI)
suspend fun bsaPdfGenerator(
    title: String,
    context: Context,
    records: List<BsaRecord>,
    timeFilter: TimeFilter,
    username: String? = null
) {
    // Sposta l'esecuzione su un thread ottimizzato per le operazioni di I/O
    withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument() // Inizializza il generatore di PDF di Android
        // Definisce gli stili (pennelli) per le linee, le intestazioni, il testo e il titolo
        val linePaint = Paint().apply { strokeWidth = 1f; color = Color.LTGRAY; style = Paint.Style.STROKE }
        val headerPaint = Paint().apply { isFakeBoldText = true; textSize = 12f }
        val textPaint = Paint().apply { textSize = 11f }
        val titlePaint = Paint().apply { isFakeBoldText = true; textSize = 18f }

        // Dimensioni standard A4 in punti (1/72 di pollice)
        val pageWidth = 595
        val pageHeight = 842
        val margin = 30f

        val recordHeight = 30f // Spazio verticale occupato da ogni riga di record
        var yPosition = 160f   // Posizione verticale iniziale per i dati (dopo l'intestazione)
        var currentPageNumber = 1

        // Funzione interna per disegnare l'intestazione della tabella (nomi colonne)
        fun drawTableHeader(canvas: Canvas, x: Float, y: Float, paint: Paint, linePaint: Paint, pageWidth: Float) {
            val endX = pageWidth - x
            canvas.drawLine(x, y - 15f, endX, y - 15f, linePaint) // Linea superiore

            canvas.drawText("Data e Ora", x + 0f, y, paint)
            canvas.drawText("Peso", x + 120f, y, paint)
            canvas.drawText("Altezza", x + 200f, y, paint)
            canvas.drawText("Sesso", x + 280f, y, paint)
            canvas.drawText("BSA (m²)", x + 360f, y, paint)
            canvas.drawText("Valutazione", x + 440f, y, paint)

            canvas.drawLine(x, y + 10f, endX, y + 10f, linePaint) // Linea inferiore
        }

        // Funzione interna per disegnare una singola riga di dati del paziente
        fun drawTableRow(canvas: Canvas, x: Float, y: Float, record: BsaRecord, paint: Paint, linePaint: Paint, pageWidth: Float) {
            val endX = pageWidth - x
            canvas.drawText(record.dataOra, x + 0f, y, paint)
            canvas.drawText("${record.peso} kg", x + 120f, y, paint)
            canvas.drawText("${record.altezza} cm", x + 200f, y, paint)
            canvas.drawText(record.sesso, x + 280f, y, paint)
            canvas.drawText(String.format(Locale.getDefault(), "%.2f", record.bsa), x + 360f, y, paint)
            canvas.drawText(record.valutazione, x + 440f, y, paint)
            canvas.drawLine(x, y + 10f, endX, y + 10f, linePaint)
        }

        var currentPage: PdfDocument.Page? = null

        // Funzione per creare una nuova pagina e disegnare intestazione app/paziente
        fun startNewPage(): Canvas {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            currentPage = page
            val canvas: Canvas = page.canvas

            // Se è una pagina successiva alla prima, mette solo il titolo, altrimenti mette info dettagliate
            if (currentPageNumber > 1) {
                canvas.drawText(title, 20f, 40f, titlePaint)
                yPosition = 80f
            } else {
                canvas.drawText(title, margin, 40f, titlePaint)
                canvas.drawText("${context.getString(R.string.stringa_paziente)} ${username ?: ""}", margin, 80f, textPaint)
                canvas.drawText("${context.getString(R.string.stringa_filtro)} ${context.getString(timeFilter.displayName)}", margin, 100f, textPaint)
                canvas.drawText("${context.getString(R.string.stringa_generato_il)} ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}", margin, 120f, textPaint)
            }

            drawTableHeader(canvas, margin, yPosition, headerPaint, linePaint, pageWidth.toFloat())
            yPosition += 30f
            return canvas
        }

        var currentCanvas = startNewPage()

        // Cicla i record (invertiti per mostrare i più recenti) e gestisce il cambio pagina se lo spazio finisce
        records.reversed().forEach { record ->
            if (yPosition + recordHeight > pageHeight - margin) {
                currentPage?.let { pdfDocument.finishPage(it) }
                currentPageNumber++
                currentCanvas = startNewPage()
            }
            drawTableRow(currentCanvas, margin, yPosition, record, textPaint, linePaint, pageWidth.toFloat())
            yPosition += recordHeight
        }

        currentPage?.let { pdfDocument.finishPage(it) } // Chiude l'ultima pagina creata

        // Aggiungiamo "_HHmmss" per rendere ogni file PDF unico e non sovrascrivere i vecchi
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filterName = context.getString(timeFilter.displayName).uppercase()
        val filename = "REPORT_BSA_${filterName}_${timestamp}.pdf"

        try {
            // Gestione salvataggio per Android 10 (API 29) e superiori usando MediaStore (Scoped Storage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1) // File in scrittura, non ancora visibile ad altre app
                }

                val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream -> pdfDocument.writeTo(outputStream) }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0) // Scrittura completata
                    resolver.update(uri, contentValues, null, null)
                    withContext(Dispatchers.Main) { Toast.makeText(context, R.string.stringa_conferma_download, Toast.LENGTH_LONG).show() }
                } ?: throw Exception("Errore MediaStore")
            } else {
                // Gestione per versioni Android più vecchie usando l'accesso diretto ai file
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, filename)
                FileOutputStream(file).use { outputStream -> pdfDocument.writeTo(outputStream) }
                withContext(Dispatchers.Main) { Toast.makeText(context, "${context.getString(R.string.stringa_conferma_download)} in Download", Toast.LENGTH_LONG).show() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) { Toast.makeText(context, R.string.stringa_errore_download, Toast.LENGTH_LONG).show() }
        } finally {
            pdfDocument.close() // Libera le risorse del documento
        }
    }
}