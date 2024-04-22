package com.practice.payment.repository

import com.practice.payment.TransactionType
import com.practice.payment.domain.Order
import com.practice.payment.domain.OrderTransaction
import com.practice.payment.domain.PaymentUser
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentUserRepository : JpaRepository<PaymentUser, Long> {

    fun findByPayUserId(payUserId: String): PaymentUser?
}

interface OrderRepository : JpaRepository<Order, Long> {

}

interface OrderTransactionRepository : JpaRepository<OrderTransaction, Long> {
    fun findByOrderAndTransactionType(
        order: Order,
        transactionType: TransactionType
    ): List<OrderTransaction>

    fun findByTransactionId(transactionId: String): OrderTransaction?
}