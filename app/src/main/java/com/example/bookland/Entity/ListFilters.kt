package com.example.bookland.Entity

class ListFilters {
    fun getFilterSetting(): EntityFilter{
        return filterSettings
    }

    fun getListGenders(): ArrayList<EntityCheck>{
        return  listGenders
    }

    companion object{
        private var filterSettings = EntityFilter()
        private val listGenders = arrayListOf<EntityCheck>()
        private val gendersName = arrayListOf<String>()
    }
}