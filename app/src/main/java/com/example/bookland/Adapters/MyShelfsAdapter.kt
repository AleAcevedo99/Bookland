package com.example.bookland.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.R
import com.example.bookland.ShelfActivity
import com.example.bookland.databinding.ItmMyShelfsBinding
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyShelfsAdapter(var shelfsList:ArrayList<EntityShelf>, val context: Context): RecyclerView.Adapter<MyShelfsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyShelfsHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyShelfsHolder(inflater.inflate(R.layout.itm_my_shelfs, parent, false))
    }

    override fun getItemCount(): Int {
        return shelfsList.size
    }

    override fun onBindViewHolder(holder: MyShelfsHolder, position: Int) {
        holder.txvShelfCount.text = "(${shelfsList[position].count})"
        holder.txvShelfName.text = "${shelfsList[position].shelfName}"


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShelfActivity::class.java).apply{
                putExtra(Constants.ID_SHELF, shelfsList[position].id)
                putExtra(Constants.ID_USER, shelfsList[position].idUser)
                putExtra(Constants.TYPE_BOOK_LIST, 0)
            }
            context.startActivity(intent)
        }

    }

}

class MyShelfsHolder(view: View): RecyclerView.ViewHolder(view){
    val binding = ItmMyShelfsBinding.bind(view)

    val txvShelfName = binding.txvShelfName
    val txvShelfCount= binding.txvShelfCount



}