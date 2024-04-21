package com.practice.payment.service

import com.practice.payment.OrderStatus
import com.practice.payment.TransactionStatus.RESERVE
import com.practice.payment.TransactionType.PAYMENT
import com.practice.payment.domain.Order
import com.practice.payment.domain.OrderTransaction
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
}