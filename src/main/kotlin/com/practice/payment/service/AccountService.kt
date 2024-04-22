package com.practice.payment.service

import com.practice.payment.adapter.AccountAdapter
import com.practice.payment.adapter.UseBalanceRequest
import com.practice.payment.exception.ErrorCode.ORDER_NOT_FOUND
import com.practice.payment.exception.PaymentException
import com.practice.payment.repository.OrderRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountAdapter: AccountAdapter,
    private val orderRepository: OrderRepository
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
}