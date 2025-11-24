package uk.ac.tees.mad.petcaretracker.Data

import retrofit2.http.GET

interface PetApi {
    @GET("/")
    suspend fun getFacts(): MeowResponse
}

data class MeowResponse(
    val data: List<String>
)