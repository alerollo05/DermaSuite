package it.uninsubria.dermasuite.viewmodels.paziente

import it.uninsubria.dermasuite.R

//classe enumerativa per rappresentare i ditretti del corpo e il loro peso che hanno basandomi
//sul file di esempio del progetto
enum class DistrettoCorpo(val displayName: String, val weight: Float,val iconRes: Int) {
        HEAD("Testa", 0.1f, R.drawable.ic_testa_umana),
        ARMS("Art-Sup", 0.2f, R.drawable.ic_braccio_umano),
        TRUNK("Tronco", 0.3f,R.drawable.ic_torso_umano),
        LEGS("Art-Inf", 0.4f,R.drawable.ic_gamba_umana)
}
