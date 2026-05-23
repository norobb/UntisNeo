package com.example
import com.example.ui.StringResources


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.UntisViewModel
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: UntisViewModel = viewModel()
            val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
            MyApplicationTheme(darkTheme = isDarkTheme, dynamicColor = viewModel.useStockThemePref) {
                MainAppLayout(viewModel)
            }
        }
    }
}

@Composable
fun MainAppLayout(viewModel: UntisViewModel) {
    if (viewModel.currentScreen == "LOGON") {
        LogonScreen(viewModel)
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Elegant M3 / Nothing Style bottom navigation
            // Match pure black design from screenshots
            NavigationBar(
                containerColor = NothingBlack,
                tonalElevation = 0.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                NavigationBarItem(
                    selected = viewModel.currentScreen == "HOME",
                    onClick = { viewModel.currentScreen = "HOME" },
                    icon = { Icon(Icons.Default.Home, contentDescription = StringResources.get("Home")) },
                    label = { Text(StringResources.get("Home"),  fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.background,
                        selectedTextColor = NothingRed,
                        unselectedIconColor = NothingMutedGray,
                        unselectedTextColor = NothingMutedGray,
                        indicatorColor = NothingRed
                    )
                )

                NavigationBarItem(
                    selected = viewModel.currentScreen == "TIMETABLE",
                    onClick = { viewModel.currentScreen = "TIMETABLE" },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = StringResources.get("Timetable")) },
                    label = { Text(StringResources.get("Timetable"), fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NothingBlack,
                        selectedTextColor = NothingRed,
                        unselectedIconColor = NothingMutedGray,
                        unselectedTextColor = NothingMutedGray,
                        indicatorColor = NothingRed
                    )
                )

                NavigationBarItem(
                    selected = viewModel.currentScreen == "MESSAGES",
                    onClick = { viewModel.currentScreen = "MESSAGES" },
                    icon = { Icon(Icons.Default.Email, contentDescription = StringResources.get("Messages")) },
                    label = { Text(StringResources.get("Messages"),  fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NothingBlack,
                        selectedTextColor = NothingRed,
                        unselectedIconColor = NothingMutedGray,
                        unselectedTextColor = NothingMutedGray,
                        indicatorColor = NothingRed
                    )
                )

                // Profile navigations leads to customizable modular pupil sections
                val isProfileSection = viewModel.currentScreen in listOf("PROFILE", StringResources.get("HOMEWORK"), "GRADES", "CHATBOT", "SETTINGS")
                NavigationBarItem(
                    selected = isProfileSection,
                    onClick = { viewModel.currentScreen = "PROFILE" },
                    icon = { Icon(Icons.Default.Person, contentDescription = StringResources.get("Profile")) },
                    label = { Text(StringResources.get("Profile"), fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NothingBlack,
                        selectedTextColor = NothingRed,
                        unselectedIconColor = NothingMutedGray,
                        unselectedTextColor = NothingMutedGray,
                        indicatorColor = NothingRed
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(NothingBlack)
                .padding(innerPadding)
        ) {
            when (viewModel.currentScreen) {
                "HOME" -> HomeScreen(viewModel)
                "TIMETABLE" -> TimetableScreen(viewModel)
                "MESSAGES" -> MessagesScreen(viewModel)
                "PROFILE" -> ProfileScreen(viewModel)
                StringResources.get("HOMEWORK") -> HomeworkScreen(viewModel)
                "GRADES" -> GradesScreen(viewModel)
                "CHATBOT" -> ChatbotScreen(viewModel)
                "SETTINGS" -> SettingsScreen(viewModel)
                "ARCADE" -> ArcadeScreen(viewModel)
                "INFO" -> InfoScreen(viewModel)
            }
        }
    }
}

// --- STANDARD REPLICATED PROFILE SCREEN SECTIONS ---
@Composable
fun ProfileScreen(viewModel: UntisViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper student card header
        NothingHeader(text = "Profil", fontSize = 28.sp)

        Surface(
            color = NothingCardGray,
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(NothingWhite, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        fontFamily = FontFamily.Monospace,
                        color = NothingBlack,
                        fontSize = 24.sp,
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = viewModel.userInput.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NothingWhite,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Gelehrtenschule des Johanneums",
                        fontFamily = FontFamily.Monospace,
                        color = NothingMutedGray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Action menu listings matching screenshot profile
        Surface(
            color = NothingCardGray,
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, Color(0xFF333333)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                ProfileOptionItem(
                    icon = Icons.Default.List,
                    title = "Hausaufgabenliste",
                    onClick = { viewModel.currentScreen = StringResources.get("HOMEWORK") }
                )
                Divider(color = Color(0xFF333333))
                ProfileOptionItem(
                    icon = Icons.Default.Star,
                    title = "Notenverwaltung",
                    onClick = { viewModel.currentScreen = "GRADES" }
                )
                Divider(color = Color(0xFF333333))
                ProfileOptionItem(
                    icon = Icons.Default.Info,
                    title = "Info & Credits",
                    onClick = { viewModel.currentScreen = "INFO" }
                )
                Divider(color = Color(0xFF333333))
                ProfileOptionItem(
                    icon = Icons.Default.Face,
                    title = "KI Hausaufgaben-Scanner",
                    onClick = { viewModel.currentScreen = "CHATBOT" }
                )
                Divider(color = Color(0xFF333333))
                ProfileOptionItem(
                    icon = Icons.Default.Settings,
                    title = StringResources.get("Einstellungen"),
                    onClick = { viewModel.currentScreen = "SETTINGS" }
                )
            }
        }

        // Lower Log out shortcut
        Spacer(modifier = Modifier.weight(1f))
        NothingButton(
            text = "Abmelden",
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth(),
            isPrimary = false
        )
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = NothingWhite, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontFamily = FontFamily.Monospace,
                color = NothingWhite,
                fontSize = 14.sp
            )
        }
        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = NothingMutedGray)
    }
}
