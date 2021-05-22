package com.example.bookland

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Constants.Constants
import com.example.bookland.Constants.NetworkConnectionState
import com.example.bookland.Entity.EntityUsers
import com.example.bookland.databinding.ActivityLoginBinding
import org.json.JSONObject

class LogInActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var queue: RequestQueue
    private val url= Constants.URL_API + "Users"

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        queue = Volley.newRequestQueue(this)

        val netwokr = NetworkConnectionState(this)
        if(netwokr.checkNetwork()){
            binding.btnLogIn.setOnClickListener {
                if (binding.edtEmail.text.trim().isNotEmpty() && binding.edtPassword.text.trim().isNotEmpty()) {
                    val user = EntityUsers()
                    user.email = binding.edtEmail.text.toString()
                    user.password = binding.edtPassword.text.toString()

                    val stringRequest = StringRequest(Request.Method.GET, url + "/" + user.email + "/" + user.password,
                            Response.Listener<String> { response ->
                                val jsonObject = JSONObject(response)
                                if(jsonObject["code"].toString().toLong() >= 1){
                                    cleanForm()
                                    val intent = Intent(this@LogInActivity, HomeActivity::class.java).apply{
                                        putExtra(Constants.ID_USER, jsonObject["code"].toString().toLong())
                                    }
                                    startActivity(intent)
                                }
                                else if(jsonObject["code"] == -1){
                                    Toast.makeText(this, R.string.txt_error_user, Toast.LENGTH_SHORT).show()
                                }else if(jsonObject["code"] == -2){
                                    Toast.makeText(this, R.string.txt_error_password, Toast.LENGTH_SHORT).show()
                                }else{
                                    Toast.makeText(this, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                                }
                            },
                            Response.ErrorListener { error ->
                                Toast.makeText(this, R.string.txt_error_load_activity, Toast.LENGTH_SHORT).show()
                            })
                    queue.add(stringRequest)
                }else{
                    Toast.makeText(this, R.string.txt_all_fields_required,Toast.LENGTH_SHORT).show()
                }

            }

        }else{
            actionDialog(getString(R.string.txt_you_need_internet)).show()
        }


    }

    fun cleanForm(){
        binding.edtEmail.setText("")
        binding.edtPassword.setText("")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_access, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itmRegister -> {
                cleanForm()
                val intent = Intent(this@LogInActivity, RegisterActivity::class.java)
                startActivity(intent)
            }

        }
        return super.onOptionsItemSelected(item)
    }

    fun actionDialog(message:String): AlertDialog {
        val alert = AlertDialog.Builder(this)
        alert.setTitle(R.string.app_name)
        alert.setMessage(message)

        alert.setPositiveButton(R.string.txt_btn_ok){ _, _ ->
            finish()
        }
        return  alert.create()
    }


}