package com.example.vacciniterapiabiologica.domain

import com.example.vacciniterapiabiologica.model.ClinicalCondition
import com.example.vacciniterapiabiologica.model.RecommendationStatus
import com.example.vacciniterapiabiologica.model.TherapyType
import com.example.vacciniterapiabiologica.model.VaccineResult
import com.example.vacciniterapiabiologica.model.VaccineType

fun calculateRecommendations(
    therapy: TherapyType,
    age: Int,
    conditions: Set<ClinicalCondition>,
    completedVaccines: Set<VaccineType>
): List<VaccineResult> {
    val results = mutableListOf<VaccineResult>()

    val therapyExplanation = when (therapy) {
        TherapyType.ANTI_TNF -> {
            "Terapia anti-TNF selezionata. Nel prototipo è trattata come terapia immunomodulante che richiede verifica delle vaccinazioni non vive e prudenza per i vaccini vivi attenuati."
        }

        TherapyType.ANTI_IL17 -> {
            "Terapia anti-IL17 selezionata. Nel prototipo è trattata come terapia immunomodulante che richiede verifica del calendario vaccinale."
        }

        TherapyType.ANTI_IL23 -> {
            "Terapia anti-IL23 selezionata. Nel prototipo è trattata come terapia immunomodulante che richiede verifica del calendario vaccinale."
        }

        TherapyType.ALTRO_IMMUNOSOPPRESSORE -> {
            "Altro immunosoppressore selezionato. Nel prototipo è richiesta una valutazione clinica più cauta delle vaccinazioni e delle tempistiche."
        }
    }

    if (VaccineType.ANTINFLUENZALE in completedVaccines) {
        results.add(
            VaccineResult(
                vaccine = VaccineType.ANTINFLUENZALE,
                status = RecommendationStatus.DA_VALUTARE,
                explanation = "Il vaccino risulta già effettuato. Verificare la data dell'ultima dose e il calendario vaccinale aggiornato. $therapyExplanation"
            )
        )
    } else {
        results.add(
            VaccineResult(
                vaccine = VaccineType.ANTINFLUENZALE,
                status = RecommendationStatus.RACCOMANDATO,
                explanation = "Vaccino non vivo generalmente considerato indicato nel prototipo per pazienti in terapia biologica. $therapyExplanation"
            )
        )
    }

    val hasPneumococcalRisk =
        age >= 65 ||
                ClinicalCondition.MALATTIA_RESPIRATORIA in conditions ||
                ClinicalCondition.MALATTIA_CARDIACA in conditions

    if (VaccineType.PNEUMOCOCCICO in completedVaccines) {
        results.add(
            VaccineResult(
                vaccine = VaccineType.PNEUMOCOCCICO,
                status = RecommendationStatus.DA_VALUTARE,
                explanation = "Il vaccino risulta già effettuato. Verificare il numero di dosi e le tempistiche previste."
            )
        )
    } else if (hasPneumococcalRisk) {
        results.add(
            VaccineResult(
                vaccine = VaccineType.PNEUMOCOCCICO,
                status = RecommendationStatus.RACCOMANDATO,
                explanation = "Raccomandato nel prototipo per età pari o superiore a 65 anni o per presenza di patologie cardiache o respiratorie."
            )
        )
    } else {
        results.add(
            VaccineResult(
                vaccine = VaccineType.PNEUMOCOCCICO,
                status = RecommendationStatus.POSSIBILE,
                explanation = "Può essere valutato in base al profilo clinico, alla storia vaccinale e alle indicazioni aggiornate."
            )
        )
    }

    results.add(
        VaccineResult(
            vaccine = VaccineType.VIVI_ATTENUATI,
            status = RecommendationStatus.CONTROINDICATO,
            explanation = "Nel prototipo didattico vengono considerati controindicati durante terapia biologica. $therapyExplanation La valutazione clinica reale deve essere effettuata dal medico."
        )
    )

    if (therapy == TherapyType.ALTRO_IMMUNOSOPPRESSORE) {
        results.add(
            VaccineResult(
                vaccine = VaccineType.PIANIFICAZIONE_VACCINALE,
                status = RecommendationStatus.DA_VALUTARE,
                explanation = "Il tipo di immunosoppressore non è specificato nel prototipo. È necessario verificare farmaco, dose, durata della terapia, vaccinazioni precedenti e tempistica con il medico."
            )
        )
    }

    if (age >= 50) {
        if (VaccineType.HERPES_ZOSTER in completedVaccines) {
            results.add(
                VaccineResult(
                    vaccine = VaccineType.HERPES_ZOSTER,
                    status = RecommendationStatus.DA_VALUTARE,
                    explanation = "Il vaccino risulta già effettuato. Verificare tipo di vaccino, dosi ricevute e tempistiche."
                )
            )
        } else {
            results.add(
                VaccineResult(
                    vaccine = VaccineType.HERPES_ZOSTER,
                    status = RecommendationStatus.POSSIBILE,
                    explanation = "Può essere valutato nel prototipo in base all'età, alle condizioni cliniche e al tipo di vaccino disponibile."
                )
            )
        }
    }

    if (VaccineType.COVID_19 !in completedVaccines) {
        results.add(
            VaccineResult(
                vaccine = VaccineType.COVID_19,
                status = RecommendationStatus.POSSIBILE,
                explanation = "Nel prototipo richiede verifica del calendario vaccinale aggiornato e delle eventuali dosi precedenti."
            )
        )
    }

    if (VaccineType.EPATITE_B !in completedVaccines) {
        results.add(
            VaccineResult(
                vaccine = VaccineType.EPATITE_B,
                status = RecommendationStatus.POSSIBILE,
                explanation = "Può essere valutato in base ai fattori di rischio, alla documentazione vaccinale e alla situazione clinica."
            )
        )
    }

    if (ClinicalCondition.GRAVIDANZA in conditions) {
        results.add(
            VaccineResult(
                vaccine = VaccineType.VACCINI_IN_GRAVIDANZA,
                status = RecommendationStatus.DA_VALUTARE,
                explanation = "È necessaria una valutazione clinica specifica della gravidanza e del calendario vaccinale."
            )
        )
    }

    return results
}