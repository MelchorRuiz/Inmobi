package com.example.inmobi.features.properties.ui.calculator

fun getIsaiRateForState(state: String): Double {
    val normalized = state.trim().lowercase()
    return stateTaxRates[normalized] ?: 4.0
}

private val stateTaxRates = mapOf(
    "aguascalientes" to 3.0,
    "baja california" to 3.6,
    "baja california sur" to 3.6,
    "campeche" to 3.0,
    "chiapas" to 2.5,
    "chihuahua" to 3.5,
    "cdmx" to 5.0,
    "ciudad de mexico" to 5.0,
    "coahuila" to 3.5,
    "colima" to 3.0,
    "durango" to 3.0,
    "estado de mexico" to 5.0,
    "estado de méxico" to 5.0,
    "guanajuato" to 2.0,
    "guerrero" to 3.0,
    "hidalgo" to 3.0,
    "jalisco" to 3.6,
    "michoacan" to 3.0,
    "michoacán" to 3.0,
    "morelos" to 3.5,
    "nayarit" to 3.0,
    "nuevo leon" to 3.6,
    "nuevo león" to 3.6,
    "oaxaca" to 3.0,
    "puebla" to 3.5,
    "queretaro" to 3.5,
    "querétaro" to 3.5,
    "quintana roo" to 3.6,
    "san luis potosi" to 3.0,
    "san luis potosí" to 3.0,
    "sinaloa" to 3.5,
    "sonora" to 3.5,
    "tabasco" to 3.0,
    "tamaulipas" to 3.5,
    "tlaxcala" to 3.0,
    "veracruz" to 3.0,
    "yucatan" to 3.0,
    "yucatán" to 3.0,
    "zacatecas" to 3.0
)
