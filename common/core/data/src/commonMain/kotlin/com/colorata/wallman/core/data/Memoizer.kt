package com.colorata.wallman.core.data

class HashMemoizer1<T, H, R>(
    private val hasher: (T) -> H,
    private val function: (T) -> R
) : (T) -> R {
    private val cache = mutableMapOf<H, R>()
    override fun invoke(p1: T): R {
        return cache.getOrPut(hasher(p1)) { function(p1) }
    }
}

class HashMemoizer2<T1, T2, H, R>(
    private val hasher: (T1, T2) -> H,
    private val function: (T1, T2) -> R
) : (T1, T2) -> R {
    private val cache = mutableMapOf<H, R>()
    override fun invoke(p1: T1, p2: T2): R {
        return cache.getOrPut(hasher(p1, p2)) { function(p1, p2) }
    }
}

class Memoizer1<T, R>(
    private val function: (T) -> R
) : (T) -> R {
    private val cache = mutableMapOf<T, R>()
    override fun invoke(p1: T): R {
        return cache.getOrPut(p1) { function(p1) }
    }
}

class Memoizer2<T1, T2, R>(
    private val function: (T1, T2) -> R
) : (T1, T2) -> R {
    private val cache = mutableMapOf<Pair<T1, T2>, R>()
    override fun invoke(p1: T1, p2: T2): R {
        return cache.getOrPut(p1 to p2) { function(p1, p2) }
    }
}

fun <T, R> memoizeHash(hasher: (T) -> Int, function: (T) -> R): (T) -> R = HashMemoizer1(hasher, function)

fun <T1, T2, H, R> memoizeHash(hasher: (T1, T2) -> H, function: (T1, T2) -> R): (T1, T2) -> R = HashMemoizer2(hasher, function)

fun <T, R> memoize(function: (T) -> R): (T) -> R = Memoizer1(function)

fun <T1, T2, R> memoize(function: (T1, T2) -> R): (T1, T2) -> R = Memoizer2(function)
