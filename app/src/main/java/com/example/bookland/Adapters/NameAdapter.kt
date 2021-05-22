package com.example.bookland.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityCheck
import com.example.bookland.R
import com.example.bookland.databinding.ItmCheckBinding
import com.example.bookland.databinding.ItmDetailBookBinding
import org.json.JSONObject

class NameAdapter(var itemsList:ArrayList<String>, val context: Context): RecyclerView.Adapter<NameHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NameHolder(inflater.inflate(R.layout.itm_detail_book, parent, false))
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: NameHolder, position: Int) {
        holder.txtName.setText(itemsList[position])
   }
}

class NameHolder(view: View): RecyclerView.ViewHolder(view){
    val binding = ItmDetailBookBinding.bind(view)

    val txtName = binding.txtName

}