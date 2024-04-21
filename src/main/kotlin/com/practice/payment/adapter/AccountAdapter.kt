package com.practice.payment.adapter

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "account - adapter",
    url = "http://localhost:8080"
)
interface AccountAdapter {

    @PostMapping("/transaction/use")
    fun useAccount(
        @RequestBody useBalanceRequest: UseBalanceRequest
    ): UseBalanceResponse
}

class UseBalanceResponse(
    val accountNumber: String,
    val transactionResult: TransactionResultType,
    val transactionId: String,
    val amount: Long,
)

enum class TransactionResultType {
    S, F
}

class UseBalanceRequest(
    val userId: Long,
    val accountNumber: String,
    val amount: Long
)