package it.qbr.testapisudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.db.Game
import java.util.concurrent.TimeUnit


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    navController: NavHostController,
    isDarkTheme : Boolean,
    games: List<Game>
) {
    val bestTime = games.filter { it.vinta }.minByOrNull { it.tempo }?.tempo
    val avgTime = games.filter { it.vinta }.map { it.tempo }.average().takeIf { !it.isNaN() }
    val gamesStarted = games.size
    val gamesCompleted = games.count { it.vinta }
    val winRate = if (gamesStarted > 0) (gamesCompleted * 100 / gamesStarted) else 0

    val topGames = games
        .filter { it.vinta }
        .sortedWith(compareBy<Game> { it.tempo }.thenBy { it.errori })
        .take(5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.Stat)) },
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sezione TEMPO
            StatsSection(
                icon = R.drawable.ic_time,
                title = stringResource(R.string.Tempo),
                isDarkTheme = isDarkTheme
            ) {
                StatsRow(label = stringResource(R.string.stats_bt), value = bestTime?.let { formatTime(it) } ?: "--",isDarkTheme = isDarkTheme)
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                StatsRow(label = stringResource(R.string.stats_at), value = avgTime?.let { formatTime(it.toInt()) } ?: "--",isDarkTheme = isDarkTheme)
            }

            Spacer(Modifier.height(20.dp))

            // Sezione PARTITE
            StatsSection(
                icon = R.drawable.ic_game,
                title = stringResource(R.string.stats_section_games),
                isDarkTheme = isDarkTheme
            ) {
                StatsRow(label = stringResource(R.string.stats_gs), value = gamesStarted.toString(),isDarkTheme = isDarkTheme)
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                StatsRow(label = stringResource(R.string.stats_gc), value = gamesCompleted.toString(),isDarkTheme = isDarkTheme)
                HorizontalDivider(Modifier.padding(vertical = 4.dp))
                StatsRow(label = stringResource(R.string.stats_wr), value = "$winRate%", isDarkTheme = isDarkTheme)
            }

            Spacer(Modifier.height(20.dp))

            // Sezione MIGLIORI PARTITE
            StatsSection(
                icon = R.drawable.ic_star,
                title = stringResource(R.string.stats_section_mp),
                isDarkTheme = isDarkTheme
            ) {
                if (topGames.isEmpty()) {
                    Text(
                        text = stringResource(R.string.stats_section_npv),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    topGames.forEachIndexed { idx, game ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${idx + 1}.",
                                modifier = Modifier.width(28.dp),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.Tempo) + ": " + formatTime(game.tempo),
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = stringResource(R.string.Errori) + ": " + game.errori,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (idx < topGames.lastIndex) HorizontalDivider(
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun StatsSection(
    icon: Int,
    title: String,
    isDarkTheme: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Color(0xFF7C7C8A),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = if (isDarkTheme) Color.White else Color(0xFF363648),
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
fun StatsRow(
    label: String,
    value: String,
    isDarkTheme: Boolean,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color =  if (isDarkTheme) Color.White else  Color(0xFF7C7C8A),
            //color =  Color(0xFF7C7C8A),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color =  if (isDarkTheme) Color.White else Color(0xFF363648),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


fun formatTime(seconds: Int): String {
    val min = TimeUnit.SECONDS.toMinutes(seconds.toLong())
    val sec = seconds - min * 60
    return "%02d:%02d".format(min, sec)
}