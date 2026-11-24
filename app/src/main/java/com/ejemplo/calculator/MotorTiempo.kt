package com.ejemplo.calculator

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.floor

internal enum class UnidadTiempo(
    val boton: String,
    val corta: String,
    val segundos: Double,
    val uno: String,
    val pocos: String,
    val muchos: String
) {
    ANIO("лет", "г", 365.0 * 86400.0, "год", "года", "лет"),
    MES("месяцев", "мес", 30.0 * 86400.0, "месяц", "месяца", "месяцев"),
    DIA("дней", "д", 86400.0, "день", "дня", "дней"),
    HORA("часов", "ч", 3600.0, "час", "часа", "часов"),
    MINUTO("минут", "мин", 60.0, "минута", "минуты", "минут"),
    SEGUNDO("секунд", "с", 1.0, "секунда", "секунды", "секунд")
}

internal sealed class ParteTiempo {
    data class Numero(var digitos: String, var unidad: UnidadTiempo? = null) : ParteTiempo()
    data class Operador(val signo: Char) : ParteTiempo()
}

internal class EstadoTiempo {
    val partes = mutableListOf<ParteTiempo>()
    var recienEvaluado: Boolean = false
    var resultado: Double? = null

    fun limpiar() {
        partes.clear()
        recienEvaluado = false
        resultado = null
    }
}

internal object MotorTiempo {

    fun evaluar(partes: List<ParteTiempo>): Double? {
        if (partes.isEmpty()) return null

        var total = 0.0
        var actual = 0.0
        var signo = 1.0
        var hayNumero = false

        for (parte in partes) {
            when (parte) {
                is ParteTiempo.Numero -> {
                    val numero = parte.digitos.replace(',', '.').toDoubleOrNull() ?: return null
                    actual += numero * (parte.unidad?.segundos ?: 1.0)
                    hayNumero = true
                }
                is ParteTiempo.Operador -> {
                    total += signo * actual
                    actual = 0.0
                    signo = if (parte.signo == '−' || parte.signo == '-') -1.0 else 1.0
                    hayNumero = false
                }
            }
        }

        if (!hayNumero) return null
        return total + signo * actual
    }

    fun formatear(totalSegundos: Double): String {
        if (totalSegundos == 0.0) return "0 ${Textos.tr("секунд")}"

        val negativo = totalSegundos < 0.0
        var resto = abs(totalSegundos)
        val partes = mutableListOf<String>()

        for (unidad in listOf(
            UnidadTiempo.ANIO,
            UnidadTiempo.MES,
            UnidadTiempo.DIA,
            UnidadTiempo.HORA,
            UnidadTiempo.MINUTO
        )) {
            val cantidad = floor(resto / unidad.segundos).toLong()
            if (cantidad > 0L) {
                partes.add(
                    "${agrupar(cantidad.toString())} ${
                        Textos.formaTiempo(cantidad.toDouble(), unidad)
                    }"
                )
                resto -= cantidad * unidad.segundos
            }
        }

        if (resto > 0.0000001) {
            val texto = cantidadCorta(resto)
            partes.add("$texto ${Textos.formaTiempo(resto, UnidadTiempo.SEGUNDO)}")
        }

        if (partes.isEmpty()) return "0 ${Textos.tr("секунд")}"
        return (if (negativo) "−" else "") + partes.joinToString(" ")
    }

    fun conversion(totalSegundos: Double): String {
        val valor = abs(totalSegundos)
        if (valor == 0.0) return "= 0 ${Textos.tr("с")}"

        val horas = cantidadCorta(valor / 3600.0)
        val minutos = cantidadCorta(valor / 60.0)
        val segundos = cantidadCorta(valor)
        return "= $horas ${Textos.tr("ч")} · $minutos ${Textos.tr("мин")} · $segundos ${Textos.tr("с")}"
    }

    private fun cantidadCorta(valor: Double): String {
        val numero = BigDecimal.valueOf(valor)
            .setScale(3, RoundingMode.HALF_UP)
            .stripTrailingZeros()
        val texto = numero.toPlainString()
        val partes = texto.split('.')
        val entero = agrupar(partes[0])
        return if (partes.size > 1) "$entero.${partes[1]}" else entero
    }

    private fun agrupar(texto: String): String {
        val sb = StringBuilder()
        for ((indice, caracter) in texto.withIndex()) {
            if (indice > 0 && (texto.length - indice) % 3 == 0) sb.append('\u00A0')
            sb.append(caracter)
        }
        return sb.toString()
    }
}
