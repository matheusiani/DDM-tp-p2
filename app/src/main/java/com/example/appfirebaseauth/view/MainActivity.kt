package com.example.appfirebaseauth.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appfirebaseauth.adapter.ProdutoAdapter
import com.example.appfirebaseauth.databinding.ActivityMainBinding
import com.example.appfirebaseauth.service.FavoritoService
import com.example.appfirebaseauth.service.ProdutoService
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var produtoAdapter: ProdutoAdapter
    private val produtoService = ProdutoService()
    private val favoritoService = FavoritoService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        carregarDadosUsuario()
        configurarRecyclerView()
        carregarProdutos()

        binding.btnVerProdutos.setOnClickListener {
            carregarProdutos()
        }

        binding.btnVerFavoritos.setOnClickListener {
            carregarFavoritos()
        }

        binding.btnSair.setOnClickListener {
            signOut()
        }

        binding.btnExcluirConta.setOnClickListener {
            confirmarExclusaoConta()
        }
    }

    private fun configurarRecyclerView() {
        produtoAdapter = ProdutoAdapter(onFavoritoClick = { produto ->
            lifecycleScope.launch {
                val isFav = favoritoService.isFavorito(produto.id)
                if (isFav) {
                    val sucesso = favoritoService.removerFavorito(produto.id)
                    if (sucesso) {
                        Toast.makeText(this@MainActivity, "Removido dos favoritos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val sucesso = favoritoService.adicionarFavorito(produto)
                    if (sucesso) {
                        Toast.makeText(this@MainActivity, "Adicionado aos favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
        binding.recyclerViewProdutos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = produtoAdapter
        }
    }

    private fun carregarProdutos() {
        lifecycleScope.launch {
            val produtos = produtoService.obterProdutos()
            if (produtos.isNotEmpty()) {
                produtoAdapter.atualizarLista(produtos)
            } else {
                Toast.makeText(this@MainActivity, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarFavoritos() {
        lifecycleScope.launch {
            val favoritos = favoritoService.obterFavoritos()
            produtoAdapter.atualizarLista(favoritos)
            if (favoritos.isEmpty()) {
                Toast.makeText(this@MainActivity, "Nenhum favorito encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            voltarParaLogin()
        }
    }

    private fun carregarDadosUsuario() {
        val user = auth.currentUser ?: run {
            voltarParaLogin()
            return
        }

        val nome = user.displayName?.takeIf { it.isNotBlank() } ?: "Usuário"
        val email = user.email ?: "-"

        binding.txtBemVindo.text = "Olá, $nome!"
        binding.txtNomeUsuario.text = "Nome: $nome"
        binding.txtEmailUsuario.text = "E-mail: $email"
        binding.txtUidUsuario.text = "UID: ${user.uid}"
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                auth.signOut()
                voltarParaLogin()
            }
    }

    private fun confirmarExclusaoConta() {
        AlertDialog.Builder(this)
            .setTitle("Excluir conta")
            .setMessage("Deseja excluir sua conta?")
            .setPositiveButton("Excluir") { _, _ ->
                AuthUI.getInstance()
                    .delete(this)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Conta excluída", Toast.LENGTH_SHORT).show()
                            voltarParaLogin()
                        } else {
                            Toast.makeText(this, "Erro ao excluir conta", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun voltarParaLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
