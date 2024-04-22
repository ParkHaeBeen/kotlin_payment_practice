package com.practice.payment.service

import com.practice.payment.exception.ErrorCode
import com.practice.payment.exception.PaymentException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
internal class PaymentServiceTest {

    @MockK
    lateinit var paymentStatusService: PaymentStatusService

    @MockK
    lateinit var accountService: AccountService

    @InjectMockKs
    lateinit var paymentService: PaymentService

    @Test
    fun `결제 성공`() {
        //given
        val request = PayServiceRequest(
            payUserId = "payUserId",
            amount = 1000,
            merchantTransactionId = "merchantTransactionId",
            orderTitle = "orderTitle"
        )

        every {
            paymentStatusService.savePayRequest(any(), any(), any(), any())
        } returns 1L
        every {
            accountService.useAccount(any())
        } returns "payMehthodTransactionId"
        every {
            paymentStatusService.saveAsSuccess(any(), any())
        } returns Pair("transactionId", LocalDateTime.now())

        //when
        val result = paymentService.pay(request)

        //then
        result.amount shouldBe (1000)
        verify(exactly = 1) {
            paymentStatusService.saveAsSuccess(any(), any())
        }
        verify(exactly = 0) {
            paymentStatusService.saveAsFailure(any(), any())
        }
    }

    @Test
    fun `결제 실패`() {
        //given
        val request = PayServiceRequest(
            payUserId = "payUserId",
            amount = 1000,
            merchantTransactionId = "merchantTransactionId",
            orderTitle = "orderTitle"
        )

        every {
            paymentStatusService.savePayRequest(any(), any(), any(), any())
        } returns 1L
        every {
            accountService.useAccount(any())
        } throws PaymentException(ErrorCode.LACK_BALANCE)
        every {
            paymentStatusService.saveAsFailure(any(), any())
        } returns Unit

        //when
        val result = shouldThrow<PaymentException> {
            paymentService.pay(request)
        }

        //then
        result.errorCode shouldBe ErrorCode.LACK_BALANCE
        verify(exactly = 0) {
            paymentStatusService.saveAsSuccess(any(), any())
        }
        verify(exactly = 1) {
            paymentStatusService.saveAsFailure(any(), any())
        }
    }
}