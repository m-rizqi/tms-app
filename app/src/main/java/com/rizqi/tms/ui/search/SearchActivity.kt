package com.rizqi.tms.ui.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.viewModelScope
import com.google.android.material.chip.ChipGroup
import com.rizqi.tms.databinding.ActivitySearchBinding
import com.rizqi.tms.databinding.ChipUnitBinding
import com.rizqi.tms.model.SearchFilter
import com.rizqi.tms.model.SearchHistory
import com.rizqi.tms.model.Unit
import com.rizqi.tms.ui.dialog.searchfilter.SearchFilterBottomSheet
import com.rizqi.tms.utility.SEARCH_FILTER
import com.rizqi.tms.utility.insertUnitIntoChipGroup
import com.rizqi.tms.utility.setChipStyle
import com.rizqi.tms.viewmodel.ItemViewModel
import com.rizqi.tms.viewmodel.SearchHistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySearchBinding
    private val searchViewModel : SearchViewModel by viewModels()
    private val itemViewModel : ItemViewModel by viewModels()
    private val searchHistoryViewModel : SearchHistoryViewModel by viewModels()
    private val searchResultAdapter = SearchResultAdapter()
    private val searchHistoryAdapter = SearchHistoryAdapter()
    private var isFirstEnter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getSerializableExtra(SEARCH_FILTER)?.let {
            searchViewModel.setSearchFilter(it as SearchFilter)
            isFirstEnter = true
        }

        searchResultAdapter.onClickListener = {itemWithPrices ->
            searchHistoryViewModel.viewModelScope.launch {
                searchHistoryViewModel.insert(SearchHistory(itemWithPrices.item.id, System.currentTimeMillis()))
            }
        }
        searchHistoryAdapter.onClickListener = {itemWithPrices ->
            searchHistoryViewModel.viewModelScope.launch {
                searchHistoryViewModel.insert(SearchHistory(itemWithPrices.item.id, System.currentTimeMillis()))
            }
        }
        binding.rvSearchResult.adapter = searchResultAdapter
        binding.lSearchHistory.rvSearchHistory.adapter = searchHistoryAdapter

        itemViewModel.getAllItem().observe(this){
            searchViewModel.setAllItem(it)
        }
        searchViewModel.resultList.observe(this){
            searchResultAdapter.submitList(it)
            binding.lSearchEmptyState.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        searchHistoryViewModel.searchHistoryPaginate.observe(this){
            searchHistoryAdapter.submitList(it)
        }
        searchHistoryViewModel.isHistoryMaxPage.observe(this){
            binding.lSearchHistory.tvSearchHistoryLoadMoreHistory.visibility = if (it) View.GONE else View.VISIBLE
        }
        searchHistoryViewModel.unitPaginate.observe(this){
            insertUnitIntoChipGroup(layoutInflater, it, searchViewModel.searchFilter.value?.units, binding.lSearchHistory.chipGroupSearchHistory){b, u ->
                if (b) searchViewModel.addUnitToSearchFilter(u)
                else searchViewModel.removeUnitFromSearchFilter(u)
            }
        }
        searchHistoryViewModel.isUnitMaxPage.observe(this){
            binding.lSearchHistory.tvSearchHistoryLoadMoreUnit.visibility = if (it) View.GONE else View.VISIBLE
        }

        binding.apply {
            btnSearchBack.setOnClickListener { onBackPressed() }
            lSearchHistory.chipSearchHistoryBarcode.setOnCheckedChangeListener { _, b ->
                setChipStyle(binding.lSearchHistory.chipSearchHistoryBarcode, b)
                searchViewModel.search(isBarcodeItem = b)
            }
            lSearchHistory.chipSearchHistoryWithoutBarcode.setOnCheckedChangeListener { _, b ->
                setChipStyle(binding.lSearchHistory.chipSearchHistoryWithoutBarcode, b)
                searchViewModel.search(isNonBarcodeItem = b)
            }
            tieSearch.doOnTextChanged { text, _, _, _ ->
                if (text.toString().isBlank()){
                    binding.lSearchHistory.root.visibility = View.VISIBLE
                }else{
                    binding.lSearchHistory.root.visibility = View.GONE
                }
                searchViewModel.search(text.toString())
            }
            tieSearch.setOnFocusChangeListener { _, b ->
                if (b && tieSearch.text.toString().isBlank()) binding.lSearchHistory.root.visibility = View.VISIBLE
            }

            tieSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener{
                override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                    if (p1 == EditorInfo.IME_ACTION_SEARCH){
                        searchViewModel.search(binding.tieSearch.text.toString())
                        binding.lSearchHistory.root.visibility = View.GONE
                        binding.tieSearch.clearFocus()
                        val im: InputMethodManager =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        im.hideSoftInputFromWindow(binding.tieSearch.windowToken, 0)
                        return true
                    }
                    return false
                }
            })
            lSearchHistory.tvSearchHistoryLoadMoreHistory.setOnClickListener { searchHistoryViewModel.getHistoryWithPaginate() }
            lSearchHistory.tvSearchHistoryLoadMoreUnit.setOnClickListener { searchHistoryViewModel.getUnitWithPaginate() }
            btnSearchFilter.setOnClickListener { showFilterBottomSheet() }
        }

    }

    override fun onResume() {
        super.onResume()
        searchViewModel.searchFilter.value?.let {
            setChipStyle(binding.lSearchHistory.chipSearchHistoryBarcode, it.isBarcodeItem)
            setChipStyle(binding.lSearchHistory.chipSearchHistoryWithoutBarcode, it.isNonBarcodeItem)
        }
        if (isFirstEnter){
            isFirstEnter = false
            return
        }
        binding.tieSearch.requestFocus()
        searchViewModel.search(binding.tieSearch.text.toString())
    }

    private fun showFilterBottomSheet(){
        binding.tilSearch.clearFocus()
        binding.lSearchHistory.root.visibility = View.GONE
        SearchFilterBottomSheet(
            searchViewModel.searchFilter.value?.copy(),
            searchHistoryViewModel
        ){
            if (it != null) {
                searchViewModel.setSearchFilter(it)
            }
        }.show(supportFragmentManager, null)
    }
}













