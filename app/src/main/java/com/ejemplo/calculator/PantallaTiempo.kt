package com.ejemplo.calculator

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode

internal class PantallaTiempo(
    private val contexto: Context,
    private val tema: Tema,
    private val estado: EstadoTiempo
) {
    private lateinit var vistaExpresion: TextView
    private lateinit var vistaResultado: TextView
    private lateinit var vistaConversion: TextView
    private val botonesUnidad = mutableMapOf<UnidadTiempo, TextView>()

    fun crear(): View {
        val scroll = ScrollView(contexto)
        scroll.isFillViewport = true
        scroll.setBackgroundColor(Color.TRANSPARENT)

        val columna = LinearLayout(contexto)
        columna.orientation = LinearLayout.VERTICAL
        columna.setPadding(
            contexto.dp(16f), contexto.dp(6f), contexto.dp(16f), contexto.dp(28f)
        )
        scroll.addView(columna, android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ))

        columna.addView(crearPantalla(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(12f) })

        columna.addView(crearUnidades(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(10f) })

        columna.addView(crearTeclado(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        val nota = contexto.texto(
            Textos.tr("1 год = 365 дней · 1 месяц = 30 дней. Без единицы число считается секундами."),
            11f,
            tema.textoSuave
        )
        nota.setPadding(contexto.dp(4f), contexto.dp(12f), contexto.dp(4f), 0)
        columna.addView(nota)

        actualizar()
        return scroll
    }

    private fun crearPantalla(): View {
        val caja = LinearLayout(contexto)
        caja.orientation = LinearLayout.VERTICAL
        caja.background = forma(contexto, tema.superficie, 26f, tema.borde, 1f)
        caja.elevation = contexto.dp(3f).toFloat()
        caja.setPadding(
            contexto.dp(20f), contexto.dp(16f), contexto.dp(20f), contexto.dp(18f)
        )

        val etiqueta = contexto.texto(Textos.tr("РАСЧЁТ ВРЕМЕНИ"), 10f, tema.textoSuave, Typeface.BOLD)
        etiqueta.letterSpacing = 0.22f
        caja.addView(etiqueta)

        vistaExpresion = contexto.texto("", 18f, tema.textoSuave, Typeface.NORMAL)
        vistaExpresion.gravity = Gravity.END
        vistaExpresion.maxLines = 3
        vistaExpresion.setPadding(0, contexto.dp(14f), 0, 0)
        caja.addView(vistaExpresion, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        vistaResultado = contexto.texto(
            Textos.tr("0 секунд"), 34f, tema.texto, Typeface.NORMAL, "sans-serif-light"
        )
        vistaResultado.gravity = Gravity.END
        vistaResultado.maxLines = 3
        vistaResultado.setPadding(0, contexto.dp(4f), 0, 0)
        caja.addView(vistaResultado, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        vistaConversion = contexto.texto("", 11f, tema.textoSuave)
        vistaConversion.gravity = Gravity.END
        vistaConversion.maxLines = 2
        vistaConversion.setPadding(0, contexto.dp(4f), 0, 0)
        caja.addView(vistaConversion, LinearLayout.LayoutParams(
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

    private fun crearUnidades(): View {
        val caja = LinearLayout(contexto)
        caja.orientation = LinearLayout.VERTICAL

        val etiqueta = contexto.texto(Textos.tr("ЕДИНИЦЫ ВРЕМЕНИ"), 10f, tema.textoSuave, Typeface.BOLD)
        etiqueta.letterSpacing = 0.2f
        etiqueta.setPadding(contexto.dp(4f), 0, contexto.dp(4f), contexto.dp(6f))
        caja.addView(etiqueta)

        val unidades = UnidadTiempo.values()
        for (fila in 0 until 2) {
            val horizontal = LinearLayout(contexto)
            horizontal.orientation = LinearLayout.HORIZONTAL
            for (columna in 0 until 3) {
                val unidad = unidades[fila * 3 + columna]
                val boton = contexto.texto(
                    Textos.tr(unidad.boton),
                    13f,
                    tema.textoSuave,
                    Typeface.BOLD
                )
                boton.gravity = Gravity.CENTER
                boton.isClickable = true
                boton.isFocusable = true
                boton.contentDescription = "${Textos.tr("Добавить {u}").replace("{u}", Textos.tr(unidad.boton))}"
                boton.animarPulsacion()
                boton.setOnClickListener { aplicarUnidad(unidad) }
                botonesUnidad[unidad] = boton

                val params = LinearLayout.LayoutParams(
                    0,
                    contexto.dp(42f),
                    1f
                )
                params.marginStart = contexto.dp(3f)
                params.marginEnd = contexto.dp(3f)
                params.topMargin = contexto.dp(3f)
                params.bottomMargin = contexto.dp(3f)
                horizontal.addView(boton, params)
            }
            caja.addView(horizontal)
        }

        return caja
    }

    private fun crearTeclado(): View {
        val caja = LinearLayout(contexto)
        caja.orientation = LinearLayout.VERTICAL

        val filas = listOf(
            listOf("7", "8", "9", "+"),
            listOf("4", "5", "6", "−"),
            listOf("1", "2", "3", "⌫"),
            listOf("0", ".", "C", "=")
        )

        for (fila in filas) {
            val horizontal = LinearLayout(contexto)
            horizontal.orientation = LinearLayout.HORIZONTAL
            for (simbolo in fila) {
                val tecla = crearTecla(simbolo)
                val params = LinearLayout.LayoutParams(0, contexto.dp(56f), 1f)
                params.marginStart = contexto.dp(4f)
                params.marginEnd = contexto.dp(4f)
                params.topMargin = contexto.dp(4f)
                params.bottomMargin = contexto.dp(4f)
                horizontal.addView(tecla, params)
            }
            caja.addView(horizontal)
        }

        return caja
    }

    private fun crearTecla(simbolo: String): TextView {
        val tecla = contexto.texto(
            simbolo,
            if (simbolo.length > 1) 19f else 24f,
            tema.teclaTexto
        )
        tecla.gravity = Gravity.CENTER
        tecla.isClickable = true
        tecla.isFocusable = true
        tecla.animarPulsacion()

        when {
            simbolo == "=" -> {
                tecla.background = conOnda(
                    contexto,
                    degradado(contexto, tema.acento, tema.acento2, 20f),
                    tema.acento
                )
                tecla.setTextColor(tema.sobreAcento)
                tecla.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            }
            simbolo == "+" || simbolo == "−" -> {
                tecla.background = conOnda(
                    contexto,
                    forma(contexto, tema.operador, 20f),
                    tema.acento
                )
                tecla.setTextColor(tema.operadorTexto)
                tecla.typeface = Typeface.create("sans-serif", Typeface.BOLD)
            }
            simbolo == "C" || simbolo == "⌫" -> {
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

        tecla.setOnClickListener { pulsar(simbolo) }
        return tecla
    }

    private fun pulsar(tecla: String) {
        when (tecla) {
            "C" -> estado.limpiar()
            "⌫" -> borrar()
            "=" -> evaluar()
            "+" -> agregarOperador('+')
            "−" -> agregarOperador('−')
            "." -> agregarDecimal()
            else -> agregarDigito(tecla[0])
        }
        actualizar()
    }

    private fun agregarDigito(digito: Char) {
        if (estado.recienEvaluado) estado.limpiar()

        val ultima = estado.partes.lastOrNull()
        if (ultima is ParteTiempo.Numero && ultima.unidad == null) {
            if (ultima.digitos == "0") {
                ultima.digitos = digito.toString()
            } else if (ultima.digitos.length < 16) {
                ultima.digitos += digito
            }
        } else {
            estado.partes.add(ParteTiempo.Numero(digito.toString()))
        }
    }

    private fun agregarDecimal() {
        if (estado.recienEvaluado) estado.limpiar()

        val ultima = estado.partes.lastOrNull()
        if (ultima is ParteTiempo.Numero && ultima.unidad == null) {
            if (!ultima.digitos.contains('.')) {
                ultima.digitos = if (ultima.digitos.isEmpty()) "0." else ultima.digitos + "."
            }
        } else {
            estado.partes.add(ParteTiempo.Numero("0."))
        }
    }

    private fun aplicarUnidad(unidad: UnidadTiempo) {
        if (estado.recienEvaluado) return
        val ultima = estado.partes.lastOrNull() as? ParteTiempo.Numero ?: return
        if (ultima.digitos.isEmpty()) return
        ultima.unidad = unidad
        actualizar()
    }

    private fun agregarOperador(signo: Char) {
        if (estado.recienEvaluado) {
            val total = estado.resultado ?: return
            estado.partes.clear()
            estado.partes.add(
                ParteTiempo.Numero(digitosDeSegundos(total), UnidadTiempo.SEGUNDO)
            )
            estado.recienEvaluado = false
        }

        when (val ultima = estado.partes.lastOrNull()) {
            null -> return
            is ParteTiempo.Numero -> {
                if (ultima.unidad == null) ultima.unidad = UnidadTiempo.SEGUNDO
                estado.partes.add(ParteTiempo.Operador(signo))
            }
            is ParteTiempo.Operador -> {
                estado.partes[estado.partes.lastIndex] = ParteTiempo.Operador(signo)
            }
        }
    }

    private fun borrar() {
        if (estado.recienEvaluado) {
            estado.limpiar()
            return
        }

        val indice = estado.partes.lastIndex
        if (indice < 0) return

        when (val ultima = estado.partes[indice]) {
            is ParteTiempo.Numero -> {
                if (ultima.unidad != null) {
                    ultima.unidad = null
                } else if (ultima.digitos.length > 1) {
                    ultima.digitos = ultima.digitos.dropLast(1)
                } else {
                    estado.partes.removeAt(indice)
                }
            }
            is ParteTiempo.Operador -> estado.partes.removeAt(indice)
        }
    }

    private fun evaluar() {
        val ultima = estado.partes.lastOrNull() ?: return
        if (ultima is ParteTiempo.Operador) return
        if (ultima is ParteTiempo.Numero && ultima.unidad == null) {
            ultima.unidad = UnidadTiempo.SEGUNDO
        }

        val total = MotorTiempo.evaluar(estado.partes) ?: return
        estado.resultado = total
        estado.recienEvaluado = true
    }

    private fun actualizar() {
        if (!::vistaExpresion.isInitialized) return
        mostrarExpresion()
        actualizarUnidades()

        if (estado.partes.isEmpty()) {
            vistaResultado.text = Textos.tr("0 секунд")
            vistaConversion.text = "= 0 ${Textos.tr("с")}"
            vistaResultado.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34f)
            return
        }

        val evaluables = if (estado.partes.lastOrNull() is ParteTiempo.Operador) {
            estado.partes.dropLast(1)
        } else {
            estado.partes
        }
        val total = MotorTiempo.evaluar(evaluables)
        if (total == null) {
            vistaResultado.text = "—"
            vistaConversion.text = ""
            vistaResultado.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34f)
            return
        }

        val texto = MotorTiempo.formatear(total)
        vistaResultado.text = texto
        vistaConversion.text = MotorTiempo.conversion(total)
        vistaResultado.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            when {
                texto.length > 34 -> 17f
                texto.length > 26 -> 21f
                texto.length > 18 -> 26f
                else -> 34f
            }
        )
    }

    private fun mostrarExpresion() {
        val texto = SpannableStringBuilder()
        for ((indice, parte) in estado.partes.withIndex()) {
            if (indice > 0) texto.append(" ")
            when (parte) {
                is ParteTiempo.Numero -> {
                    texto.append(parte.digitos)
                    val unidad = parte.unidad ?: continue
                    val inicio = texto.length + 1
                    texto.append(" ").append(Textos.tr(unidad.corta))
                    texto.setSpan(
                        RelativeSizeSpan(0.66f),
                        inicio,
                        texto.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    texto.setSpan(
                        ForegroundColorSpan(tema.acento),
                        inicio,
                        texto.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                is ParteTiempo.Operador -> texto.append(parte.signo)
            }
        }
        vistaExpresion.text = if (texto.isEmpty()) " " else texto
    }

    private fun actualizarUnidades() {
        val activa = (estado.partes.lastOrNull() as? ParteTiempo.Numero)?.unidad
        for ((unidad, boton) in botonesUnidad) {
            val seleccionada = unidad == activa
            boton.background = if (seleccionada) {
                conOnda(
                    contexto,
                    forma(contexto, colorConAlfa(tema.acento, 32), 14f, tema.acento, 1f),
                    tema.acento
                )
            } else {
                conOnda(
                    contexto,
                    forma(contexto, tema.superficieAlt, 14f, tema.borde, 1f),
                    tema.acento
                )
            }
            boton.setTextColor(if (seleccionada) tema.acento else tema.textoSuave)
        }
    }

    private fun digitosDeSegundos(total: Double): String {
        val numero = BigDecimal.valueOf(total)
            .setScale(3, RoundingMode.HALF_UP)
            .stripTrailingZeros()
        return numero.toPlainString()
    }
}
