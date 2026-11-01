package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WhitelistedAppEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.FocusAmber
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.ZenGreen

@Composable
fun WhitelistManagerScreen(
    apps: List<WhitelistedAppEntity>,
    isSessionActive: Boolean,
    searchQuery: String,
    selectedCategory: String,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onToggleApp: (pkg: String, allowed: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Communication", "Productivity", "Time", "Utility")

    val filteredApps = apps.filter { app ->
        val matchesSearch = searchQuery.isBlank() ||
                app.appName.contains(searchQuery, ignoreCase = true) ||
                app.packageName.contains(searchQuery, ignoreCase = true)
        val matchesCategory = selectedCategory == "All" || app.category.equals(selectedCategory, ignoreCase = true)
        matchesSearch && matchesCategory
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("whitelist_manager_screen"),
        color = PitchBlack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(DarkSurfaceVariant, CircleShape)
                        .border(1.dp, DarkBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Whitelisted Tools",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "Essential tools allowed during active sessions",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Locked banner if session is active
            if (isSessionActive) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("whitelist_locked_banner"),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, FocusAmber),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = FocusAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Changes Disabled During Session",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = FocusAmber
                            )
                            Text(
                                text = "To preserve deep work integrity, app whitelists cannot be altered while session lock is active.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Search installed apps...", fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("whitelist_search_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = DarkBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onCategoryChange(cat) },
                        color = if (isSelected) ElectricCyan else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) ElectricCyan else DarkBorder
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = cat,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) PitchBlack else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Apps List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 64.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (filteredApps.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No apps match filter",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    items(filteredApps, key = { it.packageName }) { app ->
                        Surface(
                            color = DarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (app.isAllowed) ElectricCyan.copy(alpha = 0.4f) else DarkBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(DarkSurfaceElevated, CircleShape)
                                        .border(1.dp, DarkBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = app.appName.take(1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = if (app.isAllowed) ElectricCyan else TextMuted,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = app.appName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (app.isEssential) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                color = DarkSurfaceVariant,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "Essential",
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                    fontSize = 9.sp,
                                                    color = ZenGreen,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = app.packageName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextMuted,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Switch(
                                    checked = app.isAllowed,
                                    onCheckedChange = { allowed ->
                                        onToggleApp(app.packageName, allowed)
                                    },
                                    enabled = !isSessionActive,
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = PitchBlack,
                                        checkedTrackColor = ElectricCyan,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = DarkSurfaceElevated
                                    ),
                                    modifier = Modifier.testTag("app_switch_${app.packageName}")
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
