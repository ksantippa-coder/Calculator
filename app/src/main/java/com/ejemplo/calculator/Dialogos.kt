package com.ejemplo.calculator

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView

internal fun mostrarListaOpciones(
    contexto: Context,
    tema: Tema,
    titulo: String,
    subtitulo: String,
    opciones: List<String>,
    seleccionada: Int,
    onElegir: (Int) -> Unit
) {
    val contenedor = LinearLayout(contexto)
    contenedor.orientation = LinearLayout.VERTICAL
    contenedor.setPadding(
        contexto.dp(20f), contexto.dp(20f), contexto.dp(20f), contexto.dp(18f)
    )

    contenedor.addView(contexto.texto(titulo, 20f, tema.texto, Typeface.BOLD))
    val sub = contexto.texto(subtitulo, 12f, tema.textoSuave)
    sub.setPadding(0, contexto.dp(4f), 0, contexto.dp(12f))
    contenedor.addView(sub)

    val scroll = ScrollView(contexto)
    val lista = LinearLayout(contexto)
    lista.orientation = LinearLayout.VERTICAL
    scroll.addView(lista)

    var dialogo: AlertDialog? = null

    for (i in opciones.indices) {
        val fila = contexto.texto(
            opciones[i],
            15f,
            if (i == seleccionada) tema.acento else tema.texto,
            if (i == seleccionada) Typeface.BOLD else Typeface.NORMAL
        )
        fila.setPadding(contexto.dp(14f), contexto.dp(13f), contexto.dp(14f), contexto.dp(13f))
        fila.background = if (i == seleccionada) {
            conOnda(
                contexto,
                forma(contexto, colorConAlfa(tema.acento, 30), 14f, tema.acento, 1f),
                tema.acento
            )
        } else {
            conOnda(
                contexto,
                forma(contexto, Color.TRANSPARENT, 14f),
                tema.acento
            )
        }
        fila.setOnClickListener {
            dialogo?.dismiss()
            onElegir(i)
        }
        lista.addView(fila, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(2f) })
    }

    contenedor.addView(scroll, LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        contexto.dp(360f)
    ))

    dialogo = AlertDialog.Builder(contexto).setView(contenedor).create()
    dialogo.show()
    dialogo.window?.setBackgroundDrawable(
        forma(contexto, tema.superficie, 26f, tema.borde, 1f)
    )
    val ancho = minOf(contexto.dp(330f), contexto.resources.displayMetrics.widthPixels - contexto.dp(32f))
    dialogo.window?.setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT)
}

internal fun mostrarSelectorTema(
    contexto: Context,
    temaActual: Tema,
    indiceActual: Int,
    onElegir: (Int) -> Unit
) {
    val contenedor = LinearLayout(contexto)
    contenedor.orientation = LinearLayout.VERTICAL
    contenedor.setPadding(
        contexto.dp(20f), contexto.dp(20f), contexto.dp(20f), contexto.dp(18f)
    )

    contenedor.addView(contexto.texto(Textos.tr("Цветовая схема"), 20f, temaActual.texto, Typeface.BOLD))
    val sub = contexto.texto(Textos.tr("Пять светлых и пять тёмных палитр"), 12f, temaActual.textoSuave)
    sub.setPadding(0, contexto.dp(4f), 0, contexto.dp(12f))
    contenedor.addView(sub)

    val scroll = ScrollView(contexto)
    val lista = LinearLayout(contexto)
    lista.orientation = LinearLayout.VERTICAL
    scroll.addView(lista)

    var dialogo: AlertDialog? = null

    for (i in Temas.todos.indices) {
        val tema = Temas.todos[i]
        if (i == 0 || i == 5) {
            val seccion = contexto.texto(
                Textos.tr(if (i == 0) "СВЕТЛЫЕ" else "ТЁМНЫЕ"),
                10f,
                temaActual.acento,
                Typeface.BOLD
            )
            seccion.letterSpacing = 0.2f
            seccion.setPadding(0, contexto.dp(if (i == 0) 0f else 14f), 0, contexto.dp(5f))
            lista.addView(seccion)
        }

        val fila = LinearLayout(contexto)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.gravity = Gravity.CENTER_VERTICAL
        fila.setPadding(contexto.dp(10f), contexto.dp(8f), contexto.dp(10f), contexto.dp(8f))
        fila.background = if (i == indiceActual) {
            conOnda(
                contexto,
                forma(contexto, colorConAlfa(temaActual.acento, 26), 14f, temaActual.acento, 1f),
                temaActual.acento
            )
        } else {
            conOnda(contexto, forma(contexto, Color.TRANSPARENT, 14f), temaActual.acento)
        }

        for (color in listOf(tema.fondo, tema.superficie, tema.acento, tema.acento2)) {
            val punto = View(contexto)
            punto.background = circulo(color, colorConAlfa(temaActual.texto, 45))
            val params = LinearLayout.LayoutParams(contexto.dp(14f), contexto.dp(14f))
            params.marginEnd = contexto.dp(4f)
            fila.addView(punto, params)
        }

        val textos = LinearLayout(contexto)
        textos.orientation = LinearLayout.VERTICAL
        textos.setPadding(contexto.dp(10f), 0, 0, 0)
        textos.addView(
            contexto.texto(
                tema.nombre,
                15f,
                if (i == indiceActual) temaActual.acento else temaActual.texto,
                Typeface.BOLD
            )
        )
        textos.addView(contexto.texto(Textos.tr(if (tema.oscuro) "тёмная" else "светлая"), 11f, temaActual.textoSuave))
        fila.addView(textos, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        if (i == indiceActual) {
            fila.addView(contexto.texto("✓", 16f, temaActual.acento, Typeface.BOLD))
        }

        fila.setOnClickListener {
            dialogo?.dismiss()
            onElegir(i)
        }

        lista.addView(fila, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(2f) })
    }

    contenedor.addView(scroll, LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        contexto.dp(400f)
    ))

    dialogo = AlertDialog.Builder(contexto).setView(contenedor).create()
    dialogo.show()
    dialogo.window?.setBackgroundDrawable(
        forma(contexto, temaActual.superficie, 26f, temaActual.borde, 1f)
    )
    val ancho = minOf(contexto.dp(340f), contexto.resources.displayMetrics.widthPixels - contexto.dp(32f))
    dialogo.window?.setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT)
}


internal fun mostrarSelectorIdioma(
    contexto: Context,
    tema: Tema,
    idiomaActual: Idioma,
    onElegir: (Idioma) -> Unit
) {
    val contenedor = LinearLayout(contexto)
    contenedor.orientation = LinearLayout.VERTICAL
    contenedor.setPadding(
        contexto.dp(20f), contexto.dp(20f), contexto.dp(20f), contexto.dp(18f)
    )

    contenedor.addView(contexto.texto(Textos.tr("Язык"), 20f, tema.texto, Typeface.BOLD))
    val sub = contexto.texto(Textos.tr("Выберите язык интерфейса"), 12f, tema.textoSuave)
    sub.setPadding(0, contexto.dp(4f), 0, contexto.dp(12f))
    contenedor.addView(sub)

    val lista = LinearLayout(contexto)
    lista.orientation = LinearLayout.VERTICAL
    var dialogo: AlertDialog? = null

    for (idioma in Idioma.values()) {
        val fila = LinearLayout(contexto)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.gravity = Gravity.CENTER_VERTICAL
        fila.setPadding(contexto.dp(12f), contexto.dp(10f), contexto.dp(12f), contexto.dp(10f))
        fila.background = if (idioma == idiomaActual) {
            conOnda(
                contexto,
                forma(contexto, colorConAlfa(tema.acento, 30), 14f, tema.acento, 1f),
                tema.acento
            )
        } else {
            conOnda(contexto, forma(contexto, Color.TRANSPARENT, 14f), tema.acento)
        }

        val bandera = contexto.texto(idioma.bandera, 24f, tema.texto, Typeface.NORMAL)
        fila.addView(bandera, LinearLayout.LayoutParams(contexto.dp(38f), contexto.dp(34f)))

        val nombre = contexto.texto(
            idioma.nombre,
            15f,
            if (idioma == idiomaActual) tema.acento else tema.texto,
            if (idioma == idiomaActual) Typeface.BOLD else Typeface.NORMAL
        )
        fila.addView(nombre, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))

        if (idioma == idiomaActual) {
            fila.addView(contexto.texto("✓", 16f, tema.acento, Typeface.BOLD))
        }

        fila.setOnClickListener {
            dialogo?.dismiss()
            onElegir(idioma)
        }

        lista.addView(fila, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(2f) })
    }

    contenedor.addView(lista, LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    ))

    dialogo = AlertDialog.Builder(contexto).setView(contenedor).create()
    dialogo.show()
    dialogo.window?.setBackgroundDrawable(
        forma(contexto, tema.superficie, 26f, tema.borde, 1f)
    )
    val ancho = minOf(contexto.dp(340f), contexto.resources.displayMetrics.widthPixels - contexto.dp(32f))
    dialogo.window?.setLayout(ancho, ViewGroup.LayoutParams.WRAP_CONTENT)
}
