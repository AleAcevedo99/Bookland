package com.example.bookland.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.R
import com.example.bookland.ShelfActivity
import com.example.bookland.databinding.ItmMyShelfsBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class CatalogShelfsAdapter(var shelfsList:ArrayList<EntityShelf>, val context: Context, val queue: RequestQueue): RecyclerView.Adapter<CatalogShelfsHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogShelfsHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CatalogShelfsHolder(inflater.inflate(R.layout.itm_my_shelfs, parent, false))
    }

    override fun getItemCount(): Int {
        return shelfsList.size
    }

    override fun onBindViewHolder(holder: CatalogShelfsHolder, position: Int) {
        holder.txvShelfName.text = "${shelfsList[position].shelfName}"
        holder.txtShelfCount.text = "${shelfsList[position].count}"


        holder.itemView.setOnClickListener {
            actionDialogEditDelete(position).show()
        }

    }

    fun actionDialog(position: Int): AlertDialog {
        val alert = AlertDialog.Builder(context)
        val editText = EditText(context)
        alert.setTitle(R.string.app_name)
        alert.setMessage(R.string.txt_edit_shelfs)

        alert.setView(editText)
        editText.setText(shelfsList[position].shelfName)
        alert.setPositiveButton(R.string.txt_ok) { _, _ ->
            val jsonBody = JSONObject()
            jsonBody.put("id", shelfsList[position].id)
            jsonBody.put("idUser", shelfsList[position].idUser)
            jsonBody.put("shelfName", editText.text.toString())
            val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.PUT, Constants.URL_API + "Shelfs",jsonBody,
                    Response.Listener { response ->
                        if(response["code"].toString().toInt() >= 1){
                            shelfsList[position].shelfName =  editText.text.toString()
                            Toast.makeText(context, R.string.txt_successful_message, Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                        }
                        notifyDataSetChanged()
                    },
                    Response.ErrorListener { error ->
                        if(error.networkResponse.statusCode == 406){
                            Toast.makeText(context, R.string.txt_name_cannot_be_repeated, Toast.LENGTH_LONG).show()
                        }else{
                            Toast.makeText(context, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                        }
                        notifyDataSetChanged()
                    })
            queue.add(jsonObjectRequest)


        }

        alert.setNegativeButton(R.string.txt_cancel) { _, _ ->

        }

        return  alert.create()
    }

    fun actionDialogEditDelete(position: Int): AlertDialog {
        val alert = AlertDialog.Builder(context)
        alert.setTitle(R.string.app_name)
        alert.setMessage(R.string.txt_would_like_to_do)

        alert.setPositiveButton(R.string.txt_edit){_,_ ->
            actionDialog(position).show()
        }


        alert.setNegativeButton(R.string.txt_delete_shelfs){_,_ ->
            deleteShelf(position)
        }

        alert.setNeutralButton(R.string.txt_cancel){_,_ ->

        }

        return  alert.create()
    }

    fun deleteShelf(position: Int){
        Log.d(Constants.LOG_TAG, Constants.URL_API + "Shelfs/${shelfsList[position].id}")
        val stringRequest = StringRequest(Request.Method.DELETE, Constants.URL_API + "Shelfs/${shelfsList[position].id}",
                Response.Listener<String> { response ->
                    val jsonObject = JSONObject(response)
                    if(jsonObject["code"] == 1) {
                        shelfsList.removeAt(position)
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
}

class CatalogShelfsHolder(view: View): RecyclerView.ViewHolder(view){
    val binding = ItmMyShelfsBinding.bind(view)

    val txvShelfName = binding.txvShelfName
    val txtShelfCount = binding.txvShelfCount

}