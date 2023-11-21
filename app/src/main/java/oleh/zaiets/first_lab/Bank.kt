package oleh.zaiets.first_lab

import java.math.BigDecimal
import kotlin.math.absoluteValue

class Bank(
    private val customerIncomingProcessor: ModelProcessor,
    private val cashierProcessor: ModelProcessor,
    accuracy: Double = DEFAULT_TIME_ACCURACY
) {

    var accuracy: Double = accuracy
        set(value) {
            require(value > 0) { "Step must be bigger then 0" }
            field = value
            customerIncomingProcessor.step = value
            cashierProcessor.step = value
        }

    fun work(from: Int, until: Int): BankWorkingResult {
        var costumersByHour = 0
        var proceedByHour = 0
        var allCostumers = 0
        var queue = 0
        val reports = mutableListOf<HourReport>()
        for (time in timeIterator(from, until)) {

            if (customerIncomingProcessor.tryProcess()) {
                allCostumers++
                costumersByHour++
                queue++
            }

            if (queue != 0 && cashierProcessor.tryProcess()) {
                queue--
                proceedByHour++
            }

            val fractional = time.remainder(BigDecimal.ONE)
            val minute = time.toInt()
            if (fractional == 0.0.toBigDecimal() && minute % 60 == 0) {
                val hour = from + minute / 60
                reports.add(
                    HourReport(
                        hour = hour,
                        incoming = costumersByHour,
                        processed = proceedByHour,
                        inQueue = queue
                    )
                )

                costumersByHour = 0
                proceedByHour = 0
            }
        }

        cashierProcessor.reset()
        customerIncomingProcessor.reset()
        return BankWorkingResult(reports)
    }

    private fun timeIterator(from: Int, until: Int): Iterator<BigDecimal> {
        require(from > 0 || from <= 24) { "'from' must be bigger then 0 and less or equals then 24" }
        require(until > 0 || until <= 24) { "'until' must be bigger then 0 and less or equals then 24" }

        return object : Iterator<BigDecimal> {

            val first = 0.0.toBigDecimal()

            val last = ((from - until).absoluteValue * 60).toBigDecimal().setScale(first.scale())

            var current = first

            override fun hasNext(): Boolean {
                return current < last
            }

            override fun next(): BigDecimal {
                val temp = current + accuracy.toBigDecimal()
                val newCurrent = if (temp >= last) last else temp
                current = newCurrent

                return current
            }

        }
    }

    private companion object {
        const val DEFAULT_TIME_ACCURACY = 0.1
    }

}

@JvmInline
value class BankWorkingResult(val reports: List<HourReport>)

data class HourReport(
    val hour: Int,
    val incoming: Int,
    val processed: Int,
    val inQueue: Int,
)
