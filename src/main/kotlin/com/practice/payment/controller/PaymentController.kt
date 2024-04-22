package com.practice.payment.controller

import com.practice.payment.service.PayServiceRequest
import com.practice.payment.service.PayServiceResponse
import com.practice.payment.service.PaymentService
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RequestMapping("/api/v1")
@RestController
class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping("/pay")
    fun pay(
        @Valid @RequestBody payRequest: PayRequest
    ) = PayResponse.fromPayServiceResponse(
        paymentService.pay(payRequest.toPayServiceRequest())
    )

}

data class PayResponse(
    val payUserId: String,
    val amount: Long,
    val transactionId: String,
    val transactedAt: LocalDateTime
) {
    companion object {
        fun fromPayServiceResponse(payServiceResponse: PayServiceResponse) =
            PayResponse(
                payUserId = payServiceResponse.payUserId,
                amount = payServiceResponse.amount,
                transactionId = payServiceResponse.transactionId,
                transactedAt = payServiceResponse.transactedAt
            )
    }
}

data class PayRequest(
    @field:NotBlank
    val payUserId: String,

    @field:Min(100)
    val amount: Long,

    @field:NotBlank
    val merchantTransactionId: String,

    @field:NotBlank
    val orderTitle: String,
) {
    fun toPayServiceRequest() = PayServiceRequest(
        payUserId = payUserId,
        amount = amount,
        merchantTransactionId = merchantTransactionId,
        orderTitle = orderTitle
    )
}
