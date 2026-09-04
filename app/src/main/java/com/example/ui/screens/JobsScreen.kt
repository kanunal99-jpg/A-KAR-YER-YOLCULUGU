package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.model.JobListing
import com.example.domain.CareerAgentEngine
import com.example.ui.components.ScoreBadge
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*
import com.example.ui.viewmodel.CareerViewModel

@Composable
fun JobsScreen(
    viewModel: CareerViewModel,
    onNavigateToInterview: (JobListing) -> Unit
) {
    val jobs by viewModel.jobs.collectAsState(initial = emptyList())
    val searchQuery by viewModel.userSearchQuery.collectAsState()
    val activeRole by viewModel.activeFilterRole.collectAsState()
    val profileState by viewModel.profile.collectAsState()
    val selectedJob by viewModel.selectedJob.collectAsState()
    val matchResult by viewModel.jobMatchResult.collectAsState()

    var tailoredCvSuccessMessage by remember { mutableStateOf<String?>(null) }
    var applicationSubmittedMessage by remember { mutableStateOf<String?>(null) }

    val profile = profileState

    val discoveryRoles = remember {
        listOf("Tümü", "Bölge Satış", "Ticari Pazarlama", "Zincir Mağazalar", "Satış Operasyon")
    }

    val filteredJobs = remember(jobs, searchQuery, activeRole) {
        jobs.filter { job ->
            val matchesQuery = searchQuery.isEmpty() ||
                    job.title.contains(searchQuery, ignoreCase = true) ||
                    job.company.contains(searchQuery, ignoreCase = true) ||
                    job.requiredSkills.any { it.contains(searchQuery, ignoreCase = true) }

            val matchesRole = activeRole == "Tümü" ||
                    job.title.contains(activeRole, ignoreCase = true) ||
                    job.sector.contains(activeRole, ignoreCase = true)

            matchesQuery && matchesRole
        }
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
                title = "Hedefe Özel Eşleşmeler",
                subtitle = "Doğrulanmış ilanlar, şeffaf uyum analizleri ve ATS optimizasyonu"
            )
        }

        // Search Input with Geometric styling
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Pozisyon, şirket veya yetkinlik ara...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Temizle")
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Multi-Role Discovery Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(discoveryRoles) { role ->
                    val isSelected = activeRole == role
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setFilterRole(role) },
                        label = { Text(role, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }
        }

        // Job Cards List with Geometric Balance Styling
        items(filteredJobs) { job ->
            val match = remember(job, profile) {
                CareerAgentEngine.evaluateJobMatch(profile, job)
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectJob(job) }
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.size(46.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Surface(
                                        color = BrandPrimary,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = job.company.take(1).uppercase(),
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Column {
                                Text(
                                    text = job.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${job.company} • ${job.sector}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        ScoreBadge(score = match.overallMatchPercent, label = "Uyumluluk")
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(job.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(job.salaryRange.split("+").first().trim(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    // Verification badge
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = MatchGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${job.source} (Doğrulanmış İlan)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Yetenek: %${match.skillsMatchPercent}  |  Deneyim: %${match.experienceMatchPercent}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        FilledTonalButton(
                            onClick = { viewModel.selectJob(job) },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Eşleşme Detayı", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Full Job Match Detail Dialog
    selectedJob?.let { job ->
        matchResult?.let { match ->
            AlertDialog(
                onDismissRequest = { viewModel.clearJobSelection() },
                shape = RoundedCornerShape(28.dp),
                title = {
                    Column {
                        Text(job.title, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        Text("${job.company} • ${job.location}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                text = {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 440.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // AI Semantic Insight: Real meaning of job
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("İlanın Gerçek Anlamı (AI İçgörüsü):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(match.aiInsight, style = MaterialTheme.typography.bodySmall, lineHeight = 17.sp)
                                }
                            }
                        }

                        // Why fit
                        item {
                            Text("Neden Uygunsunuz? (Kanıtlı Güçlü Yönler):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MatchGreen)
                            match.whyYouAreFit.forEach { fit ->
                                Text("✓ $fit", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }

                        // Why not fit & Gaps
                        item {
                            Text("Neden Tam Uygun Değilsiniz? (Eksikler):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.error)
                            match.whyYouAreNotFit.forEach { gap ->
                                Text("! $gap", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }

                        // Actionable Steps
                        item {
                            Text("Bu İşi Almak İçin Yol Haritası:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                            match.missingGapsToClose.forEach { step ->
                                Text("→ $step", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }

                        // Tailored actions
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.generateTailoredCvForJob(job) { generated ->
                                            tailoredCvSuccessMessage = "'${generated.title}' başarıyla oluşturuldu ve CV Sürümleri sekmesine eklendi!"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Özel CV Üret", fontSize = 11.sp)
                                }

                                FilledTonalButton(
                                    onClick = {
                                        viewModel.saveJobForLater(job)
                                        applicationSubmittedMessage = "${job.company} başvurusu takip listesine eklendi!"
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Başvuruyu Kaydet", fontSize = 11.sp)
                                }
                            }
                        }

                        // Start Interview
                        item {
                            Button(
                                onClick = {
                                    onNavigateToInterview(job)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.RecordVoiceOver, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Bu İlana Özel Mülakat Başlat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        tailoredCvSuccessMessage?.let { msg ->
                            item {
                                Surface(
                                    color = MatchGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = MatchGreen,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }

                        applicationSubmittedMessage?.let { msg ->
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = msg,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearJobSelection() }) {
                        Text("Kapat")
                    }
                }
            )
        }
    }
}
