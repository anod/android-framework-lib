package info.anodsplace.compose.chooser

import android.content.ComponentName
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import info.anodsplace.compose.SystemIconShape
import info.anodsplace.ktx.resourceUri
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

val chooserIconSize = 56.dp

@Immutable
data class ChooserGridListStyle(
    val grayscaleUnselectedIcons: Boolean,
    val dimUnselectedIcons: Boolean,
    val dimAlpha: Float,
    val showSelectionOutline: Boolean,
    val selectionOutlineColor: Color,
    val selectionOutlineWidth: Dp,
    val animateSelection: Boolean,
)

object ChooserGridListDefaults {
    @Composable
    fun style(
        grayscaleUnselectedIcons: Boolean = false,
        dimUnselectedIcons: Boolean = true,
        dimAlpha: Float = 0.35f,
        showSelectionOutline: Boolean = true,
        selectionOutlineColor: Color = MaterialTheme.colorScheme.primary,
        selectionOutlineWidth: Dp = 2.dp,
        animateSelection: Boolean = true,
    ): ChooserGridListStyle {
        val safeDimAlpha = dimAlpha.coerceIn(0f, 1f)
        return ChooserGridListStyle(
            grayscaleUnselectedIcons = grayscaleUnselectedIcons,
            dimUnselectedIcons = dimUnselectedIcons,
            dimAlpha = safeDimAlpha,
            showSelectionOutline = showSelectionOutline,
            selectionOutlineColor = selectionOutlineColor,
            selectionOutlineWidth = selectionOutlineWidth,
            animateSelection = animateSelection
        )
    }

    @Composable
    fun singleSelect(): ChooserGridListStyle = style(
        grayscaleUnselectedIcons = false,
        dimUnselectedIcons = false,
        showSelectionOutline = false
    )

    @Composable
    fun multiSelect(): ChooserGridListStyle = style()
}

@Composable
private fun EntryItem(
    entry: ChooserEntry,
    icon: @Composable (saturation: Float) -> Unit,
    onClick: (ChooserEntry) -> Unit = { },
    isSelected: Boolean = true,
    style: ChooserGridListStyle = ChooserGridListDefaults.style(),
) {
    // Apply visual effects purely based on style + selection state
    val applyGrayscale = !isSelected && style.grayscaleUnselectedIcons
    val targetAlpha = if (!isSelected && style.dimUnselectedIcons) style.dimAlpha else 1f
    val animatedAlpha = if (style.animateSelection) animateFloatAsState(targetValue = targetAlpha, animationSpec = tween(250), label = "alpha").value else targetAlpha
    val targetSaturation = if (applyGrayscale) 0f else 1f
    val animatedSaturation = if (style.animateSelection) animateFloatAsState(targetValue = targetSaturation, animationSpec = tween(300), label = "saturation").value else targetSaturation
    val outlineTargetColor = if (isSelected && style.showSelectionOutline) style.selectionOutlineColor else Color.Transparent
    val outlineColor = if (style.animateSelection) animateColorAsState(targetValue = outlineTargetColor, animationSpec = tween(250), label = "outline-color").value else outlineTargetColor

    Column(
        modifier = Modifier
            .then(if (isSelected && style.showSelectionOutline) Modifier.border(style.selectionOutlineWidth, outlineColor, MaterialTheme.shapes.medium) else Modifier)
            .clip(shape = MaterialTheme.shapes.medium)
            .semantics { this.selected = isSelected }
            .clickable(enabled = !entry.isSection) { onClick(entry) }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.alpha(animatedAlpha)) { icon(animatedSaturation) }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            text = entry.title,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            // Accessibility: keep full opacity for unselected items so text remains readable
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun rememberReducedMotionPreference(): Boolean {
    val context = LocalContext.current
    return remember {
        try {
            val scale = Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f)
            scale == 0f
        } catch (_: Exception) { false }
    }
}

@Composable
private fun GlowPlaceholderItem(glow: Float, animated: Boolean, baseColor: Color, highlightColor: Color) {
    val surface = MaterialTheme.colorScheme.surface
    val blendedBase = baseColor.copy(alpha = 0.60f).compositeOver(surface)
    val blendedHighlight = highlightColor.copy(alpha = 0.85f)
    val fill = if (animated) lerp(blendedBase, blendedHighlight, glow) else lerp(blendedBase, blendedHighlight, 0.5f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(4.dp)
            .widthIn(min = 64.dp)
            .semantics { contentDescription = "loading" }
    ) {
        Box(
            modifier = Modifier
                .size(chooserIconSize)
                .clip(MaterialTheme.shapes.medium)
                .background(fill)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .height(12.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(fill)
        )
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Start,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun ChooserGridList(
    headers: List<ChooserEntry>,
    list: List<ChooserEntry>,
    asyncImage: @Composable (ChooserEntry, ColorFilter?) -> Unit,
    headerShape: Shape = MaterialTheme.shapes.medium,
    selectedComponents: Set<ComponentName> = emptySet(),
    onSelect: (ChooserEntry) -> Unit = { },
    style: ChooserGridListStyle = ChooserGridListDefaults.style(),
    isLoading: Boolean = false,
    reduceMotion: Boolean = false,
    loadingSection: ChooserEntry? = null,
) {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val highlight = MaterialTheme.colorScheme.primary
    val glow by if (isLoading && !reduceMotion) rememberInfiniteTransition(label = "glow-transition")
        .animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "glow-value"
        ) else remember { mutableFloatStateOf(0.5f) }

    val placeholderCount = 7

    LazyVerticalGrid(
        contentPadding = PaddingValues(16.dp),
        columns = GridCells.Adaptive(64.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = rememberLazyGridState(),
        userScrollEnabled = !isLoading
    ) {
        if (isLoading) {
            // Show section headers if we know them up-front while loading
            if (loadingSection?.isSection == true) {
                item(key = "loading-section-${loadingSection.sectionId}", span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(title = loadingSection.title)
                }
            }
            items(placeholderCount) {
                GlowPlaceholderItem(
                    glow = glow,
                    animated = !reduceMotion,
                    baseColor = base,
                    highlightColor = highlight
                )
            }
        } else {
            // Headers (always selected, display before sections)
            items(headers.size) { index ->
                val entry = headers[index]
                EntryItem(
                    entry,
                    onClick = { onSelect(entry) },
                    icon = { _ ->
                        if (entry.iconVector != null) {
                            Icon(
                                imageVector = entry.iconVector,
                                contentDescription = entry.title,
                                modifier = Modifier
                                    .size(chooserIconSize)
                                    .background(MaterialTheme.colorScheme.primary, shape = headerShape)
                                    .padding(2.dp),
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            asyncImage(entry, null)
                        }
                    },
                    isSelected = true,
                    style = style
                )
            }
            // Mixed list with optional section entries
            list.forEach { entry ->
                if (entry.isSection) {
                    item(key = "section-${entry.sectionId}", span = { GridItemSpan(maxLineSpan) }) {
                        SectionHeader(title = entry.title)
                    }
                } else {
                    item(key = entry.componentName?.flattenToShortString() ?: entry.title) {
                        val component = entry.componentName
                        val isSelected = component != null && selectedComponents.contains(component)
                        EntryItem(
                            entry,
                            onClick = { onSelect(entry) },
                            icon = { saturation ->
                                val colorMatrix = remember(saturation) { ColorMatrix().apply { setToSaturation(saturation) } }
                                val colorFilter = ColorFilter.colorMatrix(colorMatrix)
                                asyncImage(entry, colorFilter)
                            },
                            isSelected = isSelected,
                            style = style
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChooserDialog(
    loader: ChooserLoader,
    modifier: Modifier = Modifier,
    headers: List<ChooserEntry> = listOf(),
    onDismissRequest: () -> Unit,
    asyncImage: @Composable (ChooserEntry, ColorFilter?) -> Unit,
    emptyState: @Composable (filterApplied: Boolean) -> Unit,
    onClick: (ChooserEntry) -> Unit,
    style: ChooserGridListStyle = ChooserGridListDefaults.singleSelect(),
    topContent: @Composable (List<ChooserEntry>) -> Unit = {},
    loadingSection: ChooserEntry? = null
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        ChooserScreen(
            loader = loader,
            modifier = modifier,
            headers = headers,
            asyncImage = asyncImage,
            emptyState = emptyState,
            selectedComponents = emptySet(),
            onSelect = onClick,
            style = style,
            topContent = topContent,
            loadingSection = loadingSection
        )
    }
}

@Composable
fun ChooserEmptyState(message: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Android,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChooserScreen(
    loader: ChooserLoader,
    modifier: Modifier = Modifier,
    headers: List<ChooserEntry> = listOf(),
    asyncImage: @Composable (ChooserEntry, ColorFilter?) -> Unit,
    emptyState: @Composable (filterApplied: Boolean) -> Unit,
    selectedComponents: Set<ComponentName> = emptySet(),
    onSelect: (ChooserEntry) -> Unit = { },
    style: ChooserGridListStyle = ChooserGridListDefaults.singleSelect(),
    topContent: @Composable (List<ChooserEntry>) -> Unit = {},
    showLoadingInPreview: Boolean = false,
    loadingSection: ChooserEntry? = null,
) {
    var emitted by remember { mutableStateOf(false) }
    val appsList by loader.load().onEach { emitted = true }.collectAsState(initial = emptyList())
    var minTimePassed by remember { mutableStateOf(false) }
    val isPreview = LocalInspectionMode.current
    LaunchedEffect(Unit) {
        if (!isPreview) {
            delay(250)
        }
        minTimePassed = true
    }
    val showLoading = if (isPreview && !showLoadingInPreview) false else !emitted || !minTimePassed
    val iconSizePx = with(LocalDensity.current) { chooserIconSize.roundToPx() }
    val headerShape = SystemIconShape(iconSizePx)
    val reduceMotion = rememberReducedMotionPreference()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 352.dp),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column {
            topContent(appsList)
            when {
                showLoading -> {
                    ChooserGridList(
                        headers = headers,
                        list = appsList,
                        asyncImage = asyncImage,
                        headerShape = headerShape,
                        selectedComponents = selectedComponents,
                        onSelect = onSelect,
                        style = style,
                        isLoading = true,
                        reduceMotion = reduceMotion,
                        loadingSection = loadingSection
                    )
                }
                appsList.isEmpty() && headers.isEmpty() -> {
                    emptyState(false)
                }
                else -> {
                    ChooserGridList(
                        headers = headers,
                        list = appsList,
                        asyncImage = asyncImage,
                        headerShape = headerShape,
                        selectedComponents = selectedComponents,
                        onSelect = onSelect,
                        style = style,
                        isLoading = false,
                        reduceMotion = reduceMotion
                    )
                }
            }
        }
    }
}

@Composable
fun MultiSelectChooserDialog(
    loader: ChooserLoader,
    modifier: Modifier = Modifier,
    headers: List<ChooserEntry> = emptyList(),
    selectedComponents: Set<ComponentName> = emptySet(),
    onSelect: (ChooserEntry) -> Unit = { },
    onDismissRequest: () -> Unit,
    asyncImage: @Composable (ChooserEntry, ColorFilter?) -> Unit,
    emptyState: @Composable (filterApplied: Boolean) -> Unit,
    minHeight: Dp = 420.dp,
    topContent: @Composable (List<ChooserEntry>) -> Unit = {},
    bottomContent: @Composable (List<ChooserEntry>) -> Unit = {},
    style: ChooserGridListStyle = ChooserGridListDefaults.multiSelect(),
    listFilter: (List<ChooserEntry>) -> List<ChooserEntry> = { it },
    loadingSection: ChooserEntry? = null
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        MultiSelectChooserContent(
            loader = loader,
            modifier = modifier,
            headers = headers,
            selectedComponents = selectedComponents,
            onSelect = onSelect,
            asyncImage = asyncImage,
            emptyState = emptyState,
            minHeight = minHeight,
            topContent = topContent,
            bottomContent = bottomContent,
            style = style,
            listFilter = listFilter,
            loadingSection = loadingSection
        )
    }
}

@Composable
private fun MultiSelectChooserContent(
    loader: ChooserLoader,
    modifier: Modifier = Modifier,
    headers: List<ChooserEntry> = emptyList(),
    selectedComponents: Set<ComponentName> = emptySet(),
    onSelect: (ChooserEntry) -> Unit = { },
    asyncImage: @Composable (ChooserEntry, ColorFilter?) -> Unit,
    emptyState: @Composable (filterApplied: Boolean) -> Unit,
    minHeight: Dp = 420.dp,
    topContent: @Composable (List<ChooserEntry>) -> Unit = {},
    bottomContent: @Composable (List<ChooserEntry>) -> Unit = {},
    style: ChooserGridListStyle = ChooserGridListDefaults.multiSelect(),
    listFilter: (List<ChooserEntry>) -> List<ChooserEntry> = { it },
    loadingSection: ChooserEntry? = null
) {
    var emitted by remember { mutableStateOf(false) }
    val appsList by loader.load().onEach { emitted = true }.collectAsState(initial = emptyList())
    val filteredList = remember(appsList, listFilter) { listFilter(appsList) }
    val iconSizePx = with(LocalDensity.current) { chooserIconSize.roundToPx() }
    val headerShape = SystemIconShape(iconSizePx)
    val reduceMotion = rememberReducedMotionPreference()
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = minHeight),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column {
            topContent(appsList)
            Box(modifier = Modifier.weight(1f, fill = true)) {
                when {
                    !emitted -> {
                        ChooserGridList(
                            headers = headers,
                            list = filteredList,
                            asyncImage = asyncImage,
                            headerShape = headerShape,
                            selectedComponents = selectedComponents,
                            onSelect = onSelect,
                            style = style,
                            isLoading = true,
                            reduceMotion = reduceMotion,
                            loadingSection = loadingSection
                        )
                    }
                    filteredList.isEmpty() && appsList.isNotEmpty() -> {
                        emptyState(true)
                    }
                    filteredList.isEmpty() && headers.isEmpty() -> {
                        emptyState(false)
                    }
                    else -> {
                        ChooserGridList(
                            headers = headers,
                            list = filteredList,
                            asyncImage = asyncImage,
                            headerShape = headerShape,
                            selectedComponents = selectedComponents,
                            onSelect = onSelect,
                            style = style,
                            isLoading = false,
                            reduceMotion = reduceMotion
                        )
                    }
                }
            }
            bottomContent(appsList)
        }
    }
}

@Preview(name = "Chooser Screen", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ChooserScreenPreview() {
    val context = LocalContext.current
    MaterialTheme {
        ChooserScreen(
            loader = StaticChooserLoader(
                listOf(
                    ChooserEntry(
                        context = context,
                        title = "Music",
                        iconRes = android.R.drawable.ic_input_add
                    ),
                    ChooserEntry(
                        context = context,
                        title = "Maps",
                        iconRes = android.R.drawable.ic_input_delete
                    ),
                    sectionEntry("section-1", "Apps"),
                    ChooserEntry(
                        context = context,
                        title = "Calls",
                        iconRes = android.R.drawable.ic_input_get
                    ),
                    ChooserEntry(
                        componentName = ComponentName("pkg.sample", "pkg.sample.App1"),
                        title = "App One"
                    ),
                    sectionEntry("section-2", "Tools"),
                    ChooserEntry(
                        componentName = ComponentName("pkg.sample", "pkg.sample.App2"),
                        title = "App Two"
                    )
                )
            ),
            headers = listOf(
                headerEntry(0, "Actions", iconUri = context.resourceUri(android.R.drawable.ic_popup_sync)),
                headerEntry(0, "More", Icons.Filled.Alarm)
            ),
            asyncImage = { _, _ ->
                Box(Modifier.size(chooserIconSize).background(MaterialTheme.colorScheme.onPrimaryContainer)) {

                }
            },
            emptyState = { filterApplied ->
                ChooserEmptyState("Empty ${if (filterApplied) "filtered" else ""}")
            },
            style = ChooserGridListDefaults.multiSelect().copy(
                grayscaleUnselectedIcons = true,
                dimUnselectedIcons = true,
                dimAlpha = 0.4f,
                showSelectionOutline = true
            )
        )
    }
}

@Preview(name = "Chooser Screen Loading", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun ChooserScreenLoadingPreview() {
    MaterialTheme {
        val loader = object : ChooserLoader { override fun load() =
            flow<List<ChooserEntry>> { /* never emit */ }
        }
        ChooserScreen(
            loader = loader,
            asyncImage = { _, _ ->
                Box(Modifier.size(chooserIconSize).background(MaterialTheme.colorScheme.onPrimaryContainer)) {

                }
            },
            emptyState = { filterApplied ->
                ChooserEmptyState("Empty ${if (filterApplied) "filtered" else ""}")
            },
            onSelect = {},
            headers = emptyList(),
            topContent = { _ -> Text("Loading...", modifier = Modifier.padding(16.dp)) },
            showLoadingInPreview = true
        )
    }
}

@Preview(name = "Chooser Screen Loading With Sections", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun ChooserScreenLoadingWithSectionsPreview() {
    MaterialTheme {
        val loader = object : ChooserLoader { override fun load() =
            flow<List<ChooserEntry>> { /* never emit */ }
        }
        ChooserScreen(
            loader = loader,
            asyncImage = { _, _ ->
                Box(Modifier.size(chooserIconSize).background(MaterialTheme.colorScheme.onPrimaryContainer)) {

                }
            },
            emptyState = { filterApplied ->
                ChooserEmptyState("Empty ${if (filterApplied) "filtered" else ""}")
            },
            onSelect = {},
            headers = emptyList(),
            topContent = { _ -> Text("Loading...", modifier = Modifier.padding(16.dp)) },
            showLoadingInPreview = true,
            loadingSection = sectionEntry("section-1", "Apps")
        )
    }
}

@Preview(name = "Chooser Screen Empty", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun ChooserScreenEmptyPreview() {
    MaterialTheme {
        // Loader that immediately emits an empty list to trigger the empty state
        val loader = StaticChooserLoader(emptyList())
        ChooserScreen(
            loader = loader,
            asyncImage = { _, _ ->
                Box(Modifier.size(chooserIconSize).background(MaterialTheme.colorScheme.onPrimaryContainer)) { }
            },
            emptyState = { filterApplied ->
                // Reuse existing preview string pattern to avoid introducing new untranslated strings
                ChooserEmptyState("Empty ${if (filterApplied) "filtered" else ""}")
            },
            headers = emptyList(),
            onSelect = {},
            style = ChooserGridListDefaults.singleSelect(),
            topContent = { _ -> }
        )
    }
}
