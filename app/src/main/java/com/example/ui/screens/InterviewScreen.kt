package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InterviewMode
import com.example.data.model.JobListing
import com.example.domain.SampleData
import com.example.ui.components.ScoreBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.MatchGreen
import com.example.ui.viewmodel.CareerViewModel

@Composable
fun InterviewScreen(
    viewModel: CareerViewModel
) {
    val session by viewModel.interviewSession.collectAsState()
    val allJobs by viewModel.jobs.collectAsState(initial = emptyList())
    var selectedJobForSimulation by remember { mutableStateOf(SampleData.sampleJobs.first()) }
    var selectedMode by remember { mutableStateOf(InterviewMode.JOB_SPECIFIC) }
    var answerInputText by remember { mutableStateOf("") }
    var showAddedConfirmation by remember { mutableStateOf(false) }

    val jobsList: List<JobListing> = remember(allJobs) {
        allJobs.ifEmpty { SampleData.sampleJobs }
    }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            SectionHeader(
                title = "Mülakat Simülasyonu",
                subtitle = "Pozisyona ve sektöre özel STAR soruları, kriz vakaları ve canlı AI koçluğu"
            )
        }

        // Setup & Mode Selection Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Simülasyon Modu:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(InterviewMode.values()) { mode ->
                            val isSelected = selectedMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedMode = mode
                                    viewModel.startInterview(selectedJobForSimulation, mode)
                                },
                                label = { Text(mode.titleTr, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(12.dp),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Hedef Şirket & Pozisyon:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(jobsList) { job ->
                            val isSelected = selectedJobForSimulation.id == job.id
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedJobForSimulation = job
                                    viewModel.startInterview(job, selectedMode)
                                },
                                label = { Text("${job.company} — ${job.title}", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(12.dp),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            )
                        }
                    }
                }
            }
        }

        session?.let { activeSession ->
            if (activeSession.isCompleted && activeSession.report != null) {
                // Completed Evaluation Report in Geometric Balance
                val rep = activeSession.report!!
                item {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        elevation = CardDefaults.cardElevation(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(22.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text("MÜLAKAT KARNESİ", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = Color(0xFFEADDFF))
                                    Text("${activeSession.company} — ${activeSession.jobTitle}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                ScoreBadge(score = rep.overallScore, label = "Başarı")
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(rep.executiveSummary, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f), lineHeight = 20.sp)

                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = Color.White.copy(alpha = 0.25f))
                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Kategori Dağılımı:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Teknik: %${rep.technicalScore}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                                Text("Davranışsal: %${rep.behavioralScore}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                                Text("Problem Çözme: %${rep.problemSolvingScore}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                                Text("İletişim: %${rep.communicationScore}", fontSize = 12.sp, color = Color.White.copy(alpha = 0.85f))
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.startInterview(selectedJobForSimulation, selectedMode)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFEADDFF),
                                    contentColor = Color(0xFF21005D)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Yeni Bir Mülakat Başlat", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Active Question Answering
                val currentQ = activeSession.questions.getOrNull(activeSession.currentQuestionIndex)
                if (currentQ != null) {
                    item {
                        Card(
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            elevation = CardDefaults.cardElevation(1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "Soru ${activeSession.currentQuestionIndex + 1} / ${activeSession.questions.size} • ${currentQ.questionCategory}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = currentQ.questionText,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Bağlam: ${currentQ.contextReason}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = answerInputText,
                                    onValueChange = { answerInputText = it },
                                    placeholder = { Text("Cevabınızı girin (STAR metodu: Durum, Görev, Eylem ve Somut Sonuç)...", fontSize = 13.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        if (answerInputText.isNotBlank()) {
                                            viewModel.submitInterviewAnswer(answerInputText)
                                            answerInputText = ""
                                        }
                                    },
                                    enabled = answerInputText.isNotBlank(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier.fillMaxWidth().height(46.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cevabı Gönder & Yapay Zeka Değerlendirsin", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // AI Realtime Feedback on previously answered questions
                val answeredList = activeSession.questions.filter { it.isAnswered }
                if (answeredList.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "AI Değerlendirmeleri",
                            subtitle = "Cevaplarınızın analizi ve puanlama"
                        )
                    }

                    items(answeredList) { ans ->
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = ans.questionCategory, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    ScoreBadge(score = ans.score, label = "Puan")
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Cevabınız: ${ans.userAnswer}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "💡 Koç Geri Bildirimi: ${ans.feedback}",
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(12.dp),
                                        lineHeight = 17.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // MÜLAKAT → CV ENTEGRASYONU
                val newlyDiscovered = activeSession.discoveredExperiences.filter { !it.addedToProfile }
                if (newlyDiscovered.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Yeni Başarı Keşfedildi!",
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Mülakatta paylaştığınız bu somut başarı CV'nizde bulunmuyor. Master Profile'ınıza ekleyelim mi?",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                newlyDiscovered.forEach { disc ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        shape = RoundedCornerShape(14.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(disc.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(disc.description, style = MaterialTheme.typography.bodySmall, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = {
                                                    viewModel.addDiscoveredExperienceToProfile(disc)
                                                    showAddedConfirmation = true
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                                shape = RoundedCornerShape(10.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                            ) {
                                                Text("CV'ye Ekle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddedConfirmation) {
        AlertDialog(
            onDismissRequest = { showAddedConfirmation = false },
            shape = RoundedCornerShape(24.dp),
            title = { Text("Master Profile Güncellendi", fontWeight = FontWeight.Bold) },
            text = { Text("Mülakatta paylaştığınız başarı Master Career Profile'a eklendi ve CV Güç Puanınız artırıldı!") },
            confirmButton = {
                Button(
                    onClick = { showAddedConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Harika")
                }
            }
        )
    }
}
