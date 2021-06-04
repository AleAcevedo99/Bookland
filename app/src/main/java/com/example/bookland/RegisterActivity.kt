package com.example.bookland

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookland.Constants.Constants
import com.example.bookland.Entity.EntityUsers
import com.example.bookland.databinding.ActivityLoginBinding
import com.example.bookland.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val url= Constants.URL_API + "Users"
    private lateinit var queue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.edtBirthDate.setOnClickListener {
            val myCalendar: Calendar
            val y: Int
            val m: Int
            val d: Int
            if(binding.edtBirthDate.text.toString().isEmpty()){
                myCalendar = Calendar.getInstance()
                y = myCalendar.get(Calendar.YEAR)
                m = myCalendar.get(Calendar.MONTH)
                d = myCalendar.get(Calendar.DAY_OF_MONTH)
            }else{
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val myDate: LocalDate? = sdf.parse(binding.edtBirthDate.text.toString())?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
                y = myDate!!.year
                m = myDate!!.monthValue-1
                d = myDate!!.dayOfMonth
            }
            val dpd = DatePickerDialog(this@RegisterActivity,
                    DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                        binding.edtBirthDate.setText("${twoDigits(dayOfMonth)}/${twoDigits(month+1)}/$year")
                    }, y, m, d)
            dpd.show()
        }

        binding.btnNext.setOnClickListener {
            if (binding.edtEmail.text.trim().isNotEmpty() && binding.edtPassword.text.trim().isNotEmpty()
                    && binding.edtBirthDate.text.trim().isNotEmpty() && binding.rgdSex.checkedRadioButtonId != -1) {
                queue = Volley.newRequestQueue(this)
                val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val user = EntityUsers()
                user.email = binding.edtEmail.text.toString()
                user.password = binding.edtPassword.text.toString()
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                user.birthDate = sdf.parse(binding.edtBirthDate.text.toString())
                when(binding.rgdSex.checkedRadioButtonId){
                    binding.rdbWoman.id -> { user.sex = 0 }
                    binding.rdbMen.id -> { user.sex = 1 }
                }

                //ejecuto SP UsersAdd
                val jsonBody = JSONObject()
                val pattern = "yyyy-MM-dd HH:mm"
                val sdfbirth: SimpleDateFormat = SimpleDateFormat(pattern)
                jsonBody.put("email", user.email)
                jsonBody.put("password", user.password)
                jsonBody.put("sex", user.sex)
                jsonBody.put("birthDate", sdfbirth.format(user.birthDate))
                Log.d(Constants.LOG_TAG, url + "-" + jsonBody.toString())
                val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url,jsonBody,
                        Response.Listener { response ->
                            if(response["code"].toString().toInt() > 0){
                                Toast.makeText(this, R.string.txt_user_registered, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@RegisterActivity, FavoriteAuthorsActivity::class.java).apply{
                                    putExtra(Constants.ID_USER, response["code"].toString().toLong())
                                }
                                cleanForm()
                                startActivity(intent)
                                finish()
                            }else{
                                Toast.makeText(this, R.string.txt_transaction_error, Toast.LENGTH_SHORT).show()
                            }

                        },
                        Response.ErrorListener { error ->
                            Log.d(Constants.LOG_TAG, error.toString())
                            if(error.networkResponse.statusCode == 406){
                                Toast.makeText(this, R.string.txt_email_cannot_be_repeated,Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this, R.string.txt_transaction_error,Toast.LENGTH_SHORT).show()
                            }
                        })
                queue.add(jsonObjectRequest)

            }else{
                Snackbar.make(it, R.string.txt_all_fields_required, Snackbar.LENGTH_SHORT).show()
            }


        }
    }

    fun cleanForm(){
        binding.edtEmail.setText("")
        binding.edtPassword.setText("")
        binding.edtBirthDate.setText("")
        binding.rgdSex.clearCheck()
    }

    fun twoDigits(number:Int):String{
        return if(number <= 9) "0$number" else number.toString()
    }
}