package com.kien.microservices.authorizationservice.jose

import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.spec.ECFieldFp
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

fun generateSecretKey(): SecretKey =
    runThrowingIllegalState {
        KeyGenerator.getInstance("HmacSha256").generateKey()
    }


fun generateRsaKey(): KeyPair =
    runThrowingIllegalState {
        KeyPairGenerator.getInstance("RSA")
            .apply {
                initialize(2048)
            }
            .generateKeyPair()
    }

fun generateEcKey(): KeyPair {
    val ellipticCurve = EllipticCurve(
        ECFieldFp(
            BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853951")
        ),
        BigInteger("115792089210356248762697446949407573530086143415290314195533631308867097853948"),
        BigInteger("41058363725152142129326129780047268409114441015993725554835256314039467401291")
    )
    val ecPoint = ECPoint(
        BigInteger("48439561293906451759052585252797914202762949526041747995844080717082404635286"),
        BigInteger("36134250956749795798585127919587881956611106672985015071877198253568414405109")
    )
    val ecParameterSpec = ECParameterSpec(
        ellipticCurve,
        ecPoint,
        BigInteger("115792089210356248762697446949407573529996955224135760342422259061068512044369"),
        1
    )

    return runThrowingIllegalState {
        KeyPairGenerator.getInstance("EC")
            .apply { initialize(ecParameterSpec)  }
            .generateKeyPair()
    }
}

private inline fun <R> runThrowingIllegalState(block: () -> R): R =
    try {
        block()
    } catch (ex: Exception) {
        throw IllegalStateException(ex)
    }
