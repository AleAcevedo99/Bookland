package com.example.bookland.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityGender
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.R
import com.example.bookland.ShelfActivity

class RecommendationsAdapter(var gendersList:ArrayList<EntityGender>, val context: Context): RecyclerView.Adapter<MyShelfsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyShelfsHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyShelfsHolder(inflater.inflate(R.layout.itm_my_shelfs, parent, false))
    }

    override fun getItemCount(): Int {
        return gendersList.size
    }

    override fun onBindViewHolder(holder: MyShelfsHolder, position: Int) {
        holder.txvShelfCount.text = ""
        holder.txvShelfName.text = "${gendersList[position].genderName}"


        holder.itemView.setOnClickListener {
            val intent = Intent(context, ShelfActivity::class.java).apply{
                putExtra(Constants.ID_GENDER, gendersList[position].id)
                putExtra(Constants.ID_USER, gendersList[position].idUser)
                putExtra(Constants.TYPE_BOOK_LIST, 1)
            }
            context.startActivity(intent)
        }

    }

}