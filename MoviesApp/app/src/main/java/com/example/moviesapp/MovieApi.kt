package com.example.moviesapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface MovieApi {
    @GET("/")
    fun getMovies(@Header("X-RapidAPI-Key") apiKey: String): Call<List<Movie>>
}
