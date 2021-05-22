package com.example.bookland.Entity

import java.util.*

data class EntityShelf(
        var id: Long,
        var idUser: Long,
        var shelfName: String,
        var count: Int,
        var isDefault: Int,
        var idBookShelf: Int){
    constructor():this(0, 0,"", 0, 0, 0)
}