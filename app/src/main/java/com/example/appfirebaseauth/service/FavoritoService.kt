package com.example.appfirebaseauth.service

import com.example.appfirebaseauth.model.Produto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FavoritoService {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getFavoritosCollection() = auth.currentUser?.uid?.let { uid ->
        db.collection("usuarios").document(uid).collection("favoritos")
    }

    suspend fun adicionarFavorito(produto: Produto): Boolean {
        return try {
            val collection = getFavoritosCollection() ?: return false
            collection.document(produto.id.toString()).set(produto).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun removerFavorito(produtoId: Int): Boolean {
        return try {
            val collection = getFavoritosCollection() ?: return false
            collection.document(produtoId.toString()).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun obterFavoritos(): List<Produto> {
        return try {
            val collection = getFavoritosCollection() ?: return emptyList()
            val snapshot = collection.get().await()
            snapshot.toObjects(Produto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun isFavorito(produtoId: Int): Boolean {
        return try {
            val collection = getFavoritosCollection() ?: return false
            val doc = collection.document(produtoId.toString()).get().await()
            doc.exists()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
