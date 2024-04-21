package com.practice.payment

enum class OrderStatus {
    CREATED,
    FAILED,
    PAID,
    CANCELED,
    PARTIAL_REFUNDED,
    REFUNDED
}