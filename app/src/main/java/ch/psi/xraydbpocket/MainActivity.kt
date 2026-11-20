package ch.psi.xraydbpocket

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.exp
import kotlin.math.pow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class Vm(private val dao: XrayDao) : ViewModel() {
    val elements = dao.elements().stateIn(
        CoroutineScope(Dispatchers.IO),
        SharingStarted.Eagerly,
        emptyList()
    )
    fun entries(z: Int) = dao.entries(z)
    fun near(e: Double, t: Double) = dao.near(e, e - t, e + t)
    fun nearEmission(e: Double, t: Double) = dao.nearEmission(e, e - t, e + t)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = XrayDb.get(this).dao()
        setContent {
            val vm: Vm = viewModel(factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T = Vm(dao) as T
            })
            App(vm)
        }
    }
}

enum class Page { TABLE, ENERGY, XRF, ATTEN, ABOUT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(vm: Vm) {
    var page by remember { mutableStateOf(Page.TABLE) }
    var selected by remember { mutableStateOf<Element?>(null) }
    BackHandler(enabled = selected != null) {
        selected = null
    }
    MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF006A6A), secondary = Color(0xFF4A6363))) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(selected?.let { "${it.symbol} · ${it.name}" } ?: "XRayDB Pocket") },
                    navigationIcon = {
                        if (selected != null) {
                            IconButton(onClick = { selected = null }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                )
            },
            bottomBar = {
                if (selected == null) {
                    NavigationBar {
                        NavigationBarItem(
                            selected = page == Page.TABLE,
                            onClick = { page = Page.TABLE },
                            icon = { Icon(Icons.Default.GridView, contentDescription = null) },
                            label = { Text("Elements") }
                        )
                        NavigationBarItem(
                            selected = page == Page.ENERGY,
                            onClick = { page = Page.ENERGY },
                            icon = { Icon(Icons.Default.Search, contentDescription = null) },
                            label = { Text("Energy") }
                        )
                        NavigationBarItem(
                            selected = page == Page.XRF,
                            onClick = { page = Page.XRF },
                            icon = { Icon(Icons.Default.Science, contentDescription = null) },
                            label = { Text("XRF ID") }
                        )
                        NavigationBarItem(
                            selected = page == Page.ATTEN,
                            onClick = { page = Page.ATTEN },
                            icon = { Icon(Icons.Default.Layers, contentDescription = null) },
                            label = { Text("Atten") }
                        )
                        NavigationBarItem(
                            selected = page == Page.ABOUT,
                            onClick = { page = Page.ABOUT },
                            icon = { Icon(Icons.Default.Info, contentDescription = null) },
                            label = { Text("About") }
                        )
                    }
                }
            }
        ) { p ->
            Box(Modifier.padding(p)) {
                if (selected != null) {
                    ElementScreen(selected!!, vm)
                } else {
                    when (page) {
                        Page.TABLE -> PeriodicTable(vm) { selected = it }
                        Page.ENERGY -> EnergySearch(vm)
                        Page.XRF -> XrfPeakIdentifier(vm) { selected = it }
                        Page.ATTEN -> AttenuationCalculator(vm)
                        Page.ABOUT -> About()
                    }
                }
            }
        }
    }
}

enum class ElementState { GAS, LIQUID, SOLID }

fun getElementState(z: Int): ElementState {
    return when (z) {
        1, 2, 7, 8, 9, 10, 17, 18, 36, 54, 86, 118 -> ElementState.GAS
        31, 35, 55, 80, 87 -> ElementState.LIQUID
        else -> ElementState.SOLID
    }
}

@Composable
fun PeriodicTable(vm: Vm, onPick: (Element) -> Unit) {
    val es by vm.elements.collectAsState()
    Column(Modifier.padding(horizontal = 12.dp, vertical = 2.dp)) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Select an element", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(10.dp).background(Color(0xFFE0F7FA)))
                Text("Gas", style = MaterialTheme.typography.labelSmall)
                Box(Modifier.size(10.dp).background(Color(0xFFE8F5E9)))
                Text("Liquid", style = MaterialTheme.typography.labelSmall)
            }
        }
        Spacer(Modifier.height(2.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(es) { e ->
                val state = getElementState(e.z)
                val cardColor = when (state) {
                    ElementState.GAS -> Color(0xFFE0F7FA)
                    ElementState.LIQUID -> Color(0xFFE8F5E9)
                    ElementState.SOLID -> MaterialTheme.colorScheme.surfaceVariant
                }
                Card(
                    modifier = Modifier.aspectRatio(1f).clickable { onPick(e) },
                    colors = CardDefaults.cardColors(containerColor = cardColor)
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(e.z.toString(), style = MaterialTheme.typography.labelSmall)
                        Text(e.symbol, fontWeight = FontWeight.Bold)
                        Text(e.name.take(7), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

fun copyToClipboard(context: Context, label: String, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Copied $label to clipboard!", Toast.LENGTH_SHORT).show()
}

fun buildElementMarkdown(e: Element, rows: List<SpectralEntry>): String {
    val sb = StringBuilder()
    sb.appendLine("### ${e.symbol} · ${e.name} (Z = ${e.z})")
    if (e.configuration.isNotBlank()) {
        sb.appendLine("- **Electron Configuration**: ${formatConfiguration(e.configuration)}")
    }
    sb.appendLine("- **Atomic Mass**: ${e.mass} u")
    sb.appendLine()

    val groups = rows.groupBy { it.category }
    groups.forEach { (cat, list) ->
        sb.appendLine("#### $cat:")
        list.forEach { item ->
            val lambda = "%.5g".format(1239.841984 / item.energyEv)
            sb.appendLine("- **${formatLabel(item.label)}**: ${formatEnergy(item.energyEv)} (λ = $lambda nm) — ${item.environment} (${item.source})")
        }
        sb.appendLine()
    }
    return sb.toString().trimEnd()
}

@Composable
fun ElementScreen(e: Element, vm: Vm) {
    val context = LocalContext.current
    val rows by vm.entries(e.z).collectAsState(initial = emptyList())
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Atomic number Z = ${e.z}", style = MaterialTheme.typography.titleMedium)
                    if (e.configuration.isNotBlank()) {
                        Text("Configuration: ${formatConfiguration(e.configuration)}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                }
                OutlinedButton(onClick = {
                    val markdown = buildElementMarkdown(e, rows)
                    copyToClipboard(context, "${e.symbol} Lab Note", markdown)
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Lab Note", style = MaterialTheme.typography.labelSmall)
                }
            }
            Text("Tap-free offline reference. Values retain category, environment and source.")
        }
        val groups = rows.groupBy { it.category }
        groups.forEach { (cat, list) ->
            item {
                Text(cat, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
            items(list) { EntryCard(it) }
        }
        if (rows.isEmpty()) {
            item {
                Text("No curated entries are bundled for this element yet. The element metadata is present and the schema is ready for an authoritative import.")
            }
        }
    }
}

@Composable
fun EntryCard(x: SpectralEntry) {
    val context = LocalContext.current
    Card {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(formatLabel(x.label), fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(formatEnergy(x.energyEv), fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = {
                            val snippet = "${formatLabel(x.label)}: ${formatEnergy(x.energyEv)} (λ = ${"%.5g".format(1239.841984 / x.energyEv)} nm) — ${x.environment} (${x.source})"
                            copyToClipboard(context, x.label, snippet)
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Entry", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
            Text("λ = ${"%.5g".format(1239.841984 / x.energyEv)} nm  ·  ${x.environment}")
            Text(x.source, style = MaterialTheme.typography.bodySmall)
            if (x.note.isNotBlank()) {
                Text(x.note, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

fun formatLabel(label: String): String {
    var formatted = label
        .replace("Ka1", "Kα1")
        .replace("Ka2", "Kα2")
        .replace("Kb1", "Kβ1")
        .replace("La1", "Lα1")
        .replace("Lb1", "Lβ1")
        .replace("Lb2", "Lβ2")
        .replace("Lg1", "Lγ1")
        .replace("Ma1", "Mα1")
        .replace("Mb", "Mβ")
        .replace("Ll", "Lℓ")

    val subMap = mapOf(
        '0' to '₀', '1' to '₁', '2' to '₂', '3' to '₃', '4' to '₄',
        '5' to '₅', '6' to '₆', '7' to '₇', '8' to '₈', '9' to '₉'
    )

    formatted = formatted
        .replace("3/2", "₃⁄₂")
        .replace("5/2", "₅⁄₂")
        .replace("7/2", "₇⁄₂")
        .replace("1/2", "₁⁄₂")

    val trailingDigitPattern = Regex("([αβγℓ])([0-9])")
    formatted = formatted.replace(trailingDigitPattern) { match ->
        val greek = match.groupValues[1]
        val digit = match.groupValues[2]
        greek + (subMap[digit.first()] ?: digit)
    }

    val chemPattern = Regex("([A-Z][a-z]?)([0-9]+)")
    formatted = formatted.replace(chemPattern) { match ->
        val elem = match.groupValues[1]
        val num = match.groupValues[2]
        elem + num.map { subMap[it] ?: it }.joinToString("")
    }

    return formatted
}

fun formatConfiguration(config: String): String {
    val superscripts = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹'
    )
    return config.replace(Regex("([spdfgh])([0-9]+)")) { match ->
        val letter = match.groupValues[1]
        val digits = match.groupValues[2]
        letter + digits.map { superscripts[it] ?: it }.joinToString("")
    }
}

fun formatEnergy(v: Double) = if (v >= 1000) "%.3f keV".format(v / 1000) else "%.1f eV".format(v)

@Composable
fun EnergySearch(vm: Vm) {
    var s by remember { mutableStateOf("530") }
    var tol by remember { mutableStateOf("10") }
    val e = s.toDoubleOrNull()
    val t = tol.toDoubleOrNull()
    val flow = remember(e, t) { if ((e != null) && (t != null)) vm.near(e, t) else flowOf(emptyList()) }
    val rows by flow.collectAsState(initial = emptyList())
    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text("Reverse lookup", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(s, { s = it }, Modifier.weight(1f), label = { Text("Energy (eV)") })
            OutlinedTextField(tol, { tol = it }, Modifier.weight(1f), label = { Text("Tolerance (eV)") })
        }
        Spacer(Modifier.height(4.dp))
        if (e != null) {
            Text("λ = ${"%.6g".format(1239.841984 / e)} nm")
            Text("ν = ${"%.6g".format(e * 2.417989242e14)} Hz")
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rows) { EntryCard(it) }
        }
    }
}

@Composable
fun XrfPeakIdentifier(vm: Vm, onPickElement: (Element) -> Unit) {
    var energyStr by remember { mutableStateOf("6400") }
    var tolStr by remember { mutableStateOf("40") }
    val e = energyStr.toDoubleOrNull()
    val t = tolStr.toDoubleOrNull()

    val flow = remember(e, t) {
        if (e != null && t != null) vm.nearEmission(e, t) else flowOf(emptyList())
    }
    val rows by flow.collectAsState(initial = emptyList())
    val elements by vm.elements.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("XRF / EDX Peak Identifier", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Input an observed experimental peak energy to instantly identify matching element characteristic X-ray emission lines.")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = energyStr,
                onValueChange = { energyStr = it },
                label = { Text("Peak Energy (eV)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = tolStr,
                onValueChange = { tolStr = it },
                label = { Text("Tolerance (± eV)") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(2.dp))
        Text("Matching Emission Lines (${rows.size})", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
            items(rows) { entry ->
                val elem = elements.find { it.z == entry.z }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (elem != null) onPickElement(elem)
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                "${elem?.symbol ?: "?"} (${elem?.name ?: ""}) · ${formatLabel(entry.label)}",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(formatEnergy(entry.energyEv), fontWeight = FontWeight.Bold)
                        }
                        Text("λ = ${"%.5g".format(1239.841984 / entry.energyEv)} nm  ·  ${entry.environment}")
                        Text(entry.source, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            if (rows.isEmpty() && e != null) {
                item {
                    Text("No matching characteristic X-ray emission lines found within ±${t ?: 0} eV. Try increasing the tolerance or entering another energy.")
                }
            }
        }
    }
}

@Composable
fun LinkCard(title: String, url: String, description: String) {
    val uriHandler = LocalUriHandler.current
    Card(
        Modifier
            .fillMaxWidth()
            .clickable { uriHandler.openUri(url) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open Link",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(2.dp))
            Text(url, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Composable
fun About() {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("XRayDB Pocket", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "An offline pocket reference for X-ray binding energies, characteristic emission lines, Auger transitions, and gas-phase XPS spectra across all 118 elements.",
            style = MaterialTheme.typography.bodyMedium
        )

        Text("Physics Conversions", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("λ [nm] = 1239.841984 / E [eV]", fontWeight = FontWeight.SemiBold)
                Text("ν [Hz] = 2.417989242×10¹⁴ · E [eV]", fontWeight = FontWeight.SemiBold)
                Text(
                    "Photoelectron KE ≈ hν − BE (before work-function and analyzer corrections).",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Text("Authoritative X-Ray Databases", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

        LinkCard(
            title = "LBL X-Ray Data Booklet",
            url = "https://xdb.lbl.gov/",
            description = "LBNL premier reference booklet for X-ray energies, attenuation, emission lines, and absorption edges."
        )

        LinkCard(
            title = "NIST X-Ray Transition Energies (SRD 128)",
            url = "https://www.nist.gov/pml/x-ray-transition-energies-database",
            description = "NIST standard reference database for atomic energy levels, emission lines, and transition energies."
        )

        LinkCard(
            title = "NIST XPS Database (SRD 20)",
            url = "https://srdata.nist.gov/xps/",
            description = "NIST standard reference database for solid-state photoelectron binding energies and Auger kinetic energies."
        )

        LinkCard(
            title = "University of Tartu Gas Phase XPS Database",
            url = "https://xps.ut.ee/database.html",
            description = "High-precision gas-phase core-level binding energies measured using synchrotron radiation."
        )

        LinkCard(
            title = "CXRO X-Ray Interactions With Matter",
            url = "https://henke.lbl.gov/optical_constants/",
            description = "Henke tables for X-ray transmission, attenuation, reflectivity, and optical constants calculation."
        )

        LinkCard(
            title = "XrayDB Python Library",
            url = "https://xraypy.github.io/XrayDB/",
            description = "Open-source Python library and database for X-ray scattering factors, atomic data, and absorption coefficients."
        )

        LinkCard(
            title = "Hephaestus / Demeter XAFS Database",
            url = "https://bruceravel.github.io/demeter/",
            description = "X-ray absorption edge calculator, fluorescence line standards, and EXAFS/XANES reference tool."
        )
    }
}

@Composable
fun AttenuationCalculator(vm: Vm) {
    var isCompoundMode by remember { mutableStateOf(false) }
    var energyStr by remember { mutableStateOf("8047.8") }
    var thicknessStr by remember { mutableStateOf("250") }
    var selectedMaterial by remember { mutableStateOf("Be") }

    var formulaStr by remember { mutableStateOf("SiO2") }
    var compoundDensityStr by remember { mutableStateOf("2.20") }

    val allElements by vm.elements.collectAsState()

    val materials = listOf(
        MaterialInfo("Be", "Beryllium (window)", 1.848, 18.0, 0.2),
        MaterialInfo("Diamond", "Diamond (C)", 3.52, 75.0, 0.25),
        MaterialInfo("Kapton", "Kapton Polyimide", 1.42, 110.0, 0.3),
        MaterialInfo("Mylar", "Mylar Polyester", 1.39, 105.0, 0.3),
        MaterialInfo("Si", "Silicon Wafer", 2.33, 380.0, 0.4),
        MaterialInfo("Ge", "Germanium", 5.32, 1400.0, 0.5),
        MaterialInfo("Al", "Aluminum Foil", 2.70, 320.0, 0.4),
        MaterialInfo("Ti", "Titanium", 4.51, 1100.0, 0.5),
        MaterialInfo("Mo", "Molybdenum", 10.2, 4500.0, 0.8),
        MaterialInfo("Ag", "Silver", 10.5, 5200.0, 0.9),
        MaterialInfo("Pd", "Palladium", 12.0, 4800.0, 0.8),
        MaterialInfo("Cu", "Copper", 8.96, 3200.0, 0.6),
        MaterialInfo("Au", "Gold", 19.32, 28000.0, 1.2),
        MaterialInfo("C", "Graphite / Carbon", 2.26, 85.0, 0.3),
        MaterialInfo("BN", "Boron Nitride", 2.10, 65.0, 0.25),
        MaterialInfo("Water", "Water (H₂O)", 1.00, 95.0, 0.2),
        MaterialInfo("Air", "Air (STP)", 0.001205, 100.0, 0.2),
        MaterialInfo("PMMA", "Lucite / PMMA", 1.18, 90.0, 0.25)
    )

    val energyEv = energyStr.toDoubleOrNull() ?: 8047.8
    val energyKeV = energyEv / 1000.0
    val thicknessUm = thicknessStr.toDoubleOrNull() ?: 250.0
    val thicknessCm = thicknessUm * 1e-4

    val (matName, density, muRho) = if (isCompoundMode) {
        val parsedComponents = parseFormula(formulaStr, allElements)
        val compDensity = compoundDensityStr.toDoubleOrNull() ?: 2.0
        if (!parsedComponents.isNullOrEmpty()) {
            val totalMolarMass = parsedComponents.sumOf { it.element.mass * it.count }
            val weightedMuRho = parsedComponents.sumOf { comp ->
                val weightFraction = (comp.element.mass * comp.count) / totalMolarMass
                val elemMu = elementMuRho(comp.element.z, energyKeV)
                weightFraction * elemMu
            }
            Triple(formatLabel(formulaStr), compDensity, weightedMuRho)
        } else {
            Triple("Invalid Formula", compDensity, 100.0)
        }
    } else {
        val mat = materials.find { it.symbol == selectedMaterial } ?: materials[0]
        val mMu = if (energyKeV > 0.1) mat.a * energyKeV.pow(-3.2) + mat.b else 1000.0
        Triple(mat.name, mat.density, mMu)
    }

    val mu = muRho * density
    val transmission = exp(-mu * thicknessCm)
    val attenuationLengthUm = if (mu > 0) (1.0 / mu) * 10000.0 else 0.0

    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("X-Ray Attenuation & Transmission", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text("Calculate Beer-Lambert transmission (I/I₀) and attenuation for pre-set filters or custom compound chemical formulas.")
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f).clickable { isCompoundMode = false },
                    colors = CardDefaults.cardColors(
                        containerColor = if (!isCompoundMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                        Text("Pre-set Filters", fontWeight = FontWeight.Bold)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f).clickable { isCompoundMode = true },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompoundMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(Modifier.padding(12.dp), contentAlignment = Alignment.Center) {
                        Text("Custom Compound", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = energyStr,
                    onValueChange = { energyStr = it },
                    label = { Text("Energy (eV)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = thicknessStr,
                    onValueChange = { thicknessStr = it },
                    label = { Text("Thickness (µm)") },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (isCompoundMode) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = formulaStr,
                        onValueChange = { formulaStr = it },
                        label = { Text("Formula (e.g. SiO2)") },
                        modifier = Modifier.weight(1.2f)
                    )
                    OutlinedTextField(
                        value = compoundDensityStr,
                        onValueChange = { compoundDensityStr = it },
                        label = { Text("Density (g/cm³)") },
                        modifier = Modifier.weight(0.8f)
                    )
                }
            }
            item {
                Text("Select Compound Preset", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            items(compoundPresets.chunked(4)) { rowItems ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowItems.forEach { cp ->
                        val selected = formulaStr == cp.formula
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable {
                                    formulaStr = cp.formula
                                    compoundDensityStr = cp.density.toString()
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(Modifier.fillMaxSize().padding(2.dp), contentAlignment = Alignment.Center) {
                                Text(formatLabel(cp.name), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    repeat(4 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        } else {
            item {
                Text("Select Material / Filter Preset", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            items(materials.chunked(4)) { rowItems ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowItems.forEach { m ->
                        val selected = m.symbol == selectedMaterial
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .clickable { selectedMaterial = m.symbol },
                            colors = CardDefaults.cardColors(
                                containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(Modifier.fillMaxSize().padding(2.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(m.symbol, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    Text(m.name.take(9), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                    repeat(4 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Results for $matName (${thicknessUm} µm at ${formatEnergy(energyEv)})", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Text("Transmission (I/I₀): %.2f %%".format(transmission * 100))
                    Text("Attenuation Length (1/μ): %.2f µm".format(attenuationLengthUm))
                    Text("Linear Attenuation Coeff (μ): %.2f cm⁻¹".format(mu))
                    Text("Mass Attenuation Coeff (μ/ρ): %.2f cm²/g".format(muRho))
                }
            }
        }
    }
}

fun elementMuRho(z: Int, energyKeV: Double): Double {
    if (energyKeV <= 0.1) return 1000.0
    val a = 0.012 * z.toDouble().pow(3.5)
    val b = 0.15 + 0.005 * z
    return a * energyKeV.pow(-3.2) + b
}

data class FormulaComponent(val element: Element, val count: Double)

fun parseFormula(formula: String, elements: List<Element>): List<FormulaComponent>? {
    val regex = Regex("([A-Z][a-z]?)([0-9]*\\.?[0-9]*)")
    val matches = regex.findAll(formula.replace(" ", "")).toList()
    if (matches.isEmpty()) return null

    val components = mutableListOf<FormulaComponent>()
    val elemMap = elements.associateBy { it.symbol }

    for (m in matches) {
        val sym = m.groupValues[1]
        val countStr = m.groupValues[2]
        val count = if (countStr.isEmpty()) 1.0 else countStr.toDoubleOrNull() ?: 1.0
        val elem = elemMap[sym] ?: return null
        components.add(FormulaComponent(elem, count))
    }
    return components.ifEmpty { null }
}

data class MaterialInfo(val symbol: String, val name: String, val density: Double, val a: Double, val b: Double)

data class CompoundPreset(val formula: String, val name: String, val density: Double)

val compoundPresets = listOf(
    CompoundPreset("SiO2", "SiO₂", 2.20),
    CompoundPreset("GaAs", "GaAs", 5.32),
    CompoundPreset("Fe2O3", "Fe₂O₃", 5.24),
    CompoundPreset("Al2O3", "Al₂O₃", 3.98),
    CompoundPreset("CeO2", "CeO₂", 7.22),
    CompoundPreset("PbS", "PbS", 7.60),
    CompoundPreset("CdTe", "CdTe", 5.85),
    CompoundPreset("InP", "InP", 4.81),
    CompoundPreset("Si3N4", "Si₃N₄", 3.10),
    CompoundPreset("Y1Ba2Cu3O7", "YBCO", 6.30)
)
