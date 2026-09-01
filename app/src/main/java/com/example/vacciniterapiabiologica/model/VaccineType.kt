package com.example.vacciniterapiabiologica.model

enum class VaccineType(
    val displayName: String
) {
    ANTINFLUENZALE("Vaccino antinfluenzale"),
    PNEUMOCOCCICO("Vaccino pneumococcico"),
    HERPES_ZOSTER("Vaccino contro herpes zoster"),
    COVID_19("Vaccino COVID-19"),
    EPATITE_B("Vaccino contro epatite B"),
    VIVI_ATTENUATI("Vaccini vivi attenuati"),
    PIANIFICAZIONE_VACCINALE("Pianificazione vaccinale"),
    VACCINI_IN_GRAVIDANZA("Vaccini in gravidanza")
}