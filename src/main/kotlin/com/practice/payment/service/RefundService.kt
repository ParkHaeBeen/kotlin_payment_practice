package com.practice.payment.service

import com.practice.payment.exception.ErrorCode
import com.practice.payment.exception.PaymentException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class RefundService(
    private val refundStatusService: RefundStatusService,
    private val accountService: AccountService,
) {

    fun refund(
        refundServiceRequest: RefundServiceRequest
    ): RefundServiceResponse {
        //요청 저장
        val refundTxId = refundStatusService.saveRefundRequest(
            originTransactionId = refundServiceRequest.transactionId,
            merchantRefundId = refundServiceRequest.refundId,
            refundAmount = refundServiceRequest.refundAmount,
            refundReason = refundServiceRequest.refundReason
        )

        return try {
            //계좡에 금액 사용 취소 요청
            val refundAccountTransactionId = accountService.cancelUseAccount(refundTxId)

            //성공: 거래 성공으로 저장
            val (transactionId, transactedAt) = refundStatusService.saveAsSuccess(
                refundTxId,
                refundAccountTransactionId
            )

            RefundServiceResponse(
                refundTransactionId = transactionId,
                refundAmount = refundServiceRequest.refundAmount,
                refundedAt = transactedAt

            )
        } catch (e: Exception) {
            //실패: 거래를 실패로 저장
            refundStatusService.saveAsFailure(refundTxId, getErrorCode(e))
            throw e
        }
    }

    fun getErrorCode(e: Exception) = if (e is PaymentException) e.errorCode
    else ErrorCode.INTERNAL_SERVER_ERROR
}

data class RefundServiceResponse(
    val refundTransactionId: String,
    val refundAmount: Long,
    val refundedAt: LocalDateTime
)

data class RefundServiceRequest(
    val transactionId: String,

    val refundId: String,
    val refundAmount: Long,
    val refundReason: String,
)