package oleh.zaiets.first_lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.legend.verticalLegend
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.text.TextComponent

class MainActivity : ComponentActivity() {

    data class InitialData(
        val costumerAverage: Double = 7.0,
        val cashierAverage: Double = 0.7,
        val from: Int = 7,
        val until: Int = 20,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var initialData by remember { mutableStateOf(InitialData()) }
            val components by remember {
                derivedStateOf {
                    LabComponents(
                        initialData.costumerAverage,
                        initialData.cashierAverage
                    )
                }
            }
            var uiModel by remember {
                mutableStateOf(
                    components.process(
                        from = initialData.from,
                        until = initialData.until
                    )
                )
            }
            val onResetClick = remember(initialData) {
                { uiModel = components.process(from = initialData.from, until = initialData.until) }
            }
            var shouldShowDialog by remember { mutableStateOf(true) }

            if (shouldShowDialog) {
                Dialog(onDismissRequest = { shouldShowDialog = false }) {
                    var costumerAverage by remember { mutableDoubleStateOf(initialData.costumerAverage) }
                    var cashierAverage by remember { mutableDoubleStateOf(initialData.cashierAverage) }
                    var from by remember { mutableIntStateOf(initialData.from) }
                    var until by remember { mutableIntStateOf(initialData.until) }
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = costumerAverage.toString(),
                            onValueChange = { costumerAverage = it.toDouble() },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            label = { Text("Середній прихід") }
                        )
                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = cashierAverage.toString(),
                            onValueChange = { cashierAverage = it.toDouble() },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            label = { Text("Середній час касира") }
                        )
                        Text(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            text = "Часи роботи",
                            textAlign = TextAlign.Center,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextField(
                                modifier = Modifier.weight(1f),
                                value = from.toString(),
                                onValueChange = { from = it.toInt() },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text("З") }
                            )
                            TextField(
                                modifier = Modifier.weight(1f),
                                value = until.toString(),
                                onValueChange = { until = it.toInt() },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = { Text("По") }
                            )
                        }
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .height(35.dp),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                initialData =
                                    InitialData(costumerAverage, cashierAverage, from, until)
                                shouldShowDialog = false
                            }
                        ) {
                            Text(
                                modifier = Modifier.wrapContentSize(),
                                text = "Ок",
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Заєць Олег Сергійович\n122m-23-1 Варіант 7",
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 24.dp),
                    text = "Робочі години: ${initialData.from} - ${initialData.until}\nСередній час приходу: ${initialData.costumerAverage} хвилин\nСередній час обслуговування: ${initialData.cashierAverage} хвилини",
                    textAlign = TextAlign.Start,
                )
                HourReportsChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    reports = uiModel,
                )
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .width(200.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onResetClick
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = "Перезапустити",
                        textAlign = TextAlign.Center,
                    )
                }
                Button(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .width(200.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(8.dp),
                    onClick = { shouldShowDialog = true }
                ) {
                    Text(
                        modifier = Modifier.wrapContentSize(),
                        text = "Налаштування",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
fun HourReportsChart(
    reports: HourReportsUiModel,
    modifier: Modifier = Modifier,
) {
    Chart(
        chart = lineChart(
            lines = listOf(
                LineChart.LineSpec(
                    lineColor = Color.Blue.toArgb(),
                ),
                LineChart.LineSpec(
                    lineColor = Color.Magenta.toArgb(),
                ),
            )
        ),
        model = reports.incomingAndProceeded,
        modifier = modifier,
        startAxis = rememberStartAxis(
            label = TextComponent.Builder().apply {
                color = Color.Black.toArgb()
            }.build(),
        ),
        bottomAxis = rememberBottomAxis(
            label = TextComponent.Builder().apply {
                color = Color.Black.toArgb()
            }.build(),
        ),
        legend = verticalLegend(
            items = reports.additionalInfo,
            iconSize = 0.dp,
            iconPadding = 0.dp,
        )
    )
}
