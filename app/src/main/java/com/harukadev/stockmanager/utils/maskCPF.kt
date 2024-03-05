package com.harukadev.stockmanager.utils

fun maskCPF(cpf: String): String {
    val cleanedCPF = cpf.replace("\\D".toRegex(), "")

    if (cleanedCPF.length < 11) {
        return cleanedCPF
    }

    val maskedCPF = StringBuilder()

    maskedCPF.append("###")
    maskedCPF.append(".")
    maskedCPF.append("###")
    maskedCPF.append(".")
    maskedCPF.append(cleanedCPF.substring(6, 9))
    maskedCPF.append("-")
    maskedCPF.append(cleanedCPF.substring(9, 11))

    return maskedCPF.toString()
}

