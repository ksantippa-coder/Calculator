package com.ejemplo.calculator

import java.math.BigDecimal
import java.math.RoundingMode

internal object MotorCalculadora {

    class ErrorCalculo(mensaje: String) : Exception(mensaje)

    fun evaluar(expresion: String): Double {
        val limpio = expresion
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("–", "-")
            .replace("∞", "I")
            .replace('\u00A0', ' ')
            .filterNot { it.isWhitespace() }
            .replace(",", ".")
        if (limpio.isEmpty()) return 0.0
        val analizador = Analizador(limpio)
        val resultado = analizador.leerExpresion()
        if (analizador.pos < limpio.length) throw ErrorCalculo("Неверная последовательность")
        if (resultado.isNaN()) throw ErrorCalculo("Неопределённость с бесконечностью")
        return resultado
    }

    fun texto(valor: Double): String {
        if (valor.isNaN()) return "0"
        if (valor.isInfinite()) return if (valor > 0) "∞" else "-∞"
        return BigDecimal.valueOf(valor)
            .setScale(10, RoundingMode.HALF_UP)
            .stripTrailingZeros()
            .toPlainString()
    }

    private class Analizador(private val cadena: String) {
        var pos: Int = 0

        fun leerExpresion(): Double {
            var valor = leerTermino()
            while (pos < cadena.length) {
                when (cadena[pos]) {
                    '+' -> {
                        pos++
                        valor = sumar(valor, leerTermino())
                    }
                    '-' -> {
                        pos++
                        valor = restar(valor, leerTermino())
                    }
                    else -> return valor
                }
            }
            return valor
        }

        private fun leerTermino(): Double {
            var valor = leerFactor()
            while (pos < cadena.length) {
                when (cadena[pos]) {
                    '*' -> {
                        pos++
                        valor = multiplicar(valor, leerFactor())
                    }
                    '/' -> {
                        pos++
                        valor = dividir(valor, leerFactor())
                    }
                    else -> return valor
                }
            }
            return valor
        }

        private fun leerFactor(): Double {
            var signo = 1.0
            while (pos < cadena.length) {
                when (cadena[pos]) {
                    '-' -> {
                        signo = -signo
                        pos++
                    }
                    '+' -> pos++
                    else -> break
                }
            }
            var valor = leerPrimario()
            while (pos < cadena.length && cadena[pos] == '%') {
                pos++
                valor /= 100.0
            }
            return signo * valor
        }

        private fun leerPrimario(): Double {
            if (pos >= cadena.length) throw ErrorCalculo("Не хватает числа")
            val caracter = cadena[pos]
            if (caracter == 'I') {
                pos++
                return Double.POSITIVE_INFINITY
            }
            if (caracter == '(') {
                pos++
                val valor = leerExpresion()
                if (pos >= cadena.length || cadena[pos] != ')') {
                    throw ErrorCalculo("Неверная последовательность")
                }
                pos++
                return valor
            }
            val inicio = pos
            var hayPunto = false
            while (pos < cadena.length) {
                val actual = cadena[pos]
                if (actual.isDigit()) {
                    pos++
                } else if (actual == '.' && !hayPunto) {
                    hayPunto = true
                    pos++
                } else {
                    break
                }
            }
            if (inicio == pos) throw ErrorCalculo("Ожидалось число")
            return try {
                cadena.substring(inicio, pos).toDouble()
            } catch (_: NumberFormatException) {
                throw ErrorCalculo("Неверная последовательность")
            }
        }

        private fun sumar(a: Double, b: Double): Double {
            val resultado = a + b
            if (resultado.isNaN()) throw ErrorCalculo("Неопределённость: ∞ − ∞")
            return resultado
        }

        private fun restar(a: Double, b: Double): Double {
            val resultado = a - b
            if (resultado.isNaN()) throw ErrorCalculo("Неопределённость: ∞ − ∞")
            return resultado
        }

        private fun multiplicar(a: Double, b: Double): Double {
            if ((a == 0.0 && b.isInfinite()) || (b == 0.0 && a.isInfinite())) {
                throw ErrorCalculo("Неопределённость: 0 × ∞")
            }
            val resultado = a * b
            if (resultado.isNaN()) throw ErrorCalculo("Неопределённость с бесконечностью")
            return resultado
        }

        private fun dividir(a: Double, b: Double): Double {
            if (b == 0.0) {
                if (a == 0.0) throw ErrorCalculo("Неопределённость: 0 ÷ 0")
                return if (a > 0) Double.POSITIVE_INFINITY else Double.NEGATIVE_INFINITY
            }
            if (a.isInfinite() && b.isInfinite()) {
                throw ErrorCalculo("Неопределённость: ∞ ÷ ∞")
            }
            val resultado = a / b
            if (resultado.isNaN()) throw ErrorCalculo("Неопределённость с бесконечностью")
            return resultado
        }
    }
}
