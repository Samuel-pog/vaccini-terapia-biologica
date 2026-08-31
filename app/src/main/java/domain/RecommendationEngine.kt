package com.example.vacciniterapiabiologica.domain

import com.example.vacciniterapiabiologica.model.VaccineResult

fun calculateRecommendations(
    therapy: String,
    age: Int,
    conditions: Set<String>,
    completedVaccines: Set<String>
): List<VaccineResult> {
    val results = mutableListOf<VaccineResult>()

    val therapyExplanation = when (therapy) {
        "Anti-TNF" -> {
            "Terapia anti-TNF selezionata. Nel prototipo è trattata come terapia immunomodulante che richiede verifica delle vaccinazioni non vive e prudenza per i vaccini vivi attenuati."
        }

        "Anti-IL17" -> {
            "Terapia anti-IL17 selezionata. Nel prototipo è trattata come terapia immunomodulante che richiede verifica del calendario vaccinale."
        }

        "Anti-IL23" -> {
            "Terapia anti-IL23 selezionata. Nel prototipo è trattata come terapia immunomodulante che richiede verifica del calendario vaccinale."
        }

        "Altro immunosoppressore" -> {
            "Altro immunosoppressore selezionato. Nel prototipo è richiesta una valutazione clinica più cauta delle vaccinazioni e delle tempistiche."
        }

        else -> {
            "Terapia biologica selezionata. Verificare il piano vaccinale con il medico."
        }
    }

    if ("Vaccino antinfluenzale" in completedVaccines) {
        results.add(
            VaccineResult(
                name = "Vaccino antinfluenzale",
                status = "Da valutare",
                explanation = "Il vaccino risulta già effettuato. Verificare la data dell'ultima dose e il calendario vaccinale aggiornato. $therapyExplanation"
            )
        )
    } else {
        results.add(
            VaccineResult(
                name = "Vaccino antinfluenzale",
                status = "Raccomandato",
                explanation = "Vaccino non vivo generalmente considerato indicato nel prototipo per pazienti in terapia biologica. $therapyExplanation"
            )
        )
    }

    val hasPneumococcalRisk =
        age >= 65 ||
                "Malattia respiratoria" in conditions ||
                "Malattia cardiaca" in conditions

    if ("Vaccino pneumococcico" in completedVaccines) {
        results.add(
            VaccineResult(
                name = "Vaccino pneumococcico",
                status = "Da valutare",
                explanation = "Il vaccino risulta già effettuato. Verificare il numero di dosi e le tempistiche previste."
            )
        )
    } else if (hasPneumococcalRisk) {
        results.add(
            VaccineResult(
                name = "Vaccino pneumococcico",
                status = "Raccomandato",
                explanation = "Raccomandato nel prototipo per età pari o superiore a 65 anni o per presenza di patologie cardiache o respiratorie."
            )
        )
    } else {
        results.add(
            VaccineResult(
                name = "Vaccino pneumococcico",
                status = "Possibile",
                explanation = "Può essere valutato in base al profilo clinico, alla storia vaccinale e alle indicazioni aggiornate."
            )
        )
    }

    results.add(
        VaccineResult(
            name = "Vaccini vivi attenuati",
            status = "Controindicato",
            explanation = "Nel prototipo didattico vengono considerati controindicati durante terapia biologica. $therapyExplanation La valutazione clinica reale deve essere effettuata dal medico."
        )
    )

    if (therapy == "Altro immunosoppressore") {
        results.add(
            VaccineResult(
                name = "Pianificazione vaccinale",
                status = "Da valutare",
                explanation = "Il tipo di immunosoppressore non è specificato nel prototipo. È necessario verificare farmaco, dose, durata della terapia, vaccinazioni precedenti e tempistica con il medico."
            )
        )
    }

    if (age >= 50) {
        if ("Vaccino contro herpes zoster" in completedVaccines) {
            results.add(
                VaccineResult(
                    name = "Vaccino contro herpes zoster",
                    status = "Da valutare",
                    explanation = "Il vaccino risulta già effettuato. Verificare tipo di vaccino, dosi ricevute e tempistiche."
                )
            )
        } else {
            results.add(
                VaccineResult(
                    name = "Vaccino contro herpes zoster",
                    status = "Possibile",
                    explanation = "Può essere valutato nel prototipo in base all'età, alle condizioni cliniche e al tipo di vaccino disponibile."
                )
            )
        }
    }

    if ("Vaccino COVID-19" !in completedVaccines) {
        results.add(
            VaccineResult(
                name = "Vaccino COVID-19",
                status = "Possibile",
                explanation = "Nel prototipo richiede verifica del calendario vaccinale aggiornato e delle eventuali dosi precedenti."
            )
        )
    }

    if ("Vaccino contro epatite B" !in completedVaccines) {
        results.add(
            VaccineResult(
                name = "Vaccino contro epatite B",
                status = "Possibile",
                explanation = "Può essere valutato in base ai fattori di rischio, alla documentazione vaccinale e alla situazione clinica."
            )
        )
    }

    if ("Gravidanza" in conditions) {
        results.add(
            VaccineResult(
                name = "Vaccini in gravidanza",
                status = "Da valutare",
                explanation = "È necessaria una valutazione clinica specifica della gravidanza e del calendario vaccinale."
            )
        )
    }

    return results
}