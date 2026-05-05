package it.uninsubria.dermasuite.viewmodels

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
import it.uninsubria.dermasuite.viewmodels.paziente.PasiRecord
import it.uninsubria.dermasuite.viewmodels.paziente.TimeFilter
import it.uninsubria.dermasuite.viewmodels.paziente.mapSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


 //Questa funzione si occupa di creare un file PDF con i dati dei test PASI.
 //È definita come 'suspend' per poter essere eseguita in background senza bloccare l'applicazione.

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
        val headerPaint = Paint().apply { isFakeBoldText = true; textSize = 12f }
        val textPaint = Paint().apply { textSize = 10f }
        val titlePaint = Paint().apply { isFakeBoldText = true; textSize = 18f }

        // Impostiamo le dimensioni standard di una pagina A4 e i margini del foglio
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val rowHeight = 30f     // Altezza di ogni riga della tabella
        var yPosition = 160f    // Posizione di partenza per scrivere (verticale)
        var currentPageNumber = 1

        /**
         * Funzione di supporto per disegnare l'intestazione della tabella (nomi delle colonne)
         */
        fun drawTableHeader(
            canvas: Canvas,
            x: Float,
            y: Float,
            paint: Paint,
            linePaint: Paint,
            pageWidth: Float
        ) {
            // Recuperiamo i nomi delle colonne dalle traduzioni dell'app
            val stringaData = context.getString(R.string.data_pdf)
            val stringaPasiTot = context.getString(R.string.pasi_tot_pdf)
            val stringaSeverity = context.getString(R.string.severity_pdf)
            val stringaDesquamazione = context.getString(R.string.desquamazione_pdf)
            val stringaArea = context.getString(R.string.area_pdf)
            val stringaEritema = context.getString(R.string.eritema_pdf)
            val stringaIndurimento = context.getString(R.string.indurimento_pdf)

            val endX = pageWidth - x
            // Disegniamo una linea orizzontale sopra i titoli
            canvas.drawLine(x, y - 15f, endX, y - 15f, linePaint)

            // Scriviamo i nomi delle colonne in posizioni precise orizzontalmente
            canvas.drawText(stringaData, x + 0f, y, paint)
            canvas.drawText(stringaPasiTot, x + 65f, y, paint)
            canvas.drawText(stringaEritema, x + 130f, y, paint)
            canvas.drawText(stringaIndurimento, x + 195f, y, paint)
            canvas.drawText(stringaDesquamazione, x + 280f, y, paint)
            canvas.drawText(stringaArea, x + 380f, y, paint)
            canvas.drawText(stringaSeverity, x + 430f, y, paint)

            // Disegniamo una linea orizzontale sotto i titoli
            canvas.drawLine(x, y + 10f, endX, y + 10f, linePaint)
        }


        //Funzione di supporto per scrivere una riga di dati nel PDF

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
            // Formattiamo la data del record
            val dateStr = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(record.CalculationDate)
            val stringaLivello = context.getString(R.string.stringa_livello)
            
            // Prepariamo i valori da mostrare (per ora usiamo i dati della testa come esempio)
            val eritema = "$stringaLivello${record.ParameterDistrict.head.erythema}"
            val desquam = "$stringaLivello${record.ParameterDistrict.head.desquamation}"
            val indurim = "$stringaLivello${record.ParameterDistrict.head.hardening}"
            val area = "$stringaLivello${record.ParameterDistrict.head.percentageArea}"

            // Scriviamo i valori effettivi allineandoli alle colonne
            canvas.drawText(dateStr, x + 0f, y, paint)
            canvas.drawText(record.PasiTot.toString(), x + 80f, y, paint)
            canvas.drawText(eritema, x + 140f, y, paint)
            canvas.drawText(indurim, x + 215f, y, paint)
            canvas.drawText(desquam, x + 310f, y, paint)
            canvas.drawText(area, x + 385f, y, paint)
            canvas.drawText(record.mapSeverity(context), x + 440f, y, paint)

            // Linea orizzontale per chiudere la riga
            canvas.drawLine(x, y + 10f, endX, y + 10f, linePaint)
        }

        // Variabile per tenere traccia della pagina su cui stiamo scrivendo attualmente
        var currentPage: PdfDocument.Page? = null
        

        //Funzione per iniziare un nuovo foglio quando il precedente è pieno

        fun startNewPage(): Canvas {
            // Definiamo le specifiche del nuovo foglio
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPageNumber).create()
            val page = pdfDocument.startPage(pageInfo)
            currentPage = page
            val canvas: Canvas = page.canvas

            // Recuperiamo testi utili per l'intestazione
            val stringaGeneratoIl = context.getString(R.string.stringa_generato_il)
            val filterLabel = context.getString(timeFilter.displayName)
            val stringaPaz = context.getString(R.string.stringa_paziente)
            val stringaFiltro = context.getString(R.string.stringa_filtro)

            // Se è una pagina successiva alla prima, mettiamo un'intestazione ridotta
            if (currentPageNumber > 1) {
                canvas.drawText(title, 20f, 40f, titlePaint)
                yPosition = 80f
            } else {
                // Se è la prima pagina, mettiamo tutte le informazioni del report
                canvas.drawText(title, margin, 40f, titlePaint)
                canvas.drawText("$stringaPaz $username", margin, 80f, textPaint)
                canvas.drawText("$stringaFiltro $filterLabel", margin, 100f, textPaint)
                canvas.drawText(
                    "$stringaGeneratoIl ${
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    }", margin, 120f, textPaint
                )
            }
            // Disegniamo i titoli della tabella in alto nella nuova pagina
            drawTableHeader(canvas, margin, yPosition, headerPaint, linePaint, pageWidth.toFloat())
            yPosition += rowHeight // Scendiamo di una riga per iniziare con i dati

            return canvas
        }

        // Creiamo la prima pagina per iniziare
        var currentCanvas = startNewPage()
        
        // Cicliamo su tutti i record (dal più recente) e li scriviamo nel PDF
        records.reversed().forEach { record ->
            // Controllo salto pagina: se la prossima riga non ci sta nel foglio, ne apriamo uno nuovo
            if (yPosition + rowHeight > pageHeight - margin) {
                currentPage?.let { pdfDocument.finishPage(it) } // Chiudiamo il foglio attuale
                currentPageNumber++
                currentCanvas = startNewPage() // Iniziamo un nuovo foglio
            }

            // Scriviamo i dati del record corrente sul foglio
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
            yPosition += rowHeight // Spostiamoci alla riga successiva
        }

        // Abbiamo finito i dati: chiudiamo l'ultima pagina rimasta aperta
        currentPage?.let { pdfDocument.finishPage(it) }


        //SALVATAGGIO DEL FILE

        // Creiamo un nome file unico basato sul filtro e sulla data di oggi
        val filename = "REPORT_PASI_${
            context.getString(timeFilter.displayName).uppercase()
        }_${SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())}.pdf"
        
        val resolver = context.contentResolver

        // Specifichiamo le proprietà del file per il sistema Android (nome, tipo, cartella Download)
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
        }

        // Chiediamo al sistema Android dove possiamo salvare il file
        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
        }

        val stringaConfermaDownload = context.getString(R.string.stringa_conferma_download)
        val stringaErroreDownload = context.getString(R.string.stringa_errore_download)
        
        try {
            uri?.let {
                // Scriviamo fisicamente il contenuto del PDF nel file creato
                resolver.openOutputStream(it)?.use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                    
                    // Mostriamo un messaggio di conferma (dobbiamo farlo sul thread principale)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, stringaConfermaDownload, Toast.LENGTH_LONG).show()
                    }
                }
            } ?: throw Exception("Impossibile creare l'indirizzo del file")
        } catch (e: Exception) {
            e.printStackTrace()
            // In caso di errore, avvisiamo l'utente
            withContext(Dispatchers.Main) {
                Toast.makeText(context, stringaErroreDownload, Toast.LENGTH_LONG).show()
            }
        } finally {
            // Chiudiamo il documento per liberare la memoria
            pdfDocument.close()
        }
    }
}
