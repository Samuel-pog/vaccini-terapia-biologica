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
import androidx.compose.material3.Divider
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
        mutableStateOf("")
    }

    var results by rememberSaveable {
        mutableStateOf<List<VaccineResult>>(emptyList())
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    val conditions = listOf(
        "Nessuna",
        "Diabete",
        "Malattia cardiaca",
        "Malattia respiratoria",
        "Gravidanza"
    )

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
                onTherapySelected = {
                    therapy = it
                }
            )

            OutlinedTextField(
                value = ageText,
                onValueChange = {
                    ageText = it
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

            conditions.forEach { condition ->
                ConditionCheckbox(
                    condition = condition,
                    selectedConditions = selectedConditions,
                    onConditionChanged = { selected ->
                        selectedConditions = selected
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

                        selectedConditions.isBlank() -> {
                            errorMessage = "Seleziona almeno una condizione clinica."
                            results = emptyList()
                        }

                        else -> {
                            errorMessage = ""
                            results = calculateRecommendations(
                                therapy = therapy,
                                age = age,
                                condition = selectedConditions
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

            if (results.isNotEmpty()) {
                Divider()

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
                .menuAnchor(),
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
    selectedConditions: String,
    onConditionChanged: (String) -> Unit
) {
    val isChecked = selectedConditions == condition

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                if (it) {
                    onConditionChanged(condition)
                } else {
                    onConditionChanged("")
                }
            }
        )

        Text(text = condition)
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
    condition: String
): List<VaccineResult> {
    val results = mutableListOf<VaccineResult>()

    results.add(
        VaccineResult(
            name = "Vaccino antinfluenzale",
            status = "Raccomandato",
            explanation = "Vaccino non vivo generalmente considerato indicato nei pazienti in terapia biologica."
        )
    )

    if (age >= 65 || condition == "Malattia respiratoria" || condition == "Malattia cardiaca") {
        results.add(
            VaccineResult(
                name = "Vaccino pneumococcico",
                status = "Raccomandato",
                explanation = "Raccomandato in presenza di età avanzata o di alcune condizioni cliniche."
            )
        )
    } else {
        results.add(
            VaccineResult(
                name = "Vaccino pneumococcico",
                status = "Possibile",
                explanation = "Può essere valutato in base al profilo clinico e alla storia vaccinale."
            )
        )
    }

    results.add(
        VaccineResult(
            name = "Vaccini vivi attenuati",
            status = "Controindicato",
            explanation = "Nel prototipo vengono considerati controindicati durante terapia biologica."
        )
    )

    if (age >= 50) {
        results.add(
            VaccineResult(
                name = "Vaccino contro herpes zoster",
                status = "Possibile",
                explanation = "La valutazione dipende dall'età, dalle condizioni cliniche e dal tipo di vaccino."
            )
        )
    }

    if (condition == "Gravidanza") {
        results.add(
            VaccineResult(
                name = "Vaccini in gravidanza",
                status = "Da valutare",
                explanation = "È necessaria una valutazione specifica da parte del medico."
            )
        )
    }

    return results
}