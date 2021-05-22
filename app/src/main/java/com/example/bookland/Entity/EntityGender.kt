package com.example.bookland.Entity

data class EntityGender(
    var id: Long,
    var idUser: Long,
    var genderName: String)   {
    constructor():this(0,0, "")
}