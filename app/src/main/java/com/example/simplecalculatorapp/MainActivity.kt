package com.example.simplecalculatorapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.simplecalculatorapp.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastNumeric = false
    private var stateError = false
    private var lastDot = false
    private lateinit var expression: Expression

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    public fun onAllClearClick(view: View) {
        binding.dataTV.text = ""
        binding.resultTV.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false
        binding.resultTV.visibility = View.GONE
    }

    public fun onBracesClick(view: View) {
        val currentText = binding.dataTV.text.toString()
        val openBracesCount = currentText.count { it == '(' }
        val closeBracesCount = currentText.count { it == ')' }

        if (openBracesCount > closeBracesCount) {
            if (currentText.isEmpty() || !currentText.last().isOperator() && !currentText.last()
                    .isDigit()
            ) {
                binding.dataTV.append(")")
            } else {
                binding.dataTV.append(")")
            }
        } else {
            if (currentText.isEmpty() || !currentText.last().isOperator() && !currentText.last()
                    .isDigit()
            ) {
                binding.dataTV.append("(")
            } else {
                binding.dataTV.append("(")
            }
        }
        lastNumeric = false
        stateError = false
    }

    private fun Char.isOperator(): Boolean {
        return this == '+' || this == '-' || this == '*' || this == '/'
    }

    private fun onEqual() {
        if (lastNumeric && !stateError) {
            val txt = binding.dataTV.text.toString()
            expression = ExpressionBuilder(txt).build()

            try {
                val result = expression.evaluate()
                binding.resultTV.visibility = View.VISIBLE
                binding.resultTV.text = result.toString()

            } catch (ex: ArithmeticException) {
                Log.e("evaluate error", ex.toString())
                binding.resultTV.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

    public fun onBackClick(view: View) {
        binding.dataTV.text = binding.dataTV.text.toString().dropLast(1)

        try {
            val lastChar = binding.dataTV.text.toString().last()
            if (lastChar.isDigit()) {
                onEqual()
            }
        } catch (e: Exception) {
            binding.resultTV.text = ""
            binding.resultTV.visibility = View.GONE
            Log.e("last char error", e.toString())
        }
    }

    public fun onOperatorClick(view: View) {
        if (!stateError) {
            val currentText = binding.dataTV.text.toString()
            if (currentText.isEmpty()) {
                // Do not allow operator at the start
            } else if (currentText.last() == '(') {
                binding.dataTV.append((view as Button).text)
            } else if (currentText.last() == ')') {
                binding.dataTV.append((view as Button).text)
            } else if (currentText.last().isOperator()) {
                binding.dataTV.text = currentText.dropLast(1) + (view as Button).text
            } else {
                binding.dataTV.append((view as Button).text)
            }
            lastNumeric = false
            lastDot = false
            stateError = false
        } else {
            // If there's an error, reset to allow a fresh start
            binding.dataTV.text = ""
            binding.resultTV.text = ""
            lastNumeric = false
            lastDot = false
            stateError = false
        }
    }

    public fun onEqualClick(view: View) {
        if (lastNumeric && !stateError) {
            val txt = binding.dataTV.text.toString()
            var expressionString = txt

            // Evaluate expressions inside parentheses first
            while (expressionString.contains("(") && expressionString.contains(")")) {
                val start = expressionString.indexOf("(")
                val end = expressionString.indexOf(")")
                val innerExpression = expressionString.substring(start + 1, end)
                val innerResult = ExpressionBuilder(innerExpression).build().evaluate()
                expressionString = expressionString.replace("($innerExpression)", innerResult.toString())
            }

            // Now evaluate the entire expression
            expression = ExpressionBuilder(expressionString).build()

            try {
                val result = expression.evaluate()
                binding.resultTV.visibility = View.VISIBLE
                binding.resultTV.text = result.toString()

            } catch (ex: ArithmeticException) {
                Log.e("evaluate error", ex.toString())
                binding.resultTV.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

    public fun onDigitClick(view: View) {

        if (stateError) {
            binding.dataTV.text = (view as Button).text
            stateError = false
            lastNumeric = true
        } else if (binding.dataTV.text.toString() == binding.resultTV.text.toString()) {
            binding.dataTV.text = (view as Button).text
            lastNumeric = true
        } else {
            val currentText = binding.dataTV.text.toString()
            if (currentText.isEmpty()) {
                binding.dataTV.append((view as Button).text)
            } else {
                if (currentText.last() == '.') {
                    // Do not allow another . to be appended
                } else if (currentText.last() == '(') {
                    binding.dataTV.append((view as Button).text)
                } else {
                    binding.dataTV.append((view as Button).text)
                }
            }
            lastNumeric = true
        }
    }
}
