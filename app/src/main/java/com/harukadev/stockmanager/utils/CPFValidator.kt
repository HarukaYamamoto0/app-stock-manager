package com.harukadev.stockmanager.utils

/**
* Utility class for CPF validation.
*/
object CPFValidator {

fun isValid(cpf: String): Boolean {
val cleanCPF = cpf.filter { it.isDigit() }
if (cleanCPF.length != 11) {
return false
}

val digits = cleanCPF.map { it.toString().toInt() }
if (digits.all { it == digits[0] }) {
return false
}

val firstVerifierDigit = calculateVerifierDigit(digits.take(9))
if (firstVerifierDigit != digits[9]) {
return false
}

val secondVerifierDigit = calculateVerifierDigit(digits.take(10))
if (secondVerifierDigit != digits[10]) {
return false
}

return true
}

private fun calculateVerifierDigit(digits: List<Int>): Int {
var sum = 0
for ((index, digit) in digits.withIndex()) {
sum += digit * (digits.size + 1 - index)
}
var remainder = sum % 11
if (remainder < 2) {
remainder = 0
} else {
remainder = 11 - remainder
}
return remainder
}
}