package com.xos.personalsystem.core.alarm

import kotlin.random.Random

class MathChallengeGenerator {
    
    private val operators = listOf('+', '-', '×')
    
    fun generateQuestions(count: Int): List<Quadruple<Int, Char, Int, Int>> {
        val questions = mutableListOf<Quadruple<Int, Char, Int, Int>>()
        
        repeat(count) {
            questions.add(generateQuestion())
        }
        
        return questions
    }
    
    private fun generateQuestion(): Quadruple<Int, Char, Int, Int> {
        val operator = operators.random()
        var num1: Int
        var num2: Int
        var answer: Int
        
        when (operator) {
            '+' -> {
                num1 = Random.nextInt(1, 20)
                num2 = Random.nextInt(1, 20)
                answer = num1 + num2
            }
            '-' -> {
                num1 = Random.nextInt(10, 30)
                num2 = Random.nextInt(1, num1)
                answer = num1 - num2
            }
            '×' -> {
                num1 = Random.nextInt(2, 12)
                num2 = Random.nextInt(2, 12)
                answer = num1 * num2
            }
            else -> {
                num1 = 0
                num2 = 0
                answer = 0
            }
        }
        
        return Quadruple(num1, operator, num2, answer)
    }
}

data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
