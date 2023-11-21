package oleh.zaiets.first_lab

class LabComponents(
    costumerAverage: Double,
    cashierAverage: Double,
) {

    private val exponentialDivision = ExponentialDivision()

    private val uiMapper = HourReportsUiModelMapper()

    private val costumerModel = ModelProcessor(
        probability = exponentialDivision,
        historicalAverage = costumerAverage,
        defaultStep = 0.1,
    )

    private val cashierModel = ModelProcessor(
        probability = exponentialDivision,
        historicalAverage = cashierAverage,
        defaultStep = 0.1,
    )

    private val bank = Bank(
        customerIncomingProcessor = costumerModel,
        cashierProcessor = cashierModel,
        accuracy = 0.1
    )

    fun process(from: Int, until: Int): HourReportsUiModel {
        return uiMapper.map(bank.work(from, until))
    }

}
