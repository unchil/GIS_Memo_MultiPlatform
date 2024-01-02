package com.jetbrains.handson.kmm.shared.network


import com.jetbrains.handson.kmm.shared.entity.CURRENTWEATHER_TBL
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


class GisMemoApi {

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            })
        }
    }

    suspend fun getWeatherData(
        lat:String,
        lon:String,
        units:String,
        appid:String
    ): CURRENTWEATHER_TBL
    {
        return httpClient.get( " https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${lon}&units=${units}&appid=${appid}").body()
    }



}

