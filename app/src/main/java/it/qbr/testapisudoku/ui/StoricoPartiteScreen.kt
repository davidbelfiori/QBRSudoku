package it.qbr.testapisudoku.ui

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.db.AppDatabase
import it.qbr.testapisudoku.db.Game
import it.qbr.testapisudoku.ui.theme.blue_secondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class FiltroVittoria { TUTTE, VINTE, PERSE }
enum class Ordinamento(val label: String) { CRONO("Data"), TEMPO("Tempo di gioco") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoricoPartiteScreen(navController: NavHostController, isDarkTheme: Boolean) {
    val context = LocalContext.current
    var storico by remember { mutableStateOf<List<Game>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val expandedStates = remember { mutableStateMapOf<Long, Boolean>() }

    // Stati per i filtri
    var filtroVittoria by remember { mutableStateOf(FiltroVittoria.TUTTE) }
    var ordinamento by remember { mutableStateOf(Ordinamento.CRONO) }
    var ordineDecrescente by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            storico = AppDatabase.getDatabase(context).partitaDao().tutteLePartite()
        }
    }

    // Applica filtri e ordinamento
    val storicoFiltratoOrdinato = remember(storico, filtroVittoria, ordinamento, ordineDecrescente) {
        storico
            .filter {
                when (filtroVittoria) {
                    FiltroVittoria.TUTTE -> true
                    FiltroVittoria.VINTE -> it.vinta
                    FiltroVittoria.PERSE -> !it.vinta
                }
            }
            .let { lista ->
                when (ordinamento) {
                    Ordinamento.CRONO ->
                        if (ordineDecrescente)
                            lista.sortedByDescending { it.dataOra }
                        else
                            lista.sortedBy { it.dataOra }
                    Ordinamento.TEMPO ->
                        if (ordineDecrescente)
                            lista.sortedByDescending { it.tempo }
                        else
                            lista.sortedBy { it.tempo }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text( text = stringResource(R.string.StoricoPartite)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_svgrepo_com),
                            contentDescription = "Torna alla Home",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Sezione filtri
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                colors = CardDefaults.cardColors(containerColor = if(isDarkTheme) Color(34, 40, 49) else Color(0xFFE0F1FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.Filtra)+":", modifier = Modifier.padding(end = 8.dp), color =  if (isDarkTheme) Color.White else Color.Black)
                        // Filtro vittoria
                        FiltroTab(
                            options = FiltroVittoria.entries,
                            selected = filtroVittoria,
                            onSelect = { filtroVittoria = it },
                            isDarkTheme = isDarkTheme
                        )
                    }
                    Spacer(Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = stringResource(R.string.ordina_per)+":", modifier = Modifier.padding(end = 8.dp),color =  if (isDarkTheme) Color.White else Color.Black)
                        // Ordinamento
                        FiltroTab(
                            options = Ordinamento.entries,
                            selected = ordinamento,
                            onSelect = { ordinamento = it },
                            showArrows = true,
                            ordineDecrescente = ordineDecrescente,
                            onInvertiOrdine = { ordineDecrescente = !ordineDecrescente },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }

            if (storicoFiltratoOrdinato.isEmpty()) {
                Box(
                    Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Nessuna partita trovata", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(storicoFiltratoOrdinato) { partita ->
                        val expanded = expandedStates[partita.dataOra] == true

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .animateContentSize() // Lunghezza card in base al contenuto
                                .clickable {
                                    expandedStates[partita.dataOra] = expandedStates[partita.dataOra] != true
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (partita.vinta) Color(0xFFD0F5E8) else Color(0xFFFFE0E0)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            // elevation = CardDefaults.cardElevation(4.dp)
                        ) {

                            Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .padding(11.dp)
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = if (expanded) Dp.Unspecified else 45.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Icona vinto/perso
                                    Icon(
                                        painter = painterResource(
                                            id = if (partita.vinta) R.drawable.ic_win else R.drawable.ic_lose
                                        ),
                                        contentDescription = null,
                                        tint = if (partita.vinta) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        modifier = Modifier.size(32.dp)
                                    )

                                    // Data
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = stringResource(R.string.OridinaPerData)+":", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium,color =   Color.Black)
                                        Text(
                                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(partita.dataOra)),
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color =  Color.Black
                                        )
                                    }

                                    // Tempo
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        val min = partita.tempo / 60
                                        val secs = partita.tempo % 60
                                        Text(text = stringResource(R.string.Tempo)+":", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium,color =  Color.Black)
                                        Text(
                                            "%02d:%02d".format(min, secs),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color =   Color.Black
                                        )
                                    }

                                    // Difficoltà
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = stringResource(R.string.Difficoltà)+":", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium,color =  Color.Black)
                                        Text(
                                            partita.difficolta,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color =   Color.Black
                                        )
                                    }

                                    // Errori
                                    Column(
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = stringResource(R.string.Errori)+":", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium,color = Color.Black)
                                        Text(
                                            "${partita.errori}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = Color.Black
                                        )
                                    }
                                }

                                // Contenuto espanso
                                if (expanded) {
                                    Spacer(modifier = Modifier.height(15.dp))
                                    HorizontalDivider(
                                        modifier = Modifier,
                                        0.5.dp,
                                        Color.Gray
                                    )
                                    partita.finalBard?.let { boardFinaleJson ->
                                        SudokuBoardPreview(boardFinaleJson)
                                    } ?: Text(
                                        "Nessuna tabella finale salvata.",
                                        Modifier.padding(12.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
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

// Sezione dei filtri
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun <T> FiltroTab(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    showArrows: Boolean = false,
    ordineDecrescente: Boolean = true,
    onInvertiOrdine: (() -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) where T : Enum<T> {
    val tabHeight = 36.dp
    val arrowWidth = if (showArrows) tabHeight else 0.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(50))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        val tabCount = options.size
        val tabWidth = (maxWidth - (8.dp * (tabCount - 1)) - arrowWidth) / tabCount
        val selectedIndex = options.indexOf(selected)
        val animatedOffset by animateDpAsState(
            targetValue = (tabWidth + 8.dp) * selectedIndex,
            label = "FiltroTab Ovale Offset"
        )

        Box {
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset, y = 0.dp)
                    .width(tabWidth)
                    .height(tabHeight)
                    .clip(RoundedCornerShape(50))
                    .background(if (isDarkTheme) Color(34, 40, 49) else Color(0xFFCCE6FF))
                    .border(
                        width = 2.dp,
                        color = blue_secondary,
                        shape = RoundedCornerShape(50)
                    ),
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(tabHeight), // allinea tutto verticalmente
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEachIndexed { i, opzione ->
                    Box(
                        modifier = Modifier
                            .width(tabWidth)
                            .height(tabHeight)
                            .clip(RoundedCornerShape(50))
                            .clickable { onSelect(opzione) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            if (opzione is FiltroVittoria) stringResource(
                                when (opzione) {
                                    FiltroVittoria.TUTTE -> R.string.StoricoFiltraTutte
                                    FiltroVittoria.VINTE -> R.string.StoricoFiltraVinte
                                    FiltroVittoria.PERSE -> R.string.StoricoFiltraPerse
                                }
                            )
                            else if (opzione is Ordinamento) stringResource(
                                when (opzione) {
                                    Ordinamento.CRONO -> R.string.OridinaPerData
                                    Ordinamento.TEMPO -> R.string.OrdinaPerTDG
                                }
                            )
                            else opzione.name.replaceFirstChar { it.uppercase() },
                            fontSize = MaterialTheme.typography.bodySmall.fontSize,
                            fontWeight = if (selected == opzione) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected == opzione) blue_secondary else  Color.Black,
                            modifier = Modifier.padding(horizontal = 6.dp)
                        )
                    }
                    if (i != options.lastIndex) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
                if (showArrows && onInvertiOrdine != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onInvertiOrdine,
                        modifier = Modifier
                            .size(tabHeight)
                            .padding(end = 2.dp)
                    ) {
                        Icon(
                            imageVector = if (ordineDecrescente) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                            contentDescription = "Inverti ordine",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}


// Sezione della tabella finale
@Composable
fun SudokuBoardPreview(boardJson: String) {
    val board: List<List<Int>> = remember(boardJson) {
        try {
            val type = object : TypeToken<List<List<Int>>>() {}.type
            Gson().fromJson(boardJson, type)
        } catch (_: Exception) {
            List(9) { List(9) { 0 } }
        }
    }
    Column(Modifier.padding(12.dp)) {
        Text(text = stringResource(R.string.tabella_finale), fontWeight = FontWeight.SemiBold, color = Color.Black)
        Spacer(Modifier.height(8.dp))
        // Box per centrare la griglia
        Box(
            Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier
                    .border(2.dp, Color.Black)
                    .padding(1.dp)
            ) {
                Column {
                    board.forEachIndexed { rowIdx, row ->
                        Row {
                            row.forEachIndexed { colIdx, cell ->
                                val top = if (rowIdx % 3 == 0) 2.dp else 0.5.dp
                                val left = if (colIdx % 3 == 0) 2.dp else 0.5.dp
                                val right = if (colIdx == 8) 2.dp else 0.5.dp
                                val bottom = if (rowIdx == 8) 2.dp else 0.5.dp

                                Box(
                                    Modifier
                                        .size(28.dp)
                                        .drawBehind {
                                            // Top
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(0f, 0f),
                                                end = Offset(size.width, 0f),
                                                strokeWidth = top.toPx()
                                            )
                                            // Left
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(0f, 0f),
                                                end = Offset(0f, size.height),
                                                strokeWidth = left.toPx()
                                            )
                                            // Right
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(size.width, 0f),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = right.toPx()
                                            )
                                            // Bottom
                                            drawLine(
                                                color = Color.Black,
                                                start = Offset(0f, size.height),
                                                end = Offset(size.width, size.height),
                                                strokeWidth = bottom.toPx()
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (cell == 0) "" else cell.toString(),
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                        fontWeight = if (cell == 0) FontWeight.Normal else FontWeight.Bold,
                                        color = Color.Black
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