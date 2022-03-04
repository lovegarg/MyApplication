package com.brave.bravescryptoapplication

data class CryptoModel(
    val name: String?,
    val symbol: String,
    val price: Double,
    val lastUpdate: String,
    val btcPrice: Double
)
