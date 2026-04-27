package it.uninsubria.dermasuite.viewmodels.paziente

//classe enumerativa per rappresentare i ditretti del corpo e il loro peso che hanno basandomi
//sul file di esempio del progetto
enum class DistrettoCorpo(val displayName: String, val technicalName: String, val weight: Float) {
        HEAD("Testa", "HEAD", 0.1f),
        ARMS("Arti Superiori", "ARMS", 0.2f),
        TRUNK("Tronco", "TRUNK", 0.3f),
        LEGS("Arti Inferiori", "LEGS", 0.4f)
}

//Funzione helper per mappare la percentuale al punteggio Area (A)
//Cerchiamo di racchiudere dei range di are in 6 valori di base
fun mapPercentageToAreaScore(percentage: Int): Int {
    return when {
        percentage == 0 -> 0
        percentage < 10 -> 1
        percentage in 10..29 -> 2
        percentage in 30..49 -> 3
        percentage in 50..69 -> 4
        percentage in 70..89 -> 5
        else -> 6
    }
}