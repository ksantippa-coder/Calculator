package com.ejemplo.calculator

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import kotlin.math.PI
import kotlin.math.sqrt

internal data class MedidaGeometrica(val nombre: String)

internal data class FiguraGeometrica(
    val id: String,
    val nombre: String,
    val medidas: List<MedidaGeometrica>,
    val formula: String,
    val calcular: (DoubleArray) -> Double
)

internal object FigurasGeometricas {

    val nombresUnidad = listOf("мм", "см", "м")

    fun sufijoArea(unidad: Int): String = Textos.tr(nombresUnidad[unidad]) + "²"
    fun sufijoVolumen(unidad: Int): String = Textos.tr(nombresUnidad[unidad]) + "³"

    val areas: List<FiguraGeometrica> = listOf(
        FiguraGeometrica(
            "rectangulo", "Прямоугольник",
            listOf(MedidaGeometrica("Длина a"), MedidaGeometrica("Ширина b")),
            "S = a · b"
        ) { v -> v[0] * v[1] },
        FiguraGeometrica(
            "cuadrado", "Квадрат",
            listOf(MedidaGeometrica("Сторона a")),
            "S = a²"
        ) { v -> v[0] * v[0] },
        FiguraGeometrica(
            "circulo", "Круг",
            listOf(MedidaGeometrica("Радиус r")),
            "S = π · r²"
        ) { v -> PI * v[0] * v[0] },
        FiguraGeometrica(
            "triangulo", "Треугольник",
            listOf(MedidaGeometrica("Основание a"), MedidaGeometrica("Высота h")),
            "S = a · h / 2"
        ) { v -> v[0] * v[1] / 2.0 },
        FiguraGeometrica(
            "trapecio", "Трапеция",
            listOf(
                MedidaGeometrica("Основание a"),
                MedidaGeometrica("Основание b"),
                MedidaGeometrica("Высота h")
            ),
            "S = (a + b) · h / 2"
        ) { v -> (v[0] + v[1]) * v[2] / 2.0 },
        FiguraGeometrica(
            "paralelogramo", "Параллелограмм",
            listOf(MedidaGeometrica("Сторона a"), MedidaGeometrica("Высота h")),
            "S = a · h"
        ) { v -> v[0] * v[1] },
        FiguraGeometrica(
            "rombo", "Ромб",
            listOf(MedidaGeometrica("Диагональ d₁"), MedidaGeometrica("Диагональ d₂")),
            "S = d₁ · d₂ / 2"
        ) { v -> v[0] * v[1] / 2.0 },
        FiguraGeometrica(
            "elipse", "Эллипс",
            listOf(MedidaGeometrica("Полуось a"), MedidaGeometrica("Полуось b")),
            "S = π · a · b"
        ) { v -> PI * v[0] * v[1] },
        FiguraGeometrica(
            "sector", "Сектор круга",
            listOf(MedidaGeometrica("Радиус r"), MedidaGeometrica("Угол α, °")),
            "S = π · r² · α / 360"
        ) { v -> PI * v[0] * v[0] * v[1] / 360.0 }
    )

    val volumenes: List<FiguraGeometrica> = listOf(
        FiguraGeometrica(
            "cubo", "Куб",
            listOf(MedidaGeometrica("Ребро a")),
            "V = a³"
        ) { v -> v[0] * v[0] * v[0] },
        FiguraGeometrica(
            "prisma", "Параллелепипед",
            listOf(
                MedidaGeometrica("Длина a"),
                MedidaGeometrica("Ширина b"),
                MedidaGeometrica("Высота c")
            ),
            "V = a · b · c"
        ) { v -> v[0] * v[1] * v[2] },
        FiguraGeometrica(
            "cilindro", "Цилиндр",
            listOf(MedidaGeometrica("Радиус r"), MedidaGeometrica("Высота h")),
            "V = π · r² · h"
        ) { v -> PI * v[0] * v[0] * v[1] },
        FiguraGeometrica(
            "cono", "Конус",
            listOf(MedidaGeometrica("Радиус r"), MedidaGeometrica("Высота h")),
            "V = π · r² · h / 3"
        ) { v -> PI * v[0] * v[0] * v[1] / 3.0 },
        FiguraGeometrica(
            "esfera", "Шар",
            listOf(MedidaGeometrica("Радиус r")),
            "V = 4/3 · π · r³"
        ) { v -> 4.0 / 3.0 * PI * v[0] * v[0] * v[0] },
        FiguraGeometrica(
            "piramide", "Пирамида",
            listOf(MedidaGeometrica("Сторона основания a"), MedidaGeometrica("Высота h")),
            "V = a² · h / 3"
        ) { v -> v[0] * v[0] * v[1] / 3.0 },
        FiguraGeometrica(
            "tetraedro", "Тетраэдр",
            listOf(MedidaGeometrica("Ребро a")),
            "V = a³ / (6√2)"
        ) { v -> v[0] * v[0] * v[0] / (6.0 * sqrt(2.0)) },
        FiguraGeometrica(
            "cono_truncado", "Усечённый конус",
            listOf(
                MedidaGeometrica("Радиус r₁"),
                MedidaGeometrica("Радиус r₂"),
                MedidaGeometrica("Высота h")
            ),
            "V = π · h · (r₁² + r₁r₂ + r₂²) / 3"
        ) { v -> PI * v[2] * (v[0] * v[0] + v[0] * v[1] + v[1] * v[1]) / 3.0 },
        FiguraGeometrica(
            "toro", "Тор",
            listOf(MedidaGeometrica("Большой радиус R"), MedidaGeometrica("Малый радиус r")),
            "V = 2π² · R · r²"
        ) { v -> 2.0 * PI * PI * v[0] * v[1] * v[1] }
    )
}

internal class EstadoFiguras {
    var categoria: Int = 0
    var unidad: Int = 2
    val seleccion: IntArray = intArrayOf(0, 0)

    private val entradas = mutableMapOf<String, MutableList<String>>()

    fun valores(figura: FiguraGeometrica): MutableList<String> =
        entradas.getOrPut(figura.id) { MutableList(figura.medidas.size) { "" } }

    fun limpiar(figura: FiguraGeometrica) {
        entradas[figura.id] = MutableList(figura.medidas.size) { "" }
    }
}

internal class PantallaFiguras(
    private val contexto: Context,
    private val tema: Tema,
    private val estado: EstadoFiguras
) {
    private lateinit var scroll: ScrollView
    private lateinit var columna: LinearLayout
    private lateinit var vistaResultado: TextView
    private lateinit var vistaSufijo: TextView
    private lateinit var vistaFormulaResultado: TextView
    private lateinit var figuraActual: FiguraGeometrica

    fun crear(): View {
        scroll = ScrollView(contexto)
        scroll.isFillViewport = true
        scroll.setBackgroundColor(Color.TRANSPARENT)

        columna = LinearLayout(contexto)
        columna.orientation = LinearLayout.VERTICAL
        columna.setPadding(
            contexto.dp(16f), contexto.dp(6f), contexto.dp(16f), contexto.dp(28f)
        )
        scroll.addView(columna, android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        ))

        montar(false)
        return scroll
    }

    private fun montar(animar: Boolean) {
        val lista = if (estado.categoria == 0) FigurasGeometricas.areas else FigurasGeometricas.volumenes
        val indice = estado.seleccion[estado.categoria].coerceIn(0, lista.lastIndex)
        estado.seleccion[estado.categoria] = indice
        figuraActual = lista[indice]

        columna.removeAllViews()

        columna.addView(crearCategorias(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(10f) })

        columna.addView(crearUnidades(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(10f) })

        columna.addView(crearFiguras(lista, indice), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(12f) })

        columna.addView(crearEntradas(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { bottomMargin = contexto.dp(12f) })

        columna.addView(crearResultado(), LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        val nota = contexto.texto(
            Textos.tr("Введите размеры в выбранных единицах — результат будет в них же, в квадрате или кубе."),
            12f,
            tema.textoSuave
        )
        nota.setPadding(contexto.dp(4f), contexto.dp(14f), contexto.dp(4f), 0)
        columna.addView(nota)

        recalcular()

        if (animar) {
            columna.alpha = 0f
            columna.animate().alpha(1f).setDuration(170).start()
        }
    }

    private fun crearCategorias(): View {
        val fila = LinearLayout(contexto)
        fila.orientation = LinearLayout.HORIZONTAL
        val nombres = listOf(Textos.tr("Площадь"), Textos.tr("Объём"))
        for (i in nombres.indices) {
            val activo = estado.categoria == i
            val chip = contexto.texto(nombres[i], 14f, tema.textoSuave, Typeface.BOLD)
            chip.gravity = Gravity.CENTER
            chip.setPadding(0, contexto.dp(11f), 0, contexto.dp(11f))
            chip.isClickable = true
            chip.isFocusable = true
            chip.animarPulsacion()
            chip.background = if (activo) {
                conOnda(contexto, degradado(contexto, tema.acento, tema.acento2, 16f), tema.acento)
            } else {
                conOnda(contexto, forma(contexto, tema.superficieAlt, 16f, tema.borde, 1f), tema.acento)
            }
            chip.setTextColor(if (activo) tema.sobreAcento else tema.textoSuave)
            chip.setOnClickListener {
                if (estado.categoria != i) {
                    estado.categoria = i
                    montar(true)
                }
            }
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            params.marginStart = contexto.dp(3f)
            params.marginEnd = contexto.dp(3f)
            fila.addView(chip, params)
        }
        return fila
    }

    private fun crearUnidades(): View {
        val fila = LinearLayout(contexto)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.gravity = Gravity.CENTER_VERTICAL

        val etiqueta = contexto.texto(Textos.tr("ЕДИНИЦА"), 10f, tema.textoSuave, Typeface.BOLD)
        etiqueta.letterSpacing = 0.2f
        val paramsEtiqueta = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        paramsEtiqueta.marginEnd = contexto.dp(10f)
        fila.addView(etiqueta, paramsEtiqueta)

        for (i in FigurasGeometricas.nombresUnidad.indices) {
            val activo = estado.unidad == i
            val chip = contexto.texto(
                Textos.tr(FigurasGeometricas.nombresUnidad[i]),
                12f,
                if (activo) tema.acento else tema.textoSuave,
                Typeface.BOLD
            )
            chip.gravity = Gravity.CENTER
            chip.setPadding(
                contexto.dp(14f), contexto.dp(7f), contexto.dp(14f), contexto.dp(7f)
            )
            chip.isClickable = true
            chip.isFocusable = true
            chip.animarPulsacion()
            chip.background = if (activo) {
                conOnda(
                    contexto,
                    forma(contexto, colorConAlfa(tema.acento, 28), 12f, tema.acento, 1f),
                    tema.acento
                )
            } else {
                conOnda(
                    contexto,
                    forma(contexto, tema.superficieAlt, 12f, tema.borde, 1f),
                    tema.acento
                )
            }
            chip.setOnClickListener {
                if (estado.unidad != i) {
                    estado.unidad = i
                    montar(true)
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = contexto.dp(6f)
            fila.addView(chip, params)
        }
        return fila
    }

    private fun crearFiguras(lista: List<FiguraGeometrica>, seleccionada: Int): View {
        val desplazable = HorizontalScrollView(contexto)
        desplazable.isHorizontalScrollBarEnabled = false
        desplazable.clipToPadding = false

        val fila = LinearLayout(contexto)
        fila.orientation = LinearLayout.HORIZONTAL
        val chips = mutableListOf<TextView>()

        for (i in lista.indices) {
            val activo = i == seleccionada
            val chip = contexto.texto(
                Textos.tr(lista[i].nombre),
                13f,
                if (activo) tema.sobreAcento else tema.textoSuave,
                Typeface.BOLD
            )
            chip.setPadding(
                contexto.dp(13f), contexto.dp(9f), contexto.dp(13f), contexto.dp(9f)
            )
            chip.isClickable = true
            chip.isFocusable = true
            chip.animarPulsacion()
            chip.background = if (activo) {
                conOnda(contexto, degradado(contexto, tema.acento, tema.acento2, 13f), tema.acento)
            } else {
                conOnda(contexto, forma(contexto, tema.superficieAlt, 13f, tema.borde, 1f), tema.acento)
            }
            chip.setOnClickListener {
                if (estado.seleccion[estado.categoria] != i) {
                    estado.seleccion[estado.categoria] = i
                    montar(true)
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = contexto.dp(6f)
            fila.addView(chip, params)
            chips.add(chip)
        }

        desplazable.addView(fila)
        desplazable.post {
            val objetivo = chips.getOrNull(seleccionada) ?: return@post
            val destino = (objetivo.left - contexto.dp(10f)).coerceAtLeast(0)
            desplazable.smoothScrollTo(destino, 0)
        }
        return desplazable
    }

    private fun crearEntradas(): View {
        val caja = contexto.tarjeta(tema, 18f, 24f)

        val cabecera = LinearLayout(contexto)
        cabecera.orientation = LinearLayout.HORIZONTAL
        cabecera.gravity = Gravity.CENTER_VERTICAL

        val titulo = contexto.texto(Textos.tr(figuraActual.nombre), 17f, tema.texto, Typeface.BOLD)
        cabecera.addView(titulo, LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        ))

        val limpiar = contexto.texto(Textos.tr("Очистить"), 12f, tema.acento, Typeface.BOLD)
        limpiar.setPadding(contexto.dp(9f), contexto.dp(6f), contexto.dp(9f), contexto.dp(6f))
        limpiar.background = conOnda(
            contexto,
            forma(contexto, colorConAlfa(tema.acento, 22), 10f, tema.acento, 1f),
            tema.acento
        )
        limpiar.isClickable = true
        limpiar.isFocusable = true
        limpiar.animarPulsacion()
        limpiar.setOnClickListener {
            estado.limpiar(figuraActual)
            montar(false)
        }
        cabecera.addView(limpiar)

        caja.addView(cabecera)

        val formula = contexto.texto(figuraActual.formula, 12f, tema.textoSuave)
        formula.setPadding(0, contexto.dp(2f), 0, contexto.dp(10f))
        caja.addView(formula)

        val valores = estado.valores(figuraActual)

        for (i in figuraActual.medidas.indices) {
            val fila = LinearLayout(contexto)
            fila.orientation = LinearLayout.HORIZONTAL
            fila.gravity = Gravity.CENTER_VERTICAL

            val etiqueta = contexto.texto(Textos.tr(figuraActual.medidas[i].nombre), 13f, tema.textoSuave)
            fila.addView(etiqueta, LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ))

            val campo = EditText(contexto)
            campo.background = null
            campo.hint = "0"
            campo.setText(valores.getOrNull(i).orEmpty())
            campo.setTextColor(tema.texto)
            campo.setHintTextColor(colorConAlfa(tema.textoSuave, 140))
            campo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22f)
            campo.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
            campo.gravity = Gravity.END
            campo.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            campo.setPadding(0, contexto.dp(6f), 0, contexto.dp(6f))
            campo.setSelection(campo.text.length)
            campo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (i < valores.size) valores[i] = s?.toString().orEmpty()
                    recalcular()
                }
            })
            fila.addView(campo, LinearLayout.LayoutParams(
                contexto.dp(130f),
                LinearLayout.LayoutParams.WRAP_CONTENT
            ))
            caja.addView(fila)

            if (i < figuraActual.medidas.lastIndex) {
                val divisor = View(contexto)
                divisor.setBackgroundColor(colorConAlfa(tema.borde, 200))
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    contexto.dp(1f)
                )
                params.topMargin = contexto.dp(2f)
                params.bottomMargin = contexto.dp(2f)
                caja.addView(divisor, params)
            }
        }

        return caja
    }

    private fun crearResultado(): View {
        val caja = LinearLayout(contexto)
        caja.orientation = LinearLayout.VERTICAL
        caja.background = degradado(contexto, tema.acento, tema.acento2, 26f)
        caja.elevation = contexto.dp(4f).toFloat()
        caja.setPadding(
            contexto.dp(20f), contexto.dp(18f), contexto.dp(20f), contexto.dp(18f)
        )

        val etiqueta = contexto.texto(
            Textos.tr(if (estado.categoria == 0) "ПЛОЩАДЬ" else "ОБЪЁМ"),
            10f,
            colorConAlfa(tema.sobreAcento, 190),
            Typeface.BOLD
        )
        etiqueta.letterSpacing = 0.22f
        caja.addView(etiqueta)

        val fila = LinearLayout(contexto)
        fila.orientation = LinearLayout.HORIZONTAL
        fila.gravity = Gravity.BOTTOM

        vistaResultado = contexto.texto(
            "—", 36f, tema.sobreAcento, Typeface.NORMAL, "sans-serif-light"
        )
        fila.addView(vistaResultado)

        vistaSufijo = contexto.texto("", 18f, colorConAlfa(tema.sobreAcento, 210), Typeface.BOLD)
        vistaSufijo.setPadding(contexto.dp(6f), 0, 0, contexto.dp(4f))
        fila.addView(vistaSufijo)

        caja.addView(fila)

        vistaFormulaResultado = contexto.texto(
            figuraActual.formula,
            12f,
            colorConAlfa(tema.sobreAcento, 185)
        )
        vistaFormulaResultado.setPadding(0, contexto.dp(8f), 0, 0)
        caja.addView(vistaFormulaResultado)

        return caja
    }

    private fun recalcular() {
        if (!::vistaResultado.isInitialized) return
        val valores = estado.valores(figuraActual)
        val numeros = DoubleArray(figuraActual.medidas.size)
        var completo = true

        for (i in numeros.indices) {
            val numero = valores.getOrNull(i)
                ?.trim()
                ?.replace(',', '.')
                ?.toDoubleOrNull()
            if (numero == null) {
                completo = false
                break
            }
            numeros[i] = numero
        }

        vistaFormulaResultado.text = figuraActual.formula

        if (!completo) {
            vistaResultado.text = "—"
            vistaSufijo.text = ""
            return
        }

        val salida = figuraActual.calcular(numeros)
        if (salida.isNaN() || salida.isInfinite() || salida < 0.0) {
            vistaResultado.text = Textos.tr("Ошибка")
            vistaSufijo.text = ""
            return
        }

        val texto = formatearNumero(salida)
        vistaResultado.text = texto
        vistaSufijo.text = if (estado.categoria == 0) {
            FigurasGeometricas.sufijoArea(estado.unidad)
        } else {
            FigurasGeometricas.sufijoVolumen(estado.unidad)
        }
        vistaResultado.setTextSize(
            TypedValue.COMPLEX_UNIT_SP,
            when {
                texto.length > 14 -> 24f
                texto.length > 10 -> 30f
                else -> 36f
            }
        )
    }
}
