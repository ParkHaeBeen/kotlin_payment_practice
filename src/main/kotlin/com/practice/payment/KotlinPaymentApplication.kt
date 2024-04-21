package com.practice.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinPaymentApplication

fun main(args: Array<String>) {
	runApplication<KotlinPaymentApplication>(*args)
}
