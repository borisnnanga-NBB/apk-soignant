package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.HealingViewModel
import com.example.data.Medicine
import com.example.data.OrderEntry
import com.example.data.PatientInquiry
import com.example.data.ScheduleEntry
import com.example.ui.theme.*

// Data class to avoid Triple invocation naming conflicts
data class HealingServiceItem(
    val name: String,
    val icon: ImageVector,
    val description: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainHealingApp(viewModel: HealingViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userRole by viewModel.currentUserRole.collectAsStateWithLifecycle()

    HealerTheme {
        Scaffold(
            bottomBar = {
                if (currentScreen != "auth") {
                    HealerBottomNavigation(
                        currentRoute = currentScreen,
                        role = userRole,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        slideInHorizontally(animationSpec = spring()) { if (targetState == "auth") -it else it } with
                                slideOutHorizontally(animationSpec = spring()) { if (targetState == "auth") it else -it }
                    },
                    label = "HealingScreensAnimation"
                ) { targetState ->
                    when (targetState) {
                        "auth" -> AuthScreen(viewModel)
                        "patient_home" -> PatientHomeScreen(viewModel)
                        "service_detail" -> ServiceDetailScreen(viewModel)
                        "shop" -> ShopScreen(viewModel)
                        "history" -> PatientOrderHistoryScreen(viewModel)
                        "soignant" -> SoignantSpaceScreen(viewModel)
                        "admin" -> AdminSpaceScreen(viewModel)
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------
// 1. Bottom Navigation Bar
// ------------------------------------------------------------
@Composable
fun HealerBottomNavigation(
    currentRoute: String,
    role: String,
    onNavigate: (String) -> Unit
) {
    Surface(
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (role == "soignant") {
                HealerBottomNavItem(
                    label = "Praticien",
                    icon = Icons.Filled.Email,
                    selected = currentRoute == "soignant",
                    onClick = { onNavigate("soignant") }
                )
                HealerBottomNavItem(
                    label = "Boutique",
                    icon = Icons.Filled.ShoppingCart,
                    selected = currentRoute == "shop",
                    onClick = { onNavigate("shop") }
                )
                HealerBottomNavItem(
                    label = "Déconnexion",
                    icon = Icons.Filled.ExitToApp,
                    selected = false,
                    onClick = { onNavigate("auth") }
                )
            } else if (role == "admin") {
                HealerBottomNavItem(
                    label = "Dashboard",
                    icon = Icons.Filled.DateRange,
                    selected = currentRoute == "admin",
                    onClick = { onNavigate("admin") }
                )
                HealerBottomNavItem(
                    label = "Boutique",
                    icon = Icons.Filled.ShoppingCart,
                    selected = currentRoute == "shop",
                    onClick = { onNavigate("shop") }
                )
                HealerBottomNavItem(
                    label = "Déconnexion",
                    icon = Icons.Filled.ExitToApp,
                    selected = false,
                    onClick = { onNavigate("auth") }
                )
            } else {
                // Patient menu
                HealerBottomNavItem(
                    label = "Accueil",
                    icon = Icons.Filled.Home,
                    selected = currentRoute == "patient_home",
                    onClick = { onNavigate("patient_home") }
                )
                HealerBottomNavItem(
                    label = "Boutique",
                    icon = Icons.Filled.ShoppingCart,
                    selected = currentRoute == "shop",
                    onClick = { onNavigate("shop") }
                )
                HealerBottomNavItem(
                    label = "Historique",
                    icon = Icons.Filled.List,
                    selected = currentRoute == "history",
                    onClick = { onNavigate("history") }
                )
                HealerBottomNavItem(
                    label = "Déconnexion",
                    icon = Icons.Filled.ExitToApp,
                    selected = false,
                    onClick = { onNavigate("auth") }
                )
            }
        }
    }
}

@Composable
fun RowScope.HealerBottomNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(onClick = onClick, interactionSource = remember { MutableInteractionSource() }, indication = LocalIndication.current)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

// ------------------------------------------------------------
// 2. Authentication Screen (with Predefined role selectors)
// ------------------------------------------------------------
@Composable
fun AuthScreen(viewModel: HealingViewModel) {
    var userIdentity by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegisterState by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFFEFA), Color(0xFFFBFBE2)),
                    center = Offset(size.width / 2f, size.height / 2f),
                    radius = size.width * 1.5f
                )
                drawRect(brush)
            }
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botanical Header Icon and brand identity layout
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = "Papa George logo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Text(
                text = "Papa GEORGE",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = "Sagesse Ancestrale et Guérison Botanique",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .padding(top = 4.dp, bottom = 24.dp)
                    .background(Color(0xFFEAEAD1).copy(alpha = 0.5f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 2.dp)
            )

            // Dynamic card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegisterState) "Enregistrement Patient" else "Espace de Connexion",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Role selector panel for testing convenience
                    Text(
                        text = "Évaluer en un clic (Sélectionnez un Rôle) :",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.loginAs("patient", "Mamadou Koulibaly", "+237 699112233") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text("Patient", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.loginAs("soignant") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text("Soignant", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { viewModel.loginAs("admin") },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text("Admin", fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(bottom = 16.dp))

                    OutlinedTextField(
                        value = userIdentity,
                        onValueChange = { userIdentity = it },
                        label = { Text("Email ou téléphone") },
                        placeholder = { Text("Saisissez votre identifiant") },
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(50.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        shape = RoundedCornerShape(50.dp)
                    )

                    Button(
                        onClick = {
                            if (userIdentity.isBlank() || password.isBlank()) {
                                Toast.makeText(context, "Saisissez vos paramètres d'accès", Toast.LENGTH_SHORT).show()
                            } else {
                                // Default as customized patient
                                viewModel.loginAs("patient", userIdentity, "+237 688111222")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = if (isRegisterState) "S'enregistrer maintenant" else "Se connecter",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }

            TextButton(onClick = { isRegisterState = !isRegisterState }) {
                Text(
                    text = if (isRegisterState) "Déjà un compte ? Se connecter" else "Nouveau patient ? Créer un compte",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.Underline
                )
            }

            // Trust emblem badge
            Row(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .background(Color(0xFFCBEBC3).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Certified",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cabinet traditionnel de Mimboman, Yaoundé",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// ------------------------------------------------------------
// 3. Patient Home Screen
// ------------------------------------------------------------
@Composable
fun PatientHomeScreen(viewModel: HealingViewModel) {
    val patientName by viewModel.currentPatientName.collectAsStateWithLifecycle()
    val schedules by viewModel.scheduleEntries.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Paix et guérison sur vous,",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$patientName",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Hero title
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Trouvez la paix et la guérison aujourd'hui.",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "L'alliance de la connaissance ancestrale et de la pureté végétale pour harmoniser le corps et l'esprit.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Services offering header
        item {
            Text(
                text = "Services de Soin & Cures",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Services Buttons list
        val serviceItems = listOf(
            HealingServiceItem("Problème physique", Icons.Filled.Person, "Douleurs articulaires, blessures incurables et musculaires"),
            HealingServiceItem("Problème individuel", Icons.Filled.AccountBox, "Chances, mariage, blocages de vie, couple, voyance"),
            HealingServiceItem("Problème spirituel", Icons.Filled.Star, "Possession, mauvais sort, blocages obscurs, purification"),
            HealingServiceItem("Maladie pathologique", Icons.Filled.Add, "Diabète, typhoïde, maux chroniques, suivi naturel"),
            HealingServiceItem("Consultation à distance", Icons.Filled.Phone, "WhatsApp Direct : Contactez directement Papa George")
        )

        items(serviceItems) { service ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (service.name == "Consultation à distance") {
                            viewModel.contactDistanceConsultation(context)
                        } else {
                            viewModel.selectService(service.name)
                        }
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (service.name == "Consultation à distance") MaterialTheme.colorScheme.secondary.copy(
                                    alpha = 0.15f
                                )
                                else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = service.icon,
                            contentDescription = service.name,
                            tint = if (service.name == "Consultation à distance") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = service.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = service.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }

        // Available schedule Table Header
        item {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text(
                    text = "Planning d'Emploi de Temps de Papa George",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Vérifiez la disponibilité avant de planifier un déplacement.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Available Schedules Table List
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Jour/Lieu", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1.5f))
                        Text("Motif", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(2f))
                        Text("Statut", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }

                    // Content Rows
                    schedules.forEach { sEntry ->
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(sEntry.day, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(sEntry.place, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Column(modifier = Modifier.weight(2f).padding(horizontal = 4.dp)) {
                                    Text(sEntry.reason, style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif), maxLines = 2, overflow = TextOverflow.Ellipsis)
                                    Text("Durée: ${sEntry.durationString}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when(sEntry.statusString) {
                                                "Disponible" -> Color(0xFFCBEBC3)
                                                "Occupé" -> Color(0xFFF9DBC5)
                                                else -> Color(0xFFECEEEC)
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = sEntry.statusString,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = when(sEntry.statusString) {
                                                "Disponible" -> MaterialTheme.colorScheme.primary
                                                "Occupé" -> MaterialTheme.colorScheme.tertiary
                                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                                            },
                                            fontWeight = FontWeight.Bold
                                        ),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        // Testimonial Section
        item {
            Text(
                text = "Témoignages de Guérison",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        val testimonials = listOf(
            Triple("Marie-Louise K.", "Grâce aux écorces et aux rituels de bénédiction de Papa George, j'ai retrouvé une vitalité incroyable que je pensais perdue à jamais.", "Patiente depuis 2024"),
            Triple("Jean-Paul D.", "Un accueil chaleureux et une écoute profonde qui soigne non seulement les maux du corps, mais aussi l'esprit.", "Patient guéri en 2025"),
            Triple("Sophie L.", "Les consultations à distance sur WhatsApp sont incroyables de clarté. Une aide efficace même de l'étranger.", "Consultation en ligne")
        )

        items(testimonials) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "\"${item.second}\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Serif),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.first, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text(item.third, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        // Physical clinic address anchor details
        item {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Home,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Notre Clinique Traditionnelle", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Text("Mimbomane, Yaoundé", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.tertiary)
                        Text("Implanté à l'entrée de la salle des fêtes Florencia.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

// ------------------------------------------------------------
// 4. Service Detail Screen with Body hotspot diagram!
// ------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ServiceDetailScreen(viewModel: HealingViewModel) {
    val serviceType by viewModel.selectedServiceType.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val bodyParts = listOf("Tête", "Yeux / Oreilles", "Cou / Épaules", "Ventre / Dos", "Bras / Mains", "Articulations", "Orteils / Pieds", "Douleur générale")
    val selectedBodyParts = remember { mutableStateListOf<String>() }
    var detailText by remember { mutableStateOf("") }
    var isSimulatingRecording by remember { mutableStateOf(false) }
    var recordingFinishedText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo("patient_home") }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = serviceType,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Image representation
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAa2VZkVziY2d-NLxVnL31UyzGtdEFmQ5qT5shpHke9-lU0OnvTcKXHvux5REtumYfwlNLC1COy-FEAvVPItQYIaWO5Px0JtQvRHBW5Bwri1D4NFngsisq2RT1yY0oRMc9yEiYL7DvH0VjKT0xueQHKmPCQQNG2RSucDJXB_4stK77sL12h2CWgRlPoOuGdWc2IKBcM8SyzfRtoSGi_6DYdmSMUak3gxlBzThmhWUINl-IVq-4MDAw6FRCdZXzpGpNZnSiqMm0tkOcy",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )
                Text(
                    text = "Laissez Papa George étudier vos symptômes",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomStart)
                )
            }
        }

        // Custom diagrams hotspot logic for physical service
        if (serviceType == "Problème physique") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sélectionnez les zones de douleurs sur le corps",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Human Body mock visual with active hotspot buttons!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(Color(0xFFFBFBE2), RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing simplified human form
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            // Head
                            drawCircle(Color(0xFF72796e), radius = 18f, center = Offset(w/2f, h * 0.15f))
                            // Body Outline
                            drawLine(Color(0xFF72796e), start = Offset(w/2f, h * 0.15f + 18f), end = Offset(w/2f, h * 0.5f), strokeWidth = 8f)
                            // Arms
                            drawLine(Color(0xFF72796e), start = Offset(w/2f - 40f, h * 0.28f), end = Offset(w/2f + 40f, h * 0.28f), strokeWidth = 6f)
                            drawLine(Color(0xFF72796e), start = Offset(w/2f - 40f, h * 0.28f), end = Offset(w/2f - 50f, h * 0.5f), strokeWidth = 6f)
                            drawLine(Color(0xFF72796e), start = Offset(w/2f + 40f, h * 0.28f), end = Offset(w/2f + 50f, h * 0.5f), strokeWidth = 6f)
                            // Legs
                            drawLine(Color(0xFF72796e), start = Offset(w/2f, h * 0.5f), end = Offset(w/2f - 30f, h * 0.85f), strokeWidth = 6f)
                            drawLine(Color(0xFF72796e), start = Offset(w/2f, h * 0.5f), end = Offset(w/2f + 30f, h * 0.85f), strokeWidth = 6f)
                        }

                        // Circular hotspot buttons
                        HotspotComponent(
                            label = "Tête",
                            x = 0.5f, y = 0.15f,
                            isSelected = selectedBodyParts.contains("Tête"),
                            onClick = { toggleSelection(selectedBodyParts, "Tête") }
                        )

                        HotspotComponent(
                            label = "Yeux / Oreilles",
                            x = 0.54f, y = 0.11f,
                            isSelected = selectedBodyParts.contains("Yeux / Oreilles"),
                            onClick = { toggleSelection(selectedBodyParts, "Yeux / Oreilles") }
                        )

                        HotspotComponent(
                            label = "Ventre / Dos",
                            x = 0.5f, y = 0.42f,
                            isSelected = selectedBodyParts.contains("Ventre / Dos"),
                            onClick = { toggleSelection(selectedBodyParts, "Ventre / Dos") }
                        )

                        HotspotComponent(
                            label = "Bras / Mains",
                            x = 0.35f, y = 0.4f,
                            isSelected = selectedBodyParts.contains("Bras / Mains"),
                            onClick = { toggleSelection(selectedBodyParts, "Bras / Mains") }
                        )

                        HotspotComponent(
                            label = "Articulations",
                            x = 0.43f, y = 0.65f,
                            isSelected = selectedBodyParts.contains("Articulations"),
                            onClick = { toggleSelection(selectedBodyParts, "Articulations") }
                        )

                        HotspotComponent(
                            label = "Orteils / Pieds",
                            x = 0.41f, y = 0.85f,
                            isSelected = selectedBodyParts.contains("Orteils / Pieds"),
                            onClick = { toggleSelection(selectedBodyParts, "Orteils / Pieds") }
                        )
                    }

                    // Chips representation
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bodyParts.forEach { part ->
                            val isSelected = selectedBodyParts.contains(part)
                            FilterChip(
                                selected = isSelected,
                                onClick = { toggleSelection(selectedBodyParts, part) },
                                label = { Text(part) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Voice Message Recording Mockup area
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Mic Button
                IconButton(
                    onClick = {
                        if (isSimulatingRecording) {
                            isSimulatingRecording = false
                            recordingFinishedText = "✓ Message audio enregistré (28 secondes)."
                        } else {
                            isSimulatingRecording = true
                            recordingFinishedText = ""
                        }
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (isSimulatingRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        imageVector = if (isSimulatingRecording) Icons.Filled.Close else Icons.Filled.PlayArrow,
                        contentDescription = "Voice Record",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isSimulatingRecording) "Enregistrement en cours (Appuyez pour stopper)..." else "Appuyez pour laisser un message vocal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSimulatingRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Dites à Papa George vos symptômes de vive voix.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                if (recordingFinishedText.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = recordingFinishedText,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Written message text field
        OutlinedTextField(
            value = detailText,
            onValueChange = { detailText = it },
            label = { Text("Décrivez précisément votre problème (écrit)") },
            placeholder = { Text("Exemple: J'ai de fréquentes douleurs lancinantes...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(16.dp),
            maxLines = 5
        )

        // WhatsApp direct contact buttons
        Button(
            onClick = {
                val fullDescription = buildString {
                    append(detailText)
                    if (recordingFinishedText.isNotEmpty()) {
                        append("\n[Un message vocal de 28s a été pré-enregistré dans l'app.]")
                    }
                }
                viewModel.sendWhatsAppInquiry(
                    context = context,
                    inquiryType = serviceType,
                    description = fullDescription,
                    bodyPartsSelected = selectedBodyParts
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ClearPrimaryContainer),
            shape = RoundedCornerShape(50.dp)
        ) {
            Icon(Icons.Filled.Send, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Envoyer à Papa GEORGE sur WhatsApp", color = Color.White, style = MaterialTheme.typography.labelLarge)
        }

        Text(
            text = "Le message sera automatiquement enregistré dans votre historique et envoyé au guérisseur Papa GEORGE sur son numéro (+237 695 413 620).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun BoxScope.HotspotComponent(
    label: String,
    x: Float,
    y: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val leftInt = (x * 280).toInt()
    val topInt = (y * 240).toInt()
    Box(
        modifier = Modifier
            .align(Alignment.TopStart)
            .padding(start = leftInt.dp, top = topInt.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                .border(2.dp, Color.White, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

private fun toggleSelection(list: MutableList<String>, part: String) {
    if (list.contains(part)) {
        list.remove(part)
    } else {
        list.add(part)
    }
}

// ------------------------------------------------------------
// 5. Shop Screen (with Orange Money Cameroon Integration)
// ------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShopScreen(viewModel: HealingViewModel) {
    val medicinesList by viewModel.medicines.collectAsStateWithLifecycle()
    val openPaymentByMedicine by viewModel.orangePaymentMedicine.collectAsStateWithLifecycle()
    val role by viewModel.currentUserRole.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf("Tout") }
    val categories = listOf("Tout", "Écorces", "Infusions", "Racines", "Miels")

    // Admin append product states
    var isAddingProductMode by remember { mutableStateOf(false) }
    var newProdName by remember { mutableStateOf("") }
    var newProdPrice by remember { mutableStateOf("") }
    var newProdDesc by remember { mutableStateOf("") }
    var newProdCat by remember { mutableStateOf("Écorces") }
    var newProdProps by remember { mutableStateOf("") }

    // Orange phone states
    var phoneOrangeMoney by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Text(
                    text = "Sagesse des Anciens",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Achetez et réservez des écorces et produits naturels de Mimboman d'une efficacité thérapeutique prouvée.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Horizontal chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Button(
                        onClick = { selectedCategory = cat },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Healer / Admin Product addition FAB trigger
        if (role == "admin" || role == "soignant") {
            item {
                if (!isAddingProductMode) {
                    Button(
                        onClick = { isAddingProductMode = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Poster un nouveau médicament", color = Color.White)
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Formulaire Publication Produit (Admin/Soignant)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                            OutlinedTextField(
                                value = newProdName,
                                onValueChange = { newProdName = it },
                                label = { Text("Nom du remède (Écorce, Plante...)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = newProdPrice,
                                onValueChange = { newProdPrice = it },
                                label = { Text("Prix (FCFA)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = newProdDesc,
                                onValueChange = { newProdDesc = it },
                                label = { Text("Propriétés & Description d'utilisation") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = newProdProps,
                                onValueChange = { newProdProps = it },
                                label = { Text("Propriétés clefs (e.g. Calmant, Purifiant)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Dropdown category simulation
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Catégorie : ")
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf("Écorces", "Infusions", "Huiles").forEach { c ->
                                        FilterChip(
                                            selected = newProdCat == c,
                                            onClick = { newProdCat = c },
                                            label = { Text(c) }
                                        )
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { isAddingProductMode = false },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Annuler", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Button(
                                    onClick = {
                                        val priceVal = newProdPrice.toDoubleOrNull() ?: 0.0
                                        if (newProdName.isBlank() || priceVal <= 0) {
                                            Toast.makeText(context, "Données invalides", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.addMedicine(
                                                Medicine(
                                                    name = newProdName,
                                                    price = priceVal,
                                                    description = newProdDesc,
                                                    category = newProdCat,
                                                    properties = newProdProps,
                                                    imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuD32mpjXMW7Ce3rrSwG7suoSpz_aYV8zDgMz6v66GeI9RKXzLe4OkN7aQfzaLCvPnv8CRR4-TVeLxVT_gPy50XQNJq5KZaEQXzmnu5osu6KuxsR1IHpcrmOFyCHMJLkJWa0zaMiFplCQ8k7v3-AdQmUSnAAVrmgRiFWTKg49NvZ-Gph7FBQbtLYEWWcj0V47KXI73lkg_rSwG232d7OSCSsJcQBT0eduyJuHizkSnZzvgtYesLmpmJSvsMpcydlz_ds_aSVRtjwLBuT"
                                                )
                                            )
                                            isAddingProductMode = false
                                            newProdName = ""
                                            newProdPrice = ""
                                            newProdDesc = ""
                                            Toast.makeText(context, "Nouveau remède publié sur l'application", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Publier", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Gallery remedies items grid
        val filteredList = medicinesList.filter {
            selectedCategory == "Tout" || it.category.equals(selectedCategory, ignoreCase = true)
        }

        if (filteredList.isEmpty()) {
            item {
                Text("Aucun remède de cette catégorie en stock.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.outline)
            }
        }

        items(filteredList) { med ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFEFFFEE))
                    ) {
                        AsyncImage(
                            model = med.imageUrl,
                            contentDescription = med.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // Badges stock indicators
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(med.category, color = Color.White, style = MaterialTheme.typography.labelSmall)
                            }

                            if (med.isStockLow) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.error)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Stock Faible", color = Color.White, style = MaterialTheme.typography.labelSmall)
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFCBEBC3))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Disponible", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = med.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${med.price.toInt()} FCFA",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = med.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Properties sub categories tags chips
                        FlowRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            med.properties.split(",").forEach { chipText ->
                                if (chipText.trim().isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = chipText.trim(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { viewModel.showPaymentModal(med) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Acheter par Orange Money", color = Color.White)
                            }

                            if (role == "admin" || role == "soignant") {
                                IconButton(
                                    onClick = { 
                                        viewModel.deleteMedicine(med.id)
                                        Toast.makeText(context, "Produit retiré de la boutique", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Payment Drawer Overlay simulation
    openPaymentByMedicine?.let { prod ->
        AlertDialog(
            onDismissRequest = { viewModel.showPaymentModal(null) },
            confirmButton = {
                Button(
                    onClick = {
                        if (phoneOrangeMoney.isBlank()) {
                            Toast.makeText(context, "Saisissez un numéro Orange", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.simulateOrangeMoneyPayment(context, prod, phoneOrangeMoney)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFC6404)) // High-fidelity Orange color
                ) {
                    Text("Confirmer le paiement Orange Money")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showPaymentModal(null) }) {
                    Text("Annuler")
                }
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = Color(0xFFFC6404), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Paiement Orange Money Cameroun", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineMedium)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Produit : ${prod.name}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Total : ${prod.price.toInt()} FCFA", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.ExtraBold)

                    Text(
                        text = "Les fonds arriveront directement sur le compte Orange Money de Papa GEORGE.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = phoneOrangeMoney,
                        onValueChange = { phoneOrangeMoney = it },
                        label = { Text("Numéro Orange (+237)") },
                        placeholder = { Text("695413620") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )

                    Text(
                        text = "Une demande USSD de confirmation de transaction sera envoyée sur votre mobile de paiement Orange Cameroun. Livraison sous 24h à Yaoundé et 48h à Douala.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        )
    }
}

// ------------------------------------------------------------
// 6. Patient Order History Screen
// ------------------------------------------------------------
@Composable
fun PatientOrderHistoryScreen(viewModel: HealingViewModel) {
    val orders by viewModel.orders.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
                text = "Historique de Commandes",
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
            )
            Text(
                text = "Retrouvez ici la liste de vos réservations et médicaments commandés par Orange Money.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Aucune commande n'a encore été passée.", color = MaterialTheme.colorScheme.outline)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                items(orders) { order ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(order.medicineName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Payé via Orange Money : ${order.paymentPhone}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Commande effectuée le ${formatTimestamp(order.timestamp)}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${order.totalPrice.toInt()} FCFA", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFCBEBC3))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Validé", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Format date helper
fun formatTimestamp(time: Long): String {
    val date = java.util.Date(time)
    val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.FRANCE)
    return formatter.format(date)
}

// ------------------------------------------------------------
// 7. Espace Soignant / Papa GEORGE inbox + Scheduler
// ------------------------------------------------------------
@Composable
fun SoignantSpaceScreen(viewModel: HealingViewModel) {
    val inquiries by viewModel.inquiries.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedInquiryToAnswer by remember { mutableStateOf<PatientInquiry?>(null) }
    var answerText by remember { mutableStateOf("") }
    var meetingDaySet by remember { mutableStateOf("Mardi") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcoming
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Healer portrait icon matching Papa GEORGE
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.AccountBox, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Espace Soignant de Papa GEORGE", style = MaterialTheme.typography.titleLarge, color = Color.White)
                    Text("Numéro officiel de soin : +237 695 413 620", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }

        Text("Consultations reçues des Patients :", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (inquiries.isEmpty()) {
                item {
                    Text("Aucune demande enregistrée dans la clinique pour l'instant.", color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(16.dp))
                }
            }

            items(inquiries) { inq ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = inq.patientName.take(2).uppercase(),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(inq.patientName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(inq.patientPhone, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(inq.inquiryType, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Message : ${inq.messageText}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (inq.responseText.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text("Réponse de Papa GEORGE :", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                                    Text(inq.responseText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                    if (inq.scheduledMeetingDate.isNotEmpty()) {
                                        Text("Rencontre programmée le : ${inq.scheduledMeetingDate} (Automatiquement inscrit au calendrier)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.tertiary)
                                    }
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { selectedInquiryToAnswer = inq },
                                sizeMin = 40.dp,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Répondre & Programmer rencontre", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Answer dialog overlay
    selectedInquiryToAnswer?.let { inq ->
        AlertDialog(
            onDismissRequest = { selectedInquiryToAnswer = null },
            confirmButton = {
                Button(
                    onClick = {
                        if (answerText.isBlank()) {
                            Toast.makeText(context, "Donnez une reponse", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.saveAdminResponse(inq.id, answerText, meetingDaySet)
                            Toast.makeText(context, "Message de réponse envoyé au patient et calendrier synchronisé !", Toast.LENGTH_SHORT).show()
                            selectedInquiryToAnswer = null
                            answerText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Valider la programmation")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedInquiryToAnswer = null }) {
                    Text("Annuler")
                }
            },
            title = {
                Text("Prendre en charge la demande de ${inq.patientName}")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Saisir ordonnance / message au patient :", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = answerText,
                        onValueChange = { answerText = it },
                        placeholder = { Text("Exemple: Je valide. Prenez l'Écorce de Moabi matin et soir.") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Programmer une rencontre physique ? (Remplira l'emploi du temps) :", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("Lundi", "Mardi", "Jeudi", "Vendredi").forEach { day ->
                            FilterChip(
                                selected = meetingDaySet == day,
                                onClick = { meetingDaySet = day },
                                label = { Text(day) }
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    sizeMin: androidx.compose.ui.unit.Dp = 48.dp,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(12.dp),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = sizeMin),
        colors = colors,
        shape = shape,
        contentPadding = contentPadding,
        content = content
    )
}

// ------------------------------------------------------------
// 8. Espace Administrateur / statistics charts & schedule updates
// ------------------------------------------------------------
@Composable
fun AdminSpaceScreen(viewModel: HealingViewModel) {
    val schedules by viewModel.scheduleEntries.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Schedule addition states
    var isAddingSchedule by remember { mutableStateOf(false) }
    var entryDay by remember { mutableStateOf("Lundi") }
    var entryPlace by remember { mutableStateOf("Mimboman") }
    var entryReason by remember { mutableStateOf("") }
    var entryStatus by remember { mutableStateOf("Disponible") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Column {
                Text(
                    text = "Ancestral Insights",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp, color = MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = "Peace be with you, Administrator. Here is the vitality of your sanctuary today.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Bento Grid statistics cards
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard(
                        title = "Monthly Revenue",
                        value = "CFA 450k",
                        sub = "+12% vs last month",
                        icon = Icons.Filled.ShoppingCart,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Contact Volume",
                        value = "28",
                        sub = "3 unread inquiries",
                        icon = Icons.Filled.Email,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard(
                        title = "Patient Index",
                        value = "98.4%",
                        sub = "Wellness: Pristine",
                        icon = Icons.Filled.Favorite,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Inventory Level",
                        value = "82%",
                        sub = "12 items stocked",
                        icon = Icons.Filled.List,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Dynamic Sales velocity custom canvas chart representing weekly sales!
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Vitesse des Ventes (Sales Velocity - Trimestriel)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated bar chart drawings
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val weeklySales = listOf(
                            Pair("Lun", 0.4f), Pair("Mar", 0.75f), Pair("Mer", 0.3f),
                            Pair("Jeu", 0.6f), Pair("Ven", 0.9f), Pair("Sam", 0.5f), Pair("Dim", 0.2f)
                        )

                        weeklySales.forEach { week ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .fillMaxHeight(week.second)
                                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                        .background(
                                            if (week.first == "Ven") MaterialTheme.colorScheme.primary 
                                            else MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f)
                                        )
                                )
                                Text(week.first, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Manage Availability Schedule list
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Gérer les Disponibilités",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = { isAddingSchedule = !isAddingSchedule },
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isAddingSchedule) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = "Add Schedule",
                        tint = Color.White
                    )
                }
            }
        }

        // Add Schedule Section Form inline if active
        if (isAddingSchedule) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Ajouter une plage horaire", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                        // Selector day inline
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Jour: ", modifier = Modifier.width(60.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi").forEach { d ->
                                    FilterChip(
                                        selected = entryDay == d,
                                        onClick = { entryDay = d },
                                        label = { Text(d) }
                                    )
                                }
                            }
                        }

                        // Selector place inline
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Lieu: ", modifier = Modifier.width(60.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Mimboman", "Bastos", "En ligne", "Sanctuaire").forEach { p ->
                                    FilterChip(
                                        selected = entryPlace == p,
                                        onClick = { entryPlace = p },
                                        label = { Text(p) }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = entryReason,
                            onValueChange = { entryReason = it },
                            label = { Text("Motif de soin (Consultations...)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Selector status inline
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Statut: ", modifier = Modifier.width(60.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("Disponible", "Occupé", "Repos").forEach { s ->
                                    FilterChip(
                                        selected = entryStatus == s,
                                        onClick = { entryStatus = s },
                                        label = { Text(s) }
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { isAddingSchedule = false },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Annuler", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = {
                                    if (entryReason.isBlank()) {
                                        Toast.makeText(context, "Saisissez un motif", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.addScheduleEntry(
                                            ScheduleEntry(
                                                day = entryDay,
                                                place = entryPlace,
                                                reason = entryReason,
                                                statusString = entryStatus
                                            )
                                        )
                                        isAddingSchedule = false
                                        entryReason = ""
                                        Toast.makeText(context, "Emploi de temps mis à jour !", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Ajouter", color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // List availability table rows with admin delete targets
        items(schedules) { sEntry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(sEntry.day, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(sEntry.place, color = MaterialTheme.colorScheme.secondary, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Text(sEntry.reason, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (sEntry.statusString == "Disponible") Color(0xFFCBEBC3) else Color(0xFFF9DBC5))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(sEntry.statusString, style = MaterialTheme.typography.labelSmall)
                        }

                        IconButton(
                            onClick = { 
                                viewModel.deleteSchedule(sEntry.id)
                                Toast.makeText(context, "Plage détruite de l'emploi du temps", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
            }

            Column {
                Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
            }
        }
    }
}
