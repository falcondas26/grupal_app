package com.example.calculadora

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    private lateinit var tvOperacion: TextView
    private lateinit var tvResultado: TextView

    private var primerNumero: Double? = null
    private var operacionActual: String? = null
    private var esNuevaEntrada: Boolean = true
    private var operacionTerminada: Boolean = false

    private val formatoDecimal = DecimalFormat("#.########")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvOperacion = findViewById(R.id.tvOperacion)
        tvResultado = findViewById(R.id.tvResultado)

        configurarBotonesNumericos()
        configurarBotonesAccion()
        configurarOperacionSuma()
        configurarOperacionResta()
        configurarOperacionMultiplicacion()
        configurarOperacionDivision()
    }

    private fun configurarBotonesNumericos() {
        val botonesNumeros = mapOf(
            R.id.btn0 to "0",
            R.id.btn1 to "1",
            R.id.btn2 to "2",
            R.id.btn3 to "3",
            R.id.btn4 to "4",
            R.id.btn5 to "5",
            R.id.btn6 to "6",
            R.id.btn7 to "7",
            R.id.btn8 to "8",
            R.id.btn9 to "9"
        )

        for ((id, valor) in botonesNumeros) {
            findViewById<Button>(id).setOnClickListener {
                agregarDigito(valor)
            }
        }

        findViewById<Button>(R.id.btnPunto).setOnClickListener {
            agregarPunto()
        }
    }

    private fun agregarDigito(digito: String) {
        if (operacionTerminada) {
            limpiarCalculadora()
            operacionTerminada = false
        }

        val textoActual = tvResultado.text.toString()
        if (esNuevaEntrada || textoActual == "0" || textoActual == "Error") {
            tvResultado.text = digito
            esNuevaEntrada = false
        } else {
            tvResultado.append(digito)
        }
    }

    private fun agregarPunto() {
        if (operacionTerminada) {
            limpiarCalculadora()
            operacionTerminada = false
        }

        val textoActual = tvResultado.text.toString()
        if (esNuevaEntrada || textoActual == "Error") {
            tvResultado.text = "0."
            esNuevaEntrada = false
        } else if (!textoActual.contains(".")) {
            tvResultado.append(".")
        }
    }

    private fun configurarBotonesAccion() {
        findViewById<Button>(R.id.btnLimpiar).setOnClickListener {
            limpiarCalculadora()
        }

        findViewById<Button>(R.id.btnBorrar).setOnClickListener {
            borrarUltimoDigito()
        }
    }

    private fun limpiarCalculadora() {
        tvResultado.text = "0"
        tvOperacion.text = ""
        primerNumero = null
        operacionActual = null
        esNuevaEntrada = true
        operacionTerminada = false
    }

    private fun borrarUltimoDigito() {
        if (operacionTerminada) {
            return
        }

        val textoActual = tvResultado.text.toString()
        if (!esNuevaEntrada && textoActual.isNotEmpty() && textoActual != "0" && textoActual != "Error") {
            if (textoActual.length == 1) {
                tvResultado.text = "0"
                esNuevaEntrada = true
            } else {
                tvResultado.text = textoActual.substring(0, textoActual.length - 1)
            }
        }
    }

    private fun configurarOperacionSuma() {
        findViewById<Button>(R.id.btnSumar).setOnClickListener {
            prepararOperacion("+")
        }

        findViewById<Button>(R.id.btnIgual).setOnClickListener {
            ejecutarIgual()
        }
    }

    private fun configurarOperacionResta() {
        findViewById<Button>(R.id.btnRestar).setOnClickListener {
            prepararOperacion("−")
        }
    }

    private fun configurarOperacionMultiplicacion() {
        findViewById<Button>(R.id.btnMultiplicar).setOnClickListener {
            prepararOperacion("×")
        }
    }

    private fun configurarOperacionDivision() {
        findViewById<Button>(R.id.btnDividir).setOnClickListener {
            prepararOperacion("÷")
        }
    }

    private fun prepararOperacion(operador: String) {
        operacionTerminada = false

        if (primerNumero != null && operacionActual != null && !esNuevaEntrada) {
            val segundoNumero = tvResultado.text.toString().toDoubleOrNull() ?: return
            val resultadoParcial = calcularResultado(primerNumero!!, segundoNumero, operacionActual!!)
            if (resultadoParcial == null) {
                mostrarErrorDivision()
                return
            }
            primerNumero = resultadoParcial
            tvResultado.text = formatoDecimal.format(resultadoParcial)
        } else if (primerNumero == null) {
            val valorActual = tvResultado.text.toString().toDoubleOrNull() ?: return
            primerNumero = valorActual
        }

        operacionActual = operador
        tvOperacion.text = "${formatoDecimal.format(primerNumero!!)} $operador"
        esNuevaEntrada = true
    }

    private fun ejecutarIgual() {
        if (primerNumero == null || operacionActual == null || esNuevaEntrada) {
            return
        }

        val segundoNumero = tvResultado.text.toString().toDoubleOrNull() ?: return
        val num1 = primerNumero!!
        val op = operacionActual!!

        val resultado = calcularResultado(num1, segundoNumero, op)
        if (resultado == null) {
            mostrarErrorDivision()
            return
        }

        tvOperacion.text = "${formatoDecimal.format(num1)} $op ${formatoDecimal.format(segundoNumero)} ="
        tvResultado.text = formatoDecimal.format(resultado)

        primerNumero = resultado
        operacionActual = null
        esNuevaEntrada = true
        operacionTerminada = true
    }

    private fun mostrarErrorDivision() {
        tvOperacion.text = "${formatoDecimal.format(primerNumero ?: 0.0)} ÷ 0 ="
        tvResultado.text = "Error"
        primerNumero = null
        operacionActual = null
        esNuevaEntrada = true
        operacionTerminada = true
    }

    private fun calcularResultado(a: Double, b: Double, op: String): Double? {
        return when (op) {
            "+" -> sumar(a, b)
            "−" -> restar(a, b)
            "×" -> multiplicar(a, b)
            "÷" -> {
                if (b == 0.0) null else dividir(a, b)
            }
            else -> b
        }
    }

    private fun sumar(a: Double, b: Double): Double {
        return a + b
    }

    private fun restar(a: Double, b: Double): Double {
        return a - b
    }

    private fun multiplicar(a: Double, b: Double): Double {
        return a * b
    }

    private fun dividir(a: Double, b: Double): Double {
        return a / b
    }
}
