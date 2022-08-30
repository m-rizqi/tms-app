package com.rizqi.tms.ui.dashboard.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.rizqi.tms.R
import com.rizqi.tms.databinding.FragmentHomeBinding
import com.rizqi.tms.ui.createitem.CreateItemActivity
import com.rizqi.tms.utility.GridSpacingItemDecoration
import com.rizqi.tms.utility.collapseAccordion
import com.rizqi.tms.utility.expandAccordion
import com.rizqi.tms.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding : FragmentHomeBinding
        get() = _binding!!
    private val itemViewModel : ItemViewModel by viewModels()
    private val popularAdapter = GridItemAdapter()
    private val nonBarcodeItemAdapter = GridItemAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        itemViewModel.getItemCount().observe(viewLifecycleOwner){
            binding.totalItems = it.toString()
        }
        itemViewModel.getBarcodeItemCount().observe(viewLifecycleOwner){
            binding.barcodeTotal = it.toString()
        }
        itemViewModel.getNonBarcodeItemCount().observe(viewLifecycleOwner){
            binding.nonBarcodeTotal = it.toString()
        }
        itemViewModel.getPopularItems().observe(viewLifecycleOwner){
            popularAdapter.submitList(it)
        }
        itemViewModel.getNonBarcodeItemsLimited().observe(viewLifecycleOwner){
            nonBarcodeItemAdapter.submitList(it)
        }

        binding.apply {
            expandAccordion(binding.rvHomeOftenUsedItems, binding.ivHomeArrowPopular)
            cbHomePopularAccordian.setOnCheckedChangeListener { _, b ->
                if (b) expandAccordion(binding.rvHomeOftenUsedItems, binding.ivHomeArrowPopular)
                else collapseAccordion(binding.rvHomeOftenUsedItems, binding.ivHomeArrowPopular)
            }
            rvHomeOftenUsedItems.apply {
                adapter = popularAdapter
                addItemDecoration(GridSpacingItemDecoration(2, 32, false))
            }
            rvHomeNonbarcodeItems.apply {
                adapter = nonBarcodeItemAdapter
                addItemDecoration(GridSpacingItemDecoration(2, 32, false))
            }
            srlHome.setOnRefreshListener { binding.srlHome.isRefreshing = false }
            fabHomeCreate.setOnClickListener {
                startActivity(Intent(context, CreateItemActivity::class.java))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}