package oleh.zaiets.first_lab

import androidx.compose.runtime.Immutable
import com.patrykandpatrick.vico.core.component.Component
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.context.DrawContext
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.legend.LegendItem
import java.math.BigDecimal
import java.math.RoundingMode

@Immutable
data class HourReportsUiModel(
    val incomingAndProceeded: ChartEntryModel,
    val additionalInfo: List<LegendItem>,
)

class HourReportsUiModelMapper {

    private val emptyIconComponent
        get() = object : Component() {
            override fun draw(context: DrawContext, left: Float, top: Float, right: Float, bottom: Float) {}
        }

    private val defaultTextComponent
        get() = TextComponent.Builder().build()

    fun map(result: BankWorkingResult): HourReportsUiModel {
        val reports = result.reports
        val incoming = mutableListOf<FloatEntry>()
        val processed = mutableListOf<FloatEntry>()

        reports.forEach { hourReport ->
            val x = hourReport.hour.toFloat()
            incoming.add(FloatEntry(x, hourReport.incoming.toFloat()))
            processed.add(FloatEntry(x, hourReport.processed.toFloat()))
        }

        val averageCostumers = reports.map { it.incoming }
            .average().toBigDecimal().setScale(2, RoundingMode.FLOOR)
        val averageProcessed = reports.map { it.processed }
            .average().toBigDecimal().setScale(2, RoundingMode.FLOOR)

        return HourReportsUiModel(
            incomingAndProceeded = entryModelOf(incoming, processed),
            additionalInfo = listOf(
                LegendItem(
                    labelText = "Синя лінія показує кількість нових заявок за годину",
                    icon = emptyIconComponent,
                    label = defaultTextComponent,
                ),
                LegendItem(
                    labelText = "Пурпурна лінія показує кількість оброблених заявок за годину",
                    icon = emptyIconComponent,
                    label = defaultTextComponent,
                ),
                LegendItem(
                    labelText = "Середня кількість заявок за годину: $averageCostumers",
                    icon = emptyIconComponent,
                    label = defaultTextComponent,
                ),
                LegendItem(
                    labelText = "Середня кількість оброблених заявок за годину: $averageProcessed",
                    icon = emptyIconComponent,
                    label = defaultTextComponent,
                ),
            )
        )
    }

}
