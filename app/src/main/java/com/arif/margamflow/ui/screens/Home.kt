package com.arif.margamflow.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------- MAIN HOME ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home() {
    Scaffold(
        topBar = { HomeTopBar() },
        bottomBar = { BottomNavBar() },
        containerColor = Color(0xFFF2F4F8)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFEEF1FF), Color(0xFFF8FAFD))
                    )
                )
                .verticalScroll(rememberScrollState())
        ) {
            // Rounded top background container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    WelcomeHeader()
                    Spacer(Modifier.height(28.dp))
                    StatsSection(statsData)
                    Spacer(Modifier.height(32.dp))
                    QuickActionsSection()
                    Spacer(Modifier.height(32.dp))
                    RecentTripsSection()
                    Spacer(Modifier.height(100.dp)) // space for bottom nav
                }
            }
        }
    }
}

// ---------------- TOP BAR ----------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "MargamFlow",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 30.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        )
    )
}

// ---------------- WELCOME HEADER ----------------

@Composable
fun WelcomeHeader() {
    val gradient = Brush.linearGradient(listOf(Color(0xFF6A5AE0), Color(0xFF9081E8)))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {

            Text(
                text = "Track Your Journeys",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Box(
            modifier = Modifier
                .size(64.dp)
                .shadow(10.dp, CircleShape, clip = true)
                .background(gradient, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// ---------------- STATS SECTION ----------------

@Composable
fun StatsSection(statsData: List<StatData>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp, vertical = 8.dp)) {
        Text(
            text = "Your Stats",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 1.dp)
        ) {
            items(statsData) { stat ->
                StatCard(stat = stat)
            }
        }
    }
}

@Composable
fun StatCard(stat: StatData) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")
    val elevation by animateDpAsState(if (isPressed) 4.dp else 12.dp, label = "elevation")

    Card(
        modifier = Modifier
            .width(170.dp)
            .height(130.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(interactionSource = interactionSource, indication = null) {},
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(elevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(stat.color.copy(alpha = 0.25f), stat.color.copy(alpha = 0.1f))
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                // Floating Icon
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(stat.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        stat.icon,
                        contentDescription = stat.title,
                        tint = stat.color,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Stats Text
                Column {
                    Text(
                        text = stat.value,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stat.title,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


// ---------------- QUICK ACTIONS ----------------

@Composable
fun QuickActionsSection() {
    val primaryGradient = Brush.linearGradient(listOf(Color(0xFF6A5AE0), Color(0xFF9081E8)))
    val secondaryGradient = Brush.linearGradient(listOf(Color(0xFF4ECDC4), Color(0xFF6CE5E0)))

    Text("Quick Actions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(16.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
        PremiumButton("New Trip", Icons.Outlined.DirectionsCar, primaryGradient, Modifier.weight(1f))
        PremiumButton("History", Icons.Outlined.History, secondaryGradient, Modifier.weight(1f))
    }
}

// ---------------- PREMIUM BUTTON ----------------

@Composable
fun PremiumButton(
    text: String,
    icon: ImageVector,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // Tap animation
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "scale")
    val elevation by animateDpAsState(if (pressed) 4.dp else 12.dp, label = "elevation")

    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = elevation,
        shadowElevation = elevation,
        modifier = modifier
            .height(72.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(

            ) {
                /* */
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient, RoundedCornerShape(20.dp))
                .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Glowing circular background for icon
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = text,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Text
                Text(
                    text,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


// ---------------- RECENT TRIPS ----------------

@Composable
fun RecentTripsSection() {
    Text("Recent Trips", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Outlined.Route, contentDescription = "No trips", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(34.dp))
            Spacer(Modifier.height(12.dp))
            Text("No trips recorded yet", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Text("Start your first journey to see it here", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// ---------------- BOTTOM NAV ----------------

@Composable
fun BottomNavBar() {
    var selectedIndex by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 12.dp,
        modifier = Modifier
            .navigationBarsPadding() // fixes overlap with system nav
            .height(76.dp)
            .shadow(12.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
    ) {
        val items = listOf(
            NavItem("Home", Icons.Filled.Home, Icons.Outlined.Home),
            NavItem("Trips", Icons.Filled.DirectionsCar, Icons.Outlined.DirectionsCar),
            NavItem("Stats", Icons.Filled.Analytics, Icons.Outlined.Analytics),
            NavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person),
        )

        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                icon = {
                    Icon(
                        if (selectedIndex == index) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontSize = 12.sp
                    )
                }
            )
        }
    }
}

// ---------------- DATA ----------------

data class StatData(val title: String, val value: String, val icon: ImageVector, val color: Color)
data class NavItem(val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)

val statsData = listOf(
    StatData("Total Trips", "12", Icons.Outlined.Route, Color(0xFF6A5AE0)),
    StatData("This Week", "3", Icons.Outlined.CalendarMonth, Color(0xFFFF6B6B)),
    StatData("Avg Distance", "24.5 km", Icons.Outlined.SocialDistance, Color(0xFF4ECDC4)),
    StatData("Saved CO₂", "8.2 kg", Icons.Outlined.Eco, Color(0xFFFFD166))
)
