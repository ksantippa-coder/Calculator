package com.ejemplo.calculator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.Typeface
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Fondo vivo: fórmulas y glifos matemáticos que derivan lentamente, una red
 * de puntos conectados y destellos dorados. La idea es la pizarra mental de
 * «Una mente maravillosa», pero muy tenue para no competir con la interfaz.
 */
internal class FondoMatematico(contexto: Context) : View(contexto) {

    private var tema: Tema = Temas.todos[0]
    private val inicio = System.nanoTime()

    private val pincelFondo = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pincelTexto = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pincelLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }
    private val pincelNodo = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pincelGlow = Paint(Paint.ANTI_ALIAS_FLAG)

    private val shaderFondo: LinearGradient?
        get() = fondoActual
    private var fondoActual: LinearGradient? = null
    private var glowA: RadialGradient? = null
    private var glowB: RadialGradient? = null

    private var ancho = 0f
    private var alto = 0f

    private val formulas = listOf(
        "e^{iπ} + 1 = 0",
        "E = mc²",
        "F = m · a",
        "∇ × B = μ₀J + μ₀ε₀ ∂E/∂t",
        "π = 3,14159265…",
        "φ = (1 + √5) / 2",
        "∫ e^(−x²) dx = √π",
        "a² + b² = c²",
        "λ = h / p",
        "Gμν = 8πG Tμν / c⁴",
        "iℏ ∂ψ/∂t = Ĥψ",
        "Σ 1/n² = π²/6",
        "x = (−b ± √(b² − 4ac)) / 2a",
        "sin²θ + cos²θ = 1",
        "Δx · Δp ≥ ℏ/2",
        "∂²u/∂t² = c² ∇²u",
        "√2 = 1,41421356…",
        "e = 2,71828182…"
    )

    private val glifosGrandes = listOf("π", "∑", "∞", "√", "φ", "Δ", "∫", "θ")

    private val destellos = 26
    private val nodos = 15

    init {
        pincelTexto.typeface = Typeface.create("serif", Typeface.NORMAL)
        setLayerType(LAYER_TYPE_HARDWARE, null)
    }

    fun aplicarTema(nuevo: Tema) {
        tema = nuevo
        crearShaders()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        ancho = w.toFloat()
        alto = h.toFloat()
        crearShaders()
    }

    private fun crearShaders() {
        if (ancho <= 0f || alto <= 0f) return
        fondoActual = LinearGradient(
            0f, 0f, ancho, alto,
            intArrayOf(tema.fondo, tema.fondoAlt, tema.fondo),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
        glowA = crearGlow(tema.acento, 300f, 62)
        glowB = crearGlow(tema.acento2, 330f, 48)
    }

    private fun crearGlow(color: Int, radio: Float, alfa: Int): RadialGradient {
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return RadialGradient(
            0f, 0f, radio,
            intArrayOf(
                Color.argb(alfa, r, g, b),
                Color.argb(alfa / 3, r, g, b),
                Color.argb(0, r, g, b)
            ),
            floatArrayOf(0f, 0.55f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (ancho <= 0f || alto <= 0f) return
        val t = (System.nanoTime() - inicio) / 1_000_000_000.0

        val shader = shaderFondo
        if (shader != null) {
            pincelFondo.shader = shader
            canvas.drawRect(0f, 0f, ancho, alto, pincelFondo)
        } else {
            canvas.drawColor(tema.fondo)
        }

        dibujarGlows(canvas, t)
        dibujarRed(canvas, t)
        dibujarFormulas(canvas, t)
        dibujarDestellos(canvas, t)

        postInvalidateOnAnimation()
    }

    private fun dibujarGlows(canvas: Canvas, t: Double) {
        val escala = (min(ancho, alto) / 760f).coerceIn(0.8f, 2.4f)
        val a = glowA
        if (a != null) {
            pincelGlow.shader = a
            val x = ancho * 0.5f + cos(t * 0.065).toFloat() * ancho * 0.36f
            val y = alto * 0.26f + sin(t * 0.052).toFloat() * alto * 0.16f
            canvas.save()
            canvas.translate(x, y)
            canvas.scale(escala, escala)
            canvas.drawCircle(0f, 0f, 300f, pincelGlow)
            canvas.restore()
        }
        val b = glowB
        if (b != null) {
            pincelGlow.shader = b
            val x = ancho * 0.5f + cos(t * 0.047 + 2.1).toFloat() * ancho * 0.42f
            val y = alto * 0.74f + sin(t * 0.071 + 1.0).toFloat() * alto * 0.18f
            canvas.save()
            canvas.translate(x, y)
            canvas.scale(escala, escala)
            canvas.drawCircle(0f, 0f, 330f, pincelGlow)
            canvas.restore()
        }
    }

    private fun dibujarRed(canvas: Canvas, t: Double) {
        val centroX = ancho / 2f
        val centroY = alto / 2f
        val minimo = min(ancho, alto)
        val xs = FloatArray(nodos)
        val ys = FloatArray(nodos)

        for (i in 0 until nodos) {
            val fase = i * 2.399963
            val radio = minimo * (0.16f + 0.34f * ((i * 37 % 100) / 100f))
            val angulo = t * 0.035 * (1.0 + (i % 3) * 0.22) + fase
            xs[i] = centroX + cos(angulo).toFloat() * radio * 1.35f
            ys[i] = centroY + sin(angulo * 0.83 + i * 1.7).toFloat() * radio
        }

        val distanciaMaxima = minimo * 0.30f
        pincelLinea.strokeWidth = resources.displayMetrics.density * 0.8f
        for (i in 0 until nodos) {
            for (j in i + 1 until nodos) {
                val dx = xs[i] - xs[j]
                val dy = ys[i] - ys[j]
                val distancia = kotlin.math.sqrt(dx * dx + dy * dy)
                if (distancia < distanciaMaxima) {
                    val alfa = ((1f - distancia / distanciaMaxima) * 46f).toInt()
                    pincelLinea.color = colorConAlfa(tema.acento2, alfa)
                    canvas.drawLine(xs[i], ys[i], xs[j], ys[j], pincelLinea)
                }
            }
        }

        val radioNodo = resources.displayMetrics.density * 1.6f
        val radioHalo = resources.displayMetrics.density * 8f
        for (i in 0 until nodos) {
            pincelNodo.color = colorConAlfa(tema.acento, 22)
            canvas.drawCircle(xs[i], ys[i], radioHalo, pincelNodo)
            pincelNodo.color = colorConAlfa(tema.acento2, 110)
            canvas.drawCircle(xs[i], ys[i], radioNodo, pincelNodo)
        }
    }

    private fun dibujarFormulas(canvas: Canvas, t: Double) {
        val densidad = resources.displayMetrics.scaledDensity
        val recorrido = alto + 260f * resources.displayMetrics.density
        pincelTexto.color = colorConAlfa(
            if (tema.oscuro) tema.acento2 else tema.acento,
            1
        )

        for (i in formulas.indices) {
            val fase = i * 1.37
            val tamano = (12f + (i * 7 % 13)) * densidad
            pincelTexto.textSize = tamano
            val alfa = (18 + 18 * ((sin(t * 0.31 + fase) + 1) / 2)).toInt()
            pincelTexto.color = colorConAlfa(if (tema.oscuro) tema.acento2 else tema.acento, alfa)

            val baseX = (((i * 0.61803398875) % 1.0) * (ancho + 220f * resources.displayMetrics.density).toDouble() -
                110.0 * resources.displayMetrics.density.toDouble()).toFloat()
            val baseY = (((i * 0.38196601125) % 1.0) * recorrido.toDouble()).toFloat()
            var x = baseX + sin(t * 0.14 + fase).toFloat() * 26f * resources.displayMetrics.density
            var y = (baseY - t.toFloat() * (7f + (i % 5) * 3f) * resources.displayMetrics.density) % recorrido
            if (y < -130f * resources.displayMetrics.density) y += recorrido
            val texto = formulas[i]
            val anchoTexto = pincelTexto.measureText(texto)
            if (x + anchoTexto > ancho) x = ancho - anchoTexto
            if (x < -40f * resources.displayMetrics.density) x = -40f * resources.displayMetrics.density

            canvas.save()
            canvas.rotate(
                (sin(t * 0.09 + fase) * 5.0).toFloat(),
                x + anchoTexto / 2f,
                y
            )
            canvas.drawText(texto, x, y, pincelTexto)
            canvas.restore()
        }

        for (i in glifosGrandes.indices) {
            val fase = i * 2.11
            val tamano = (70f + (i * 29 % 90)) * densidad
            pincelTexto.textSize = tamano
            val alfa = (8 + 7 * ((sin(t * 0.17 + fase) + 1) / 2)).toInt()
            pincelTexto.color = colorConAlfa(if (tema.oscuro) tema.acento else tema.acento2, alfa)
            val x = (((i * 0.754877666) % 1.0) * (ancho - 90f).toDouble()).toFloat()
            val y = (((i * 0.569840291) % 1.0) * (alto - 90f).toDouble() + 70.0).toFloat() +
                sin(t * 0.12 + fase).toFloat() * 18f
            canvas.drawText(glifosGrandes[i], x, y, pincelTexto)
        }
    }

    private fun dibujarDestellos(canvas: Canvas, t: Double) {
        val densidad = resources.displayMetrics.density
        for (i in 0 until destellos) {
            val fase = i * 1.913
            val x = (((i * 0.61803398875 + 0.17) % 1.0) * ancho.toDouble()).toFloat() +
                sin(t * 0.11 + fase).toFloat() * 22f * densidad
            val recorrido = alto + 80f * densidad
            var y = (((i * 0.41421356237) % 1.0) * recorrido.toDouble()).toFloat() -
                t.toFloat() * (3f + (i % 4) * 2.4f) * densidad
            y %= recorrido
            if (y < -40f * densidad) y += recorrido

            val pulso = (sin(t * 0.9 + fase) + 1) / 2
            val alfa = (28 + 80 * pulso).toInt()
            val color = tema.colorDestello()

            pincelNodo.color = colorConAlfa(color, (alfa / 5).coerceAtLeast(4))
            canvas.drawCircle(x, y, 7f * densidad, pincelNodo)
            pincelNodo.color = colorConAlfa(color, alfa)
            canvas.drawCircle(x, y, (0.9f + 0.9f * pulso.toFloat()) * densidad, pincelNodo)
        }
    }
}
