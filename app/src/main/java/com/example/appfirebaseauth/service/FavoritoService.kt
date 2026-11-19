package com.example.appfirebaseauth.service

import android.util.Log
import com.example.appfirebaseauth.model.Produto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FavoritoService {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUid(): String? = auth.currentUser?.uid

    suspend fun adicionarFavorito(produto: Produto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = getUid() ?: return@withContext Result.failure(Exception("Usuário não autenticado"))
            db.collection("usuarios")
                .document(uid)
                .collection("favoritos")
                .document(produto.id.toString())
                .set(produto)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FavoritoService", "Erro ao adicionar favorito", e)
            Result.failure(e)
        }
    }

    suspend fun removerFavorito(produtoId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = getUid() ?: return@withContext Result.failure(Exception("Usuário não autenticado"))
            db.collection("usuarios")
                .document(uid)
                .collection("favoritos")
                .document(produtoId.toString())
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FavoritoService", "Erro ao remover favorito", e)
            Result.failure(e)
        }
    }

    suspend fun obterFavoritos(): Result<List<Produto>> = withContext(Dispatchers.IO) {
        try {
            val uid = getUid() ?: return@withContext Result.failure(Exception("Usuário não autenticado"))
            val snapshot = db.collection("usuarios")
                .document(uid)
                .collection("favoritos")
                .get()
                .await()
            val favoritos = snapshot.toObjects(Produto::class.java)
            Result.success(favoritos)
        } catch (e: Exception) {
            Log.e("FavoritoService", "Erro ao obter favoritos", e)
            Result.failure(e)
        }
    }

    suspend fun isFavorito(produtoId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            val uid = getUid() ?: return@withContext false
            val doc = db.collection("usuarios")
                .document(uid)
                .collection("favoritos")
                .document(produtoId.toString())
                .get()
                .await()
            doc.exists()
        } catch (e: Exception) {
            Log.e("FavoritoService", "Erro ao verificar favorito", e)
            false
        }
    }
}
