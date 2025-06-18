package it.qbr.testapisudoku.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.blue_secondary


@SuppressLint("DefaultLocale")
@Composable
fun GameResultScreen(
    isWin: Boolean,
    grid: List<List<Int>>,
    fixedCells: List<List<Boolean>>,
    solution: List<List<Int>>,
    errorCount: Int,
    seconds: Int,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean
) {

    var showSolution by remember { mutableStateOf(false) }
    val timeText = String.format("%02d:%02d", seconds / 60, seconds % 60)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDarkTheme)  Color(34, 40, 49) else Color(0xFFE7EBF0))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Text(
            text = if (isWin) stringResource(R.string.mess_vittoria) else stringResource(R.string.game_over),
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isWin) Color(0xFF2E7D32) else Color(0xFFC62828),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.Tempo), style = MaterialTheme.typography.bodyMedium, color = if ( isDarkTheme) Color.White else Color.Black)
                Text(timeText, style = MaterialTheme.typography.bodyLarge, color = if ( isDarkTheme) Color.White else Color.Black)
            }
            Spacer(Modifier.height(12.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.Errori), style = MaterialTheme.typography.bodyMedium, color = if ( isDarkTheme) Color.White else Color.Black)
                Text("$errorCount", style = MaterialTheme.typography.bodyLarge, color = if ( isDarkTheme) Color.White else Color.Black)
            }
        }
        Spacer(Modifier.height(24.dp))
        // Griglia finale
        Box(
            modifier = Modifier
                .sizeIn(maxWidth = 360.dp, maxHeight = 360.dp)
                .aspectRatio(1f)
                // .background(Color.White, RoundedCornerShape(16.dp))
                .padding(8.dp)
        ) {
            SudokuBoard(
                grid = if (showSolution) solution else grid,
                fixedCells = fixedCells,
                selectedCell = null,
                errorCells = emptySet(),
                selectedNumber = null,
                onSuggestMove = {},
                onCellSelected = { _, _ -> },
                modifier = Modifier.fillMaxSize(),
                isDarkTheme = isDarkTheme 
            )
        }
        Spacer(Modifier.height(24.dp))

        if (!isWin && !showSolution) {

            // Tasto Mostra Soluzione
            Button(
                onClick = { showSolution = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .size(270.dp, 50.dp)
                    .wrapContentWidth()
                    .border(
                        width = 2.dp,
                        color = blue_secondary,
                        shape = RoundedCornerShape(50.dp)
                    ),
            ) {
                Text(stringResource(R.string.mostra_sol), fontSize = 15.sp, color = blue_secondary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            Spacer(Modifier.height(8.dp))
        }

        // Tasto Home
        Button(
            onClick = onHomeClick,
            modifier = Modifier
                .padding(8.dp)
                .size(120.dp, 50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blue_secondary),
            shape = RoundedCornerShape(50.dp),
        ) {
            Text("Home", fontSize = 20.sp)
        }
    }
}

