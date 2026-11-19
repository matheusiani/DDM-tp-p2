package com.example.appfirebaseauth.service

import com.example.appfirebaseauth.model.Produto
import retrofit2.http.GET

interface ProdutoApi {
    @GET("products")
    suspend fun obterProdutos(): List<Produto>
}
