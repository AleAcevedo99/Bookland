package com.example.bookland.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.*
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityBooks
import com.example.bookland.Entity.EntityShelf
import com.example.bookland.databinding.ItmMyShelfsBinding
import com.example.bookland.databinding.ItmReadBookBinding
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BooksAdapter(var booksList:ArrayList<EntityBooks>, val context: Context, val idUser: Long): RecyclerView.Adapter<BooksHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BooksHolder(inflater.inflate(R.layout.itm_read_book, parent, false))
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    override fun onBindViewHolder(holder: BooksHolder, position: Int) {
        var queue: RequestQueue = Volley.newRequestQueue(context)
        Picasso.get().load(booksList[position].imageURL).fit()
                .placeholder(R.drawable.imgplaceholder)
                .error(R.drawable.imgplaceholder)
                .into(holder.imgCover)
        holder.txvTitle.text = "${booksList[position].title}"
        holder.txvAuthor.text = "${booksList[position].authorName}"
        holder.txvDate.text = "${booksList[position].publicationYear}"
        holder.txtAvg.text = "${context.getString(R.string.txt_avg)}: ${booksList[position].avgRating}"
        holder.ratingBar.rating = booksList[position].rating.toFloat()

        holder.itemView.setOnClickListener {
            val intent = Intent(context, BookDetailActivity::class.java).apply{
                putExtra(Constants.ID_BOOK, booksList[position].id)
                putExtra(Constants.ID_USER, idUser)
            }
            context.startActivity(intent)
        }

        holder.ratingBar.setOnRatingBarChangeListener {ratingBar, rating, fromUser ->
            if(fromUser){
                Log.d(Constants.LOG_TAG, "Cambio")
                val jsonBody = JSONObject()
                jsonBody.put("idUser", idUser)
                jsonBody.put("idBook", booksList[position].id)
                jsonBody.put("rating", rating)
                val jsonObjectRequest = JsonObjectRequest(
                        Request.Method.POST, Constants.URL_API + "UserBooks",jsonBody,
                        Response.Listener { response ->
                            if(response["code"].toString().toInt() >= 1){
                                booksList[position].rating = holder.ratingBar.rating.toDouble()
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
            }


        }

        holder.btnShelfs.setOnClickListener {
            val intent = Intent(context, BookShelfsActivity::class.java).apply{
                putExtra(Constants.ID_BOOK, booksList[position].id)
                putExtra(Constants.ID_USER, idUser)
            }
            context.startActivity(intent)
        }

    }

}

class BooksHolder(view: View): RecyclerView.ViewHolder(view){
    val binding = ItmReadBookBinding.bind(view)

    val imgCover = binding.imgCover
    val txvTitle = binding.txvTitle
    val txvAuthor = binding.txvAuthor
    val txvDate = binding.txvDate
    val txtAvg = binding.txvAvg
    val btnShelfs = binding.btnShelfs
    val ratingBar = binding.ratingBar


}