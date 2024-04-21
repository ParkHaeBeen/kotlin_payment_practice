package com.practice.payment.domain

import jakarta.persistence.Entity

@Entity
class PaymentUser (

    val payUserId: String,
    val accountUserId: Long,
    val accountNumber: String,
    val name: String,

):BaseEntity()