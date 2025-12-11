import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.HorizontalAlignment
import java.io.File
import java.io.FileOutputStream

data class CellData(
    var text: String,
    var bold: Boolean = false,
    var italic: Boolean = false,
    var underline: Boolean = false,
    var fontSize: Short = 12
)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val rows = 10
    val columns = 5

    // Table state
    val tableState = remember {
        Array(rows) { row ->
            Array(columns) { mutableStateOf(CellData(text = "", bold = false, italic = false, underline = false)) }
        }
    }

    // Global formatting toggles
    var isBoldChecked by remember { mutableStateOf(false) }
    var isItalicChecked by remember { mutableStateOf(false) }
    var isUnderlineChecked by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Formatting buttons
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isBoldChecked, onCheckedChange = { isBoldChecked = it })
                Text("Bold", modifier = Modifier.padding(end = 8.dp))

                Checkbox(checked = isItalicChecked, onCheckedChange = { isItalicChecked = it })
                Text("Italic", modifier = Modifier.padding(end = 8.dp))

                Checkbox(checked = isUnderlineChecked, onCheckedChange = { isUnderlineChecked = it })
                Text("Underline")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Button to generate Excel
            Button(
                onClick = { createAndOpenExcel(context, tableState) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Generate & Open Excel")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Scrollable table
            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    for (row in 0 until rows) {
                        Row {
                            for (col in 0 until columns) {
                                val cell = tableState[row][col]
                                TextField(
                                    value = cell.value.text,
                                    onValueChange = { newText ->
                                        // Update text and formatting based on global toggles
                                        cell.value = cell.value.copy(
                                            text = newText,
                                            bold = isBoldChecked,
                                            italic = isItalicChecked,
                                            underline = isUnderlineChecked
                                        )
                                    },
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(50.dp)
                                        .border(1.dp, Color.Gray)
                                        .background(Color.White)
                                        .padding(2.dp),
                                    singleLine = true,
                                    textStyle = LocalTextStyle.current.copy(
                                        fontWeight = if (cell.value.bold) FontWeight.Bold else FontWeight.Normal,
                                        fontStyle = if (cell.value.italic) androidx.compose.ui.text.font.FontStyle.Italic else androidx.compose.ui.text.font.FontStyle.Normal,
                                        textDecoration = if (cell.value.underline) androidx.compose.ui.text.style.TextDecoration.Underline else null,
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


fun createAndOpenExcel(context: Context, tableState: Array<Array<MutableState<CellData>>>) {
    try {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")

        for (rowIndex in tableState.indices) {
            val row = sheet.createRow(rowIndex)
            for (colIndex in tableState[rowIndex].indices) {
                val cellData = tableState[rowIndex][colIndex].value
                val cell = row.createCell(colIndex)
                cell.setCellValue(cellData.text)

                // Only first row is bold
                val font = workbook.createFont().apply {
                    bold = rowIndex == 0  // bold only for first row
                    fontHeightInPoints = cellData.fontSize
                }

                val style = workbook.createCellStyle().apply {
                    setFont(font)
                    alignment = HorizontalAlignment.CENTER
                }

                cell.cellStyle = style
            }
        }

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "MyExcel.xlsx")
        FileOutputStream(file).use { workbook.write(it) }
        workbook.close()

        // Open file
        val uri = FileProvider.getUriForFile(
            context, context.packageName + ".fileprovider", file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)

        Toast.makeText(context, "Excel Created: ${file.name}", Toast.LENGTH_LONG).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
