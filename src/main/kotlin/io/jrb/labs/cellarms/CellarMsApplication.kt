package io.jrb.labs.cellarms

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CellarMsApplication

fun main(args: Array<String>) {
	runApplication<CellarMsApplication>(*args)
}
