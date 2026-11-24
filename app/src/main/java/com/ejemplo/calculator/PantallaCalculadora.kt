package com.ejemplo.calculator

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

internal class EstadoCalculadora {
    var expresion: String = ""
    var resultado: String = "0"
    var valor: Double = 0.0
    var mensaje: String = ""
    var recienEvaluado: Boolean = false
}

internal class PantallaCalculadora(
    private val contexto: Context,
    private val tema: Tema,
    private val estado: EstadoCalculadora
) {
    private lateinit var vistaExpresion: TextView
    private lateinit var vistaResultado: TextView
    private lateinit var vistaMensaje: TextView

    private val operadores = setOf('+', '−', '×', '÷', '*', '/')

    fun crear(): View {
        val scroll = ScrollView(contexto)
        scroll.isFillViewport = true
        scroll.setBackgroundColor(Color.TRANSPARENT)

        val columna = LinearLayout(contexto)
        columna.orientation = LinearLayout.VERTICAL
        columna.setPadding(contexto.dp(16f), contexto.dp(6f), contexto.dp(16f), contexto.dp(28f))
        scroll.addView(columna, android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ))

        columna.addView(crearPantalla(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(14f) })

        val filas = listOf(
            listOf("C", "⌫", "%", "÷"),
            listOf("7", "8", "9", "×"),
            listOf("4", "5", "6", "−"),
            listOf("1", "2", "3", "+"),
            listOf("±", "0", ".", "="),
            listOf("∞")
        )

        for (fila in filas) {
            val horizontal = LinearLayout(contexto)
            horizontal.orientation = LinearLayout.HORIZONTAL
            for (texto in fila) {
                val tecla = crearTecla(texto)
                val params = LinearLayout.LayoutParams(0, contexto.dp(62f), if (fila.size == 1) 4f else 1f)
                params.marginStart = contexto.dp(4f)
                params.marginEnd = contexto.dp(4f)
                params.topMargin = contexto.dp(4f)
                params.bottomMargin = contexto.dp(4f)
                horizontal.addView(tecla, params)
            }
            columna.addView(horizontal, LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))
        }

        actualizar()
        return scroll
    }

    private fun crearPantalla(): View {
        val caja = LinearLayout(contexto)
        caja.orientation = LinearLayout.VERTICAL
        caja.background = forma(contexto, tema.superficie, 26f, tema.borde, 1f)
        caja.elevation = contexto.dp(3f).toFloat()
        caja.setPadding(contexto.dp(20f), contexto.dp(16f), contexto.dp(20f), contexto.dp(18f))

        val etiqueta = contexto.texto(Textos.tr("СТАНДАРТНЫЙ РЕЖИМ"), 10f, tema.textoSuave, Typeface.BOLD)
        etiqueta.letterSpacing = 0.22f
        caja.addView(etiqueta)

        vistaExpresion = contexto.texto("", 16f, tema.textoSuave, Typeface.NORMAL)
        vistaExpresion.gravity = Gravity.END
        vistaExpresion.maxLines = 2
        vistaExpresion.setPadding(0, contexto.dp(16f), 0, 0)
        caja.addView(vistaExpresion, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        vistaResultado = contexto.texto("0", 46f, tema.texto, Typeface.NORMAL, "sans-serif-light")
        vistaResultado.gravity = Gravity.END
        vistaResultado.maxLines = 2
        vistaResultado.setPadding(0, contexto.dp(2f), 0, 0)
        caja.addView(vistaResultado, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        vistaMensaje = contexto.texto("", 11f, tema.acento, Typeface.NORMAL)
        vistaMensaje.gravity = Gravity.END
        vistaMensaje.setPadding(0, contexto.dp(4f), 0, 0)
        vistaMensaje.visibility = View.GONE
        caja.addView(vistaMensaje, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        val linea = View(contexto)
        linea.background = degradado(contexto, tema.acento, tema.acento2, 2f)
        val paramsLinea = LinearLayout.LayoutParams(contexto.dp(46f), contexto.dp(3f))
        paramsLinea.gravity = Gravity.END
        paramsLinea.topMargin = contexto.dp(10f)
        caja.addView(linea, paramsLinea)

        return caja
    }

    private fun crearTecla(texto: String): TextView {
        val tecla = contexto.texto(texto, if (texto.length > 1) 19f else 24f, tema.teclaTexto, Typeface.NORMAL)
        tecla.gravity = Gravity.CENTER
        tecla.isClickable = true
        tecla.isFocusable = true
        tecla.animarPulsacion()

        when {
            texto == "=" -> {
                tecla.background = conOnda(
                    contexto,
                    degradado(contexto, tema.acento, tema.acento2, 20f),
                    tema.acento
                )
                tecla.setTextColor(tema.sobreAcento)
                tecla.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            }
            texto in listOf("÷", "×", "−", "+") -> {
                tecla.background = conOnda(
                    contexto,
                    forma(contexto, tema.operador, 20f, null),
                    tema.acento
                )
                tecla.setTextColor(tema.operadorTexto)
                tecla.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            }
            texto in listOf("C", "⌫", "%", "±") -> {
                tecla.background = conOnda(
                    contexto,
                    forma(contexto, tema.superficieAlt, 20f, tema.borde, 1f),
                    tema.acento
                )
                tecla.setTextColor(tema.acento)
                tecla.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            }
            else -> {
                tecla.background = conOnda(
                    contexto,
                    forma(contexto, tema.tecla, 20f, tema.borde, 1f),
                    tema.acento
                )
                tecla.setTextColor(tema.teclaTexto)
            }
        }

        tecla.setOnClickListener { pulsar(texto) }
        return tecla
    }

    private fun pulsar(tecla: String) {
        when (tecla) {
            "C" -> limpiarTodo()
            "⌫" -> borrar()
            "=" -> evaluar()
            "±" -> cambiarSigno()
            "%" -> aplicarPorcentaje()
            "∞" -> agregarInfinito()
            else -> agregar(tecla)
        }
        actualizar()
    }

    private fun limpiarTodo() {
        estado.expresion = ""
        estado.resultado = "0"
        estado.valor = 0.0
        estado.mensaje = ""
        estado.recienEvaluado = false
    }

    private fun descartarMensaje() {
        if (estado.mensaje.isEmpty()) return
        estado.mensaje = ""
        estado.expresion = ""
        estado.recienEvaluado = false
    }

    private fun borrar() {
        if (estado.mensaje.isNotEmpty() || estado.recienEvaluado) {
            limpiarTodo()
            return
        }
        if (estado.expresion.isEmpty()) return
        estado.expresion = estado.expresion.dropLast(1)
        if (estado.expresion.isEmpty()) {
            estado.resultado = "0"
            estado.valor = 0.0
        } else {
            previsualizar()
        }
    }

    private fun inicioUltimoOperando(): Int {
        val texto = estado.expresion
        for (i in texto.length - 1 downTo 0) {
            val caracter = texto[i]
            if (caracter in operadores) return i + 1
            if (caracter == '-' || caracter == '−') {
                val anterior = if (i == 0) null else texto[i - 1]
                if (anterior == null || anterior in operadores || anterior == '(') continue
                return i + 1
            }
        }
        return 0
    }

    private fun negarUltimoOperando() {
        val texto = estado.expresion
        if (texto.isEmpty()) {
            estado.expresion = "-"
            return
        }
        val inicio = inicioUltimoOperando()
        val operando = texto.substring(inicio)
        val negado = when (operando) {
            "∞" -> "-∞"
            "-∞" -> "∞"
            "" -> "-"
            else -> try {
                MotorCalculadora.texto(-MotorCalculadora.evaluar(operando))
            } catch (_: Exception) {
                "-$operando"
            }
        }
        estado.expresion = texto.substring(0, inicio) + negado
    }

    private fun cambiarSigno() {
        descartarMensaje()
        if (estado.recienEvaluado) {
            estado.expresion = MotorCalculadora.texto(-estado.valor)
            estado.recienEvaluado = false
        } else {
            negarUltimoOperando()
        }
        previsualizar()
    }

    private fun aplicarPorcentaje() {
        descartarMensaje()
        if (estado.recienEvaluado) {
            estado.expresion = MotorCalculadora.texto(estado.valor / 100.0)
            estado.recienEvaluado = false
            previsualizar()
            return
        }
        val texto = estado.expresion
        if (texto.isEmpty()) return
        val ultimo = texto.last()
        if (ultimo in operadores || ultimo == '-' || ultimo == '−' || ultimo == '%') return
        estado.expresion = texto + "%"
        previsualizar()
    }

    private fun agregarInfinito() {
        descartarMensaje()
        if (estado.recienEvaluado) {
            estado.expresion = ""
            estado.recienEvaluado = false
        }
        val texto = estado.expresion
        when {
            texto.isEmpty() -> estado.expresion = "∞"
            texto.last() == '∞' -> return
            texto.last() in operadores || texto.last() == '-' || texto.last() == '−' -> {
                estado.expresion = texto + "∞"
            }
            texto.length > 80 -> return
            else -> estado.expresion = texto + "×∞"
        }
        previsualizar()
    }

    private fun agregar(tecla: String) {
        val esOperador = tecla.length == 1 && tecla[0] in operadores
        descartarMensaje()

        if (esOperador) {
            val simbolo = if (tecla == "−" || tecla == "-") "-" else tecla
            if (estado.recienEvaluado) {
                estado.expresion = MotorCalculadora.texto(estado.valor) + simbolo
                estado.recienEvaluado = false
                previsualizar()
                return
            }
            if (estado.expresion.isEmpty()) {
                if (simbolo == "-") estado.expresion = "-"
                return
            }
            val ultimo = estado.expresion.last()
            val base = if (ultimo in operadores || ultimo == '-' || ultimo == '−') {
                estado.expresion.dropLast(1)
            } else {
                estado.expresion
            }
            estado.expresion = base + simbolo
            previsualizar()
            return
        }

        if (estado.recienEvaluado) {
            estado.expresion = ""
            estado.recienEvaluado = false
        }

        val texto = estado.expresion
        val ultimo = texto.lastOrNull()
        val separador = if (ultimo == '%' || ultimo == '∞') "×" else ""
        if (texto.length + separador.length + 1 > 80) return

        if (tecla == ".") {
            val inicio = inicioUltimoOperando()
            val operando = texto.substring(inicio)
            when {
                operando.contains('.') -> return
                operando.isEmpty() || operando == "-" || operando == "−" || operando == "∞" || operando == "-∞" -> {
                    estado.expresion = texto + separador + "0."
                }
                else -> estado.expresion = texto + separador + "."
            }
        } else {
            estado.expresion = texto + separador + tecla
        }
        previsualizar()
    }

    private fun previsualizar() {
        if (estado.expresion.isEmpty()) {
            estado.resultado = "0"
            estado.valor = 0.0
            estado.mensaje = ""
            return
        }
        val ultimo = estado.expresion.last()
        if (ultimo in operadores || ultimo == '-' || ultimo == '−' || ultimo == '(') return
        try {
            val valor = MotorCalculadora.evaluar(estado.expresion)
            estado.resultado = formatearNumero(valor)
            estado.valor = valor
            estado.mensaje = ""
        } catch (error: MotorCalculadora.ErrorCalculo) {
            estado.resultado = "—"
            estado.mensaje = Textos.tr(error.message ?: "Неверная последовательность")
        }
    }

    private fun evaluar() {
        if (estado.expresion.isBlank()) return
        if (estado.mensaje.isNotEmpty()) {
            estado.resultado = "—"
            return
        }
        try {
            val valor = MotorCalculadora.evaluar(estado.expresion)
            estado.resultado = formatearNumero(valor)
            estado.valor = valor
            estado.expresion = MotorCalculadora.texto(valor)
            estado.mensaje = ""
            estado.recienEvaluado = true
        } catch (error: MotorCalculadora.ErrorCalculo) {
            estado.resultado = "—"
            estado.mensaje = Textos.tr(error.message ?: "Неверная последовательность")
            estado.recienEvaluado = false
        }
    }

    private fun actualizar() {
        if (!::vistaExpresion.isInitialized) return
        vistaExpresion.text = estado.expresion
            .ifBlank { " " }
            .replace("-", "−")
            .replace('*', '×')
            .replace('/', '÷')
        vistaResultado.text = estado.resultado
        val largo = estado.resultado.length
        vistaResultado.setTextSize(
            android.util.TypedValue.COMPLEX_UNIT_SP,
            when {
                largo > 18 -> 24f
                largo > 12 -> 32f
                largo > 9 -> 38f
                else -> 46f
            }
        )
        if (estado.mensaje.isEmpty()) {
            vistaMensaje.visibility = View.GONE
        } else {
            vistaMensaje.text = estado.mensaje
            vistaMensaje.visibility = View.VISIBLE
        }
    }
}
