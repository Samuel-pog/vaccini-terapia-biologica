package com.example.vacciniterapiabiologica.model

enum class RecommendationStatus(
    val displayName: String
) {
    RACCOMANDATO("Raccomandato"),
    POSSIBILE("Possibile"),
    CONTROINDICATO("Controindicato"),
    DA_VALUTARE("Da valutare")
}