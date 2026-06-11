package it.uninsubria.dermasuite.ui.screens

// Funzione per trasformare la sigla della lingua in un'emoji
fun getFlagEmoji(lingua: String): String {
    return when(lingua.lowercase()) {
        "it" -> "🇮🇹" //Emoji della bandiera italiana
        "en" -> "🇬🇧" //Emoji della bandiera di gran bretagna
        else -> "\uD83C\uDF0D" // Se non riconosciamo la lingua, mostriamo il mappamondo
    }
}