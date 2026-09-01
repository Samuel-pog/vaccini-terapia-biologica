package com.example.vacciniterapiabiologica.model

enum class TherapyType(
    val displayName: String
) {
    ANTI_TNF("Anti-TNF"),
    ANTI_IL17("Anti-IL17"),
    ANTI_IL23("Anti-IL23"),
    ALTRO_IMMUNOSOPPRESSORE("Altro immunosoppressore")
}