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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ApplicationItem
import com.example.data.model.ApplicationStatus
import com.example.ui.components.SectionHeader
import com.example.ui.theme.MatchAmber
import com.example.ui.theme.MatchBlue
import com.example.ui.theme.MatchGreen
import com.example.ui.viewmodel.CareerViewModel

@Composable
fun ApplicationsScreen(
    viewModel: CareerViewModel
) {
    val applications by viewModel.applications.collectAsState(initial = emptyList())
    var selectedStatusFilter by remember { mutableStateOf<ApplicationStatus?>(null) }

    val filteredApps = remember(applications, selectedStatusFilter) {
        if (selectedStatusFilter == null) applications else applications.filter { it.status == selectedStatusFilter }
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
                title = "Başvuru Takip & Kariyer Yönetimi",
                subtitle = "Tüm başvurularınız, kullanılan CV sürümleri ve süreç aşamaları"
            )
        }

        // Geometric Learning & Optimization Insights Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Insights, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI Öğrenme & Tercih Analizi", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "FMCG ve Saha Liderliği başvurularınızda %85'in üzerinde dönüş potansiyeli tespit edildi. Ön yazınızda bayi konsolidasyonu ve ciro verilerini ön planda tuttuğunuz sürümler en yüksek ilgiyi görüyor.",
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Status Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    val isSelected = selectedStatusFilter == null
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = null },
                        label = { Text("Tümü (${applications.size})", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        shape = RoundedCornerShape(12.dp),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    )
                }
                items(ApplicationStatus.values()) { status ->
                    val count = applications.count { it.status == status }
                    val isSelected = selectedStatusFilter == status
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatusFilter = status },
                        label = { Text("${status.titleTr} ($count)", fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

        if (filteredApps.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(28.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AssignmentLate, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Henüz bu kategoride başvuru bulunmuyor", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("İş İlanları sekmesinden size uygun ilanları kaydedebilir veya doğrudan başvurabilirsiniz.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(filteredApps) { app ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = app.jobTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(text = "${app.company} • ${app.location}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        val statusBg = when (app.status) {
                            ApplicationStatus.OFFER, ApplicationStatus.ACCEPTED -> MatchGreen
                            ApplicationStatus.INTERVIEW -> MatchBlue
                            ApplicationStatus.APPLIED -> MaterialTheme.colorScheme.primary
                            ApplicationStatus.SAVED -> MatchAmber
                            ApplicationStatus.TO_APPLY -> MatchAmber
                            ApplicationStatus.REJECTED -> MaterialTheme.colorScheme.error
                        }
                        Surface(
                            color = statusBg.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = app.status.titleTr,
                                color = statusBg,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Kullanılan CV: ${app.cvVersionTitle}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                    if (app.coverLetterPreview.isNotEmpty()) {
                        Text(text = "Ön Yazı: ${app.coverLetterPreview.take(80)}...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Tarih: ${app.dateApplied}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            if (app.status == ApplicationStatus.SAVED || app.status == ApplicationStatus.TO_APPLY) {
                                FilledTonalButton(
                                    onClick = { viewModel.updateApplicationStatus(app.id, ApplicationStatus.APPLIED) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Başvuruldu Yap", fontSize = 11.sp)
                                }
                            } else if (app.status == ApplicationStatus.APPLIED) {
                                FilledTonalButton(
                                    onClick = { viewModel.updateApplicationStatus(app.id, ApplicationStatus.INTERVIEW) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Mülakat Aşaması", fontSize = 11.sp)
                                }
                            } else if (app.status == ApplicationStatus.INTERVIEW) {
                                FilledTonalButton(
                                    onClick = { viewModel.updateApplicationStatus(app.id, ApplicationStatus.OFFER) },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Teklif Alındı", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
