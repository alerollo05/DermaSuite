package it.uninsubria.dermasuite.ui.components

import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import it.uninsubria.dermasuite.R
import it.uninsubria.dermasuite.firebase.DermaUser
import it.uninsubria.dermasuite.ui.screens.getFlagEmoji
import java.net.URL

@Composable
fun DermaAvatar(
    avatarURL: String? = null,
    modifier: Modifier = Modifier,
    isComplex: String? = null, // L'emoji da mostrare (es. Bandiera o Matita)
    iconResId: Int = R.drawable.ic_profile,
    size: Int = 48
) {

    Box(
        modifier = modifier.size(size.dp)
    ) {

        //Box dove mettiamo l'immagine e l'icona
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape) // Ritagliamo solo l'immagine/icona!
                .background(Color.Gray.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (avatarURL != null) {
                // Se c'è l'URL, carica la foto
                AsyncImage(
                    model = avatarURL,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Se NON c'è l'URL carica l'icona passata come parametro
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = "Icona Default",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size((size * 0.6).dp)
                )
            }
        }

        //Il badge dove se serve mettiamo un emoji in primo piano
        if (isComplex != null) {
            //Per gestire al meglio le diverse dimensioni delle icone, creiamo una logia a soglie
            // Se la size è maggiore di 70, siamo nel Profilo (Avatar grande).
            // Altrimenti siamo nella Card (Avatar piccolo).
            val isLargeAvatar = size > 70

            val badgeOffset = if (isLargeAvatar) 4.dp else 2.dp   // Quanto sporge in fuori rispetto all'immagine
            val badgePadding = if (isLargeAvatar) 6.dp else 3.dp  // Lo spessore del cerchietto bianco
            val emojiSize = if (isLargeAvatar) 18.sp else 14.sp   // Grandezza dell'emoji vera e propria

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    // Sporge in positivo fuori dal cerchio
                    .offset(x = badgeOffset, y = badgeOffset)
                    .background(Color.White, CircleShape)
                    .padding(badgePadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = isComplex,
                    fontSize = emojiSize,
                )
            }
        }
    }
}