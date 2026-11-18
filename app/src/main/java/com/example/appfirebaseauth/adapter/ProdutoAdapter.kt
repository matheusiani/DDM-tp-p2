package com.example.appfirestore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appfirebaseauth.databinding.ItemProdutoBinding
import com.example.appfirebaseauth.model.Produto
import java.text.NumberFormat
import java.util.Locale

class ProdutoAdapter(
    private val listaProdutos: MutableList<Produto> = mutableListOf()
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(val binding: ItemProdutoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val binding = ItemProdutoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProdutoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = listaProdutos[position]
        val formatoMoeda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

        holder.binding.txtNomeProduto.text = produto.title
        holder.binding.txtCategoriaProduto.text = "Categoria: ${produto.category}"
        holder.binding.txtPrecoProduto.text = formatoMoeda.format(produto.price)
        holder.binding.txtDescricaoProduto.text = produto.description
        holder.binding.txtImagemProduto.text = produto.image

        holder.binding.txtIdDocumento.text = "Documento (Autogerado): ${produto.id}"
    }

    override fun getItemCount(): Int = listaProdutos.size

    fun atualizarLista(novosProdutos: List<Produto>) {
        listaProdutos.clear()
        listaProdutos.addAll(novosProdutos)
        notifyDataSetChanged()
    }
}