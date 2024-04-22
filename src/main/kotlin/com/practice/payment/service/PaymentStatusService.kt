package com.practice.payment.service

import com.practice.payment.OrderStatus
import com.practice.payment.TransactionStatus.*
import com.practice.payment.TransactionType.PAYMENT
import com.practice.payment.domain.Order
import com.practice.payment.domain.OrderTransaction
import com.practice.payment.exception.ErrorCode
import com.practice.payment.exception.ErrorCode.INTERNAL_SERVER_ERROR
import com.practice.payment.exception.ErrorCode.INVALID_REQUEST
import com.practice.payment.exception.PaymentException
import com.practice.payment.repository.OrderRepository
import com.practice.payment.repository.OrderTransactionRepository
import com.practice.payment.repository.PaymentUserRepository
import com.practice.payment.util.generateOrderId
import com.practice.payment.util.generateTransactionId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/*
*  결제의 요청 저장, 성공, 실패 저장
* */
@Service
class PaymentStatusService(
    private val paymentUserRepository: PaymentUserRepository,
    private val orderRepository: OrderRepository,
    private val orderTransactionRepository: OrderTransactionRepository
) {

    @Transactional
    fun savePayRequest(
        payUserId: String,
        amount: Long,
        orderTitle: String,
        merchantTransactionId: String
    ): Long {
        val paymentUser = paymentUserRepository.findByPayUserId(payUserId)
            ?: throw PaymentException(INVALID_REQUEST, "사용자 없음 : $payUserId")

        val order = orderRepository.save(
            Order(
                orderId = generateOrderId(),
                paymentUser = paymentUser,
                orderStatus = OrderStatus.CREATED,
                orderTitle = orderTitle,
                orderAmount = amount
            )
        )

        orderTransactionRepository.save(
            OrderTransaction(
                transactionId = generateTransactionId(),
                order = order,
                transactionType = PAYMENT,
                transactionStatus = RESERVE,
                transactionAmount = amount,
                merchantTransactionId = merchantTransactionId,
                description = orderTitle

            )
        )

        return order.id ?: throw PaymentException(INTERNAL_SERVER_ERROR)
    }

    @Transactional
    fun saveAsSuccess(orderId: Long, payMethodTransactionId: String): Pair<String, LocalDateTime> {
        val order = getOrderByOrderId(orderId)
            .apply {
                orderStatus = OrderStatus.PAID
                paidAmount = orderAmount
            }

        val orderTransaction = getOrderTransactionByOrder(order).apply {
            transactionStatus = SUCCESS
            this.payMethodTransactionId = payMethodTransactionId
            transactedAt = LocalDateTime.now()
        }

        return Pair(
            orderTransaction.transactionId,
            orderTransaction.transactedAt ?: throw PaymentException(INTERNAL_SERVER_ERROR)
        )
    }

    fun saveAsFailure(orderId: Long, errorCode: ErrorCode) {
        val order = getOrderByOrderId(orderId)
            .apply {
                orderStatus = OrderStatus.FAILED
            }

        val orderTransaction = getOrderTransactionByOrder(order).apply {
            transactionStatus = FAILURE
            failureCode = errorCode.name
            description = errorCode.errorMessage
        }

    }

    private fun getOrderTransactionByOrder(order: Order) =
        orderTransactionRepository.findByOrderAndTransactionType(
            order = order,
            transactionType = PAYMENT
        ).first()

    private fun getOrderByOrderId(orderId: Long): Order =
        orderRepository.findById(orderId)
            .orElseThrow { throw PaymentException(ErrorCode.ORDER_NOT_FOUND) }
}