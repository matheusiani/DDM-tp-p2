package com.example.appfirebaseauth.service

import com.example.appfirebaseauth.model.Produto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProdutoService {

    private val api: ProdutoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProdutoApi::class.java)
    }

    suspend fun obterProdutos(): List<Produto> {
        return withContext(Dispatchers.IO) {
            try {
                api.obterProdutos()
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
