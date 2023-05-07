package com.rizqi.tms.ui.dashboard.home

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.animation.doOnEnd
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.rizqi.tms.R
import com.rizqi.tms.databinding.FragmentHomeBinding
import com.rizqi.tms.ui.*
import com.rizqi.tms.ui.createitem.CreateItemActivity
import com.rizqi.tms.ui.dashboard.DashboardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding
        get() = _binding!!
    private val popularItemsAdapter = GridItemAdapter()
    private val nonBarcodeItemsAdapter = GridItemAdapter()
    private val viewModel : HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { uiState ->
                    uiState.popularItems.let {popularItems->
                        popularItemsAdapter.submitList(popularItems)
                    }
                    uiState.nonBarcodeItems.let { nonBarcodeItems ->
                        nonBarcodeItemsAdapter.submitList(nonBarcodeItems)
                    }
                }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        expandAccordion(binding.lHomeRvAndEmpty, binding.ivHomeArrowPopular)
        binding.apply {
            cbHomePopularAccordian.setOnCheckedChangeListener { _, b ->
                if (b) expandAccordion(binding.lHomeRvAndEmpty, binding.ivHomeArrowPopular)
                else collapseAccordion(binding.lHomeRvAndEmpty, binding.ivHomeArrowPopular)
            }
            rvHomeOftenUsedItems.apply {
                adapter = popularItemsAdapter
                addItemDecoration(GridSpacingItemDecoration(2, 32, false))
            }
            rvHomeNonbarcodeItems.apply {
                adapter = nonBarcodeItemsAdapter
                addItemDecoration(GridSpacingItemDecoration(2, 32, false))
            }
//            lNotificationDialog.rvNotificationRemindedItems.adapter = notificationAdapter
            srlHome.setOnRefreshListener { binding.srlHome.isRefreshing = false }
            fabHomeCreate.setOnClickListener {
                startActivity(Intent(context, CreateItemActivity::class.java))
            }
            cbHomeNotification.setOnClickListener {
                binding.cbHomeNotification.isChecked = !(it as CheckBox).isChecked
                showNotificationDialog()
            }
            lNotificationDialog.tvNotificationMarkRead.setOnClickListener {
//                val remindedItems = notificationAdapter.currentList
//                remindedItems.forEach { itemWithPrices ->
//                    itemWithPrices.item.isReminded = false
//                    itemViewModel.viewModelScope.launch(Dispatchers.IO){
//                        itemViewModel.updateItem(itemWithPrices.item)
//                    }
//                }
//                notificationAdapter.submitList(mutableListOf())
                binding.lNotificationDialog.lNotificationEmptyState.visibility = View.VISIBLE
            }
            mcvHomeScan.setOnClickListener {
//                startActivity(Intent(context, BarcodeSearchActivity::class.java))
            }
            mcvHomeSearch.setOnClickListener {
//                startActivity(Intent(context, SearchActivity::class.java))
            }
            tvHomeMoreNonbarcode.setOnClickListener {
//                val searchIntent = Intent(context, SearchActivity::class.java).apply {
//                    putExtra(SEARCH_FILTER, SearchFilter("", isBarcodeItem = false, isNonBarcodeItem = true))
//                }
//                startActivity(searchIntent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onDispatchTouchEvent(event: MotionEvent?){
        dispatchTouchListener(event, binding.cbHomeNotification.isChecked)
    }

    private fun showNotificationDialog(){
        binding.lNotificationDialog.root.apply {
            visibility = View.VISIBLE
            pivotX = this.layoutParams.width.toFloat()
            pivotY = 0f
        }
        val alphaAnim = ValueAnimator.ofFloat(0f, 1.0f).apply {
            addUpdateListener {
                binding.lNotificationDialog.root.alpha = it.animatedValue as Float
            }
        }
        val scaleXAnim = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                binding.lNotificationDialog.root.scaleY = it.animatedValue as Float
            }
        }
        val scaleYAnim = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                binding.lNotificationDialog.root.scaleX = it.animatedValue as Float
            }
        }
        if (!binding.cbHomeNotification.isChecked){
            AnimatorSet().apply {
                playTogether(alphaAnim, scaleXAnim, scaleYAnim)
                duration = 200
                doOnEnd {
                    binding.cbHomeNotification.isChecked = true
                }
                start()
            }
        }
    }

    @SuppressLint("Recycle")
    private fun hideNotificationDialog(){
        val alphaAnim = ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                binding.lNotificationDialog.root.alpha = it.animatedValue as Float
            }
        }
        val scaleXAnim = ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                binding.lNotificationDialog.root.scaleY = it.animatedValue as Float
            }
        }
        val scaleYAnim = ValueAnimator.ofFloat(1f, 0f).apply {
            addUpdateListener {
                binding.lNotificationDialog.root.scaleX = it.animatedValue as Float
            }
        }
        if (binding.cbHomeNotification.isChecked){
            AnimatorSet().apply {
                playTogether(alphaAnim, scaleXAnim, scaleYAnim)
                duration = 200
                doOnEnd {
                    binding.lNotificationDialog.root.visibility = View.GONE
                    binding.cbHomeNotification.isChecked = false
                }
                start()
            }
        }
    }

    private fun dispatchTouchListener(
        event: MotionEvent?,
        isFocused : Boolean? = true,
    ){
        event?.let {
            if (event.action == MotionEvent.ACTION_UP){
                val countryRect = Rect()
                binding.lNotificationDialog.root.getGlobalVisibleRect(countryRect)
                if (!countryRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    if (isFocused!!) {
                        hideNotificationDialog()
                    }
                }
            }
        }
    }

}