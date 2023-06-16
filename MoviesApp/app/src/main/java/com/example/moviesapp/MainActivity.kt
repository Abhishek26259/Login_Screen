package com.example.moviesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moviesapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var movieAdapter: MovieAdapter
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moveToAnotherActivity()

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }

        movieApiService()

        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            searchMovie(query)
        }

    }

    private fun moveToAnotherActivity() {
        movieAdapter = MovieAdapter(emptyList()) { movie ->
            val intent = Intent(this@MainActivity, MovieDetailActivity::class.java)
            intent.putExtra("movie", movie)
            startActivity(intent)
        }
    }
    private fun movieApiService() {
        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of the movie API
        val movieApiService = retrofit.create(MovieApi::class.java)

        // Make the API call to get the movies
        movieApiService.getMovies(API_KEY).enqueue(object : Callback<List<Movie>> {
            override fun onResponse(call: Call<List<Movie>>, response: Response<List<Movie>>) {
                if (response.isSuccessful) {
                    val movies = response.body()
                    if (movies != null) {
                        movieAdapter.movies = movies
                        movieAdapter.notifyDataSetChanged()
                        Log.i("success", "worked", null)
                    }
                } else {
                    Log.i("failed", "error")
                    showToast("Failed to retrieve movies")
                }
            }

            override fun onFailure(call: Call<List<Movie>>, t: Throwable) {
                showToast("Error occurred: ${t.message}")
            }
        })
    }

    private fun searchMovie(query: String){
        movieAdapter.filterMovies(query)

        if(movieAdapter.itemCount==0){
            showToast("No movie found")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val BASE_URL = "https://imdb-top-100-movies.p.rapidapi.com"
        private const val API_KEY = "721754987amsh3d2455b5703573dp1381e7jsn4905c08f97c3"
    }

}
