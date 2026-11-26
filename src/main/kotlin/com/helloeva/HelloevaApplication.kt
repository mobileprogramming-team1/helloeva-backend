package com.helloeva

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HelloevaApplication

fun main(args: Array<String>) {
    runApplication<HelloevaApplication>(*args)
}
