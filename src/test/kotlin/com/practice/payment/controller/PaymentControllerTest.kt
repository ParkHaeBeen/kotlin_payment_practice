package com.practice.payment.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.practice.payment.service.PaymentService
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(PaymentController::class)
internal class PaymentControllerTest @Autowired constructor(
    private val mockMvc: MockMvc
) {

    @MockkBean
    private lateinit var paymentService: PaymentService

    private val mapper = ObjectMapper()

    @Test
    fun `결제 요청 - 성공 응답`() {
        //given

        //when

        //then
        mockMvc.post("/api/v1/pay") {
            contentType = MediaType.APPLICATION_JSON
            headers {
                this.accept = listOf(MediaType.APPLICATION_JSON)
            }
            content = mapper.writeValueAsString(
                PayRequest(
                    payUserId = "p1",
                    amount = 1000,
                    merchantTransactionId = "ts",
                    "title"
                )
            )
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.payUserId", equalTo("p1")) }
            content { jsonPath("$.amount", equalTo(1000)) }
            content { jsonPath("$.transactionId", equalTo("ts")) }
        }.andDo {
            print()
        }
    }

}