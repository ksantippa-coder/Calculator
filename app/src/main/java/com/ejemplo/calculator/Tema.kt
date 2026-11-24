package com.ejemplo.calculator

import android.graphics.Color

/**
 * Una paleta completa. La interfaz se dibuja por código para poder recolorear
 * absolutamente todo (fondo, tarjetas, teclas, degradados) al cambiar de tema.
 */
internal data class Tema(
    val nombre: String,
    val oscuro: Boolean,
    val fondo: Int,
    val fondoAlt: Int,
    val superficie: Int,
    val superficieAlt: Int,
    val texto: Int,
    val textoSuave: Int,
    val acento: Int,
    val acento2: Int,
    val borde: Int,
    val tecla: Int,
    val teclaTexto: Int,
    val operador: Int,
    val operadorTexto: Int,
    val sobreAcento: Int
) {
    fun esClaro(): Boolean = !oscuro

    fun colorDestello(): Int =
        if (oscuro) Color.rgb(246, 214, 142) else Color.rgb(176, 132, 52)
}

internal object Temas {
    val todos: List<Tema> = listOf(
        // ── Cinco paletas claras ────────────────────────────────────────────
        Tema(
            nombre = "Aurora",
            oscuro = false,
            fondo = 0xFFF4F7FF.toInt(),
            fondoAlt = 0xFFE7EEFF.toInt(),
            superficie = 0xFFFFFFFF.toInt(),
            superficieAlt = 0xFFF1F5FF.toInt(),
            texto = 0xFF101322.toInt(),
            textoSuave = 0xFF5F6880.toInt(),
            acento = 0xFF6C5CE7.toInt(),
            acento2 = 0xFF00CEC9.toInt(),
            borde = 0xFFDCE3F5.toInt(),
            tecla = 0xFFFFFFFF.toInt(),
            teclaTexto = 0xFF1A1F33.toInt(),
            operador = 0xFFEDEBFF.toInt(),
            operadorTexto = 0xFF6C5CE7.toInt(),
            sobreAcento = 0xFFFFFFFF.toInt()
        ),
        Tema(
            nombre = "Sakura",
            oscuro = false,
            fondo = 0xFFFFF5F7.toInt(),
            fondoAlt = 0xFFFFE7EE.toInt(),
            superficie = 0xFFFFFFFF.toInt(),
            superficieAlt = 0xFFFFF0F4.toInt(),
            texto = 0xFF2A1420.toInt(),
            textoSuave = 0xFF8A6675.toInt(),
            acento = 0xFFFF4D8D.toInt(),
            acento2 = 0xFFFF9E6D.toInt(),
            borde = 0xFFFFD6E2.toInt(),
            tecla = 0xFFFFFFFF.toInt(),
            teclaTexto = 0xFF3A1B28.toInt(),
            operador = 0xFFFFE3EC.toInt(),
            operadorTexto = 0xFFE03A78.toInt(),
            sobreAcento = 0xFFFFFFFF.toInt()
        ),
        Tema(
            nombre = "Solar",
            oscuro = false,
            fondo = 0xFFFFFBF2.toInt(),
            fondoAlt = 0xFFFFF1D6.toInt(),
            superficie = 0xFFFFFFFF.toInt(),
            superficieAlt = 0xFFFFF7E6.toInt(),
            texto = 0xFF241A0B.toInt(),
            textoSuave = 0xFF8A7452.toInt(),
            acento = 0xFFF39C12.toInt(),
            acento2 = 0xFFFF6B35.toInt(),
            borde = 0xFFF5E3C2.toInt(),
            tecla = 0xFFFFFFFF.toInt(),
            teclaTexto = 0xFF2B1F0E.toInt(),
            operador = 0xFFFFEBCB.toInt(),
            operadorTexto = 0xFFD97A0A.toInt(),
            sobreAcento = 0xFF2A1804.toInt()
        ),
        Tema(
            nombre = "Glaciar",
            oscuro = false,
            fondo = 0xFFF2FAFF.toInt(),
            fondoAlt = 0xFFDFF2FF.toInt(),
            superficie = 0xFFFFFFFF.toInt(),
            superficieAlt = 0xFFEDF8FF.toInt(),
            texto = 0xFF0B1E2A.toInt(),
            textoSuave = 0xFF5A7585.toInt(),
            acento = 0xFF0FA3E0.toInt(),
            acento2 = 0xFF5BC8AF.toInt(),
            borde = 0xFFCDE9F7.toInt(),
            tecla = 0xFFFFFFFF.toInt(),
            teclaTexto = 0xFF0E2A38.toInt(),
            operador = 0xFFDDF3FF.toInt(),
            operadorTexto = 0xFF0B86BA.toInt(),
            sobreAcento = 0xFFFFFFFF.toInt()
        ),
        Tema(
            nombre = "Lima",
            oscuro = false,
            fondo = 0xFFF6FBEF.toInt(),
            fondoAlt = 0xFFE6F6D2.toInt(),
            superficie = 0xFFFFFFFF.toInt(),
            superficieAlt = 0xFFF1FAE4.toInt(),
            texto = 0xFF14210B.toInt(),
            textoSuave = 0xFF61774C.toInt(),
            acento = 0xFF6FBE2A.toInt(),
            acento2 = 0xFF00B894.toInt(),
            borde = 0xFFD9EDBF.toInt(),
            tecla = 0xFFFFFFFF.toInt(),
            teclaTexto = 0xFF1B2B10.toInt(),
            operador = 0xFFE6F7CF.toInt(),
            operadorTexto = 0xFF4F9A15.toInt(),
            sobreAcento = 0xFF102008.toInt()
        ),

        // ── Cinco paletas oscuras ───────────────────────────────────────────
        Tema(
            nombre = "Medianoche",
            oscuro = true,
            fondo = 0xFF070B18.toInt(),
            fondoAlt = 0xFF0C1226.toInt(),
            superficie = 0xFF111A33.toInt(),
            superficieAlt = 0xFF172142.toInt(),
            texto = 0xFFEAF0FF.toInt(),
            textoSuave = 0xFF8B97B8.toInt(),
            acento = 0xFF5B8CFF.toInt(),
            acento2 = 0xFF9B6BFF.toInt(),
            borde = 0xFF22305A.toInt(),
            tecla = 0xFF131D3A.toInt(),
            teclaTexto = 0xFFEAF0FF.toInt(),
            operador = 0xFF1B2A55.toInt(),
            operadorTexto = 0xFF7FA6FF.toInt(),
            sobreAcento = 0xFFFFFFFF.toInt()
        ),
        Tema(
            nombre = "Neón",
            oscuro = true,
            fondo = 0xFF050508.toInt(),
            fondoAlt = 0xFF0B0714.toInt(),
            superficie = 0xFF12101F.toInt(),
            superficieAlt = 0xFF1A1630.toInt(),
            texto = 0xFFF4F0FF.toInt(),
            textoSuave = 0xFF9A8FC0.toInt(),
            acento = 0xFFFF2FB9.toInt(),
            acento2 = 0xFF22E1FF.toInt(),
            borde = 0xFF2A2147.toInt(),
            tecla = 0xFF161228.toInt(),
            teclaTexto = 0xFFF4F0FF.toInt(),
            operador = 0xFF241A3F.toInt(),
            operadorTexto = 0xFFFF6FD0.toInt(),
            sobreAcento = 0xFFFFFFFF.toInt()
        ),
        Tema(
            nombre = "Obsidiana",
            oscuro = true,
            fondo = 0xFF0B0B0D.toInt(),
            fondoAlt = 0xFF131316.toInt(),
            superficie = 0xFF17171B.toInt(),
            superficieAlt = 0xFF202026.toInt(),
            texto = 0xFFF2EDE4.toInt(),
            textoSuave = 0xFF9A948A.toInt(),
            acento = 0xFFE8B04B.toInt(),
            acento2 = 0xFFF0D08A.toInt(),
            borde = 0xFF2B2B33.toInt(),
            tecla = 0xFF1A1A1F.toInt(),
            teclaTexto = 0xFFF2EDE4.toInt(),
            operador = 0xFF26221A.toInt(),
            operadorTexto = 0xFFE8B04B.toInt(),
            sobreAcento = 0xFF1A1206.toInt()
        ),
        Tema(
            nombre = "Abismo",
            oscuro = true,
            fondo = 0xFF03110F.toInt(),
            fondoAlt = 0xFF061C19.toInt(),
            superficie = 0xFF0A2420.toInt(),
            superficieAlt = 0xFF0F3029.toInt(),
            texto = 0xFFE4FFF7.toInt(),
            textoSuave = 0xFF79A79B.toInt(),
            acento = 0xFF17E0B0.toInt(),
            acento2 = 0xFF2FA8FF.toInt(),
            borde = 0xFF15443A.toInt(),
            tecla = 0xFF0D2B26.toInt(),
            teclaTexto = 0xFFE4FFF7.toInt(),
            operador = 0xFF113B33.toInt(),
            operadorTexto = 0xFF45F0C6.toInt(),
            sobreAcento = 0xFF03211B.toInt()
        ),
        Tema(
            nombre = "Vulcano",
            oscuro = true,
            fondo = 0xFF120608.toInt(),
            fondoAlt = 0xFF1D0A0E.toInt(),
            superficie = 0xFF241015.toInt(),
            superficieAlt = 0xFF2E161C.toInt(),
            texto = 0xFFFFEDE9.toInt(),
            textoSuave = 0xFFB08A86.toInt(),
            acento = 0xFFFF5A3C.toInt(),
            acento2 = 0xFFFFB02E.toInt(),
            borde = 0xFF45202A.toInt(),
            tecla = 0xFF2A1218.toInt(),
            teclaTexto = 0xFFFFEDE9.toInt(),
            operador = 0xFF3A1820.toInt(),
            operadorTexto = 0xFFFF8A70.toInt(),
            sobreAcento = 0xFFFFFFFF.toInt()
        )
    )
}
