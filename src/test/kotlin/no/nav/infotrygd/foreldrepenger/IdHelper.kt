package no.nav.infotrygd.foreldrepenger

import java.math.BigInteger

private var current: BigInteger = BigInteger.ONE

fun nextId(): BigInteger {
    current = current.add(BigInteger.ONE)
    return current
}