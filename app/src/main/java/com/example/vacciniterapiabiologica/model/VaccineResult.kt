package com.example.vacciniterapiabiologica.model
data class VaccineResult(
    val vaccine: VaccineType,
    val status: RecommendationStatus,
    val explanation: String
)