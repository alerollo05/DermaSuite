package it.uninsubria.dermasuite.viewmodels.paziente

import androidx.annotation.StringRes
import it.uninsubria.dermasuite.R

//classe enumerativa per rappresentare i ditretti del corpo e il loro peso che hanno basandomi
//sul file di esempio del progetto
enum class DistrettoCorpo(@StringRes val nameResId: Int, val displayName: String, val technicalName: String, val weight: Float, val iconRes: Int) {
        HEAD(R.string.distretto_testa,"Testa", "HEAD",0.1f, R.drawable.ic_testa_umana),
        ARMS(R.string.distretto_braccia,"Art-Sup", "ARMS",0.2f, R.drawable.ic_braccio_umano),
        TRUNK(R.string.distretto_tronco,"Tronco", "TRUNK",0.3f,R.drawable.ic_torso_umano),
        LEGS(R.string.distretto_gambe,"Art-Inf", "LEGS",0.4f,R.drawable.ic_gamba_umana)
}
