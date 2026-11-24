package com.ejemplo.calculator

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView

internal class PantallaDatos(
    private val contexto: Context,
    private val tema: Tema,
    private val titulo: String,
    private val subtitulo: String,
    private val grupos: List<GrupoDatos>
) {
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

        val hero = crearHero()
        columna.addView(hero, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(18f) })
        hero.alpha = 0f
        hero.animate().alpha(1f).setDuration(400).start()

        var retraso = 80L
        var primerGrupo = true
        for (grupo in grupos) {
            val encabezado = LinearLayout(contexto)
            encabezado.orientation = LinearLayout.HORIZONTAL
            encabezado.gravity = Gravity.CENTER_VERTICAL
            val tituloGrupo = contexto.texto(Textos.tr(grupo.titulo).uppercase(), 11f, tema.acento, Typeface.BOLD)
            tituloGrupo.letterSpacing = 0.18f
            encabezado.addView(tituloGrupo)

            val linea = View(contexto)
            linea.background = degradado(
                contexto,
                colorConAlfa(tema.acento, 140),
                colorConAlfa(tema.acento2, 0),
                1f
            )
            val paramsLinea = LinearLayout.LayoutParams(0, contexto.dp(1f), 1f)
            paramsLinea.marginStart = contexto.dp(12f)
            encabezado.addView(linea, paramsLinea)

            val paramsEncabezado = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            paramsEncabezado.topMargin = contexto.dp(if (primerGrupo) 0f else 18f)
            paramsEncabezado.bottomMargin = contexto.dp(8f)
            columna.addView(encabezado, paramsEncabezado)
            primerGrupo = false

            for (dato in grupo.datos) {
                val tarjeta = crearDato(dato)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.bottomMargin = contexto.dp(8f)
                tarjeta.alpha = 0f
                columna.addView(tarjeta, params)
                tarjeta.aparecer(retraso)
                retraso = (retraso + 28L).coerceAtMost(620L)
            }
        }

        return scroll
    }

    private fun crearHero(): View {
        val caja = LinearLayout(contexto)
        caja.orientation = LinearLayout.VERTICAL
        caja.background = degradado(contexto, tema.acento, tema.acento2, 26f)
        caja.elevation = contexto.dp(4f).toFloat()
        caja.setPadding(contexto.dp(20f), contexto.dp(18f), contexto.dp(20f), contexto.dp(18f))

        val etiqueta = contexto.texto(Textos.tr("СПРАВОЧНИК"), 10f, colorConAlfa(tema.sobreAcento, 190), Typeface.BOLD)
        etiqueta.letterSpacing = 0.24f
        caja.addView(etiqueta)

        val tituloVista = contexto.texto(titulo, 27f, tema.sobreAcento, Typeface.BOLD)
        tituloVista.setPadding(0, contexto.dp(4f), 0, 0)
        caja.addView(tituloVista)

        val subVista = contexto.texto(subtitulo, 13f, colorConAlfa(tema.sobreAcento, 200))
        subVista.setPadding(0, contexto.dp(6f), 0, 0)
        subVista.maxLines = 3
        caja.addView(subVista)
        return caja
    }

    private fun crearDato(dato: Dato): View {
        val tarjeta = LinearLayout(contexto)
        tarjeta.orientation = LinearLayout.HORIZONTAL
        tarjeta.background = forma(contexto, tema.superficie, 18f, tema.borde, 1f)
        tarjeta.elevation = contexto.dp(1.5f).toFloat()
        tarjeta.setPadding(contexto.dp(14f), contexto.dp(12f), contexto.dp(14f), contexto.dp(12f))
        tarjeta.gravity = Gravity.CENTER_VERTICAL

        val barra = View(contexto)
        barra.background = degradado(contexto, tema.acento, tema.acento2, 2f)
        val paramsBarra = LinearLayout.LayoutParams(contexto.dp(4f), contexto.dp(38f))
        paramsBarra.marginEnd = contexto.dp(12f)
        tarjeta.addView(barra, paramsBarra)

        val columna = LinearLayout(contexto)
        columna.orientation = LinearLayout.VERTICAL

        val tituloVista = contexto.texto(Textos.tr(dato.titulo), 13f, tema.textoSuave)
        columna.addView(tituloVista)

        val valorVista = contexto.texto(dato.valor, 17f, tema.texto, Typeface.BOLD)
        valorVista.setPadding(0, contexto.dp(2f), 0, 0)
        columna.addView(valorVista)

        if (dato.nota.isNotEmpty()) {
            val notaVista = contexto.texto(Textos.tr(dato.nota), 11f, colorConAlfa(tema.textoSuave, 200))
            notaVista.setPadding(0, contexto.dp(3f), 0, 0)
            columna.addView(notaVista)
        }

        tarjeta.addView(columna, LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ))
        return tarjeta
    }
}
