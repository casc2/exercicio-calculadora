package br.ufpe.cin.if710.calculadora

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

//    Variáveis para armazenamento da expressão e resultado na mudança de configuração.
    private var persistedExpression = ""
    private var persistedResult = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Propaga as informações salvas na mudança de configuração para os respectivos campos.
        text_info.text = savedInstanceState?.getString(persistedResult)
        text_calc.setText(savedInstanceState?.getString(persistedExpression))

//        Associando listener para cada botão da calculadora.
        btn_0.setOnClickListener {
            text_calc.append(btn_0.text.toString())
        }

        btn_1.setOnClickListener {
            text_calc.append(btn_1.text.toString())
        }

        btn_2.setOnClickListener {
            text_calc.append(btn_2.text.toString())
        }

        btn_3.setOnClickListener {
            text_calc.append(btn_3.text.toString())
        }

        btn_4.setOnClickListener {
            text_calc.append(btn_4.text.toString())
        }

        btn_5.setOnClickListener {
            text_calc.append(btn_5.text.toString())
        }

        btn_6.setOnClickListener {
            text_calc.append(btn_6.text.toString())
        }

        btn_7.setOnClickListener {
            text_calc.append(btn_7.text.toString())
        }

        btn_8.setOnClickListener {
            text_calc.append(btn_8.text.toString())
        }

        btn_9.setOnClickListener {
            text_calc.append(btn_9.text.toString())
        }

        btn_Dot.setOnClickListener {
            text_calc.append(btn_Dot.text.toString())
        }

        btn_LParen.setOnClickListener {
            text_calc.append(btn_LParen.text.toString())
        }

        btn_RParen.setOnClickListener {
            text_calc.append(btn_RParen.text.toString())
        }

        btn_Add.setOnClickListener {
            text_calc.append(btn_Add.text.toString())
        }

        btn_Subtract.setOnClickListener {
            text_calc.append(btn_Subtract.text.toString())
        }

        btn_Divide.setOnClickListener {
            text_calc.append(btn_Divide.text.toString())
        }

        btn_Multiply.setOnClickListener {
            text_calc.append(btn_Multiply.text.toString())
        }

        btn_Power.setOnClickListener {
            text_calc.append(btn_Power.text.toString())
        }

        /*
        *   Avalia a expressão ao clicar no botão "=".
        *
        *   É feita uma tentativa de computação da expressão aritmética.
        *   Em caso de sucesso, o resultado é exibido em "text_info" e o campo "text_calc" é limpo.
        *   Em caso de falha, o campo "text_calc" não é limpo e um toast é exibido  com a mensagem:
        *   "Expressão mal formada".
        */
        btn_Equal.setOnClickListener {
            val result: Double
            try {
                result = eval(text_calc.text.toString())
            } catch (e: RuntimeException) {
                val msg = "Expressão mal formada"
//                Criação do toast.
                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            text_info.text = result.toString()
            text_calc.text.clear()
        }

//        O botão "C" valida primeiro a existência da expressão.
//        Em caso positivo, consome um caractere de "text_calc", do contrário, não faz nada.
        btn_Clear.setOnClickListener {
            val textCalcString = text_calc.text.toString()
            if (textCalcString.isNotEmpty()) {
                text_calc.setText(textCalcString.substring(0, textCalcString.length - 1))
            }
        }
    }

    //Como usar a função:
    // eval("2+2") == 4.0
    // eval("2+3*4") = 14.0
    // eval("(2+3)*4") = 20.0
    //Fonte: https://stackoverflow.com/a/26227947
    fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch: Char = ' '
            fun nextChar() {
                val size = str.length
                ch = if ((++pos < size)) str.get(pos) else (-1).toChar()
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Caractere inesperado: " + ch)
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            // | number | functionName factor | factor `^` factor
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'))
                        x += parseTerm() // adição
                    else if (eat('-'))
                        x -= parseTerm() // subtração
                    else
                        return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'))
                        x *= parseFactor() // multiplicação
                    else if (eat('/'))
                        x /= parseFactor() // divisão
                    else
                        return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor() // + unário
                if (eat('-')) return -parseFactor() // - unário
                var x: Double
                val startPos = this.pos
                if (eat('(')) { // parênteses
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') { // números
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                } else if (ch in 'a'..'z') { // funções
                    while (ch in 'a'..'z') nextChar()
                    val func = str.substring(startPos, this.pos)
                    x = parseFactor()
                    if (func == "sqrt")
                        x = Math.sqrt(x)
                    else if (func == "sin")
                        x = Math.sin(Math.toRadians(x))
                    else if (func == "cos")
                        x = Math.cos(Math.toRadians(x))
                    else if (func == "tan")
                        x = Math.tan(Math.toRadians(x))
                    else
                        throw RuntimeException("Função desconhecida: " + func)
                } else {
                    throw RuntimeException("Caractere inesperado: " + ch.toChar())
                }
                if (eat('^')) x = Math.pow(x, parseFactor()) // potência
                return x
            }
        }.parse()
    }

//        Persistindo a equação e resultado quando ocorrer mudança de configuração.
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString(persistedExpression, text_calc.text.toString())
        outState?.putString(persistedResult, text_info.text.toString())
        super.onSaveInstanceState(outState)
    }
}
