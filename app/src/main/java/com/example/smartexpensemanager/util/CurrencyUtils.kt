package com.example.smartexpensemanager.util

import java.text.NumberFormat
import java.util.Locale

enum class AppCurrency(val code: String, val locale: Locale) {
    VND(
        "VND",
        Locale.Builder()
            .setLanguage("vi")
            .setRegion("VN")
            .build()),
    USD("USD", Locale.US),
    EUR("EUR", Locale.FRANCE),
    JPY("JPY", Locale.JAPAN);

    companion object {
        fun fromCode(code: String): AppCurrency {
            return entries.find { it.code.equalsIgnoreCase(code) } ?: VND
        }
    }
}

private fun String?.equalsIgnoreCase(other: String?): Boolean {
    return this?.equals(other, ignoreCase = true) == true
}

fun Number.toCurrency(currencyCode: String = " VND"): String {
    val currency = AppCurrency.fromCode(currencyCode)
    val formatter = NumberFormat.getCurrencyInstance(currency.locale)
    return formatter.format(this)
}