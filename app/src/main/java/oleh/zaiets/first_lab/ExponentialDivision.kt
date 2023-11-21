package oleh.zaiets.first_lab

import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.random.nextInt

interface ProbabilityCalculation<DATA> {
    fun probability(inputData: DATA): Int
}

class ExponentialDivision : ProbabilityCalculation<HistoricalAverage> {

    override fun probability(inputData: HistoricalAverage): Int {
        val m = 1 / inputData.average
        return ((1 - exp(m.unaryMinus() * inputData.current)) * 100).roundToInt()
    }

}

data class HistoricalAverage(
    val average: Double,
    val current: Double
)

fun Int.asProbabilityToBoolean(): Boolean {
    return Random(
        seed = System.currentTimeMillis()
    ).nextInt(0..100) <= this
}
