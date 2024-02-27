package com.example.demo.meetphoto

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun test_when(){
        var a = false
        var b = true
        when {
            a -> {
                System.out.println("a")
            }

            b -> {
                System.out.println("b")
            }

            else -> {
                System.out.println("c")
            }
        }
    }
}