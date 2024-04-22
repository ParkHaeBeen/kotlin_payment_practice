package com.practice.payment.service

import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PaymentService(
    private val paymentStatusService: PaymentStatusService,
    private val accountService: AccountService,
) {

    fun pay(
        payServiceRequest: PayServiceRequest
    ): PayServiceResponse {
        //요청 저장
        val orderId = paymentStatusService.savePayRequest(
            payUserId = payServiceRequest.payUserId,
            amount = payServiceRequest.amount,
            orderTitle = payServiceRequest.orderTitle,
            merchantTransactionId = payServiceRequest.merchantTransactionId
        )

        //계좡에 금액 사용 요청
        val payMethodTransactionId = accountService.useAccount(orderId)

        //성공: 거래 성공으로 저장
        val (transactionId, transactedAt) = paymentStatusService.saveAsSuccess(
            orderId,
            payMethodTransactionId
        )

        //실패: 거래를 실패로 저장

        return PayServiceResponse(
            payUserId = payServiceRequest.payUserId,
            amount = payServiceRequest.amount,
            transactionId = transactionId,
            transactedAt = transactedAt
        )
    }
}

data class PayServiceResponse(
    val payUserId: String,
    val amount: Long,
    val transactionId: String,
    val transactedAt: LocalDateTime
)

data class PayServiceRequest(
    val payUserId: String,
    val amount: Long,
    val merchantTransactionId: String,
    val orderTitle: String,
)