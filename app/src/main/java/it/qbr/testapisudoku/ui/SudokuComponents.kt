package it.qbr.testapisudoku.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.AlertDialogDefaults.shape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import it.qbr.testapisudoku.R
import it.qbr.testapisudoku.ui.theme.gray
import it.qbr.testapisudoku.ui.theme.gray_light
import it.qbr.testapisudoku.ui.theme.light_error
import it.qbr.testapisudoku.ui.theme.light_primary
import it.qbr.testapisudoku.ui.theme.light_secondary
import it.qbr.testapisudoku.ui.theme.white


/*
@Composable
fun SudokuBoard(grid: List<List<Int>>) {
    Column(Modifier.padding(16.dp)) {
        for (row in grid) {
            Row {
                for (cell in row) {
                    SudokuCell(cell)
                }
            }
        }
    }
}

@Composable
fun SudokuCell(value: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            .padding(1.dp)
            .background(Color.LightGray, RoundedCornerShape(4.dp))
    ) {
        if (value != 0) {
            Text(text = value.toString(), fontSize = 18.sp, color = Color.Black)
        }
    }
}*/

@Preview(showBackground = true)
@Composable
fun SudokuPreview() {
    SudokuBottomBar()
}


@Composable
fun SudokuTopBar(seconds: Int, errorCount: Int) {
    val minutes = seconds / 60
    val secs = seconds % 60
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Image(
            painter = painterResource(id = R.drawable.ic_timer_icon),
            contentDescription = "Icona timer"
        )
            Spacer(modifier = Modifier.width(2.dp)) // Spazio tra icona e testo
            Text(
                text = "%02d:%02d".format(minutes, secs),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        Text(
            text = "Errori: $errorCount",
            fontSize = 20.sp,
            //fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}


@Composable
fun SudokuBoard(
    grid: List<List<Int>>,
    fixedCells: List<List<Boolean>>,
    selectedCell: Pair<Int, Int>?,
    errorCell: Pair<Int, Int>?,
    onSuggestMove: ()-> Unit,
    onCellSelected: (Int, Int) -> Unit
) {
    Box(
        modifier = Modifier
            .padding(top = 80.dp, start = 12.dp, end = 12.dp, bottom = 12.dp)
            //.shadow(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .background(Color.White),
    ) {
        Column(
            Modifier
                .padding(1.dp)
        ) {
            for ((rowIdx, row) in grid.withIndex()) {
                Row {
                    for ((colIdx, cell) in row.withIndex()) {
                        val thickTop = if (rowIdx % 3 == 0) 2.dp else 0.5.dp
                        val thickLeft = if (colIdx % 3 == 0) 2.dp else 0.5.dp
                        val thickRight = if (colIdx == 8) 2.dp else 0.5.dp
                        val thickBottom = if (rowIdx == 8) 2.dp else 0.5.dp

                        val isHighlighted = selectedCell?.let { (selRow, selCol) ->
                            rowIdx == selRow ||
                                    colIdx == selCol ||
                                    (rowIdx / 3 == selRow / 3 && colIdx / 3 == selCol / 3)
                        } == true

                        SudokuCell(
                            value = cell,
                            isSelected = selectedCell == Pair(rowIdx, colIdx),
                            isFixed = fixedCells[rowIdx][colIdx],
                            isError = errorCell == Pair(rowIdx, colIdx),
                            isHighlighted = isHighlighted && selectedCell != Pair(rowIdx, colIdx),
                            onClick = { onCellSelected(rowIdx, colIdx) },
                            borderTop = thickTop,
                            borderLeft = thickLeft,
                            borderRight = thickRight,
                            borderBottom = thickBottom
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
    onClick: () -> Unit,
    borderTop: Dp = 1.dp,
    borderLeft: Dp = 1.dp,
    borderRight: Dp = 1.dp,
    borderBottom: Dp = 1.dp,
    isHighlighted: Boolean
) {

    val shapeBackgroundColor = when {
        isError -> light_error
        isSelected -> light_secondary
        // isHighlighted -> light_secondary
        else -> light_primary
    }

    val backgroundColor = when{
        // isSelected -> gray
        else -> white
    }

    val borderColor = Color.Black
    val textColor = Color.Black

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)
            //.padding(0.5.dp)
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .border(1.dp, borderColor, RectangleShape)
            .clickable(enabled = !isFixed, onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center) // centra il cerchio nel Box genitore
                .background(
                    color = if (value != 0) shapeBackgroundColor else Color.White,
                    shape = CircleShape
                )
        ){
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                // fontWeight = FontWeight.Bold,
                color = white
            )
        }

    }
}


@Composable
fun SudokuKeypad(onNumberSelected: (Int) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Prima fila: 1-5
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (number in 1..5) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, light_primary, RoundedCornerShape(16.dp))
                        .background(white)
                        .clickable { onNumberSelected(number) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 18.sp,
                        color = Color.Black,
                        // fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        // Seconda fila: 6-9
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            for (number in 6..9) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .size(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, light_primary, RoundedCornerShape(16.dp))
                        .background(white)
                        .clickable { onNumberSelected(number) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        fontSize = 18.sp,
                        color = Color.Black,
                        // fontWeight = FontWeight.Bold
                    )
                }
            }

            /*
            // Bottone "C"
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .size(55.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                    .background(white)
                    .border(2.dp, Color.White, RectangleShape)
                    .clickable { onNumberSelected(0) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "C",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
            */
        }

    }
}


@Composable
fun SudokuBottomBar() {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                // logica
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),

            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) light_secondary else Color.White
            ),
            interactionSource = interactionSource,
            contentPadding = ButtonDefaults.ContentPadding
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                modifier = Modifier.size(20.dp),
                tint = Color.Black
            )
        }

        Button(
            onClick = { /* Azione */ },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),

            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) light_secondary else Color.White
            ),
            interactionSource = interactionSource,
            contentPadding = ButtonDefaults.ContentPadding
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_delete),
                contentDescription = "cancella",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }

        Button(
            onClick = { /* Azione */ },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),

            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) light_secondary else Color.White
            ),
            interactionSource = interactionSource,
            contentPadding = ButtonDefaults.ContentPadding
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_light),
                contentDescription = "suggerimento",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }

        Button(
            onClick = { /* Azione */ },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .weight(1f)
                .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),

            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPressed) light_secondary else Color.White
            ),
            interactionSource = interactionSource,
            contentPadding = ButtonDefaults.ContentPadding
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_history),
                contentDescription = "cronologia",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }
    }
}



