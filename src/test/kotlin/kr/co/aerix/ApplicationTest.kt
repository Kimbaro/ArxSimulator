package kr.co.aerix

import io.ktor.features.*
import org.slf4j.event.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.jackson.*
import com.fasterxml.jackson.databind.*
import io.ktor.application.*
import io.ktor.response.*
import kotlin.test.*
import io.ktor.server.testing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.aerix.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() {

    }
    @Test
    fun test() {
        class Counter {
            private var current=0
            private var max=0
            fun addOne() {
                synchronized(this) {
                    current++
                    max = Math.max(max,current)

                }
            }
            fun subOne() {
                synchronized(this) {
                    current--
                }
            }
            fun getMax() = max
        }

        val counter = Counter()

        val jobs = (0..500).map {
            CoroutineScope(Dispatchers.IO).launch {
                counter.addOne()
                Thread.sleep(100)
                counter.subOne()
            }
        }
        var done = false
        while(!done) {
            val j = jobs.find{it.isActive}
            if( j == null) {
                done = true
            } else {
                Thread.sleep(100)
            }
        }

        println("max = ${counter.getMax()}")
    }
}