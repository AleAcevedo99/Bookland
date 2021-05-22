package com.example.bookland.Entity

data class EntityBooks(
        var id: Long,
        var title: String,
        var authorName: String,
        var publicationYear: Int,
        var avgRating: Double,
        var imageURL: String,
        var rating: Double,
        var idShelf: Long,
        var shelfName: String,
        var description: String,
        var idSaga: Long){
    constructor():this(0, "", "", 0, 0.0, "", 0.0, 0, "", "", 0)
}