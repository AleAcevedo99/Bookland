package com.example.bookland.Entity

import java.util.*

data class EntityUsers(
    var id: Int,
    var email:String,
    var password:String,
    var sex:Int,
    var birthDate: Date?){
    constructor():this(0, "", "", 0, null)
}