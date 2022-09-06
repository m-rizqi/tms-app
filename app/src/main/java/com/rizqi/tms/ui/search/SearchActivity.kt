package com.rizqi.tms.ui.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivitySearchBinding
import com.rizqi.tms.model.SearchFilter
import com.rizqi.tms.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchBinding
    private val searchViewModel : SearchViewModel by viewModels()
    private val itemViewModel : ItemViewModel by viewModels()
    private val searchResultAdapter = SearchResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemViewModel.getAllItem().observe(this){
            searchViewModel.setAllItem(it)
        }
        searchViewModel.resultList.observe(this){
            searchResultAdapter.submitList(it)
        }
        binding.apply {
            rvSearchResult.adapter = searchResultAdapter
            btnSearchBack.setOnClickListener { onBackPressed() }
            tieSearch.doAfterTextChanged {
                searchViewModel.search(SearchFilter(it.toString()))
            }
        }

    }
}