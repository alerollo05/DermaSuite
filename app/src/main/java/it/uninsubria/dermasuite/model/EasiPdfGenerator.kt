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

suspend fun easiPdfGenerator(
    title: String,
    context: Context,
    records: List<EasiRecord>,
    timeFilter: TimeFilter,
    username: String? = null
) {
    withContext(Dispatchers.IO) {
        val pdfDocument = PdfDocument()
        val linePaint = Paint().apply { strokeWidth = 1f; color = Color.LTGRAY; style = Paint.Style.STROKE }
        val headerPaint = Paint().apply { isFakeBoldText = true; textSize = 10f } // Leggermente più piccolo per far stare 5 parametri
        val textPaint = Paint().apply { textSize = 10f }
        val titlePaint = Paint().apply { isFakeBoldText = true; textSize = 18f }

        val pageWidth = 595
        val pageHeight = 842
        val margin = 30f // Margine ridotto per avere più spazio orizzontale

        val recordHeight = 85f
        var yPosition = 160f
        var currentPageNumber = 1

        fun drawTableHeader(canvas: Canvas, x: Float, y: Float, paint: Paint, linePaint: Paint, pageWidth: Float) {
            val endX = pageWidth - x
            canvas.drawLine(x, y - 15f, endX, y - 15f, linePaint)

            // Header EASI: abbiamo 5 parametri al posto di 3
            canvas.drawText(context.getString(R.string.data_pdf), x + 0f, y, paint)
            canvas.drawText("EASI Tot", x + 60f, y, paint)
            canvas.drawText(context.getString(R.string.distretto), x + 115f, y, paint)
            canvas.drawText(context.getString(R.string.eritema_pdf), x + 180f, y, paint)
            canvas.drawText(context.getString(R.string.edema_pdf), x + 240f, y, paint) // Usa un nome corto in stringhe tipo "Edem/Pap"
            canvas.drawText(context.getString(R.string.escoriazione_pdf), x + 295f, y, paint)
            canvas.drawText(context.getString(R.string.lichenificazione_pdf), x + 380f, y, paint)
            canvas.drawText(context.getString(R.string.area_pdf), x + 435f, y, paint)
            canvas.drawText(context.getString(R.string.severity_pdf), x + 480f, y, paint)

            canvas.drawLine(x, y + 10f, endX, y + 10f, linePaint)
        }

        fun drawTableRow(canvas: Canvas, x: Float, y: Float, record: EasiRecord, context: Context, paint: Paint, linePaint: Paint, pageWidth: Float) {
            val endX = pageWidth - x
            val dateStr = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(record.CalculationDate)
            val stringaLivello = context.getString(R.string.stringa_livello)

            val subRowHeight = 18f
            var currentY = y

            val districts = listOf(
                Triple(context.getString(DistrettoCorpo.HEAD.nameResId), record.ParameterDistrict.head, 0),
                Triple(context.getString(DistrettoCorpo.ARMS.nameResId), record.ParameterDistrict.arms, 1),
                Triple(context.getString(DistrettoCorpo.TRUNK.nameResId), record.ParameterDistrict.trunk, 2),
                Triple(context.getString(DistrettoCorpo.LEGS.nameResId), record.ParameterDistrict.legs, 3)
            )

            districts.forEach { (name, district, index) ->
                val eritema = "$stringaLivello${district.eritema}"
                val edema = "$stringaLivello${district.edemaPapulizzazione}"
                val escoriazione = "$stringaLivello${district.escoriazione}"
                val lichen = "$stringaLivello${district.lichenificazione}"
                val area = "$stringaLivello${district.percentualeArea}"

                if (index == 0) {
                    canvas.drawText(dateStr, x + 0f, currentY, paint)
                    // Format del float per evitare numeri troppo lunghi (es. 12.50)
                    canvas.drawText(String.format(Locale.getDefault(), "%.1f", record.EasiTot), x + 70f, currentY, paint)
                    canvas.drawText(record.mapSeverity(context), x + 480f, currentY, paint)
                }

                canvas.drawText(name, x + 115f, currentY, paint)
                canvas.drawText(eritema, x + 190f, currentY, paint)
                canvas.drawText(edema, x + 245f, currentY, paint)
                canvas.drawText(escoriazione, x + 315f, currentY, paint)
                canvas.drawText(lichen, x + 390f, currentY, paint)
                canvas.drawText(area, x + 435f, currentY, paint)

                currentY += subRowHeight
            }

            canvas.drawLine(x, currentY - 8f, endX, currentY - 8f, linePaint)
        }

        var currentPage: PdfDocument.Page? = null

        fun startNewPage(): Canvas {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            currentPage = page
            val canvas: Canvas = page.canvas

            if (currentPageNumber > 1) {
                canvas.drawText(title, 20f, 40f, titlePaint)
                yPosition = 80f
            } else {
                canvas.drawText(title, margin, 40f, titlePaint)
                canvas.drawText("${context.getString(R.string.stringa_paziente)} $username", margin, 80f, textPaint)
                canvas.drawText("${context.getString(R.string.stringa_filtro)} ${context.getString(timeFilter.displayName)}", margin, 100f, textPaint)
                canvas.drawText("${context.getString(R.string.stringa_generato_il)} ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}", margin, 120f, textPaint)
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
            drawTableRow(currentCanvas, margin, yPosition, record, context, textPaint, linePaint, pageWidth.toFloat())
            yPosition += recordHeight
        }

        currentPage?.let { pdfDocument.finishPage(it) }

        val filename = "REPORT_EASI_${context.getString(timeFilter.displayName).uppercase()}_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.pdf"

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
                    resolver.openOutputStream(it)?.use { outputStream -> pdfDocument.writeTo(outputStream) }
                    contentValues.clear()
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)

                    withContext(Dispatchers.Main) { Toast.makeText(context, R.string.stringa_conferma_download, Toast.LENGTH_LONG).show() }
                } ?: throw Exception("Errore MediaStore")
            } else {
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
            pdfDocument.close()
        }
    }
}