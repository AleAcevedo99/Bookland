package com.example.bookland.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityCheck
import com.example.bookland.Entity.ListFilters
import com.example.bookland.R
import com.example.bookland.databinding.ItmCheckBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.io.Console

class CheckAdapter(var itemsList:ArrayList<EntityCheck>, val context: Context, val type: Int, val idUser: Long, val queue: RequestQueue, val idBook: Long): RecyclerView.Adapter<CheckHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CheckHolder(inflater.inflate(R.layout.itm_check, parent, false))
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: CheckHolder, position: Int) {
        holder.chkName.setText(itemsList[position].name)
        holder.chkName.isChecked = itemsList[position].idFavorite >= 1


        holder.chkName.setOnCheckedChangeListener { buttonView, isChecked ->
            //isChecked --> estado al que pasa
            if (buttonView.isPressed()){
                if(type == 1){
                    //Autores
                    if(isChecked){
                        val jsonBody = JSONObject()
                        jsonBody.put("idUser", idUser)
                        jsonBody.put("idAuthor", itemsList[position].id)
                        val jsonObjectRequest = JsonObjectRequest(
                                Request.Method.POST, Constants.URL_API + "FavoriteAuthorsPerUser",jsonBody,
                                Response.Listener { response ->
                                    if(response["code"].toString().toInt() >= 1){
                                        itemsList[position].idFavorite = response["code"].toString().toLong()
                                        Toast.makeText(context, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                    }
                                    notifyDataSetChanged()

                                },
                                Response.ErrorListener { error ->
                                    Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                    notifyDataSetChanged()
                                })
                        queue.add(jsonObjectRequest)
                    }else{
                        val stringRequest = StringRequest(Request.Method.DELETE, Constants.URL_API + "FavoriteAuthorsPerUser/" + itemsList[position].idFavorite,
                                Response.Listener<String> { response ->
                                    val jsonObject = JSONObject(response)
                                    if(jsonObject["code"].toString().toInt() >= 1){
                                        itemsList[position].idFavorite = 0
                                        Toast.makeText(context, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                    }
                                    notifyDataSetChanged()

                                },
                                Response.ErrorListener { error ->
                                    Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                    notifyDataSetChanged()
                                })
                        queue.add(stringRequest)
                    }
                }else if(type == 2){
                    //Generos
                    if(isChecked){
                        val jsonBody = JSONObject()
                        jsonBody.put("idUser", idUser)
                        jsonBody.put("idGender", itemsList[position].id)
                        val jsonObjectRequest = JsonObjectRequest(
                                Request.Method.POST, Constants.URL_API + "FavoriteGendersPerUser",jsonBody,
                                Response.Listener { response ->
                                    if(response["code"].toString().toInt() >= 1){
                                        itemsList[position].idFavorite = response["code"].toString().toLong()
                                        Toast.makeText(context, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                                    }else{
                                        Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                    }
                                    notifyDataSetChanged()

                                },
                                Response.ErrorListener { error ->
                                    notifyDataSetChanged()
                                    Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                })
                        queue.add(jsonObjectRequest)
                    }else{
                        val stringRequest = StringRequest(Request.Method.DELETE, Constants.URL_API + "FavoriteGendersPerUser/" + itemsList[position].idFavorite,
                                Response.Listener<String> { response ->
                                    val jsonObject = JSONObject(response)
                                    if(jsonObject["code"].toString().toInt() >= 1){
                                        itemsList[position].idFavorite = 0
                                        Toast.makeText(context, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                                    }else{
                                          Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                    }
                                    notifyDataSetChanged()

                                },
                                Response.ErrorListener { error ->
                                    Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                    notifyDataSetChanged()
                                })
                        queue.add(stringRequest)
                    }
                }else if(type == 3){
                    //Shelfs
                    if(isChecked){
                        val jsonBody = JSONObject()
                        jsonBody.put("idBook", idBook)
                        jsonBody.put("idShelf", itemsList[position].id)
                        val jsonObjectRequest = JsonObjectRequest(
                            Request.Method.POST, Constants.URL_API + "BookShelfs",jsonBody,
                            Response.Listener { response ->
                                if(response["code"].toString().toInt() >= 1){
                                    itemsList[position].idFavorite = response["code"].toString().toLong()
                                    Toast.makeText(context, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                }
                                notifyDataSetChanged()

                            },
                            Response.ErrorListener { error ->
                                notifyDataSetChanged()
                                Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                            })
                        queue.add(jsonObjectRequest)
                    }else{
                        val stringRequest = StringRequest(Request.Method.DELETE, Constants.URL_API + "BookShelfs/" + itemsList[position].idFavorite,
                            Response.Listener<String> { response ->
                                val jsonObject = JSONObject(response)
                                if(jsonObject["code"].toString().toInt() >= 1){
                                    itemsList[position].idFavorite = 0
                                    Toast.makeText(context, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                }
                                notifyDataSetChanged()

                            },
                            Response.ErrorListener { error ->
                                Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                notifyDataSetChanged()
                            })
                        queue.add(stringRequest)
                    }
                }else if(type == 4){
                    //Tags de filtro
                    if(isChecked){
                        itemsList[position].idFavorite = 1
                        val filterSetting = ListFilters().getFilterSetting()
                        filterSetting.tropesSearch.add("${itemsList[position].id}:1")
                        filterSetting.tropes.add(itemsList[position].name)
                        notifyDataSetChanged()
                    }else{
                        itemsList[position].idFavorite = 0
                        val filterSetting = ListFilters().getFilterSetting()
                        filterSetting.tropes.remove(itemsList[position].name)
                        filterSetting.tropesSearch.remove("${itemsList[position].id}:1")
                        notifyDataSetChanged()
                    }
                }
            }



        }


    }
}

class CheckHolder(view: View): RecyclerView.ViewHolder(view){
    val binding = ItmCheckBinding.bind(view)

    val chkName= binding.chkName

}

