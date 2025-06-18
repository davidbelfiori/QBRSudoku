package it.qbr.testapisudoku.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.QBRSudokuTheme
import it.qbr.testapisudoku.ui.theme.blue_secondary
import kotlinx.coroutines.delay


@Composable
fun HomeScreen(
    onStartGame: () -> Unit,
    onStorico: () -> Unit,
    onStats: () -> Unit,
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {

    QBRSudokuTheme(darkTheme = isDarkTheme) {
        // Main content of the HomeScreen
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            topBar = {

                // Icona modalità scura/chiara
                IconButton(
                    onClick = onToggleDarkTheme,
                    modifier = Modifier.size(50.dp).statusBarsPadding()
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isDarkTheme) R.drawable.sunny else R.drawable.ic_darkmode
                        ),
                        contentDescription = "Cambio modalita' scura",
                        //modifier = Modifier.size(40.dp)
                    )
                }
            },

            bottomBar = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                ) {
                    Button(
                        onClick = onStartGame,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(150.dp, 50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = blue_secondary),
                        shape = RoundedCornerShape(50.dp),
                    ) {
                        Text(text = stringResource(id = R.string.StartGame), fontSize = 22.sp, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onStats,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .size(150.dp, 50.dp)
                            .wrapContentWidth()
                            .border(
                                width = 2.dp,
                                color = blue_secondary,
                                shape = RoundedCornerShape(50.dp)
                            ),
                    ) {
                        Text(
                            text = stringResource(R.string.Stat),
                            fontSize = 15.sp,
                            color = blue_secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Button(
                        onClick = onStorico,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        modifier = Modifier
                            .size(150.dp, 50.dp)
                            .wrapContentWidth()
                            .border(
                                width = 2.dp,
                                color = blue_secondary,
                                shape = RoundedCornerShape(50.dp)
                            ),
                    ) {
                        Text(
                            text = stringResource(R.string.History),
                            fontSize = 15.sp,
                            color = blue_secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "by QBR",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        ) { innerPadding ->
            // Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp)
                    .padding(innerPadding)
            ) {
                TypewriterText(
                    text = "QBRSudoku",
                )
                Image(
                    painter = painterResource(id = if(isDarkTheme) R.drawable.sudokuimage_dark else R.drawable.sudokuimage),
                    contentDescription = "Logo",
                    modifier = Modifier.weight(1f, fill = true).padding(horizontal = 16.dp, vertical = 40.dp)
                )
            }
        }
    }
}


// Funzione per l'effetto di scrittura
@Composable
fun TypewriterText(
    text: String,
    specialPartLength: Int = 3, // "QBR"
    specialColor: Color = blue_secondary, // colore di "QBR"
    restColor: Color = Color.Unspecified,
    fontSize: TextUnit = 45.sp,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    charDelayMillis: Long = 250L // velocita' di scrittura
) {
    var visibleTextLength by remember { mutableIntStateOf(0) }

    LaunchedEffect(text) {
        visibleTextLength = 0
        while (visibleTextLength < text.length) {
            delay(charDelayMillis)
            visibleTextLength++
        }
    }

    val visibleText = text.take(visibleTextLength)
    val specialText = visibleText.take(specialPartLength)
    val restText = if (visibleTextLength > specialPartLength) visibleText.drop(specialPartLength) else ""

    Text(
        text = buildAnnotatedString {
            if (specialText.isNotEmpty()) {
                withStyle(SpanStyle(color = specialColor)) {
                    append(specialText)
                }
            }
            if (restText.isNotEmpty()) {
                withStyle(SpanStyle(color = restColor)) {
                    append(restText)
                }
            }
        },
        fontSize = fontSize,
        fontWeight = fontWeight,
    )
}

