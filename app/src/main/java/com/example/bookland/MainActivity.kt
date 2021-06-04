package com.example.bookland

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.bookland.Constants.Constants
import com.example.bookland.Fragments.AdvancedSearchFragment
import com.example.bookland.Fragments.HomeFragment
import com.example.bookland.Fragments.RecommendationsFragment
import com.example.bookland.Fragments.SearchFragment
import com.example.bookland.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var previewMenuItem: MenuItem
    private var idUser: Long = -1
    private var currentPositionMenu = 0
    private var currentMenuSelected = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.popupTheme = R.style.ThemeOverlay_AppCompat_Dark_ActionBar
        setSupportActionBar(toolbar)

        idUser= intent.getLongExtra(Constants.ID_USER, -1)
        configureSlideMenu()

    }

    private fun initFragments(idUser: Long):ArrayList<Fragment>{
        return arrayListOf(HomeFragment.newInstance(idUser),
            RecommendationsFragment.newInstance(idUser),
            AdvancedSearchFragment.newInstance(idUser)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bookland, menu)
        val searchItem = menu?.findItem(R.id.itmSimpleSearch)
        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if(!query.isNullOrEmpty()){
                        currentMenuSelected =  binding.navigationView.selectedItemId
                        currentPositionMenu =  binding.viewPager.currentItem
                        val menuAdapter = com.example.bookland.Adapters.MenuAdapter(arrayListOf(SearchFragment.newInstance(idUser, query)),
                                supportFragmentManager)
                        binding.viewPager.adapter = menuAdapter
                    }
                    return true
                }


                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }

            })

            searchView.setOnCloseListener(object : SearchView.OnCloseListener{
                override fun onClose(): Boolean {
                    searchView.onActionViewCollapsed()
                    configureSlideMenu()
                    binding.navigationView.selectedItemId = currentMenuSelected
                    currentPositionMenu =  binding.viewPager.currentItem
                    return true
                }

            })

            searchView.setOnSearchClickListener(object : View.OnClickListener{
                override fun onClick(v: View?) {
                    binding.navigationView.isVisible = false
                }

            })

        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itmShelf -> {
                val intent = Intent(this, CatalogShelfsActivity::class.java).apply{
                    putExtra(Constants.ID_USER, idUser)
                }
                startActivity(intent)
            }
            R.id.itmEditFavAuthors -> {
                val intent = Intent(this, FavoriteAuthorsActivity::class.java).apply{
                    putExtra(Constants.ID_USER, idUser)
                }
                startActivity(intent)
            }
            R.id.itmAboutUs -> {
                val intent = Intent(this, AboutUsActivity::class.java)
                startActivity(intent)
            }
            R.id.itmExit -> {
                val intent = Intent(this, LogInActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }
            R.id.itmNewBookRequest -> {
                val emails = arrayOf("ale.by.3008@gmail.com")
                var intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_EMAIL, emails)
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.txt_request_book_add))
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.txt_email_text))
                }

                if(intent.resolveActivity(packageManager) != null){
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, getString(R.string.txt_no_app),
                            Toast.LENGTH_LONG).show()
                }
            }


        }
        return super.onOptionsItemSelected(item)
    }

    fun configureSlideMenu(){
        binding.navigationView.isVisible = true
        val menuAdapter = com.example.bookland.Adapters.MenuAdapter(initFragments(idUser), supportFragmentManager)
        binding.viewPager.adapter = menuAdapter
        binding.navigationView.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.itmHomeNav ->{
                    binding.viewPager.currentItem = 0
                }
                R.id.itmRecommendationsNav ->{
                    binding.viewPager.currentItem = 1
                }
                R.id.itmAdvancedSearchNav ->{
                    binding.viewPager.currentItem = 2
                }
            }
            true
        }

        binding.viewPager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                if(::previewMenuItem.isInitialized){
                    previewMenuItem.setChecked(false)
                }else{
                    binding.navigationView.menu.getItem(0).setChecked(false)
                }

                binding.navigationView.menu.getItem(position).setChecked(true)
                previewMenuItem = binding.navigationView.menu.getItem(position)
            }

        })
    }


}