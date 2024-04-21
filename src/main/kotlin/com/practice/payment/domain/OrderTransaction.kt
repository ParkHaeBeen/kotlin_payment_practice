package com.practice.payment.domain

import com.practice.payment.TransactionStatus
import com.practice.payment.TransactionType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class OrderTransaction(

    val transactionId: String,

    @ManyToOne
    val order: Order,

    @Enumerated(EnumType.STRING)
    val transactionType: TransactionType,

    @Enumerated(EnumType.STRING)
    val transactionStatus: TransactionStatus,
    val transactionAmount: Long,
    val merchantTransactionId: String,
    var payMethodTransactionId: String? = null,
    var transactedAt: LocalDateTime? = null,
    var failureCode: String? = null,
    var description: String? = null,
) : BaseEntity()