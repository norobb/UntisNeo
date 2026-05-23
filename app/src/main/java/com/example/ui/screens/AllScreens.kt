package com.example.ui.screens
import com.example.ui.StringResources


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.room.*
import com.example.ui.ChatMessage
import com.example.ui.UntisViewModel
import com.example.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

// --- LOGON SCREEN ---
@Composable
fun LogonScreen(viewModel: UntisViewModel) {
    var step by remember { mutableStateOf(1) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .widthIn(max = 450.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Ndot Header logo
            NothingHeader(text = StringResources.get("Untis Neo"), fontSize = 32.sp, showRedDot = true)
            Text(
                text = StringResources.get("Die schönere Oberfläche für deinen Schulalltag."),
                
                color = NothingMutedGray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = StringResources.get("ANMELDUNG"),
                        fontWeight = FontWeight.Bold,
                        color = NothingWhite,
                        fontSize = 16.sp
                    )

                    NothingTextField(
                        value = viewModel.serverInput,
                        onValueChange = { viewModel.serverInput = it },
                        label = "Server"
                    )

                    NothingTextField(
                        value = viewModel.schoolInput,
                        onValueChange = { viewModel.schoolInput = it },
                        label = "Schule"
                    )

                    NothingTextField(
                        value = viewModel.userInput,
                        onValueChange = { viewModel.userInput = it },
                        label = "Benutzername"
                    )

                    NothingTextField(
                        value = viewModel.passwordInput,
                        onValueChange = { viewModel.passwordInput = it },
                        label = "Passwort",
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.useDemoModePref = !viewModel.useDemoModePref },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Checkbox(
                            checked = viewModel.useDemoModePref,
                            onCheckedChange = { viewModel.useDemoModePref = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NothingRed, // Red active check state!
                                uncheckedColor = Color(0xFF333333),
                                checkmarkColor = MaterialTheme.colorScheme.background
                            )
                        )
                        Column {
                            Text(StringResources.get("Demo-Modus aktivieren"),
                                color = NothingWhite,
                                
                                fontSize = 13.sp
                            )
                            Text(StringResources.get("Lädt die echten Screenshots-Daten zur Vorschau!"),
                                color = NothingMutedGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            NothingButton(
                text = "Verbinden & Starten",
                onClick = {
                    if (viewModel.userInput.isEmpty() && !viewModel.useDemoModePref) {
                        Toast.makeText(context, StringResources.get("Bitte gib einen Benutzernamen ein oder starte den Demo-Modus!"), Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.saveAppSettings()
                        viewModel.currentScreen = "HOME"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            NothingButton(
                text = "Demo-Modus starten",
                onClick = {
                    viewModel.logonAsDemo()
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false
            )
        }
    }
}


// --- HOME DASHBOARD SCREEN ---
@Composable
fun HomeScreen(viewModel: UntisViewModel) {
    val lessons by viewModel.lessons.collectAsState()
    val homeworks by viewModel.homeworks.collectAsState()
    val memos by viewModel.eventMemos.collectAsState()
    val context = LocalContext.current

    // Current Date Formatter
    val curDateStr = SimpleDateFormat("EEEE, dd. MMMM yyyy", Locale.GERMAN).format(Date())

    // Active substitution alarm count
    val substitutionsTodayCount = lessons.filter {
        it.dateStr == "2026-05-22" && (it.status == "SUBSTITUTION" || it.status == "SHIFTED")
    }.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Status bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    NothingHeader(text = StringResources.get("LERNPLATZ"), showRedDot = true, fontSize = 28.sp)
                    Text(
                        text = curDateStr,
                        fontFamily = FontFamily.Monospace,
                        color = NothingMutedGray,
                        fontSize = 11.sp
                    )
                }
                IconButton(
                    onClick = { viewModel.triggerSync() },
                    modifier = Modifier.background(NothingCardGray, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = StringResources.get("Zwangssync"),
                        tint = NothingWhite
                    )
                }
            }
        }

        // Top syncing status
        if (viewModel.isSyncing) {
            item {
                Surface(
                    color = NothingCardGray,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = NothingWhite, strokeWidth = 2.dp)
                        Text(viewModel.syncMessage, fontFamily = FontFamily.Monospace, color = NothingWhite, fontSize = 12.sp)
                    }
                }
            }
        }

        // Real-Time Substituted/Room Change Alert system
        if (substitutionsTodayCount > 0) {
            item {
                Surface(
                    color = Color(0x20FF3131), // matches new NothingRed
                    shape = RoundedCornerShape(28.dp), // modern 28.dp rounded shape
                    border = BorderStroke(1.dp, NothingRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(NothingRed, shape = CircleShape)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "VERTRETUNGSALARM",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = NothingRed,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Du hast heute $substitutionsTodayCount kurzfristige Vertretungen oder Raumänderungen! Prüfe deinen Stundenplan.",
                                fontFamily = FontFamily.Monospace,
                                color = NothingWhite,
                                fontSize = 12.sp
                            )
                        }
                        Button(
                            onClick = {
                                viewModel.selectedWeekStart = "2026-05-18"
                                viewModel.selectedDayOfWeek = "Fr"
                                viewModel.currentScreen = "TIMETABLE"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NothingRed, contentColor = NothingBlack), // high contrast black on red button!
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Öffnen", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Seeding Banner Announcement (mimicking Gelehrtenschule desc Johanneums 5886)
        if (memos.isNotEmpty()) {
            items(memos) { memo ->
                Surface(
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = memo.title.uppercase(),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFF59E0B),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = memo.content,
                            fontFamily = FontFamily.Monospace,
                            color = NothingWhite,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Quick Stats/Shortcuts Widget (Pixel/Nothing Cards layout)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Widget Card 1: Homeworks Todo
                Surface(
                    color = NothingCardGray,
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color(0xFF333333)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.currentScreen = StringResources.get("HOMEWORK") }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val activeHwList = homeworks.filter { !it.isDone }
                        Text(
                            text = "${activeHwList.size}",
                            fontSize = 32.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NothingWhite
                        )
                        Text(
                            text = StringResources.get("Aufgaben offen"),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NothingMutedGray
                        )
                    }
                }

                // Widget Card 2: Messages / Absences combo
                Surface(
                    color = NothingCardGray,
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, Color(0xFF333333)),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.currentScreen = "MESSAGES" }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "0",
                            fontSize = 32.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NothingWhite
                        )
                        Text(
                            text = StringResources.get("Fehlstunden (0 krank)"),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = NothingMutedGray
                        )
                    }
                }
            }
        }

        // School Information Quick shortcuts mimicking original Untis
        item {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0xFF333333)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { Toast.makeText(context, StringResources.get("Sprechstunden geladen."), Toast.LENGTH_SHORT).show() }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = NothingWhite, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(StringResources.get("Lehrer Sprechstunden"), fontFamily = FontFamily.Monospace, color = NothingWhite, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NothingMutedGray)
                    }
                    Divider(color = Color(0xFF333333))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.selectedWeekStart = "2026-05-25"
                                viewModel.selectedDayOfWeek = "Mo"
                                viewModel.currentScreen = "TIMETABLE"
                                Toast.makeText(context, StringResources.get("Schulferien anzeigen..."), Toast.LENGTH_SHORT).show()
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DateRange, contentDescription = null, tint = NothingWhite, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(StringResources.get("Ferien & Feiertage"), fontFamily = FontFamily.Monospace, color = NothingWhite, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = NothingMutedGray)
                    }
                }
            }
        }

        // Prompt user for smart assist chatbot shortcuts
        item {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, Color(0xFF333333)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.currentScreen = "CHATBOT" }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(NothingWhite, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, tint = NothingBlack)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(StringResources.get("AI HAUSAUFGABEN HELFER"),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NothingWhite,
                            fontSize = 13.sp
                        )
                        Text(StringResources.get("Fotografiere dein Buch oder Arbeitsblatt, um Hausaufgaben direkt einzutragen!"),
                            fontFamily = FontFamily.Monospace,
                            color = NothingMutedGray,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}


// --- TIMETABLE SCREEN ---
@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun TimetableScreen(viewModel: UntisViewModel) {
    val lessons by viewModel.lessons.collectAsState()
    
    // Determine unique weeks
    val sdf = remember { java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()) }
    val weeks = remember(lessons) {
        lessons.map { lesson ->
            val date = sdf.parse(lesson.dateStr) ?: java.util.Date()
            val cal = java.util.Calendar.getInstance()
            cal.time = date
            cal.firstDayOfWeek = java.util.Calendar.MONDAY
            cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
            sdf.format(cal.time)
        }.distinct().sorted()
    }
    
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = if (weeks.isNotEmpty()) weeks.indexOf(viewModel.selectedWeekStart).coerceAtLeast(0) else 0,
        pageCount = { weeks.size.coerceAtLeast(1) }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        // Timetable Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = NothingWhite, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Stundenplan", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, color = NothingWhite, fontSize = 18.sp)
                }
                IconButton(
                    onClick = { viewModel.triggerSync() },
                    modifier = Modifier.background(NothingCardGray, shape = CircleShape).size(36.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = NothingWhite, modifier = Modifier.size(18.dp))
                }
            }

            // Pager Indicator / Week Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentWeekStr = weeks.getOrNull(pagerState.currentPage) ?: "Woche"
                val endDateStr = try {
                    val d = sdf.parse(currentWeekStr)
                    val c = java.util.Calendar.getInstance()
                    if (d != null) c.time = d
                    c.add(java.util.Calendar.DAY_OF_YEAR, 4)
                    sdf.format(c.time)
                } catch(e: Exception) { currentWeekStr }

                val displayTitle = if (currentWeekStr.length >= 8 && endDateStr.length >= 8) {
                    "${currentWeekStr.substring(8)}.${currentWeekStr.substring(5,7)}. - ${endDateStr.substring(8)}.${endDateStr.substring(5,7)}."
                } else currentWeekStr

                Text(
                    text = displayTitle,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NothingMutedGray,
                    modifier = Modifier.padding(start = 4.dp)
                )
                
                // Indicators
                if (weeks.size > 1) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in weeks.indices) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = if (i == pagerState.currentPage) NothingWhite else NothingCardGray,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        } // Header End

        // Pager for Weeks View
        if (weeks.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(StringResources.get("Keine Stunden."), fontFamily = FontFamily.Monospace, color = NothingMutedGray)
            }
        } else {
            androidx.compose.foundation.pager.HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val currentWeekStart = weeks[page]
                val filteredLessons = lessons.filter {
                    val date = sdf.parse(it.dateStr) ?: java.util.Date()
                    val cal = java.util.Calendar.getInstance()
                    cal.time = date
                    cal.firstDayOfWeek = java.util.Calendar.MONDAY
                    cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
                    sdf.format(cal.time) == currentWeekStart
                }
                
                // Grid View
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    // Periods Column (1 to 9)
                    Column(
                        modifier = Modifier
                            .width(20.dp)
                            .fillMaxHeight()
                            .padding(top = 24.dp) // account for day headers
                    ) {
                        for (period in 1..9) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$period",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = NothingMutedGray
                                )
                            }
                        }
                    }
                    
                    // Days Grid
                    val days = listOf("Mo" to "Montag", "Tu" to "Dienstag", "We" to "Mittwoch", "Th" to "Donnerstag", "Fr" to "Freitag")
                    
                    days.forEach { (dayCode, dayName) ->
                        val dayLessons = filteredLessons.filter { it.dayOfWeek == dayCode }
                        
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(horizontal = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier.height(24.dp).fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayName.take(2).uppercase(),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NothingWhite
                                )
                            }
                            
                            for (period in 1..9) {
                                val lessonList = dayLessons.filter { it.period == period }
                                val lesson = lessonList.firstOrNull() // take first if multiple (layered substitutions)
                                
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .padding(vertical = 1.dp)
                                ) {
                                    if (lesson != null) {
                                        val colorFromHex = try {
                                            Color(android.graphics.Color.parseColor("#" + lesson.colorHex))
                                        } catch (e: Exception) {
                                            NothingMutedGray
                                        }
                                        val isCancelled = lesson.status == "CANCELLED"
                                        val isSubstituted = lesson.status == "SUBSTITUTION"

                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    color = if (isCancelled) Color(0x30111111) else colorFromHex.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isCancelled) NothingRed.copy(alpha=0.5f) else colorFromHex.copy(alpha = 0.8f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = lesson.subjectCode,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isCancelled) NothingMutedGray else NothingWhite,
                                                    textDecoration = if (isCancelled) TextDecoration.LineThrough else null,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = lesson.roomCode,
                                                    fontFamily = FontFamily.Monospace,
                                                    fontSize = 8.sp,
                                                    color = if (isSubstituted) NothingRed else NothingMutedGray,
                                                    textAlign = TextAlign.Center,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
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
}

fun getGermanDayName(code: String): String {
    return when(code) {
        "Mo" -> "Montag"
        "Tu" -> "Dienstag"
        "We" -> "Mittwoch"
        "Th" -> "Donnerstag"
        "Fr" -> "Freitag"
        else -> code
    }
}


// --- MESSAGES SCREEN ---
@Composable
fun MessagesScreen(viewModel: UntisViewModel) {
    var selectedTab by remember { mutableStateOf("INBOX") }
    var searchInput by remember { mutableStateOf("") }

    val inbox by viewModel.messagesInbox.collectAsState()
    val sent by viewModel.messagesSent.collectAsState()

    val currentList = if (selectedTab == "INBOX") inbox else sent
    val filteredList = currentList.filter {
        it.sender.lowercase().contains(searchInput.lowercase()) ||
                it.subject.lowercase().contains(searchInput.lowercase()) ||
                it.content.lowercase().contains(searchInput.lowercase())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NothingHeader(text = StringResources.get("Mitteilungen"), fontSize = 28.sp)

            // Tabs toggle: Inbox, Sent, Drafts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("INBOX" to "Posteingang", "SENT" to "Gesendet").forEach { tabInfo ->
                    Button(
                        onClick = { selectedTab = tabInfo.first },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == tabInfo.first) NothingWhite else NothingCardGray,
                            contentColor = if (selectedTab == tabInfo.first) NothingBlack else NothingWhite
                        )
                    ) {
                        Text(tabInfo.second, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                }
            }

            // Search input field matching Untis screenshot
            NothingTextField(
                value = searchInput,
                onValueChange = { searchInput = it },
                label = StringResources.get("Inhalt oder Person suchen"),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NothingMutedGray) }
            )
        }

        // List Scroll
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = NothingMutedGray, modifier = Modifier.size(48.dp))
                        Text(
                            text = StringResources.get("Keine Nachrichten vorhanden."),
                            fontFamily = FontFamily.Monospace,
                            color = NothingMutedGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredList) { msg ->
                    var isExpanded by remember { mutableStateOf(false) }

                    Surface(
                        color = NothingCardGray,
                        shape = RoundedCornerShape(24.dp), // modern 24.dp round
                        border = BorderStroke(1.dp, Color(0xFF333333)), // updated border color
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(NothingWhite.copy(alpha = 0.1f), shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = msg.sender.firstOrNull()?.toString()?.uppercase() ?: "S",
                                            color = NothingWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = msg.sender,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            color = NothingWhite,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Betreff: ${msg.subject}",
                                            fontFamily = FontFamily.Monospace,
                                            color = NothingMutedGray,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                val sfd = SimpleDateFormat("dd.MM HH:mm", Locale.getDefault())
                                Text(
                                    text = sfd.format(Date(msg.timestamp)),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = NothingMutedGray
                                )
                            }

                            AnimatedVisibility(visible = isExpanded) {
                                Column(modifier = Modifier.padding(top = 12.dp)) {
                                    Divider(color = Color(0xFF2C2C2E))
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = msg.content,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        color = NothingWhite,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Compose Message Floating button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            FloatingActionButton(
                onClick = { viewModel.showSendMessageDialog = true },
                containerColor = NothingWhite,
                contentColor = NothingBlack,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = StringResources.get("Nachricht schreiben"))
            }
        }
    }

    // Modal Compose Message form dialog
    if (viewModel.showSendMessageDialog) {
        Dialog(onDismissRequest = { viewModel.showSendMessageDialog = false }) {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(StringResources.get("NEUE NACHRICHT"),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NothingWhite,
                        fontSize = 16.sp
                    )

                    NothingTextField(
                        value = viewModel.newMessageRecipient,
                        onValueChange = { viewModel.newMessageRecipient = it },
                        label = "Empfänger (nur Untis Neo Nutzer)"
                    )

                    NothingTextField(
                        value = viewModel.newMessageSubject,
                        onValueChange = { viewModel.newMessageSubject = it },
                        label = "Betreff"
                    )

                    NothingTextField(
                        value = viewModel.newMessageContent,
                        onValueChange = { viewModel.newMessageContent = it },
                        label = "Inhalt der Nachricht (Mehrzeilig)",
                        singleLine = false
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.showSendMessageDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = NothingWhite),
                            border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(StringResources.get("Bestätigen"), fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                viewModel.sendMessage(
                                    viewModel.newMessageRecipient,
                                    viewModel.newMessageSubject,
                                    viewModel.newMessageContent
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NothingWhite, contentColor = NothingBlack),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(StringResources.get("Senden"), fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}


// --- HOMEWORKS SCREEN ---
@Composable
fun HomeworkScreen(viewModel: UntisViewModel) {
    val homeworks by viewModel.homeworks.collectAsState()
    var showOnlyTodo by remember { mutableStateOf(false) }

    val filteredHw = homeworks.filter { if (showOnlyTodo) !it.isDone else true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NothingHeader(text = StringResources.get("Hausaufgaben"), fontSize = 28.sp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(StringResources.get("Nur ungelöste Aufgaben"),
                    fontFamily = FontFamily.Monospace,
                    color = NothingWhite,
                    fontSize = 13.sp
                )
                Switch(
                    checked = showOnlyTodo,
                    onCheckedChange = { showOnlyTodo = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = NothingWhite,
                        checkedTrackColor = NothingRed,
                        uncheckedThumbColor = NothingMutedGray,
                        uncheckedTrackColor = NothingDarkGray
                    )
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredHw.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(StringResources.get("Sehr schön, keine Hausaufgaben ausstehend!"),
                            fontFamily = FontFamily.Monospace,
                            color = NothingMutedGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(filteredHw) { hw ->
                    Surface(
                        color = NothingCardGray,
                        shape = RoundedCornerShape(24.dp), // modern 24.dp round
                        border = BorderStroke(1.dp, Color(0xFF333333)), // updated border color
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = hw.isDone,
                                onCheckedChange = { viewModel.toggleHomeworkCompletion(hw) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = NothingRed, // Red active check state!
                                    uncheckedColor = Color(0xFF333333),
                                    checkmarkColor = NothingBlack
                                )
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(NothingWhite.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = hw.subjectCode,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NothingWhite
                                        )
                                    }
                                    if (hw.isCustom) {
                                        Box(
                                            modifier = Modifier
                                                .background(NothingRed.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = StringResources.get("PRIVAT"),
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NothingRed
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = hw.description,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 13.sp,
                                    color = if (hw.isDone) NothingMutedGray else NothingWhite,
                                    textDecoration = if (hw.isDone) TextDecoration.LineThrough else null
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Fällig bis: ${hw.dueDate}",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = NothingMutedGray
                                )
                            }

                            IconButton(onClick = { viewModel.deleteHomeworkItem(hw) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Entfernen", tint = NothingMutedGray)
                            }
                        }
                    }
                }
            }
        }

        // Custom manual add Homework button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            FloatingActionButton(
                onClick = { viewModel.showAddHomeworkDialog = true },
                containerColor = NothingWhite,
                contentColor = NothingBlack,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Eintrag hinzufügen")
            }
        }
    }

    if (viewModel.showAddHomeworkDialog) {
        Dialog(onDismissRequest = { viewModel.showAddHomeworkDialog = false }) {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(StringResources.get("EIGENE HAUSAUFGABE"),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NothingWhite,
                        fontSize = 16.sp
                    )

                    NothingTextField(
                        value = viewModel.newHwSubject,
                        onValueChange = { viewModel.newHwSubject = it },
                        label = "Fach Code (z.B. Ma, D, Ch)"
                    )

                    NothingTextField(
                        value = viewModel.newHwDesc,
                        onValueChange = { viewModel.newHwDesc = it },
                        label = "Aufgabenbeschreibung"
                    )

                    NothingTextField(
                        value = viewModel.newHwDueDate,
                        onValueChange = { viewModel.newHwDueDate = it },
                        label = "Fälligkeitsdatum (YYYY-MM-DD)"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.showAddHomeworkDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = NothingWhite),
                            border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(StringResources.get("Abbrechen"), fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                viewModel.createHomework(
                                    viewModel.newHwSubject,
                                    viewModel.newHwDesc,
                                    viewModel.newHwDueDate
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NothingWhite, contentColor = NothingBlack),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(StringResources.get("Hinzufügen"), fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}


// --- GRADES TRACKER SCREEN ---
@Composable
fun GradesScreen(viewModel: UntisViewModel) {
    val grades by viewModel.grades.collectAsState()

    // Advanced Average calculator
    var selectedGradeScalePoints by remember { mutableStateOf(false) } // false = 1 to 6 scale, true = 0 to 15 scale

    val avgGradeStr = remember(grades, selectedGradeScalePoints) {
        if (grades.isEmpty()) "--" else {
            var sum = 0f
            var weightSum = 0f
            for (g in grades) {
                // Parse grade values like "1", "2+", "13", "12 Points"
                val cleaned = g.gradeValue.replace("+", ".75").replace("-", ".25").replace(" Punkte", "").replace(" Points", "").trim()
                val parsed = cleaned.toFloatOrNull()
                if (parsed != null) {
                    sum += parsed * g.weight
                    weightSum += g.weight
                }
            }
            if (weightSum > 0) String.format("%.2f", sum / weightSum) else "--"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NothingHeader(text = StringResources.get("Notenspiegel"), fontSize = 28.sp)

            // Dynamic Average Stats widget card
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = StringResources.get("NOTENSCHNITT"),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = NothingMutedGray,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "Ø $avgGradeStr",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.ExtraBold,
                            color = NothingWhite,
                            fontSize = 32.sp
                        )
                        Text(
                            text = StringResources.get("Unter Berücksichtigung aller Gewichtungen"),
                            fontFamily = FontFamily.Monospace,
                            color = NothingMutedGray,
                            fontSize = 10.sp
                        )
                    }

                    // Theme toggle point scale
                    Button(
                        onClick = { selectedGradeScalePoints = !selectedGradeScalePoints },
                        colors = ButtonDefaults.buttonColors(containerColor = NothingWhite, contentColor = NothingBlack),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp)
                    ) {
                        Text(if (selectedGradeScalePoints) StringResources.get("0-15 PUNKTE") else StringResources.get("1-6 SCHULNOTEN"), fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (grades.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(StringResources.get("Noch keine Noten eingetragen."),
                            fontFamily = FontFamily.Monospace,
                            color = NothingMutedGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(grades) { g ->
                    Surface(
                        color = NothingCardGray,
                        shape = RoundedCornerShape(24.dp), // modern 24.dp round
                        border = BorderStroke(1.dp, Color(0xFF333333)), // updated border color
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .background(NothingWhite.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = g.subjectCode,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = NothingWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = g.subjectName,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = NothingWhite,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = g.description, fontFamily = FontFamily.Monospace, color = NothingMutedGray, fontSize = 12.sp)
                                Text(text = "Gewicht: ${g.weight} | Datum: ${g.examDate}", fontFamily = FontFamily.Monospace, color = NothingMutedGray, fontSize = 10.sp)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(NothingWhite, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = g.gradeValue,
                                        color = NothingBlack,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                }

                                IconButton(onClick = { viewModel.deleteGradeItem(g) }) {
                                    Icon(Icons.Default.Delete, contentDescription = StringResources.get("Löschen"), tint = NothingMutedGray)
                                }
                            }
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            FloatingActionButton(
                onClick = { viewModel.showAddGradeDialog = true },
                containerColor = NothingWhite,
                contentColor = NothingBlack,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Eintrag hinzufügen")
            }
        }
    }

    if (viewModel.showAddGradeDialog) {
        Dialog(onDismissRequest = { viewModel.showAddGradeDialog = false }) {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(StringResources.get("NEUE NOTE EINTRAGEN"),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NothingWhite,
                        fontSize = 16.sp
                    )

                    NothingTextField(
                        value = viewModel.newGradeSubject,
                        onValueChange = { viewModel.newGradeSubject = it },
                        label = "Fachname (z.B. Mathematik)"
                    )

                    NothingTextField(
                        value = viewModel.newGradeSubjectCode,
                        onValueChange = { viewModel.newGradeSubjectCode = it },
                        label = "Fach Code (z.B. Ma)"
                    )

                    NothingTextField(
                        value = viewModel.newGradeValue,
                        onValueChange = { viewModel.newGradeValue = it },
                        label = "Notenwert (z.B. 1+, 12, 2.5)"
                    )

                    NothingTextField(
                        value = viewModel.newGradeWeight,
                        onValueChange = { viewModel.newGradeWeight = it },
                        label = "Gewichtung (z.B. 1.0, 0.5)"
                    )

                    NothingTextField(
                        value = viewModel.newGradeDesc,
                        onValueChange = { viewModel.newGradeDesc = it },
                        label = "Leistungsnachweis (z.B. Klausur)"
                    )

                    NothingTextField(
                        value = viewModel.newGradeDate,
                        onValueChange = { viewModel.newGradeDate = it },
                        label = "Prüfungsdatum (YYYY-MM-DD)"
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.showAddGradeDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = NothingWhite),
                            border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(StringResources.get("Bestätigen"), fontFamily = FontFamily.Monospace)
                        }

                        Button(
                            onClick = {
                                val wVal = viewModel.newGradeWeight.toFloatOrNull() ?: 1.0f
                                viewModel.createGrade(
                                    viewModel.newGradeSubject,
                                    viewModel.newGradeSubjectCode,
                                    viewModel.newGradeValue,
                                    wVal,
                                    viewModel.newGradeDesc,
                                    viewModel.newGradeDate
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NothingWhite, contentColor = NothingBlack),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(StringResources.get("Eintragen"), fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}


// --- AI CHATBOT SCREEN ---
@Composable
fun ChatbotScreen(viewModel: UntisViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NothingHeader(text = StringResources.get("KI-Hausaufgaben"), fontSize = 28.sp)
            Text(StringResources.get("Lade Hausaufgabenscreenshots hoch & die KI trägt sie strukturiert ein!"),
                fontFamily = FontFamily.Monospace,
                color = NothingMutedGray,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }

        // Chat logs bubble scroll list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(viewModel.chatMessages) { chat ->
                val isMe = chat.sender == "User"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                ) {
                    Column(
                        modifier = Modifier.widthIn(max = 290.dp),
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        // Sender ID
                        Text(
                            text = chat.sender.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isMe) NothingWhite else NothingRed,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )

                        Surface(
                            color = if (isMe) NothingCardGray else Color(0xFF1E1E1E),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isMe) 16.dp else 4.dp,
                                bottomEnd = if (isMe) 4.dp else 16.dp
                            ),
                            border = BorderStroke(1.dp, if (isMe) Color(0xFF3A3A3C) else Color(0xFF2C2C2E))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (chat.image != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Face, contentDescription = null, tint = NothingMutedGray, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("[Screenshot hochgeladen]", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = NothingMutedGray)
                                    }
                                }
                                Text(
                                    text = chat.text,
                                    fontFamily = FontFamily.Monospace,
                                    color = NothingWhite,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            if (viewModel.isChatAnalyzing) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = NothingCardGray,
                            shape = RoundedCornerShape(24.dp), // modern 24.dp round
                            border = BorderStroke(1.dp, Color(0xFF333333)) // updated border color
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), color = NothingWhite, strokeWidth = 2.dp)
                                Text(StringResources.get("Analysiere Hausaufgaben mit Gemini..."), fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = NothingWhite)
                            }
                        }
                    }
                }
            }
        }

        // Input entry bar + Screenshot Simulator selector
        Surface(
            color = NothingCardGray,
            border = BorderStroke(1.dp, Color(0xFF333333)), // updated border color
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                // Interactive helper shortcuts triggers
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.activeChatInput = "Mathe Hausaufgabe S.124 Nr. 1-4 bis Montag eintragen"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NothingDarkGray),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("Mathe S.124", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NothingWhite)
                    }

                    Button(
                        onClick = {
                            // Simulate upload of a textbook screenshot
                            Toast.makeText(context, StringResources.get("Screenshot aus Galerie ausgewählt!"), Toast.LENGTH_SHORT).show()
                            // Pass mock bitmap of drawing representatively
                            val width = 100
                            val height = 100
                            val conf = Bitmap.Config.ARGB_8888
                            val mockBitmap = Bitmap.createBitmap(width, height, conf)
                            viewModel.sendChatPrompt("Lese diese Chemie Hausaufgabe aus dem Foto aus", mockBitmap)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NothingDarkGray),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Face, contentDescription = null, modifier = Modifier.size(12.dp), tint = NothingRed)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(StringResources.get("MOCK SCREENSHOT HOCHLADEN"), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = NothingWhite)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NothingTextField(
                        value = viewModel.activeChatInput,
                        onValueChange = { viewModel.activeChatInput = it },
                        label = "Schreibe dem Bot...",
                        modifier = Modifier.weight(1f),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (viewModel.activeChatInput.isNotEmpty()) {
                                    viewModel.sendChatPrompt(viewModel.activeChatInput, null)
                                }
                            }) {
                                Icon(Icons.Default.Send, contentDescription = StringResources.get("Senden"), tint = NothingWhite)
                            }
                        }
                    )
                }
            }
        }
    }
}


// --- SETTINGS CONFIG SCREEN ---
@Composable
fun SettingsScreen(viewModel: UntisViewModel) {
    var showComplateTeachers by remember { mutableStateOf(true) }
    var showComplateSubjects by remember { mutableStateOf(true) }
    var useColorsOfSubject by remember { mutableStateOf(true) }
    var representationChanges by remember { mutableStateOf(true) }
    var showCancellationsState by remember { mutableStateOf(true) }
    var showClassRooms by remember { mutableStateOf(true) }
    var showColorsElements by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            NothingHeader(text = StringResources.get("Einstellungen"), fontSize = 28.sp)
        }

        // Language Section
        item {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(StringResources.get("Sprache / Language"), fontFamily = FontFamily.Monospace, color = NothingMutedGray, fontSize = 11.sp)

                    SettingsToggleRow(
                        title = "English UI",
                        desc = StringResources.get("Wähle die App-Sprache aus"),
                        checked = StringResources.currentLanguage.value == com.example.ui.AppLanguage.EN,
                        onCheckedChange = { isEn ->
                            StringResources.currentLanguage.value = if (isEn) com.example.ui.AppLanguage.EN else com.example.ui.AppLanguage.DE
                        }
                    )
                    
                    HorizontalDivider(color = Color(0xFFE5E5E5))
                    
                    SettingsToggleRow(
                        title = "Stock Android Theme",
                        desc = "Nutze das Standard Android Theme / Dynamic Colors",
                        checked = viewModel.useStockThemePref,
                        onCheckedChange = { 
                            viewModel.useStockThemePref = it
                            viewModel.saveAppSettings()
                        }
                    )
                }
            }
        }

        // Credentials Section
        item {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(StringResources.get("API & VERBINDUNG"), fontFamily = FontFamily.Monospace, color = NothingMutedGray, fontSize = 11.sp)

                    NothingTextField(
                        value = viewModel.geminiApiKeyInput,
                        onValueChange = { viewModel.geminiApiKeyInput = it },
                        label = "Gemini AI API Key (für AI Chatbot)"
                    )

                    NothingTextField(
                        value = viewModel.reminderMinutesInput.toString(),
                        onValueChange = { viewModel.reminderMinutesInput = it.toIntOrNull() ?: 60 },
                        label = "Hausaufgaben Erinnerung (Minuten vorher)"
                    )

                    NothingButton(
                        text = "Keys & Einstellungen Sichern",
                        onClick = {
                            viewModel.saveAppSettings()
                            Toast.makeText(context, StringResources.get("Einstellungen gespeichert!"), Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Original Timetable Widget configuration toggles from settings screenshot
        item {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFF2C2C2E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(StringResources.get("STUNDENPLAN ANSICHT"), fontFamily = FontFamily.Monospace, color = NothingMutedGray, fontSize = 11.sp)

                    SettingsToggleRow(
                        title = "Lehrernamen ausschreiben",
                        desc = "Zeigt beim Stundenplan den vollständigen Namen an, sofern Platz ist",
                        checked = showComplateTeachers,
                        onCheckedChange = { showComplateTeachers = it }
                    )

                    Divider(color = Color(0xFF2C2C2E))

                    SettingsToggleRow(
                        title = "Fachnamen ausschreiben",
                        desc = "Zeigt beim Stundenplan den vollständigen Fachnamen anstelle des Kürzels",
                        checked = showComplateSubjects,
                        onCheckedChange = { showComplateSubjects = it }
                    )

                    Divider(color = Color(0xFF2C2C2E))

                    SettingsToggleRow(
                        title = "Fachfarben anzeigen",
                        desc = "Aktiviert den bunten Stundenplan mit benutzerdefinierten Farben für jedes Fach",
                        checked = useColorsOfSubject,
                        onCheckedChange = { useColorsOfSubject = it }
                    )

                    Divider(color = Color(0xFF2C2C2E))

                    SettingsToggleRow(
                        title = "Vertretungskennzeichnung",
                        desc = "Vertretungen und Raumänderungen auffällig hervorheben",
                        checked = representationChanges,
                        onCheckedChange = { representationChanges = it }
                    )

                    Divider(color = Color(0xFF2C2C2E))

                    SettingsToggleRow(
                        title = "Entfälle anzeigen",
                        desc = "Ausgefallene Unterrichtsstunden durchgestrichen anzeigen",
                        checked = showCancellationsState,
                        onCheckedChange = { showCancellationsState = it }
                    )

                    Divider(color = Color(0xFF2C2C2E))

                    SettingsToggleRow(
                        title = "Räume einblenden",
                        desc = "Zeigt die jeweilige Raumnummer im Stundenplanfeld an",
                        checked = showClassRooms,
                        onCheckedChange = { showClassRooms = it }
                    )

                    Divider(color = Color(0xFF2C2C2E))

                    SettingsToggleRow(
                        title = "Farben aus WebUntis",
                        desc = "Synchronisiert die in WebUntis hinterlegten Standardfarben",
                        checked = showColorsElements,
                        onCheckedChange = { showColorsElements = it }
                    )
                }
            }
        }
        
        item {
            Surface(
                color = NothingCardGray,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = StringResources.get("ICS KALENDERABONNEMENT"),
                        fontSize = 12.sp,
                        color = NothingWhite
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.exportCalendarSubscription()
                                Toast.makeText(context, StringResources.get("Stundenplan lokal als ICS exportiert (Cache)!"), Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NothingWhite, contentColor = NothingBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(StringResources.get("ICS DATEI EXPORT"), fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val link = viewModel.getIcsCalendarSubscriptionLink()
                                clipboard.setText(AnnotatedString(link))
                                Toast.makeText(context, StringResources.get("ICS Link in die Zwischenablage kopiert! Trage diesen im Google Kalender ein."), Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = NothingWhite),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(StringResources.get("ICS LINK KOPIEREN"), fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // About / Log out
        item {
            NothingButton(
                text = "Ausloggen & Reset",
                onClick = { viewModel.logout() },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false
            )
        }
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontFamily = FontFamily.Monospace, color = NothingWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(desc, fontFamily = FontFamily.Monospace, color = NothingMutedGray, fontSize = 10.sp, lineHeight = 13.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NothingWhite,
                checkedTrackColor = NothingRed,
                uncheckedThumbColor = NothingMutedGray,
                uncheckedTrackColor = NothingDarkGray
            )
        )
    }
}

// --- INFO SCREEN ---
@Composable
fun InfoScreen(viewModel: UntisViewModel) {
    var clickCount by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NothingBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NothingHeader(text = "INFO & CREDITS", fontSize = 28.sp)
            IconButton(
                onClick = { viewModel.currentScreen = "PROFILE" },
                modifier = Modifier.background(NothingCardGray, CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Close Info", tint = NothingWhite)
            }
        }

        Surface(
            color = NothingCardGray,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "UNTIS NEO",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = NothingWhite,
                    fontSize = 20.sp
                )
                
                Text(
                    text = "Version 1.0.0 (BETA)\nDie inoffizielle, schönere Alternative zum Schulalltag.",
                    fontFamily = FontFamily.Monospace,
                    color = NothingMutedGray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = Color(0xFF333333))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Macher",
                        fontFamily = FontFamily.Monospace,
                        color = NothingMutedGray,
                        fontSize = 14.sp
                    )
                    
                    Text(
                        text = "norobb",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = if (clickCount >= 4) NothingRed else NothingWhite,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable {
                                clickCount++
                                if (clickCount >= 7) {
                                    clickCount = 0
                                    viewModel.currentScreen = "ARCADE"
                                }
                            }
                            .padding(8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Design System",
                        fontFamily = FontFamily.Monospace,
                        color = NothingMutedGray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "NDot / Nothing",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = NothingWhite,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
