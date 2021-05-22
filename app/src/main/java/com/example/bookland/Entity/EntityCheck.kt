package com.example.bookland.Entity

data class EntityCheck(
    var id: Long,
    var idFavorite: Long,
    var name: String){
    constructor():this(0,0, "")
}