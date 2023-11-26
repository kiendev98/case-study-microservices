package com.kien.microservices.authorization.jose

import java.security.KeyPair
import java.security.KeyPairGenerator

fun generateRsaKey(): KeyPair =
    runThrowingIllegalState {
        KeyPairGenerator.getInstance("RSA")
            .apply {
                initialize(2048)
            }
            .generateKeyPair()
    }

private inline fun <R> runThrowingIllegalState(block: () -> R): R =
    try {
        block()
    } catch (ex: Exception) {
        throw IllegalStateException(ex)
    }
