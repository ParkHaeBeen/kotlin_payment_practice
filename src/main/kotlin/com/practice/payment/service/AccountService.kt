package com.practice.payment.service

import com.practice.payment.TransactionType
import com.practice.payment.adapter.AccountAdapter
import com.practice.payment.adapter.CancelBalanceRequest
import com.practice.payment.adapter.UseBalanceRequest
import com.practice.payment.exception.ErrorCode.INTERNAL_SERVER_ERROR
import com.practice.payment.exception.ErrorCode.ORDER_NOT_FOUND
import com.practice.payment.exception.PaymentException
import com.practice.payment.repository.OrderRepository
import com.practice.payment.repository.OrderTransactionRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountAdapter: AccountAdapter,
    private val orderRepository: OrderRepository,
    private val orderTransactionRepository: OrderTransactionRepository
) {

    @Transactional
    fun useAccount(orderId: Long): String {
        val order = orderRepository.findById(orderId)
            .orElseThrow { throw PaymentException(ORDER_NOT_FOUND) }

        return accountAdapter.useAccount(
            UseBalanceRequest(
                userId = order.paymentUser.accountUserId,
                accountNumber = order.paymentUser.accountNumber,
                amount = order.paidAmount
            )
        ).transactionId
    }

    @Transactional
    fun cancelUseAccount(refundTxId: Long): String {
        val refundTransaction = orderTransactionRepository.findById((refundTxId))
            .orElseThrow {
                throw PaymentException(INTERNAL_SERVER_ERROR)
            }

        val order = refundTransaction.order
        val paymentTransaction = orderTransactionRepository.findByOrderAndTransactionType(
            order, TransactionType.PAYMENT
        ).first()

        return accountAdapter.cancelUseAccount(
            CancelBalanceRequest(
                transactionId = paymentTransaction.payMethodTransactionId ?: throw PaymentException(
                    INTERNAL_SERVER_ERROR
                ),
                accountNumber = order.paymentUser.accountNumber,
                amount = refundTransaction.transactionAmount
            )
        ).transactionId
    }
}