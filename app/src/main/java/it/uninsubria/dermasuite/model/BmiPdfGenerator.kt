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

// Questa funzione si occupa di creare un file PDF con i dati dei test BMI.
// È definita come 'suspend' per poter essere eseguita in background.

suspend fun bmiPdfGenerator(
    title: String,             // Titolo del documento
    context: Context,           // Informazioni sull'app
    records: List<BmiRecord>,  // La lista dei dati BMI
    timeFilter: TimeFilter,     // Il filtro temporale selezionato
    username : String? = null    // Nome dell'utente/paziente
) {
    withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()

        val linePaint = Paint().apply { strokeWidth = 1f; color = Color.LTGRAY; style = Paint.Style.STROKE }
        val headerPaint = Paint().apply { isFakeBoldText = true; textSize = 11f }
        val textPaint = Paint().apply { textSize = 10f }
        val titlePaint = Paint().apply { isFakeBoldText = true; textSize = 18f }

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        val recordHeight = 35f // Altezza ridotta rispetto al PASI perché il BMI è su una riga sola
        var yPosition = 160f
        var currentPageNumber = 1

        fun drawTableHeader(
            canvas: Canvas,
            x: Float,
            y: Float,
            paint: Paint,
            linePaint: Paint,
            pageWidth: Float
        ) {
            val stringaData = context.getString(R.string.data_pdf_bmi)
            val stringaAltezza = context.getString(R.string.label_altezza)
            val stringaPeso = context.getString(R.string.label_peso)
            val stringaBmi = context.getString(R.string.label_bmi)
            val stringaCategoria = context.getString(R.string.severity_pdf_bmi)

            val endX = pageWidth - x
            canvas.drawLine(x, y - 15f, endX, y - 15f, linePaint)

            canvas.drawText(stringaData, x + 0f, y, paint)
            canvas.drawText(stringaAltezza, x + 100f, y, paint)
            canvas.drawText(stringaPeso, x + 200f, y, paint)
            canvas.drawText(stringaBmi, x + 300f, y, paint)
            canvas.drawText(stringaCategoria, x + 400f, y, paint)

            canvas.drawLine(x, y + 10f, endX, y + 10f, linePaint)
        }

        fun drawTableRow(
            canvas: Canvas,
            x: Float,
            y: Float,
            record: BmiRecord,
            context: Context,
            paint: Paint,
            linePaint: Paint,
            pageWidth: Float
        ) {
            val endX = pageWidth - x
            val dateStr = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(record.CalculationDate)

            // Usiamo la funzione di utilità nel companion object per mappare la categoria localizzata
            val categoryStr = BmiRecord.getBMICategory(record.BmiTot, context)

            canvas.drawText(dateStr, x + 0f, y, paint)
            canvas.drawText("${record.Height} cm", x + 100f, y, paint)
            canvas.drawText("${record.Weight} kg", x + 200f, y, paint)
            canvas.drawText(record.BmiTot.toString(), x + 300f, y, paint)
            canvas.drawText(categoryStr, x + 400f, y, paint)

            canvas.drawLine(x, y + 10f, endX, y + 10f, linePaint)
        }

        var currentPage: PdfDocument.Page? = null

        fun startNewPage(): Canvas {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            currentPage = page
            val canvas: Canvas = page.canvas

            val stringaGeneratoIl = context.getString(R.string.stringa_generato_il)
            val filterLabel = context.getString(timeFilter.displayName)
            val stringaPaz = context.getString(R.string.stringa_paziente)
            val stringaFiltro = context.getString(R.string.stringa_filtro)

            if (currentPageNumber > 1) {
                canvas.drawText(title, 20f, 40f, titlePaint)
                yPosition = 80f
            } else {
                canvas.drawText(title, margin, 40f, titlePaint)
                canvas.drawText("$stringaPaz $username", margin, 80f, textPaint)
                canvas.drawText("$stringaFiltro $filterLabel", margin, 100f, textPaint)
                canvas.drawText("$stringaGeneratoIl ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}", margin, 120f, textPaint)
            }

            drawTableHeader(canvas, margin, yPosition, headerPaint, linePaint, pageWidth.toFloat())
            yPosition += 30f

            return canvas
        }

        var currentCanvas = startNewPage()

        records.reversed().forEach { record ->
            if (yPosition + recordHeight > pageHeight - margin) {
                currentPage?.let { pdfDocument.finishPage(it) }
                currentPageNumber++
                currentCanvas = startNewPage()
            }

            drawTableRow(
                currentCanvas,
                margin,
                yPosition,
                record,
                context,
                textPaint,
                linePaint,
                pageWidth.toFloat()
            )
            yPosition += recordHeight
        }

        currentPage?.let { pdfDocument.finishPage(it) }

        val filename = "REPORT_BMI_${context.getString(timeFilter.displayName).uppercase()}_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.pdf"
        val stringaConfermaDownload = context.getString(R.string.stringa_conferma_download)
        val stringaErroreDownload = context.getString(R.string.stringa_errore_download)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }

                val uri: Uri? = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        pdfDocument.writeTo(outputStream)
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, stringaConfermaDownload, Toast.LENGTH_LONG).show()
                    }
                } ?: throw Exception("Impossibile creare l'indirizzo del file tramite MediaStore")
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, filename)
                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "$stringaConfermaDownload in Download", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, stringaErroreDownload, Toast.LENGTH_LONG).show()
            }
        } finally {
            pdfDocument.close()
        }
    }
}