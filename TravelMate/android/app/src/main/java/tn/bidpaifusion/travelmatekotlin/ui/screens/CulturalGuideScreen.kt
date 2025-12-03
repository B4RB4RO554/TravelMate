package tn.bidpaifusion.travelmatekotlin.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import tn.bidpaifusion.travelmatekotlin.viewmodel.CulturalGuideViewModel

data class PhraseSuggestion(
    val english: String,
    val translation: String,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CulturalGuideScreen(
    onNavigateBack: () -> Unit,
    viewModel: CulturalGuideViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    
    // Common phrases for travelers
    val commonPhrases = remember {
        listOf(
            PhraseSuggestion("Hello", "Bonjour / مرحبا", "Greetings"),
            PhraseSuggestion("Thank you", "Merci / شكرا", "Greetings"),
            PhraseSuggestion("Please", "S'il vous plaît / من فضلك", "Greetings"),
            PhraseSuggestion("How much?", "Combien? / بكم؟", "Shopping"),
            PhraseSuggestion("Where is...?", "Où est...? / أين...؟", "Directions"),
            PhraseSuggestion("I need help", "J'ai besoin d'aide / أحتاج مساعدة", "Emergency"),
            PhraseSuggestion("Hospital", "Hôpital / مستشفى", "Emergency"),
            PhraseSuggestion("Police", "Police / شرطة", "Emergency")
        )
    }

    val languages = listOf(
        "en" to "English",
        "fr" to "French",
        "ar" to "Arabic",
        "es" to "Spanish",
        "de" to "German",
        "it" to "Italian"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cultural Guide & Translation") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Translation Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "🌍 Translator",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // Language selectors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            var sourceExpanded by remember { mutableStateOf(false) }
                            var targetExpanded by remember { mutableStateOf(false) }

                            // Source language
                            ExposedDropdownMenuBox(
                                expanded = sourceExpanded,
                                onExpandedChange = { sourceExpanded = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = languages.find { it.first == state.sourceLanguage }?.second ?: "English",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("From") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sourceExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = sourceExpanded,
                                    onDismissRequest = { sourceExpanded = false }
                                ) {
                                    languages.forEach { (code, name) ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                viewModel.updateSourceLanguage(code)
                                                sourceExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = { viewModel.swapLanguages() }) {
                                Icon(Icons.Default.SwapHoriz, "Swap languages")
                            }

                            // Target language
                            ExposedDropdownMenuBox(
                                expanded = targetExpanded,
                                onExpandedChange = { targetExpanded = it },
                                modifier = Modifier.weight(1f)
                            ) {
                                OutlinedTextField(
                                    value = languages.find { it.first == state.targetLanguage }?.second ?: "French",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("To") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = targetExpanded) },
                                    modifier = Modifier.menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = targetExpanded,
                                    onDismissRequest = { targetExpanded = false }
                                ) {
                                    languages.forEach { (code, name) ->
                                        DropdownMenuItem(
                                            text = { Text(name) },
                                            onClick = {
                                                viewModel.updateTargetLanguage(code)
                                                targetExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Input text
                        OutlinedTextField(
                            value = state.inputText,
                            onValueChange = { viewModel.updateInputText(it) },
                            label = { Text("Enter text to translate") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 5
                        )

                        // Translate button
                        Button(
                            onClick = { viewModel.translate() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading && state.inputText.isNotBlank()
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            } else {
                                Icon(Icons.Default.Translate, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Translate")
                            }
                        }

                        // Translation result
                        if (state.translatedText != null) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Translation:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = state.translatedText!!,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        if (state.error != null) {
                            Text(
                                text = state.error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            // Common Phrases Section
            item {
                Text(
                    text = "💬 Common Travel Phrases",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(commonPhrases.groupBy { it.category }.entries.toList()) { (category, phrases) ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        phrases.forEach { phrase ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = phrase.english,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = phrase.translation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (phrase != phrases.last()) {
                                Divider()
                            }
                        }
                    }
                }
            }

            // Cultural Tips
            item {
                Text(
                    text = "📚 Cultural Tips",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CulturalTip(
                            icon = "🤝",
                            title = "Greetings",
                            description = "A handshake is common. In some cultures, slight bows or cheek kisses are traditional."
                        )
                        Divider()
                        CulturalTip(
                            icon = "🍽️",
                            title = "Dining Etiquette",
                            description = "Wait to be seated, and don't start eating until everyone is served."
                        )
                        Divider()
                        CulturalTip(
                            icon = "👕",
                            title = "Dress Code",
                            description = "Dress modestly when visiting religious sites. Cover shoulders and knees."
                        )
                        Divider()
                        CulturalTip(
                            icon = "💰",
                            title = "Tipping",
                            description = "Tipping customs vary. Research local practices: 10-15% is common in many places."
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CulturalTip(icon: String, title: String, description: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.headlineMedium
        )
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}
