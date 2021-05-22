package com.example.bookland.Entity


data class EntityFilter(
        var gender:Int,
        var subgender: Int,
        var genderPos:Int,
        var subgenderPos: Int,
        var tropes: ArrayList<String>,
        var tropesSearch: ArrayList<String>,
        var pov: Int,
        var narrating: Int,
        var ageRange: Int,
        var minPage: Int?,
        var maxPage: Int?){
    constructor():this(0, 0, 0, 0, arrayListOf<String>(),arrayListOf<String>(), 0, 0, 0, null, null)
}


