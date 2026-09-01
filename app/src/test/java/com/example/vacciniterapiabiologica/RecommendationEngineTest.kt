package com.example.vacciniterapiabiologica

import com.example.vacciniterapiabiologica.domain.calculateRecommendations
import com.example.vacciniterapiabiologica.model.ClinicalCondition
import com.example.vacciniterapiabiologica.model.RecommendationStatus
import com.example.vacciniterapiabiologica.model.TherapyType
import com.example.vacciniterapiabiologica.model.VaccineType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecommendationEngineTest {

    @Test
    fun patientOver65GetsRecommendedPneumococcalVaccine() {
        val results = calculateRecommendations(
            therapy = TherapyType.ANTI_TNF,
            age = 65,
            conditions = emptySet(),
            completedVaccines = emptySet()
        )

        val pneumococcalResult = results.first {
            it.vaccine == VaccineType.PNEUMOCOCCICO
        }

        assertEquals(
            RecommendationStatus.RACCOMANDATO,
            pneumococcalResult.status
        )
    }

    @Test
    fun cardiacConditionMakesPneumococcalVaccineRecommended() {
        val results = calculateRecommendations(
            therapy = TherapyType.ANTI_IL17,
            age = 30,
            conditions = setOf(ClinicalCondition.MALATTIA_CARDIACA),
            completedVaccines = emptySet()
        )

        val pneumococcalResult = results.first {
            it.vaccine == VaccineType.PNEUMOCOCCICO
        }

        assertEquals(
            RecommendationStatus.RACCOMANDATO,
            pneumococcalResult.status
        )
    }

    @Test
    fun noPneumococcalRiskMakesVaccinePossible() {
        val results = calculateRecommendations(
            therapy = TherapyType.ANTI_IL23,
            age = 30,
            conditions = emptySet(),
            completedVaccines = emptySet()
        )

        val pneumococcalResult = results.first {
            it.vaccine == VaccineType.PNEUMOCOCCICO
        }

        assertEquals(
            RecommendationStatus.POSSIBILE,
            pneumococcalResult.status
        )
    }

    @Test
    fun completedInfluenzaVaccineNeedsEvaluation() {
        val results = calculateRecommendations(
            therapy = TherapyType.ANTI_TNF,
            age = 40,
            conditions = emptySet(),
            completedVaccines = setOf(VaccineType.ANTINFLUENZALE)
        )

        val influenzaResult = results.first {
            it.vaccine == VaccineType.ANTINFLUENZALE
        }

        assertEquals(
            RecommendationStatus.DA_VALUTARE,
            influenzaResult.status
        )
    }

    @Test
    fun otherImmunosuppressorAddsPlanningWarning() {
        val results = calculateRecommendations(
            therapy = TherapyType.ALTRO_IMMUNOSOPPRESSORE,
            age = 40,
            conditions = emptySet(),
            completedVaccines = emptySet()
        )

        assertTrue(
            results.any {
                it.vaccine == VaccineType.PIANIFICAZIONE_VACCINALE &&
                        it.status == RecommendationStatus.DA_VALUTARE
            }
        )
    }

    @Test
    fun pregnancyAddsSpecificEvaluationWarning() {
        val results = calculateRecommendations(
            therapy = TherapyType.ANTI_TNF,
            age = 35,
            conditions = setOf(ClinicalCondition.GRAVIDANZA),
            completedVaccines = emptySet()
        )

        assertTrue(
            results.any {
                it.vaccine == VaccineType.VACCINI_IN_GRAVIDANZA &&
                        it.status == RecommendationStatus.DA_VALUTARE
            }
        )
    }

    @Test
    fun liveVaccinesAreContraindicatedInDidacticPrototype() {
        val results = calculateRecommendations(
            therapy = TherapyType.ANTI_IL17,
            age = 30,
            conditions = emptySet(),
            completedVaccines = emptySet()
        )

        val liveVaccinesResult = results.first {
            it.vaccine == VaccineType.VIVI_ATTENUATI
        }

        assertEquals(
            RecommendationStatus.CONTROINDICATO,
            liveVaccinesResult.status
        )
    }
}
