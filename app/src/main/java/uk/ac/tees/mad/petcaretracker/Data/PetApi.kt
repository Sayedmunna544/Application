package uk.ac.tees.mad.petcaretracker.Data

import retrofit2.http.GET
import retrofit2.http.Path

interface PetApi {
    @GET("/animal/{type}")
    suspend fun getFacts(@Path("type") type: String): MeowResponse
}

data class MeowResponse(
    val image: String,
    val fact: String
)