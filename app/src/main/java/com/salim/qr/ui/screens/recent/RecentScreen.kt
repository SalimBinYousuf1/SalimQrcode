package com.salim.qr.ui.screens.recent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.salim.qr.R
import com.salim.qr.data.db.CodeRecord
import com.salim.qr.data.db.SalimDatabase
import com.salim.qr.domain.export.CodeExporter
import com.salim.qr.domain.parsing.ParsedCode
import com.salim.qr.domain.parsing.PayloadParser
import com.salim.qr.ui.components.GlassCard
import com.salim.qr.ui.components.SalimBackground
import com.salim.qr.ui.components.SalimWatermark
import com.salim.qr.ui.components.SalimWordmark
import com.salim.qr.ui.screens.create.DestinationType
import com.salim.qr.ui.screens.scan.ScanResultBottomSheet
import com.salim.qr.ui.theme.DesignSpec
import com.salim.qr.ui.theme.PoppinsFontFamily
import com.salim.qr.ui.theme.SalimDangerRed
import com.salim.qr.ui.theme.SalimInactiveTab
import com.salim.qr.ui.theme.SalimIndigo
import com.salim.qr.ui.theme.SalimNavy
import com.salim.qr.ui.theme.SalimSubtitleGray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecentScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = remember { SalimDatabase.getInstance(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedFilter by remember { mutableStateOf("all") } // "all", "scanned", "created"
    var searchQuery by remember { mutableStateOf("") }
    var selectedItemForDetail by remember { mutableStateOf<CodeRecord?>(null) }
    var selectedRecordIds by remember { mutableStateOf(setOf<Long>()) }

    val isMultiSelect = selectedRecordIds.isNotEmpty()

    val records by when {
        searchQuery.isNotBlank() -> db.codeRecordDao().searchFlow(searchQuery)
        selectedFilter == "scanned" -> db.codeRecordDao().getByOriginFlow("scanned")
        selectedFilter == "created" -> db.codeRecordDao().getByOriginFlow("created")
        else -> db.codeRecordDao().getAllFlow()
    }.collectAsState(initial = emptyList())

    SalimBackground {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent,
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Decorative QR Watermark in top right corner
                SalimWatermark(modifier = Modifier.align(Alignment.TopEnd))

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Top Bar or Selection Bar
                    if (isMultiSelect) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = DesignSpec.PageMarginContent),
                            shape = RoundedCornerShape(16.dp),
                            color = SalimNavy
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { selectedRecordIds = emptySet() },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Close",
                                            tint = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${selectedRecordIds.size} selected",
                                        fontFamily = PoppinsFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Select All
                                    IconButton(
                                        onClick = {
                                            selectedRecordIds = if (selectedRecordIds.size == records.size) {
                                                emptySet()
                                            } else {
                                                records.map { it.id }.toSet()
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.SelectAll,
                                            contentDescription = "Select All",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Share selected
                                    IconButton(
                                        onClick = {
                                            val selectedItems = records.filter { selectedRecordIds.contains(it.id) }
                                            val sharedText = selectedItems.joinToString("\n\n") { "${it.title}: ${it.payload}" }
                                            CodeExporter.shareText(context, sharedText)
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Share,
                                            contentDescription = "Share",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Delete selected
                                    IconButton(
                                        onClick = {
                                            val itemsToDelete = records.filter { selectedRecordIds.contains(it.id) }
                                            selectedRecordIds = emptySet()
                                            scope.launch(Dispatchers.IO) {
                                                itemsToDelete.forEach { db.codeRecordDao().delete(it) }
                                                val res = snackbarHostState.showSnackbar(
                                                    message = "${itemsToDelete.size} items deleted",
                                                    actionLabel = "Undo",
                                                    duration = SnackbarDuration.Short
                                                )
                                                if (res == SnackbarResult.ActionPerformed) {
                                                    itemsToDelete.forEach { db.codeRecordDao().insert(it) }
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = SalimDangerRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        SalimWordmark(
                            modifier = Modifier
                                .padding(start = DesignSpec.WordmarkLeftMargin, top = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Title: "Recent" in indigo and "QR codes" in navy
                    Row(
                        modifier = Modifier.padding(horizontal = DesignSpec.PageMarginOnboarding),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.recent_title_unfiltered_prefix) + " ",
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = SalimIndigo
                        )
                        Text(
                            text = stringResource(R.string.recent_title_unfiltered_suffix),
                            fontFamily = PoppinsFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            color = SalimNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Segmented Control (All, Scanned, Created)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = DesignSpec.PageMarginContent),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterPill(
                            label = stringResource(R.string.filter_all),
                            isSelected = selectedFilter == "all",
                            onClick = { selectedFilter = "all" }
                        )
                        FilterPill(
                            label = stringResource(R.string.filter_scanned),
                            isSelected = selectedFilter == "scanned",
                            onClick = { selectedFilter = "scanned" }
                        )
                        FilterPill(
                            label = stringResource(R.string.filter_created),
                            isSelected = selectedFilter == "created",
                            onClick = { selectedFilter = "created" }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_hint),
                                fontFamily = PoppinsFontFamily,
                                fontSize = 13.sp,
                                color = SalimSubtitleGray
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Search,
                                contentDescription = "Search",
                                tint = SalimSubtitleGray,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
                            focusedBorderColor = SalimIndigo,
                            unfocusedBorderColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = DesignSpec.PageMarginContent)
                            .height(52.dp)
                            .testTag("recent_search_field")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // History Records List or Empty State
                    if (records.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(32.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.history_empty_title),
                                    fontFamily = PoppinsFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = SalimNavy
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = stringResource(R.string.history_empty_desc),
                                    fontFamily = PoppinsFontFamily,
                                    fontSize = 12.sp,
                                    color = SalimSubtitleGray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .testTag("recent_records_list"),
                            contentPadding = PaddingValues(
                                start = DesignSpec.RecentRowSideMargin,
                                end = DesignSpec.RecentRowSideMargin,
                                bottom = 120.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(DesignSpec.RecentRowGap)
                        ) {
                            items(
                                items = records,
                                key = { it.id }
                            ) { record ->
                                val isSelected = selectedRecordIds.contains(record.id)

                                SwipeToDeleteRow(
                                    record = record,
                                    isMultiSelect = isMultiSelect,
                                    isSelected = isSelected,
                                    onDelete = {
                                        scope.launch(Dispatchers.IO) {
                                            db.codeRecordDao().delete(record)
                                            val result = snackbarHostState.showSnackbar(
                                                message = context.getString(R.string.item_deleted),
                                                actionLabel = context.getString(R.string.action_undo),
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                db.codeRecordDao().insert(record)
                                            }
                                        }
                                    },
                                    onClick = {
                                        if (isMultiSelect) {
                                            selectedRecordIds = if (isSelected) {
                                                selectedRecordIds - record.id
                                            } else {
                                                selectedRecordIds + record.id
                                            }
                                        } else {
                                            selectedItemForDetail = record
                                        }
                                    },
                                    onLongClick = {
                                        if (!isMultiSelect) {
                                            selectedRecordIds = setOf(record.id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedItemForDetail != null) {
        val parsed = remember(selectedItemForDetail) {
            PayloadParser.parse(selectedItemForDetail!!.payload, selectedItemForDetail!!.format)
        }
        ScanResultBottomSheet(
            rawText = selectedItemForDetail!!.payload,
            parsedCode = parsed,
            isFavorite = selectedItemForDetail!!.favorite,
            onToggleFavorite = {
                val updated = selectedItemForDetail!!.copy(favorite = !selectedItemForDetail!!.favorite)
                selectedItemForDetail = updated
                scope.launch(Dispatchers.IO) {
                    db.codeRecordDao().update(updated)
                }
            },
            onDismiss = { selectedItemForDetail = null }
        )
    }
}

@Composable
private fun FilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) SalimIndigo else Color.White.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = PoppinsFontFamily,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else SalimNavy
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeToDeleteRow(
    record: CodeRecord,
    isMultiSelect: Boolean,
    isSelected: Boolean,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Surface(
                    shape = RoundedCornerShape(DesignSpec.RecentDeleteButtonRadius),
                    color = SalimDangerRed,
                    modifier = Modifier.size(DesignSpec.RecentDeleteButtonSize)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        },
        enableDismissFromStartToEnd = false
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(DesignSpec.RecentRowHeight)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                )
                .testTag("recent_card_${record.id}"),
            cornerRadius = DesignSpec.RecentRowRadius,
            elevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isMultiSelect) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) SalimIndigo else Color.Transparent)
                            .border(2.dp, if (isSelected) SalimIndigo else SalimInactiveTab, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                // 28dp type icon tile at left
                val destination = DestinationType.values().firstOrNull { it.title.equals(record.parsedType, ignoreCase = true) }
                    ?: DestinationType.WEBSITE
                Box(
                    modifier = Modifier
                        .size(DesignSpec.RecentTypeTileSize)
                        .clip(RoundedCornerShape(DesignSpec.RecentTypeTileRadius))
                        .background(destination.gradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = record.payload,
                        fontFamily = PoppinsFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SalimNavy,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val dateFormatted = remember(record.timestamp) {
                        SimpleDateFormat("dd.MM.yyyy hh:mm a", Locale.getDefault()).format(Date(record.timestamp))
                    }
                    Text(
                        text = dateFormatted,
                        fontFamily = PoppinsFontFamily,
                        fontSize = 10.sp,
                        color = SalimInactiveTab
                    )
                }
            }
        }
    }
}
