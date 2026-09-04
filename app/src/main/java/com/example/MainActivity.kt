package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FactCheck
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.AiKariyerTheme
import com.example.ui.theme.NavBarBackground
import com.example.ui.theme.NavPillActive
import com.example.ui.theme.NavPillActiveContent
import com.example.ui.viewmodel.CareerViewModel
import com.example.ui.viewmodel.CareerViewModelFactory

enum class CareerNavTab(val title: String, val icon: ImageVector) {
    HOME("Ana Sayfa", Icons.Default.Home),
    PROFILE("Kariyer & CV", Icons.Default.Badge),
    JOBS("İş İlanları", Icons.Default.Work),
    INTERVIEW("Mülakat", Icons.Default.RecordVoiceOver),
    APPLICATIONS("Başvurular", Icons.AutoMirrored.Filled.FactCheck)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AiKariyerTheme {
                val factory = remember { CareerViewModelFactory(applicationContext) }
                val viewModel: CareerViewModel = viewModel(factory = factory)
                CareerMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CareerMainApp(viewModel: CareerViewModel) {
    var selectedTab by remember { mutableStateOf(CareerNavTab.HOME) }
    val profile by viewModel.profile.collectAsState(initial = null)

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ARAMA KARİYER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Hoş geldin, ${profile?.fullName?.split(" ")?.firstOrNull() ?: "Caner"}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.4).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    val initials = profile?.fullName
                        ?.split(" ")
                        ?.filter { it.isNotBlank() }
                        ?.take(2)
                        ?.joinToString(".") { it.first().toString() }
                        ?: "C.B"

                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = NavBarBackground,
                tonalElevation = 0.dp,
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(0.dp)
                )
            ) {
                CareerNavTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NavPillActiveContent,
                            selectedTextColor = NavPillActiveContent,
                            indicatorColor = NavPillActive,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                CareerNavTab.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToJobs = { selectedTab = CareerNavTab.JOBS },
                    onNavigateToProfile = { selectedTab = CareerNavTab.PROFILE },
                    onNavigateToInterview = { selectedTab = CareerNavTab.INTERVIEW }
                )
                CareerNavTab.PROFILE -> ProfileCvScreen(
                    viewModel = viewModel
                )
                CareerNavTab.JOBS -> JobsScreen(
                    viewModel = viewModel,
                    onNavigateToInterview = { job ->
                        viewModel.selectJob(job)
                        viewModel.startInterview(job)
                        selectedTab = CareerNavTab.INTERVIEW
                    }
                )
                CareerNavTab.INTERVIEW -> InterviewScreen(
                    viewModel = viewModel
                )
                CareerNavTab.APPLICATIONS -> ApplicationsScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
