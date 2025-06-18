package it.qbr.testapisudoku.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import com.google.gson.Gson
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.db.AppDatabase
import it.qbr.testapisudoku.db.Game
import it.qbr.testapisudoku.model.Board
import it.qbr.testapisudoku.model.Difficulty
import it.qbr.testapisudoku.network.SudokuApi
import it.qbr.testapisudoku.ui.theme.blue_p
import it.qbr.testapisudoku.ui.theme.blue_secondary
import it.qbr.testapisudoku.utils.PreferencesConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("MutableCollectionMutableState", "UnusedBoxWithConstraintsScope", "ConfigurationScreenWidthHeight",
    "UnusedMaterial3ScaffoldPaddingParameter"
)
@Composable
fun SudokuScreen(navController: NavHostController, isDarkTheme: Boolean) {
    val context = LocalContext.current
    var step by remember { mutableIntStateOf(0) }
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) }
    var maxErrors by remember { mutableIntStateOf(10) }
    var errorCount by remember { mutableIntStateOf(0) }
    var showGameOver by remember { mutableStateOf(false) }
    var hintsLeft by remember { mutableIntStateOf(0) }
    var showNoHintsDialog by remember { mutableStateOf(false) }
    var board by remember { mutableStateOf<Board?>(null) }
    var loading by remember { mutableStateOf(true) }
    var selectedCell by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var cells by remember { mutableStateOf<List<MutableList<Int>>>(emptyList()) }
    var fixedCells by remember { mutableStateOf<List<List<Boolean>>>(emptyList()) }
    var solution by remember { mutableStateOf<List<List<Int>>>(emptyList()) }
    var seconds by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }
    var errorCells by remember { mutableStateOf<Set<Pair<Int, Int>>>(emptySet()) }
    var selectedNumber by remember { mutableStateOf<Int?>(null) }
    var showWinDialog by remember { mutableStateOf(false) }
    val completedNumbers = remember { mutableStateListOf<Int>() }
    val isSuggestEnabled =
        selectedCell?.let { (row, col) -> !fixedCells.getOrNull(row)?.getOrNull(col).orFalse() && cells.getOrNull(row)?.getOrNull(col) != solution.getOrNull(row)?.getOrNull(col) } == true
    var noteMode by remember { mutableStateOf(false) }
    var cellNotes by remember { mutableStateOf(mutableMapOf<Pair<Int, Int>, MutableSet<Int>>()) }

    // Stato per abbandono partita
    var showAbandonConfirm by remember { mutableStateOf(false) }
    var showSolution by remember { mutableStateOf(false) }

    var showResultScreen by remember { mutableStateOf(false) }
    var resultIsWin by remember { mutableStateOf(false) } // true se ha vinto, false se ha perso
    var finalTime by remember { mutableIntStateOf(0) } // secondi al momento dell'abbandono della partita

    var isPaused by remember { mutableStateOf(false) }

    // Blur graduale
    val blurDp by animateDpAsState(
        targetValue = if (isPaused) 16.dp else 0.dp,
        label = "BlurAnimation"
    )


    LaunchedEffect(cells) {
        completedNumbers.clear()
        for (n in 1..9) {
            val count = cells.sumOf { row -> row.count { it == n } }
            if (count == 9) completedNumbers.add(n)
        }
    }

    if (step == 0) {
        Scaffold (containerColor = if (isDarkTheme) Color(34, 40, 49) else Color(0xFFE7EBF0)){
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back_svgrepo_com),
                    tint = if (isDarkTheme) Color.White else Color.Black ,
                    contentDescription = "Torna alla Home",
                    modifier = Modifier.size(30.dp)
                )
            }
            Text(text = stringResource(R.string.scelta_difficolta), fontSize = 22.sp)
            Spacer(Modifier.height(24.dp))
            Difficulty.entries.forEach { diff ->
                Button(
                    onClick = {
                        selectedDifficulty = diff
                        maxErrors = diff.maxErrors
                        errorCount = 0
                        step = 1
                    },
                    Modifier.padding(8.dp).size(150.dp, 50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = blue_secondary),
                    shape = RoundedCornerShape(50.dp),
                ) {
                    Text(
                        text =
                            when (Difficulty.entries.indexOf(diff)) {
                                0 -> stringResource(R.string.diff_facile)
                                1 -> stringResource(R.string.diff_media)
                                2 -> stringResource(R.string.diff_difficile)
                                3 -> stringResource(R.string.diff_impossibile)
                                else -> "Unknown"
                            },
                        color = Color.White
                    )
                }
            }

        }
    }
    } else {
        LaunchedEffect(Unit) {
            val (initialBoard, solutionBoard) = withContext(Dispatchers.IO) {
                SudokuApi.generateOnlineBoard()
            }
            board = initialBoard
            cells = initialBoard.cells.map { it.toMutableList() }
            fixedCells = initialBoard.cells.map { row -> row.map { it != 0 } }
            solution = solutionBoard.cells
            loading = false
        }

        LaunchedEffect(loading, isPaused) {
            if (!loading) {
                while (!isPaused) {
                    delay(1000)
                    seconds++
                }
            }
        }

        fun checkWin(): Boolean {
            if (errorCount > maxErrors) return false
            if (cells.size != 9 || solution.size != 9) return false
            for (r in 0..8)
                for (c in 0..8)
                    if (cells[r][c] != solution[r][c]) return false
            return true
        }

        fun updateCell(row: Int, col: Int, value: Int) {
            if (noteMode) {
                val key = row to col
                val notes = cellNotes.getOrPut(key) { mutableSetOf() }
                if (notes.contains(value)) notes.remove(value) else notes.add(value)
                cellNotes = cellNotes.toMutableMap()
            } else {
                if (value in 0..9 && !fixedCells.getOrNull(row)?.getOrNull(col).orFalse()) {
                    if (solution.isNotEmpty() && value != 0 && value != solution[row][col]) {
                        errorCells = errorCells + (row to col)
                        errorCount++
                        if (errorCount >= maxErrors) {
                            showGameOver = true
                        }
                    } else {
                        errorCells = errorCells - (row to col)
                    }
                    cells = cells.mapIndexed { r, rowList ->
                        if (r == row) rowList.mapIndexed { c, oldValue ->
                            if (c == col) value else oldValue
                        }.toMutableList() else rowList
                    }
                    if (checkWin()) showWinDialog = true
                }
            }
        }

        if (loading) {
            Box(Modifier.fillMaxSize().background(if (isDarkTheme)  Color(34, 40, 49) else Color(0xFFE7EBF0)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = blue_p)
            }
        } else {
            Scaffold(
                topBar = {
                    Box(modifier = Modifier.statusBarsPadding()) {
                        SudokuTopBar(
                            maxErr = maxErrors,
                            seconds = seconds,
                            errorCount = errorCount,
                            isPaused = isPaused,
                            onHomeClick = { showDialog = true },
                            onPauseClick = {
                                isPaused = !isPaused
                                if (isPaused) {
                                    selectedCell = null // Deseleziona la cella se in pausa
                                }
                            } ,
                            isDarkTheme = isDarkTheme,

                        )
                    }

                },
                containerColor = if (isDarkTheme) Color(34, 40, 49)  else Color(0xFFE7EBF0)
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    /*
                    val iconBarHeight = 64.dp
                    val keypadHeight = 110.dp
                    val verticalPadding = 32.dp // somma di eventuali padding verticali extra
                     */

                    // All'interno di SudokuScreen, nella parte dove c'è la Board:
                    val configuration = LocalConfiguration.current
                    val screenWidth = configuration.screenWidthDp.dp
                    val screenHeight = configuration.screenHeightDp.dp

// Imposta una dimensione massima ragionevole (es: 400.dp) e margini
                    val maxBoardSize = 400.dp
                    val boardSize = remember(screenWidth, screenHeight) {
                        // Usa il minore tra 90% della larghezza, 60% dell'altezza e maxBoardSize
                        listOf(
                            screenWidth * 0.9f,
                            screenHeight * 0.6f,
                            maxBoardSize
                        ).minBy { it.value }
                    }
                    // BOARD
                     Box(
                        modifier = Modifier
                            .size(boardSize)
                            .align(Alignment.CenterHorizontally)
                            .blur(blurDp)
                            .pointerInput(isPaused) {
                                if (isPaused) {
                                    awaitPointerEventScope {
                                        while (isPaused) {
                                            awaitPointerEvent()
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        SudokuBoard(
                            grid = if (showSolution) solution else cells,
                            fixedCells = fixedCells,
                            selectedCell = selectedCell,
                            errorCells = errorCells,
                            selectedNumber = selectedNumber,
                            onCellSelected = { row, col ->
                                selectedCell = row to col
                                selectedNumber = if (cells.getOrNull(row)?.getOrNull(col) != 0) cells[row][col] else null
                            },
                            onSuggestMove = { },
                            modifier = Modifier.fillMaxSize(),
                            enabled = !isPaused,
                            isDarkTheme = isDarkTheme,
                            maxBoardHeight = boardSize
                        )
                    }
                    // ICON BAR
                    SudokuIconBar(
                        noteMode = noteMode,
                        onNoteModeToggle = { noteMode = !noteMode },
                        onErase = { selectedCell?.let { (row, col) -> updateCell(row, col, 0) } },
                        onSuggest = {
                            hintsLeft++
                            selectedCell?.let { (row, col) ->
                                updateCell(row, col, solution[row][col])
                                selectedNumber = if (cells[row][col] != 0) cells[row][col] else null
                            }
                        },
                        isSuggestEnabled = isSuggestEnabled,
                        hintsLeft = hintsLeft,
                        maxHints = PreferencesConstants.MAX_HINT,
                        showNoHintsDialog = { showNoHintsDialog = true },
                        blue_p = blue_p,
                        onHelp = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://www.wikihow.com/Solve-a-Sudoku".toUri()
                            )
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        isDarkTheme = isDarkTheme,
                    )
                    // KEYPAD
                    SudokuKeypad(
                        onNumberSelected = { number ->
                            selectedNumber = if (number == 0) null else number
                            selectedCell?.let { (row, col) ->
                                updateCell(row, col, if (number == 0) 0 else number)
                            }
                        },
                        disabledNumbers = completedNumbers,
                        modifier = Modifier
                            .fillMaxWidth(),
                        enabled = !isPaused,
                        onAbbandona = { showAbandonConfirm = true }
                    )
                }
            }
        }

        // ---- DIALOGS ----

        // Messaggio suggerimenti terminati
        if (showNoHintsDialog) {
            AlertDialog(
                onDismissRequest = { showNoHintsDialog = false },
                title = { Text(stringResource(R.string.sugg_term)) },
                text = { Text(stringResource(R.string.mess_sugg_term)) },
                confirmButton = {
                    TextButton(onClick = { showNoHintsDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        // Abbandona partita (impl: riprendi partita)
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Sei sicuro?") },
                text = { Text("Tornando alla home perderai la partita in corso.") },

                confirmButton = {
                    TextButton(onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            AppDatabase.getDatabase(context).partitaDao().inserisci(
                                Game(
                                    dataOra = System.currentTimeMillis(),
                                    vinta = false,
                                    tempo = seconds,
                                    difficolta = selectedDifficulty?.name ?: "",
                                    errori = errorCount,
                                    initialBoard = Gson().toJson(board),
                                    solutionBoard = Gson().toJson(solution),
                                    finalBard = Gson().toJson(cells)
                                )
                            )
                        }
                        showDialog = false
                        navController.popBackStack()
                    }) {
                        Text("Sì, torna alla Home")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Annulla")
                    }
                }
            )
        }



        // Messaggio conferma abbandona
        if (showAbandonConfirm) {
            AlertDialog(
                onDismissRequest = {  },
                title = { Text(stringResource(R.string.abb_part)) },
                text = { Text(stringResource(R.string.conf_abb_part)) },

                confirmButton = {
                    TextButton(onClick = {
                        showGameOver = true
                        showAbandonConfirm = false }
                    ) {
                        Text("OK")
                    }
                },

                dismissButton = {
                    TextButton(onClick = {
                        showGameOver = false
                        showAbandonConfirm = false }
                    ) {
                        Text(stringResource(R.string.tasto_ann_abb))
                    }
                }
            )
        }


        // Partita persa
        LaunchedEffect(showGameOver) {
            if (showGameOver) {

                finalTime = seconds
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getDatabase(context).partitaDao().inserisci(
                        Game(
                            dataOra = System.currentTimeMillis(),
                            vinta = false,
                            tempo = seconds,
                            difficolta = selectedDifficulty?.name ?: "",
                            errori = errorCount,
                            initialBoard = Gson().toJson(board),
                            solutionBoard = Gson().toJson(solution),
                            finalBard = Gson().toJson(cells)
                        )
                    )
                }
                resultIsWin = false
                showResultScreen = true
                showGameOver = false
            }
        }


        // Partita vinta
        LaunchedEffect(showWinDialog) {
            if (showWinDialog) {
                finalTime = seconds
                CoroutineScope(Dispatchers.IO).launch {
                    AppDatabase.getDatabase(context).partitaDao().inserisci(
                        Game(
                            dataOra = System.currentTimeMillis(),
                            vinta = true,
                            tempo = seconds,
                            difficolta = selectedDifficulty?.name ?: "",
                            errori = errorCount,
                            initialBoard = Gson().toJson(board),
                            solutionBoard = Gson().toJson(solution),
                            finalBard = Gson().toJson(cells)
                        )
                    )
                }
                resultIsWin = true
                showResultScreen = true
                showWinDialog = false
            }
        }



        if (showResultScreen) {
            GameResultScreen(
                isWin = resultIsWin,
                grid = cells,
                fixedCells = fixedCells,
                solution = solution,
                errorCount = errorCount,
                seconds = finalTime,
                onHomeClick = {
                    showResultScreen = false
                    navController.popBackStack()
                },
                isDarkTheme = isDarkTheme,
            )
        }


    }
}

private fun Boolean?.orFalse() = this == true