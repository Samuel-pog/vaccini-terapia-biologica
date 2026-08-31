package com.example.vacciniterapiabiologica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.material3.ExposedDropdownMenuAnchorType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                VacciniApp()
            }
        }
    }
}

data class VaccineResult(
    val name: String,
    val status: String,
    val explanation: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VacciniApp() {

    var therapy by rememberSaveable {
        mutableStateOf("")
    }

    var ageText by rememberSaveable {
        mutableStateOf("")
    }

    var selectedConditions by rememberSaveable {
        mutableStateOf<Set<String>>(emptySet())
    }

    var completedVaccines by rememberSaveable {
        mutableStateOf<Set<String>>(emptySet())
    }

    var results by remember {
        mutableStateOf<List<VaccineResult>>(emptyList())
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    val conditions = listOf(
        "Diabete",
        "Malattia cardiaca",
        "Malattia respiratoria",
        "Gravidanza"
    )

    val historyVaccines = listOf(
        "Vaccino antinfluenzale",
        "Vaccino pneumococcico",
        "Vaccino contro herpes zoster",
        "Vaccino COVID-19",
        "Vaccino contro epatite B"
    )

    fun resetForm() {
        therapy = ""
        ageText = ""
        selectedConditions = emptySet()
        completedVaccines = emptySet()
        results = emptyList()
        errorMessage = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Vaccini in terapia biologica")
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Supporto decisionale didattico",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Inserisci i dati del paziente per ottenere una classificazione semplificata dei vaccini."
            )

            TherapyDropdown(
                selectedTherapy = therapy,
                onTherapySelected = { selectedTherapy ->
                    therapy = selectedTherapy
                    results = emptyList()
                    errorMessage = ""
                }
            )

            OutlinedTextField(
                value = ageText,
                onValueChange = {
                    ageText = it
                    results = emptyList()
                    errorMessage = ""
                },
                label = {
                    Text("Età del paziente")
                },
                placeholder = {
                    Text("Esempio: 65")
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = "Condizioni cliniche",
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Se non sono presenti condizioni cliniche, lascia tutte le opzioni non selezionate.",
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            conditions.forEach { condition ->
                ConditionCheckbox(
                    condition = condition,
                    selectedConditions = selectedConditions,
                    onConditionChanged = { updatedConditions ->
                        selectedConditions = updatedConditions
                        results = emptyList()
                        errorMessage = ""
                    }
                )
            }

            Text(
                text = "Storia vaccinale",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Seleziona i vaccini già effettuati, se conosciuti.",
                fontSize = 13.sp,
                color = Color.DarkGray
            )

            historyVaccines.forEach { vaccine ->
                VaccinationHistoryCheckbox(
                    vaccine = vaccine,
                    completedVaccines = completedVaccines,
                    onVaccinationChanged = { updatedVaccines ->
                        completedVaccines = updatedVaccines
                        results = emptyList()
                        errorMessage = ""
                    }
                )
            }

            Button(
                onClick = {
                    val age = ageText.toIntOrNull()

                    when {
                        therapy.isBlank() -> {
                            errorMessage = "Seleziona una terapia biologica."
                            results = emptyList()
                        }

                        age == null -> {
                            errorMessage = "Inserisci un'età numerica valida."
                            results = emptyList()
                        }

                        age < 0 || age > 120 -> {
                            errorMessage = "Inserisci un'età compresa tra 0 e 120 anni."
                            results = emptyList()
                        }

                        else -> {
                            errorMessage = ""
                            results = calculateRecommendations(
                                therapy = therapy,
                                age = age,
                                conditions = selectedConditions,
                                completedVaccines = completedVaccines
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcola raccomandazioni")
            }

            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { resetForm() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Azzera dati")
            }

            if (results.isNotEmpty()) {
                HorizontalDivider()

                Text(
                    text = "Risultati",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                ResultsSection(results)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Avvertenza: questa applicazione è un prototipo didattico e non sostituisce le linee guida ufficiali né la valutazione di un medico.",
                    modifier = Modifier.padding(16.dp),
                    color = Color.DarkGray,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TherapyDropdown(
    selectedTherapy: String,
    onTherapySelected: (String) -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(false)
    }

    val therapies = listOf(
        "Anti-TNF",
        "Anti-IL17",
        "Anti-IL23",
        "Altro immunosoppressore"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedTherapy,
            onValueChange = {},
            readOnly = true,
            label = {
                Text("Terapia biologica")
            },
            placeholder = {
                Text("Seleziona una terapia")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true
                ),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            therapies.forEach { therapy ->
                DropdownMenuItem(
                    text = {
                        Text(therapy)
                    },
                    onClick = {
                        onTherapySelected(therapy)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ConditionCheckbox(
    condition: String,
    selectedConditions: Set<String>,
    onConditionChanged: (Set<String>) -> Unit
) {
    val isChecked = condition in selectedConditions

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { checked ->
                val updatedConditions = selectedConditions.toMutableSet()

                if (checked) {
                    updatedConditions.add(condition)
                } else {
                    updatedConditions.remove(condition)
                }

                onConditionChanged(updatedConditions)
            }
        )

        Text(text = condition)
    }
}

@Composable
fun VaccinationHistoryCheckbox(
    vaccine: String,
    completedVaccines: Set<String>,
    onVaccinationChanged: (Set<String>) -> Unit
) {
    val isChecked = vaccine in completedVaccines

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { checked ->
                val updatedVaccines = completedVaccines.toMutableSet()

                if (checked) {
                    updatedVaccines.add(vaccine)
                } else {
                    updatedVaccines.remove(vaccine)
                }

                onVaccinationChanged(updatedVaccines)
            }
        )

        Text(text = "$vaccine già effettuato")
    }
}

@Composable
fun ResultsSection(results: List<VaccineResult>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        results.forEach { result ->
            VaccineCard(result)
        }
    }
}

@Composable
fun VaccineCard(result: VaccineResult) {
    val statusColor = when (result.status) {
        "Raccomandato" -> Color(0xFF2E7D32)
        "Possibile" -> Color(0xFF1565C0)
        "Controindicato" -> Color(0xFFC62828)
        "Da valutare" -> Color(0xFFEF6C00)
        else -> Color.DarkGray
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = result.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = result.status,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = result.explanation,
                fontSize = 14.sp
            )
        }
    }
}

fun calculateRecommendations(
    therapy: String,
    age: Int,
    conditions: Set<String>,
    completedVaccines: Set<String>
): List<VaccineResult> {
    val results = mutableListOf<VaccineResult>()

    if ("Vaccino antinfluenzale" in completedVaccines) {
        results.add(
            VaccineResult(
                name = "Vaccino antinfluenzale",
                status = "Da valutare",
                explanation = "Il vaccino risulta già effettuato. Verificare la data dell'ultima dose e il calendario vaccinale aggiornato."
            )
        )
    } else {
        results.add(
            VaccineResult(
                name = "Vaccino antinfluenzale",
                status = "Raccomandato",
                explanation = "Vaccino non vivo generalmente considerato indicato nel prototipo per pazienti in terapia biologica."
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
            explanation = "Nel prototipo didattico vengono considerati controindicati durante terapia biologica. La valutazione clinica reale deve essere effettuata dal medico."
        )
    )

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