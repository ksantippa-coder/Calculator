package com.ejemplo.calculator

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import kotlin.math.abs

internal fun Context.dp(valor: Float): Int =
    (valor * resources.displayMetrics.density + 0.5f).toInt()

internal fun Context.sp(valor: Float): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, valor, resources.displayMetrics)

internal fun colorConAlfa(color: Int, alfa: Int): Int =
    Color.argb(alfa.coerceIn(0, 255), Color.red(color), Color.green(color), Color.blue(color))

internal fun forma(
    contexto: Context,
    color: Int,
    radioDp: Float,
    borde: Int? = null,
    grosorDp: Float = 1f
): GradientDrawable {
    val dibujo = GradientDrawable()
    dibujo.shape = GradientDrawable.RECTANGLE
    dibujo.setColor(color)
    dibujo.cornerRadius = contexto.dp(radioDp).toFloat()
    if (borde != null) dibujo.setStroke(contexto.dp(grosorDp), borde)
    return dibujo
}

internal fun degradado(contexto: Context, desde: Int, hasta: Int, radioDp: Float): GradientDrawable {
    val dibujo = GradientDrawable(GradientDrawable.Orientation.TL_BR, intArrayOf(desde, hasta))
    dibujo.cornerRadius = contexto.dp(radioDp).toFloat()
    return dibujo
}

internal fun circulo(color: Int, borde: Int? = null): GradientDrawable {
    val dibujo = GradientDrawable()
    dibujo.shape = GradientDrawable.OVAL
    dibujo.setColor(color)
    if (borde != null) dibujo.setStroke(1, borde)
    return dibujo
}

internal fun conOnda(contexto: Context, fondo: GradientDrawable, colorOnda: Int): RippleDrawable =
    RippleDrawable(ColorStateList.valueOf(colorConAlfa(colorOnda, 64)), fondo, null)

internal fun Context.texto(
    contenido: String,
    tamanoSp: Float,
    color: Int,
    peso: Int = Typeface.NORMAL,
    fuente: String = "sans-serif"
): TextView {
    val vista = TextView(this)
    vista.text = contenido
    vista.setTextColor(color)
    vista.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamanoSp)
    vista.typeface = Typeface.create(fuente, peso)
    vista.includeFontPadding = false
    return vista
}

internal fun Context.tarjeta(tema: Tema, rellenoDp: Float = 18f, radioDp: Float = 22f): android.widget.LinearLayout {
    val caja = android.widget.LinearLayout(this)
    caja.orientation = android.widget.LinearLayout.VERTICAL
    caja.background = forma(this, tema.superficie, radioDp, tema.borde, 1f)
    caja.elevation = dp(2f).toFloat()
    caja.setPadding(dp(rellenoDp), dp(rellenoDp), dp(rellenoDp), dp(rellenoDp))
    return caja
}

internal fun View.animarPulsacion() {
    setOnTouchListener { vista, evento ->
        when (evento.actionMasked) {
            MotionEvent.ACTION_DOWN ->
                vista.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start()
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL ->
                vista.animate().scaleX(1f).scaleY(1f).setDuration(150).start()
        }
        false
    }
}

internal fun View.aparecer(retrasoMs: Long) {
    alpha = 0f
    translationY = resources.displayMetrics.density * 12f
    animate()
        .alpha(1f)
        .translationY(0f)
        .setStartDelay(retrasoMs)
        .setDuration(320)
        .start()
}

/** Da formato legible a un número: agrupa millares y usa notación científica cuando hace falta. */
internal fun formatearNumero(valor: Double): String {
    if (valor.isNaN()) return Textos.tr("Ошибка")
    if (valor.isInfinite()) return if (valor > 0) "∞" else "−∞"
    val absoluto = abs(valor)
    if (absoluto != 0.0 && (absoluto >= 1e12 || absoluto < 1e-7)) {
        val crudo = String.format(Locale.US, "%.4e", valor)
        val partes = crudo.split("e")
        val mantisa = BigDecimal(partes[0]).stripTrailingZeros().toPlainString()
        val exponente = partes[1].toInt()
        return "${mantisa.replace('-', '−')} × 10${superindice(exponente)}"
    }
    val negativo = valor < 0
    val redondeado = BigDecimal.valueOf(abs(valor))
        .setScale(10, RoundingMode.HALF_UP)
        .stripTrailingZeros()
    val entero = redondeado.toBigInteger().toString()
    val conGrupos = agrupar(entero)
    val decimales = redondeado.subtract(BigDecimal(redondeado.toBigInteger()))
    var cuerpo = conGrupos
    if (decimales.compareTo(BigDecimal.ZERO) != 0) {
        val parteDecimal = decimales.toPlainString().removePrefix("0.").trimEnd('0')
        if (parteDecimal.isNotEmpty()) cuerpo = "$conGrupos.$parteDecimal"
    }
    return if (negativo && cuerpo != "0") "−$cuerpo" else cuerpo
}

private fun agrupar(entero: String): String {
    val sb = StringBuilder()
    for ((indice, caracter) in entero.withIndex()) {
        if (indice > 0 && (entero.length - indice) % 3 == 0) sb.append('\u00A0')
        sb.append(caracter)
    }
    return sb.toString()
}

private fun superindice(numero: Int): String {
    val mapa = mapOf(
        '-' to '⁻', '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹'
    )
    val sb = StringBuilder()
    for (caracter in numero.toString()) sb.append(mapa[caracter] ?: caracter)
    return sb.toString()
}
