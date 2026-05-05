package it.uninsubria.dermasuite.viewmodels.paziente

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import it.uninsubria.dermasuite.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Questa funzione si occupa di creare un file PDF con i dati dei test PASI.
// È definita come 'suspend' per poter essere eseguita in background senza bloccare l'applicazione.

suspend fun pdfGenerator(
    title: String,             // Titolo del documento
    context: Context,           // Informazioni sull'app necessarie per accedere a file e testi
    records: List<PasiRecord>,  // La lista dei dati da inserire nel PDF
    timeFilter: TimeFilter,     // Il filtro temporale selezionato (es. ultimi 7 giorni)
    username : String? = null    // Nome dell'utente/paziente
) {
    // Eseguiamo tutto il lavoro "pesante" in un thread separato (IO) per non rallentare l'interfaccia
    withContext(Dispatchers.IO) {

        // Inizializziamo il documento PDF principale
        val pdfDocument = PdfDocument()

        // Definiamo i "pennelli" (stili) per scrivere il testo e disegnare le linee
        val linePaint = Paint().apply { strokeWidth = 1f; color = Color.LTGRAY; style = Paint.Style.STROKE }
        val headerPaint = Paint().apply { isFakeBoldText = true; textSize = 11f } // Testo un po' più compatto
        val textPaint = Paint().apply { textSize = 10f }
        val titlePaint = Paint().apply { isFakeBoldText = true; textSize = 18f }

        // Impostiamo le dimensioni standard di una pagina A4 e i margini del foglio
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f

        // Ogni record ora occuperà 4 righe. Impostiamo l'altezza totale di un singolo record
        val recordHeight = 85f
        var yPosition = 160f    // Posizione di partenza per scrivere (verticale)
        var currentPageNumber = 1

        //Funzione usata per disegnare l'intestazione della tabella
        fun drawTableHeader(
            canvas: Canvas,
            x: Float,
            y: Float,
            paint: Paint,
            linePaint: Paint,
            pageWidth: Float
        ) {
            val stringaData = context.getString(R.string.data_pdf)
            val stringaPasiTot = context.getString(R.string.pasi_tot_pdf)
            val stringaSeverity = context.getString(R.string.severity_pdf)
            val stringaDesquamazione = context.getString(R.string.desquamazione_pdf)
            val stringaArea = context.getString(R.string.area_pdf)
            val stringaEritema = context.getString(R.string.eritema_pdf)
            val stringaIndurimento = context.getString(R.string.indurimento_pdf)
            val stringaDistretto = context.getString(R.string.distretto)

            val endX = pageWidth - x
            canvas.drawLine(x, y - 15f, endX, y - 15f, linePaint)

            // Aggiungiamo una colonna "Zona" per specificare il distretto
            canvas.drawText(stringaData, x + 0f, y, paint)
            canvas.drawText(stringaPasiTot, x + 70f, y, paint)
            canvas.drawText(stringaDistretto, x + 140f, y, paint)
            canvas.drawText(stringaEritema, x + 200f, y, paint)
            canvas.drawText(stringaIndurimento, x + 265f, y, paint)
            canvas.drawText(stringaDesquamazione, x + 350f, y, paint)
            canvas.drawText(stringaArea, x + 450f, y, paint)
            canvas.drawText(stringaSeverity, x + 490f, y, paint)

            canvas.drawLine(x, y + 10f, endX, y + 10f, linePaint)
        }

        // Funzione di supporto per scrivere le 4 righe di dati nel PDF
        fun drawTableRow(
            canvas: Canvas,
            x: Float,
            y: Float,
            record: PasiRecord,
            context: Context,
            paint: Paint,
            linePaint: Paint,
            pageWidth: Float
        ) {
            val endX = pageWidth - x
            val dateStr = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(record.CalculationDate)
            val stringaLivello = context.getString(R.string.stringa_livello)

            // Altezza tra le singole righine dei distretti dello stesso record
            val subRowHeight = 18f
            var currentY = y

            // Creiamo una lista con tutti i distretti per ciclarli facilmente
            val districts = listOf(
                Triple(context.getString(DistrettoCorpo.HEAD.nameResId), record.ParameterDistrict.head, 0),
                Triple(context.getString(DistrettoCorpo.ARMS.nameResId), record.ParameterDistrict.arms, 1),
                Triple(context.getString(DistrettoCorpo.TRUNK.nameResId), record.ParameterDistrict.trunk, 2),
                Triple(context.getString(DistrettoCorpo.LEGS.nameResId), record.ParameterDistrict.legs, 3)
            )

            // Scriviamo una sotto-riga per ogni distretto
            districts.forEach { (name, district, index) ->
                val eritema = "$stringaLivello${district.erythema}"
                val indurim = "$stringaLivello${district.hardening}"
                val desquam = "$stringaLivello${district.desquamation}"
                val area = "$stringaLivello${district.percentageArea}"

                // Stampiamo Data, Punteggio Totale e Severità SOLO sulla prima riga (Head)
                if (index == 0) {
                    canvas.drawText(dateStr, x + 0f, currentY, paint)
                    canvas.drawText(record.PasiTot.toString(), x + 85f, currentY, paint)
                    canvas.drawText(record.mapSeverity(context), x + 500f, currentY, paint)
                }

                // I parametri dei distretti vengono incollonati
                canvas.drawText(name, x + 145f, currentY, paint)
                canvas.drawText(eritema, x + 210f, currentY, paint)
                canvas.drawText(indurim, x + 285f, currentY, paint)
                canvas.drawText(desquam, x + 380f, currentY, paint)
                canvas.drawText(area, x + 452f, currentY, paint)

                currentY += subRowHeight // Scendiamo per il prossimo distretto
            }

            // Linea orizzontale per chiudere l'intero record
            canvas.drawLine(x, currentY - 8f, endX, currentY - 8f, linePaint)
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
            yPosition += 30f // Spazio tra l'header e il primo record

            return canvas
        }

        var currentCanvas = startNewPage()

        records.reversed().forEach { record ->
            // Se non c'è spazio per le 4 righe del record, giriamo pagina
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
            yPosition += recordHeight // Spostiamo la Y per il blocco successivo
        }

        currentPage?.let { pdfDocument.finishPage(it) }

        // SALVATAGGIO DEL FILE
        val filename = "REPORT_PASI_${context.getString(timeFilter.displayName).uppercase()}_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.pdf"

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        }

        val stringaConfermaDownload = context.getString(R.string.stringa_conferma_download)
        val stringaErroreDownload = context.getString(R.string.stringa_errore_download)

        try {
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, stringaConfermaDownload, Toast.LENGTH_LONG).show()
                    }
                }
            } ?: throw Exception("Impossibile creare l'indirizzo del file")
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