package com.ejemplo.calculator

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.SparseIntArray
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

class ActividadPrincipal : Activity() {

    private val clavesModos = listOf(
        "Счёт", "Перевод", "Фигуры", "Время", "Человек", "Космос", "Константы"
    )

    private var indiceTema = 5
    private var idiomaActual = Idioma.RUSO
    private var modoActual = 0

    private val estadoCalculadora = EstadoCalculadora()
    private val estadoConversor = EstadoConversor()
    private val estadoFiguras = EstadoFiguras()
    private val estadoTiempo = EstadoTiempo()
    private val scrolls = SparseIntArray()

    private lateinit var prefs: SharedPreferences
    private lateinit var raiz: FrameLayout
    private lateinit var fondo: FondoMatematico
    private lateinit var columna: LinearLayout
    private lateinit var zonaContenido: FrameLayout

    private val chips = mutableListOf<TextView>()

    override fun onCreate(estado: Bundle?) {
        super.onCreate(estado)
        prefs = getSharedPreferences("calculadora", Context.MODE_PRIVATE)
        indiceTema = prefs.getInt("tema", 5).coerceIn(0, Temas.todos.lastIndex)
        idiomaActual = Idioma.porCodigo(prefs.getString("idioma", Idioma.RUSO.codigo))
        Textos.idioma = idiomaActual

        raiz = FrameLayout(this)
        fondo = FondoMatematico(this)
        raiz.addView(fondo, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        columna = LinearLayout(this)
        columna.orientation = LinearLayout.VERTICAL
        raiz.addView(columna, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        setContentView(raiz)
        configurarInsets()
        construir()
    }

    private fun configurarInsets() {
        if (Build.VERSION.SDK_INT >= 35) {
            raiz.setOnApplyWindowInsetsListener { _, insets ->
                val barras = insets.getInsets(
                    WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout()
                )
                columna.setPadding(0, barras.top, 0, barras.bottom)
                insets
            }
            raiz.requestApplyInsets()
        }
    }

    private fun temaActual(): Tema = Temas.todos[indiceTema]

    private fun construir() {
        val tema = temaActual()
        aplicarBarras(tema)
        fondo.aplicarTema(tema)
        raiz.setBackgroundColor(tema.fondo)

        columna.removeAllViews()
        chips.clear()

        columna.addView(crearCabecera(tema), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        columna.addView(crearPestanas(tema), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        zonaContenido = FrameLayout(this)
        columna.addView(zonaContenido, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1f
        ))

        mostrarModo(modoActual, false)
    }

    private fun crearCabecera(tema: Tema): View {
        val fila = LinearLayout(this)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.gravity = Gravity.CENTER_VERTICAL
        fila.setPadding(dp(20f), dp(14f), dp(20f), dp(4f))

        val textos = LinearLayout(this)
        textos.orientation = LinearLayout.VERTICAL

        val marca = texto("CALCULATOR", 10f, tema.acento, Typeface.BOLD)
        marca.letterSpacing = 0.34f
        textos.addView(marca)

        val titulo = texto(Textos.tr("Калькулятор"), 28f, tema.texto, Typeface.BOLD)
        titulo.setPadding(0, dp(1f), 0, 0)
        textos.addView(titulo)

        fila.addView(textos, LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ))

        val botonIdioma = texto(idiomaActual.bandera, 22f, tema.texto, Typeface.NORMAL)
        botonIdioma.gravity = Gravity.CENTER
        botonIdioma.isClickable = true
        botonIdioma.isFocusable = true
        botonIdioma.contentDescription = Textos.tr("Выбор языка")
        botonIdioma.background = conOnda(
            this,
            forma(this, tema.superficieAlt, 23f, tema.borde, 1f),
            tema.acento
        )
        botonIdioma.animarPulsacion()
        botonIdioma.setOnClickListener {
            mostrarSelectorIdioma(this, temaActual(), idiomaActual) { elegido -> cambiarIdioma(elegido) }
        }
        fila.addView(botonIdioma, LinearLayout.LayoutParams(dp(46f), dp(46f)).apply {
            marginEnd = dp(8f)
        })

        val botonTema = texto("◐", 20f, tema.acento, Typeface.BOLD)
        botonTema.gravity = Gravity.CENTER
        botonTema.isClickable = true
        botonTema.isFocusable = true
        botonTema.contentDescription = Textos.tr("Цветовая схема")
        botonTema.background = conOnda(
            this,
            forma(this, tema.superficieAlt, 23f, tema.borde, 1f),
            tema.acento
        )
        botonTema.animarPulsacion()
        botonTema.setOnClickListener {
            mostrarSelectorTema(this, temaActual(), indiceTema) { elegido -> cambiarTema(elegido) }
        }
        fila.addView(botonTema, LinearLayout.LayoutParams(dp(46f), dp(46f)))

        return fila
    }

    private fun crearPestanas(tema: Tema): View {
        val desplazable = HorizontalScrollView(this)
        desplazable.isHorizontalScrollBarEnabled = false
        desplazable.clipToPadding = false

        val fila = LinearLayout(this)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.setPadding(dp(16f), dp(8f), dp(16f), dp(10f))

        val modos = clavesModos.map(Textos::tr)
        for (i in modos.indices) {
            val chip = texto(modos[i], 14f, tema.textoSuave, Typeface.BOLD)
            chip.setPadding(dp(16f), dp(10f), dp(16f), dp(10f))
            chip.isClickable = true
            chip.isFocusable = true
            chip.animarPulsacion()
            chip.setOnClickListener {
                if (modoActual != i) mostrarModo(i, true)
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = dp(8f)
            fila.addView(chip, params)
            chips.add(chip)
        }

        desplazable.addView(fila)
        actualizarChips(tema)
        return desplazable
    }

    private fun actualizarChips(tema: Tema = temaActual()) {
        for (i in chips.indices) {
            val chip = chips[i]
            val activo = i == modoActual
            if (activo) {
                chip.background = conOnda(
                    this,
                    degradado(this, tema.acento, tema.acento2, 16f),
                    tema.acento
                )
                chip.setTextColor(tema.sobreAcento)
            } else {
                chip.background = conOnda(
                    this,
                    forma(this, tema.superficieAlt, 16f, tema.borde, 1f),
                    tema.acento
                )
                chip.setTextColor(tema.textoSuave)
            }
        }
    }

    private fun mostrarModo(nuevo: Int, animar: Boolean) {
        if (zonaContenido.childCount > 0) guardarScroll()
        modoActual = nuevo
        actualizarChips()
        zonaContenido.removeAllViews()

        val vista = crearPantallaActual()
        zonaContenido.addView(vista, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))

        val desplazamiento = scrolls.get(nuevo, 0)
        vista.post {
            buscarScroll(zonaContenido)?.scrollTo(0, desplazamiento)
        }

        if (animar) {
            vista.alpha = 0f
            vista.translationX = dp(26f).toFloat()
            vista.animate()
                .alpha(1f)
                .translationX(0f)
                .setDuration(240)
                .start()
        }
    }

    private fun crearPantallaActual(): View {
        val tema = temaActual()
        return when (modoActual) {
            0 -> PantallaCalculadora(this, tema, estadoCalculadora).crear()
            1 -> PantallaConversor(this, tema, estadoConversor).crear()
            2 -> PantallaFiguras(this, tema, estadoFiguras).crear()
            3 -> PantallaTiempo(this, tema, estadoTiempo).crear()
            4 -> PantallaDatos(
                this,
                tema,
                Textos.tr("Человек"),
                Textos.tr("Средние значения взрослого человека. Индивидуальные показатели могут заметно отличаться."),
                Datos.humanos
            ).crear()
            5 -> PantallaDatos(
                this,
                tema,
                Textos.tr("Космос"),
                Textos.tr("Ключевые величины Земли, Солнца, планет и наблюдаемой Вселенной."),
                Datos.cosmos
            ).crear()
            else -> PantallaDatos(
                this,
                tema,
                Textos.tr("Константы"),
                Textos.tr("Наиболее употребительные физические постоянные и их точные значения."),
                Datos.constantes
            ).crear()
        }
    }

    private fun guardarScroll() {
        val scroll = buscarScroll(zonaContenido) ?: return
        scrolls.put(modoActual, scroll.scrollY)
    }

    private fun buscarScroll(vista: View): ScrollView? {
        if (vista is ScrollView) return vista
        if (vista is ViewGroup) {
            for (i in 0 until vista.childCount) {
                val encontrado = buscarScroll(vista.getChildAt(i))
                if (encontrado != null) return encontrado
            }
        }
        return null
    }

    private fun cambiarTema(indice: Int) {
        if (indice == indiceTema) return
        guardarScroll()
        indiceTema = indice
        prefs.edit().putInt("tema", indice).apply()
        construir()
        columna.alpha = 0f
        columna.animate().alpha(1f).setDuration(300).start()
    }

    private fun cambiarIdioma(idioma: Idioma) {
        if (idioma == idiomaActual) return
        guardarScroll()
        idiomaActual = idioma
        Textos.idioma = idioma
        prefs.edit().putString("idioma", idioma.codigo).apply()
        construir()
        columna.alpha = 0f
        columna.animate().alpha(1f).setDuration(300).start()
    }

    private fun aplicarBarras(tema: Tema) {
        val bordeAPantalla = Build.VERSION.SDK_INT >= 35
        window.statusBarColor = if (bordeAPantalla) Color.TRANSPARENT else tema.fondo
        window.navigationBarColor = if (bordeAPantalla) Color.TRANSPARENT else tema.fondo
        if (Build.VERSION.SDK_INT >= 29) {
            window.isNavigationBarContrastEnforced = false
        }

        if (Build.VERSION.SDK_INT >= 30) {
            val controlador = window.insetsController
            if (controlador != null) {
                val mascara = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                controlador.setSystemBarsAppearance(
                    if (tema.oscuro) 0 else mascara,
                    mascara
                )
            }
        } else {
            @Suppress("DEPRECATION")
            var banderas = window.decorView.systemUiVisibility
            @Suppress("DEPRECATION")
            banderas = if (tema.oscuro) {
                banderas and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            } else {
                banderas or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            if (Build.VERSION.SDK_INT >= 26) {
                @Suppress("DEPRECATION")
                banderas = if (tema.oscuro) {
                    banderas and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                } else {
                    banderas or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                }
            }
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = banderas
        }
    }
}
