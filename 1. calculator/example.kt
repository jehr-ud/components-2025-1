class Calculator {

    fun add(a: Double, b: Double): Double {
        return a + b
    }

    fun subtract(a: Double, b: Double): Double {
        return a - b
    }

    fun multiply(a: Double, b: Double): Double {
        return a * b
    }

    fun divide(a: Double, b: Double): Double {
        if (b == 0.0) {
            throw IllegalArgumentException("Cannot divide by zero")
        }
        return a / b
    }

    fun power(base: Double, exponent: Double): Double {
        return Math.pow(base, exponent)
    }

    fun squareRoot(a: Double): Double {
        if (a < 0.0) {
            throw IllegalArgumentException("Cannot calculate square root of a negative number")
        }
        return Math.sqrt(a)
    }

    fun modulo(a: Int, b: Int): Int {
        if (b == 0) {
            throw IllegalArgumentException("Cannot calculate modulo with divisor zero")
        }
        return a % b
    }

}

fun main() {
    val calculator = Calculator()

    println("5 + 3 = ${calculator.add(5.0, 3.0)}")
    println("10 - 4 = ${calculator.subtract(10.0, 4.0)}")
    println("2 * 6 = ${calculator.multiply(2.0, 6.0)}")
    println("8 / 2 = ${calculator.divide(8.0, 2.0)}")
    println("2^3 = ${calculator.power(2.0, 3.0)}")
    println("√9 = ${calculator.squareRoot(9.0)}")
    println("10 % 3 = ${calculator.modulo(10,3)}")

    try {
        println("10 / 0 = ${calculator.divide(10.0, 0.0)}")
    } catch (e: IllegalArgumentException) {
        println("Error: ${e.message}")
    }

    try{
        println("√-1 = ${calculator.squareRoot(-1.0)}")
    } catch (e: IllegalArgumentException){
        println("Error: ${e.message}")
    }

    try{
        println("10 % 0 = ${calculator.modulo(10,0)}")
    } catch (e: IllegalArgumentException){
        println("Error: ${e.message}")
    }
}