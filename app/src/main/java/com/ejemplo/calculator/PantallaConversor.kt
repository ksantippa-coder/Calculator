package com.ejemplo.calculator

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

internal data class Unidad(val nombre: String, val factor: Double)

internal data class CategoriaConversion(
    val nombre: String,
    val unidades: List<Unidad>,
    val desdePorDefecto: Int,
    val hastaPorDefecto: Int
)

internal object Conversiones {
    val categorias: List<CategoriaConversion> = listOf(
        CategoriaConversion(
            nombre = "Длина",
            unidades = listOf(
                Unidad("мм", 0.001),
                Unidad("см", 0.01),
                Unidad("м", 1.0),
                Unidad("км", 1000.0),
                Unidad("дюйм", 0.0254),
                Unidad("фут", 0.3048),
                Unidad("ярд", 0.9144),
                Unidad("миля", 1609.344),
                Unidad("мор. миля", 1852.0),
                Unidad("а.е.", 1.495978707e11),
                Unidad("св. год", 9.4607304725808e15)
            ),
            desdePorDefecto = 2,
            hastaPorDefecto = 1
        ),
        CategoriaConversion(
            nombre = "Площадь",
            unidades = listOf(
                Unidad("мм²", 1e-6),
                Unidad("см²", 1e-4),
                Unidad("м²", 1.0),
                Unidad("км²", 1e6),
                Unidad("га", 1e4),
                Unidad("ар", 100.0),
                Unidad("фут²", 0.09290304),
                Unidad("ярд²", 0.83612736),
                Unidad("акр", 4046.8564224),
                Unidad("миля²", 2589988.110336)
            ),
            desdePorDefecto = 2,
            hastaPorDefecto = 1
        ),
        CategoriaConversion(
            nombre = "Скорость",
            unidades = listOf(
                Unidad("м/с", 1.0),
                Unidad("км/ч", 1.0 / 3.6),
                Unidad("миль/ч", 0.44704),
                Unidad("узел", 0.514444444444),
                Unidad("фут/с", 0.3048),
                Unidad("мах", 343.0),
                Unidad("км/с", 1000.0),
                Unidad("скорость света", 299792458.0)
            ),
            desdePorDefecto = 1,
            hastaPorDefecto = 0
        ),
        CategoriaConversion(
            nombre = "Объём",
            unidades = listOf(
                Unidad("мл", 1e-6),
                Unidad("л", 0.001),
                Unidad("м³", 1.0),
                Unidad("см³", 1e-6),
                Unidad("галлон", 0.003785411784),
                Unidad("пинта", 0.000473176473),
                Unidad("баррель", 0.158987294928),
                Unidad("фут³", 0.028316846592),
                Unidad("дюйм³", 1.6387064e-5)
            ),
            desdePorDefecto = 1,
            hastaPorDefecto = 0
        )
    )
}

internal class EstadoConversor {
    var categoria: Int = 0
    var desde: Int = Conversiones.categorias[0].desdePorDefecto
    var hasta: Int = Conversiones.categorias[0].hastaPorDefecto
    var entrada: String = "1"
}

internal class PantallaConversor(
    private val contexto: Context,
    private val tema: Tema,
    private val estado: EstadoConversor
) {
    private lateinit var vistaResultado: TextView
    private lateinit var vistaFormula: TextView
    private lateinit var selectorDesde: TextView
    private lateinit var selectorHasta: TextView
    private lateinit var filaCategorias: LinearLayout
    private lateinit var entrada: EditText

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

        columna.addView(crearCategorias(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(12f) })

        columna.addView(crearEntrada(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(10f) })

        columna.addView(crearUnidades(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(12f) })

        columna.addView(crearResultado(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        actualizarCategorias()
        actualizarSelectores()
        recalcular()
        return scroll
    }

    private fun crearCategorias(): View {
        filaCategorias = LinearLayout(contexto)
        filaCategorias.orientation = LinearLayout.HORIZONTAL
        for (i in Conversiones.categorias.indices) {
            val categoria = Conversiones.categorias[i]
            val chip = contexto.texto(Textos.tr(categoria.nombre), 13f, tema.textoSuave, Typeface.BOLD)
            chip.gravity = Gravity.CENTER
            chip.setPadding(0, contexto.dp(11f), 0, contexto.dp(11f))
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            params.marginStart = contexto.dp(3f)
            params.marginEnd = contexto.dp(3f)
            chip.animarPulsacion()
            chip.setOnClickListener {
                if (estado.categoria != i) {
                    estado.categoria = i
                    val nueva = Conversiones.categorias[i]
                    estado.desde = nueva.desdePorDefecto
                    estado.hasta = nueva.hastaPorDefecto
                    actualizarCategorias()
                    actualizarSelectores()
                    recalcular()
                }
            }
            filaCategorias.addView(chip, params)
        }
        return filaCategorias
    }

    private fun actualizarCategorias() {
        for (i in 0 until filaCategorias.childCount) {
            val chip = filaCategorias.getChildAt(i) as TextView
            val activo = i == estado.categoria
            chip.background = if (activo) {
                conOnda(contexto, degradado(contexto, tema.acento, tema.acento2, 14f), tema.acento)
            } else {
                conOnda(
                    contexto,
                    forma(contexto, tema.superficieAlt, 14f, tema.borde, 1f),
                    tema.acento
                )
            }
            chip.setTextColor(if (activo) tema.sobreAcento else tema.textoSuave)
        }
    }

    private fun crearEntrada(): View {
        val caja = contexto.tarjeta(tema, 18f, 24f)

        val etiqueta = contexto.texto(Textos.tr("ЗНАЧЕНИЕ"), 10f, tema.textoSuave, Typeface.BOLD)
        etiqueta.letterSpacing = 0.22f
        caja.addView(etiqueta)

        entrada = EditText(contexto)
        entrada.background = null
        entrada.hint = "0"
        entrada.setText(estado.entrada)
        entrada.setTextColor(tema.texto)
        entrada.setHintTextColor(colorConAlfa(tema.textoSuave, 140))
        entrada.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 32f)
        entrada.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        entrada.inputType = InputType.TYPE_CLASS_NUMBER or
            InputType.TYPE_NUMBER_FLAG_DECIMAL or
            InputType.TYPE_NUMBER_FLAG_SIGNED
        entrada.setPadding(0, contexto.dp(6f), 0, contexto.dp(2f))
        entrada.setSelection(entrada.text.length)
        entrada.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                estado.entrada = s?.toString().orEmpty()
                recalcular()
            }
        })
        caja.addView(entrada, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        return caja
    }

    private fun crearUnidades(): View {
        val caja = contexto.tarjeta(tema, 16f, 24f)
        val fila = LinearLayout(contexto)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.gravity = Gravity.CENTER_VERTICAL

        val izq = columnaUnidad(Textos.tr("ИЗ"))
        selectorDesde = izq.second
        fila.addView(izq.first, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        val botonCambio = contexto.texto("⇄", 20f, tema.acento, Typeface.BOLD)
        botonCambio.gravity = Gravity.CENTER
        botonCambio.background = conOnda(
            contexto,
            forma(contexto, tema.superficieAlt, 21f, tema.borde, 1f),
            tema.acento
        )
        botonCambio.isClickable = true
        botonCambio.animarPulsacion()
        val paramsCambio = LinearLayout.LayoutParams(contexto.dp(42f), contexto.dp(42f))
        paramsCambio.marginStart = contexto.dp(8f)
        paramsCambio.marginEnd = contexto.dp(8f)
        botonCambio.setOnClickListener {
            val temporal = estado.desde
            estado.desde = estado.hasta
            estado.hasta = temporal
            actualizarSelectores()
            recalcular()
        }
        fila.addView(botonCambio, paramsCambio)

        val der = columnaUnidad(Textos.tr("В"))
        selectorHasta = der.second
        fila.addView(der.first, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        selectorDesde.setOnClickListener { abrirSelector(true) }
        selectorHasta.setOnClickListener { abrirSelector(false) }

        caja.addView(fila, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))
        return caja
    }

    private fun columnaUnidad(etiqueta: String): Pair<LinearLayout, TextView> {
        val columna = LinearLayout(contexto)
        columna.orientation = LinearLayout.VERTICAL

        val titulo = contexto.texto(etiqueta, 10f, tema.textoSuave, Typeface.BOLD)
        titulo.letterSpacing = 0.22f
        columna.addView(titulo)

        val selector = contexto.texto("", 16f, tema.texto, Typeface.BOLD)
        selector.setPadding(contexto.dp(12f), contexto.dp(12f), contexto.dp(12f), contexto.dp(12f))
        selector.isClickable = true
        selector.animarPulsacion()
        selector.background = conOnda(
            contexto,
            forma(contexto, tema.superficieAlt, 14f, tema.borde, 1f),
            tema.acento
        )
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.topMargin = contexto.dp(6f)
        columna.addView(selector, params)
        return Pair(columna, selector)
    }

    private fun actualizarSelectores() {
        val categoria = Conversiones.categorias[estado.categoria]
        if (!::selectorDesde.isInitialized) return
        selectorDesde.text = "${Textos.tr(categoria.unidades[estado.desde].nombre)}  ▾"
        selectorHasta.text = "${Textos.tr(categoria.unidades[estado.hasta].nombre)}  ▾"
    }

    private fun crearResultado(): View {
        val caja = LinearLayout(contexto)
        caja.orientation = LinearLayout.VERTICAL
        caja.background = degradado(contexto, tema.acento, tema.acento2, 26f)
        caja.elevation = contexto.dp(4f).toFloat()
        caja.setPadding(contexto.dp(20f), contexto.dp(18f), contexto.dp(20f), contexto.dp(18f))

        val etiqueta = contexto.texto(Textos.tr("РЕЗУЛЬТАТ"), 10f, colorConAlfa(tema.sobreAcento, 190), Typeface.BOLD)
        etiqueta.letterSpacing = 0.22f
        caja.addView(etiqueta)

        vistaResultado = contexto.texto("—", 34f, tema.sobreAcento, Typeface.NORMAL, "sans-serif-light")
        vistaResultado.setPadding(0, contexto.dp(8f), 0, 0)
        vistaResultado.maxLines = 3
        caja.addView(vistaResultado, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        vistaFormula = contexto.texto("", 12f, colorConAlfa(tema.sobreAcento, 185))
        vistaFormula.setPadding(0, contexto.dp(8f), 0, 0)
        caja.addView(vistaFormula)
        return caja
    }

    private fun abrirSelector(esDesde: Boolean) {
        val categoria = Conversiones.categorias[estado.categoria]
        val indice = if (esDesde) estado.desde else estado.hasta
        mostrarListaOpciones(
            contexto,
            tema,
            Textos.tr(if (esDesde) "Из какой единицы" else "В какую единицу"),
            Textos.tr(categoria.nombre),
            categoria.unidades.map { Textos.tr(it.nombre) },
            indice
        ) { elegido ->
            if (esDesde) estado.desde = elegido else estado.hasta = elegido
            actualizarSelectores()
            recalcular()
        }
    }

    private fun recalcular() {
        if (!::vistaResultado.isInitialized) return
        val categoria = Conversiones.categorias[estado.categoria]
        val desde = categoria.unidades[estado.desde]
        val hasta = categoria.unidades[estado.hasta]
        val valor = estado.entrada.replace(',', '.').toDoubleOrNull()
        if (valor == null) {
            vistaResultado.text = "—"
        } else {
            val salida = valor * desde.factor / hasta.factor
            vistaResultado.text = "${formatearNumero(salida)} ${Textos.tr(hasta.nombre)}"
        }
        val uno = desde.factor / hasta.factor
        vistaFormula.text = "1 ${Textos.tr(desde.nombre)} = ${formatearNumero(uno)} ${Textos.tr(hasta.nombre)}"
        vistaResultado.setTextSize(
            android.util.TypedValue.COMPLEX_UNIT_SP,
            if (vistaResultado.text.length > 20) 22f else 34f
        )
    }
}
