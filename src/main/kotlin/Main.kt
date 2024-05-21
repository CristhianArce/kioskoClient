import com.google.gson.Gson
import encryption.AESProcessor
import models.*
import org.json.JSONObject
import java.math.BigDecimal
import java.net.SocketTimeoutException
import java.time.Instant
import java.util.*

fun main(args: Array<String>) {
    val cache = mutableListOf<JSONObject>()
    var finish = true

    println("Seleccione una opción")
    println("1. Deposito")
    println("2. Crear chunck de peticiones")

    val option = readln()
    if (option == "2") {
        println("¿Cuántas peticiones desea crear?")
        val count = readln().toIntOrNull() ?: 1
        for (i in 1..count) {
            val payload = buildRandomRequests()
            val aesProcessor = AESProcessor()

            val encryptedData = aesProcessor.aesEncrypt(payload.toByteArray())
            val requestBody = JSONObject(DepositRequest(payload = encryptedData))

            try {
                val response = khttp.post(
                    url = "http://localhost:8081/deposit",
                    json = requestBody,
                    timeout = 0.2
                )

                if (response.statusCode == 500) {
                    cache.add(requestBody)
                }

            } catch (_: SocketTimeoutException) {
                cache.add(requestBody)
            }
        }
        println("Chunk de peticiones completado.")
        finish = false
    }

    while (finish) {
        val payload = buildRequests()
        val aesProcessor = AESProcessor()

        val encryptedData = aesProcessor.aesEncrypt(payload.toByteArray())
        val requestBody = JSONObject(DepositRequest(payload = encryptedData))

        try {
            val response = khttp.post(
                url = "http://localhost:8081/deposit",
                json = requestBody,
                timeout = 0.2
            )

            if (response.statusCode == 500) {
                cache.add(requestBody)
            }

        } catch (_: SocketTimeoutException) {
            println("No se pudo establecer una conexión con el servidor de destino.")
            cache.add(requestBody)
        }

        println("Desea Terminar (Y/N)")
        val option = readln()
        finish = when(option) {
            "Y", "y" -> false
            "N", "n" -> true
            else -> false
        }
    }

    if (cache.isNotEmpty()) {
        cache.forEach {
            khttp.post(url = "http://localhost:8081/deposit", json = it)
        }
    }
}

private fun buildRequests(): String {
    val cellphones = listOf("3003885532", "3132400322", "3108076801")
    val gson = Gson()
    println("Digite el número celular (Número de referencia): ")
    val celular = readln() ?: "3003885532"
    println("Digite el valor a depositar: ")
    val valor = readln() ?: "540000"
    println("Digite el código de 4 dígitos de confirmación:")
    val code = readln() ?: "1234"

    // Build de la transacción
    val transactionReference = UUID.randomUUID()
    val amount = Amount(value = BigDecimal(valor), currency = "COP")
    val transaction = Transaction(reference = transactionReference, amount = amount, description = "Deposito Recibido a las ${Instant.now()}")

    // Build del cliente
    val accountReference = when (celular) {
        "3003885532" -> "123456789"
        "3132400322" -> "987654321"
        "3108076801" -> "147258369"
        else -> "547159863"
    }

    val customerId = when (celular) {
        "3003885532" -> "1003657892"
        "3132400322" -> "1450894090"
        "3108076801" -> "126015948909"
        else -> "1010998123"
    }

    val customer = Customer(customerId, celular, accountReference)
    val transactionData = TransactionData(transaction, customer, code)

    return gson.toJson(transactionData)
}



private fun buildRandomRequests(): String {
    val cellphones = listOf("3003885532", "3132400322", "3108076801")
    val values = listOf(45000, 450000, 1000000, 10000, 50000, 35000)
    val gson = Gson()
    val celular = cellphones.random()
    val valor = values.random()
    val code = "1234"

    // Build de la transacción
    val transactionReference = UUID.randomUUID()
    val amount = Amount(value = BigDecimal(valor), currency = "COP")
    val transaction = Transaction(reference = transactionReference, amount = amount, description = "Deposito Recibido a las ${Instant.now()}")

    // Build del cliente
    val accountReference = when (celular) {
        "3003885532" -> "123456789"
        "3132400322" -> "987654321"
        "3108076801" -> "147258369"
        else -> "547159863"
    }

    val customerId = when (celular) {
        "3003885532" -> "1003657892"
        "3132400322" -> "1450894090"
        "3108076801" -> "126015948909"
        else -> "1010998123"
    }

    val customer = Customer(customerId, celular, accountReference)
    val transactionData = TransactionData(transaction, customer, code)

    return gson.toJson(transactionData)
}