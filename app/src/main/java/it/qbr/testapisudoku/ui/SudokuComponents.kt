package it.qbr.testapisudoku.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.background_cell
import it.qbr.testapisudoku.ui.theme.background_rows
import it.qbr.testapisudoku.ui.theme.background_same_number
import it.qbr.testapisudoku.ui.theme.blue_number
import it.qbr.testapisudoku.ui.theme.blue_p
import it.qbr.testapisudoku.ui.theme.gray_2
import it.qbr.testapisudoku.ui.theme.light_gray
import it.qbr.testapisudoku.ui.theme.quit_background


@Composable
fun SudokuTopBar(
    maxErr: Int,
    seconds: Int,
    errorCount: Int,
    isPaused: Boolean,
    onHomeClick: () -> Unit,
    onPauseClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val minutes = seconds / 60
    val secs = seconds % 60
    val timeString = "%02d%02d".format(minutes, secs)
    val errorCountString = errorCount.toString().padStart(2, '0')
    val maxErrString = maxErr.toString().padStart(2, '0')

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Freccia per tornare indietro
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(onClick = onHomeClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    painter = painterResource(id = R.drawable.back_svgrepo_com),
                    contentDescription = "Torna alla Home",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Numero di errori
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in errorCountString.indices) {
                        AnimatedDigit(
                            digit = errorCountString[i],
                            key = "err$i",
                            isDarkTheme = isDarkTheme
                        )
                    }
                    Text(
                        text = "/",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily(Font(R.font.poppins_regular)),
                        color = if (isDarkTheme) Color.White else Color.Black,
                    )
                    for (i in maxErrString.indices) {
                        AnimatedDigit(
                            digit = maxErrString[i],
                            key = "maxerr$i",
                            isDarkTheme = isDarkTheme,
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.Errori),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = if (isDarkTheme) Color.White else Color.Black,
                )
            }
        }

        // Timer
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    for (i in 0..3) {
                        AnimatedDigit(
                            digit = timeString[i],
                            key = "timer$i",
                            isDarkTheme = isDarkTheme
                        )
                        if (i == 1) {
                            Text(
                                text = ":",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.poppins_bold)),
                                color = if (isDarkTheme) Color.White else Color.Black
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.Tempo),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins_regular)),
                    color = if (isDarkTheme) Color.White else Color.Black
                )
            }
        }

        // Tasto pausa/play dentro un cerchio grigio
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD3D3D3)) // grigio chiaro
                ) {
                    IconButton(
                        onClick = onPauseClick,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = if (isPaused) "Riprendi" else "Pausa",
                            modifier = Modifier.size(20.dp),
                            tint = Color.Black // colore dell'icona
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedDigit(digit: Char, key: String, isDarkTheme: Boolean) {
    AnimatedContent(
        targetState = digit,
        transitionSpec = {
            (slideInVertically { height -> height } + fadeIn()).togetherWith(slideOutVertically { height -> -height } + fadeOut())
        },
        label = key
    ) { targetDigit ->
        Text(
            text = targetDigit.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily(Font(R.font.poppins_bold)),
            color = if (isDarkTheme) Color.White else Color.Black,
        )
    }
}



@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SudokuBoard(
    grid: List<List<Int>>,
    fixedCells: List<List<Boolean>>,
    selectedCell: Pair<Int, Int>?,
    errorCells: Set<Pair<Int, Int>>,
    completedCells: Set<Pair<Int, Int>>,
    selectedNumber: Int?,
    onSuggestMove: () -> Unit,
    onCellSelected: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDarkTheme: Boolean,
    maxBoardHeight: Dp? = null
) {
    BoxWithConstraints(modifier = modifier) {
        // Calcola la dimensione massima per la cella in base allo spazio disponibile
        val usableSize = (maxBoardHeight ?: maxWidth).coerceAtMost(maxWidth).coerceAtMost(maxHeight)
        val cellSize = usableSize / 9

        Column(
            modifier = Modifier
                .width(cellSize * 9)
                .height(cellSize * 9),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        )  {
            for ((rowIdx, row) in grid.withIndex()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for ((colIdx, cell) in row.withIndex()) {
                        val thickTop = if (rowIdx == 0) 3.dp else if (rowIdx % 3 == 0) 2.dp else 0.5.dp
                        val thickLeft = if (colIdx == 0) 3.dp else if (colIdx % 3 == 0) 2.dp else 0.5.dp
                        val thickRight = if (colIdx == 8) 3.dp else if ((colIdx + 1) % 3 == 0) 2.dp else 0.5.dp
                        val thickBottom = if (rowIdx == 8) 3.dp else if ((rowIdx + 1) % 3 == 0) 2.dp else 0.5.dp

                        val isHighlighted = selectedCell?.let { (selRow, selCol) ->
                            rowIdx == selRow ||
                                    colIdx == selCol ||
                                    (rowIdx / 3 == selRow / 3 && colIdx / 3 == selCol / 3)
                        } == true

                        SudokuCell(
                            value = cell,
                            isSelected = selectedCell == Pair(rowIdx, colIdx),
                            isFixed = fixedCells.getOrNull(rowIdx)?.getOrNull(colIdx) == true,
                            isError = errorCells.contains(Pair(rowIdx, colIdx)),
                            isCompleted = completedCells.contains(Pair(rowIdx, colIdx)),
                            isHighlighted = isHighlighted && selectedCell != Pair(rowIdx, colIdx),
                            isSameNumber = selectedNumber != null && cell == selectedNumber && cell != 0,
                            onClick = {
                                if (enabled) onCellSelected(rowIdx, colIdx)
                            },
                            borderTop = thickTop,
                            borderLeft = thickLeft,
                            borderRight = thickRight,
                            borderBottom = thickBottom,
                            borderColor = if (isDarkTheme) Color.White else Color.Black,
                            cellSize = cellSize,
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun SudokuCell(
    value: Int,
    isSelected: Boolean,
    isFixed: Boolean,
    isError: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    borderTop: Dp = 0.dp,
    borderLeft: Dp = 0.dp,
    borderRight: Dp = 0.dp,
    borderBottom: Dp = 0.dp,
    isHighlighted: Boolean,
    isSameNumber: Boolean,
    borderColor: Color,
    notes: Set<Int> = emptySet(),
    cellSize: Dp,
    isDarkTheme: Boolean
) {
    // Colore per le celle completate
    val completedColor = Color(0xFF2196F3).copy(alpha = 0.3f)

    val backgroundColor = when {
        isCompleted -> completedColor // Priorità all'evidenziazione del completamento
        isHighlighted -> if(isDarkTheme) gray_2 else background_rows
        isSelected -> if(isDarkTheme) Color.DarkGray else background_cell
        isSameNumber -> if (isDarkTheme) Color.DarkGray else background_same_number
        else -> if (isDarkTheme) Color.Black else Color.White
    }

    // Animazione del colore di sfondo
    val animatedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        animationSpec = tween(
            durationMillis = 500, // Durata dell'animazione
            easing = FastOutSlowInEasing // Tipo di easing per una transizione più fluida
        ),
        label = "backgroundColor"
    )

    val numberColor = when {
        isError -> Color.Red
        isFixed -> if (isDarkTheme) Color.White else Color.Black
        else -> blue_number
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(cellSize)
            .background(animatedBackgroundColor)
            .drawBehind {
                // Bordo superiore
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = borderTop.toPx()
                )
                // Bordo sinistro
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = borderLeft.toPx()
                )
                // Bordo destro
                drawLine(
                    color = borderColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderRight.toPx()
                )
                // Bordo inferiore
                drawLine(
                    color = borderColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = borderBottom.toPx()
                )
            }
            .clickable { onClick() }
    ) {

        // TODO: note non implementate
        if (value != 0) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(cellSize * 0.8f)
            ) {
                Text(
                    text = value.toString(),
                    fontSize = (cellSize.value * 0.6).sp,
                    color = numberColor,
                    fontWeight = FontWeight.Normal
                )
            }
        } else if (notes.isNotEmpty()) {
            // Mostra le note in piccolo, ad esempio in una griglia 3x3
            Column {
                for (i in 1..9 step 3) {
                    Row {
                        for (j in i..i + 2) {
                            Text(
                                text = if (notes.contains(j)) j.toString() else "",
                                fontSize = (cellSize.value * 0.2).sp,
                                modifier = Modifier.width(cellSize / 3)
                            )
                        }
                    }
                }
            }
        }
    }
}




@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun SudokuKeypad(
    onNumberSelected: (Int) -> Unit,
    disabledNumbers: List<Int>,
    modifier: Modifier,
    enabled: Boolean = true,
    onAbbandona: (() -> Unit)? = null
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val maxButtonSize = 64.dp
        val buttonSize = remember(maxWidth) {
            // 5 tasti per riga (incluso abbandona)
            val size = maxWidth / 5
            if (size > maxButtonSize) maxButtonSize else size
        }

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Prima fila: 1-5
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (number in 1..5) {
                    val isDisabled = disabledNumbers.contains(number) || !enabled
                    Box(
                        modifier = Modifier
                            .size(buttonSize)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isDisabled) background_same_number else light_gray)
                            .clickable(enabled = !isDisabled) { onNumberSelected(number) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            fontSize = (buttonSize.value * 0.4).sp,
                            color = if (!isDisabled) blue_p else gray_2,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            // Seconda fila: 6-9 + "abbandona partita"
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                for (number in 6..9) {
                    val isDisabled = disabledNumbers.contains(number) || !enabled
                    Box(
                        modifier = Modifier
                            .size(buttonSize)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isDisabled) background_same_number else light_gray)
                            .clickable(enabled = !isDisabled) { onNumberSelected(number) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = number.toString(),
                            fontSize = (buttonSize.value * 0.4).sp,
                            color = if (!isDisabled) blue_p else gray_2,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(buttonSize)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(quit_background)
                        .clickable(enabled = enabled)  {
                            if (enabled) onAbbandona?.invoke()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Abbandona",
                        tint = Color(0xFFB71C1C),
                        modifier = Modifier.size(buttonSize * 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun SudokuIconBar(
    noteMode: Boolean,
    onNoteModeToggle: () -> Unit,
    onErase: () -> Unit,
    onSuggest: () -> Unit,
    isSuggestEnabled: Boolean,
    hintsLeft: Int,
    maxHints: Int,
    showNoHintsDialog: () -> Unit,
    blue_p: Color,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {

    Box(
        modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Row(Modifier.align(Alignment.BottomCenter)) {

            // Note Button
            var noteFill by remember { mutableStateOf(false) }

            IconButton(
                onClick = {
                    noteFill = !noteFill
                    onNoteModeToggle()
                },
                modifier = Modifier.padding(bottom = 50.dp).size(80.dp)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (noteFill) R.drawable.ic_note_fill else R.drawable.ic_note_nofill
                    ),
                    contentDescription = "Modalità note",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(R.string.seleziona),
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.padding(top = 55.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }


            // Erase Button
            var erasePressed by remember { mutableStateOf(false) }

            IconButton(
                onClick = {
                    erasePressed = true
                    onErase()
                },
                modifier = Modifier.padding(bottom = 50.dp).size(80.dp)
            ) {
                // Effetto: torna a nofill dopo 0.5 secondo se premuto
                if (erasePressed) {
                    LaunchedEffect(erasePressed) {
                        kotlinx.coroutines.delay(500)
                        erasePressed = false
                    }
                }
                Icon(
                    painter = painterResource(
                        id = if (erasePressed) R.drawable.ic_backspace_fill else R.drawable.ic_backspace_nofill
                    ),
                    contentDescription = "Cancella cella",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(R.string.canc),
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.padding(top = 55.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }


            // Suggest Button
            var hintPressed by remember { mutableStateOf(false) }

            IconButton(
                onClick = {
                    if (hintsLeft < maxHints) {
                        hintPressed = true
                        onSuggest()
                    } else {
                        showNoHintsDialog()
                    }
                },
                enabled = isSuggestEnabled,
                modifier = Modifier.padding(bottom = 50.dp).size(80.dp)
            ) {
                if (hintPressed && hintsLeft < maxHints) {
                    LaunchedEffect(hintPressed, hintsLeft) {
                        kotlinx.coroutines.delay(500)
                        hintPressed = false
                    }
                }
                val (iconRes, iconTint) = if (hintsLeft >= maxHints) {
                    R.drawable.ic_no_hints_left to Color.Gray
                } else if (hintPressed) {
                    R.drawable.ic_hints_fill to if (isDarkTheme) Color.White else Color.Black
                } else {
                    R.drawable.ic_hints_nofill to if (isDarkTheme) Color.White else Color.Black
                }
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Suggerimento",
                    modifier = Modifier.size(44.dp),
                    tint = iconTint
                )
                Text(
                    text = stringResource(R.string.sugg),
                    fontSize = 12.sp,
                    color = if (isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.padding(top = 55.dp),
                    fontWeight = FontWeight.SemiBold
                )
                if (hintsLeft < maxHints) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = 8.dp, y = 12.dp)
                            .background(blue_p, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (maxHints - hintsLeft).toString(),
                            color =   Color.White ,
                            fontSize = 12.sp
                        )
                    }
                }
            }


            // Help Button
            IconButton(
                onClick = onHelp,
                modifier = Modifier.padding(bottom = 50.dp).size(80.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_help),
                    contentDescription = "Come si gioca",
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = stringResource(R.string.aiuto),
                    fontSize = 12.sp,
                    color =  if (isDarkTheme) Color.White else Color.Black,
                    modifier = Modifier.padding(top = 55.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}



