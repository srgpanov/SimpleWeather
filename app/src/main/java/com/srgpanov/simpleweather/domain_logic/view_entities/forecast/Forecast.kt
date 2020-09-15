package com.srgpanov.simpleweather.domain_logic.view_entities.forecast


data class Forecast(
    val condition: ConditionViewItem,
    val wind: WindViewItem,
    val humidity: HumidityViewItem,
    val pressure: PressureViewItem,
    val sunState: SunViewItem,
    val magnetic: MagneticViewItem
)