package com.example.digisim

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform