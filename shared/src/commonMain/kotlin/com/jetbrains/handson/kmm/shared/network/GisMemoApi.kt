package com.jetbrains.handson.kmm.shared.network

import com.jetbrains.handson.kmm.shared.entity.CurrentWeather
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.timeout
import io.ktor.client.request.*
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


class GisMemoApi {

    private val httpClient = HttpClient(CIO) {

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 1000
            socketTimeoutMillis = 1000
        }
    }

    suspend fun getWeatherData(
        lat:String,
        lon:String,
        units:String,
        appid:String
    ): CurrentWeather
    {
            return  httpClient.get(
                " https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=${units}&appid=${appid}"
            ).body()
    }



}

