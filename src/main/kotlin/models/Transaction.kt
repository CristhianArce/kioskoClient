package models

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

data class Amount(
    val value: BigDecimal,
    val currency: String
)

data class Transaction(
    val reference: UUID,
    val type: String = "DEPOSIT",
    val timestamp: String = Instant.now().toString(),
    val amount: Amount,
    val description: String
)

data class Customer(
    val id: String,
    val cellphone: String,
    val account: String
)

data class TransactionData(
    val transaction: Transaction,
    val customer: Customer,
    val verificationCode: String
)
