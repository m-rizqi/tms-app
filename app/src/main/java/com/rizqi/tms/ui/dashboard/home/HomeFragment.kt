package com.rizqi.tms.ui.dashboard.home

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.rizqi.tms.R
import com.rizqi.tms.databinding.FragmentHomeBinding
import com.rizqi.tms.ui.barcodesearch.BarcodeSearchActivity
import com.rizqi.tms.ui.createitem.CreateItemActivity
import com.rizqi.tms.utility.GridSpacingItemDecoration
import com.rizqi.tms.utility.collapseAccordion
import com.rizqi.tms.utility.expandAccordion
import com.rizqi.tms.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding : FragmentHomeBinding? = null
    private val binding : FragmentHomeBinding
        get() = _binding!!
    private val itemViewModel : ItemViewModel by viewModels()
    private val popularAdapter = GridItemAdapter()
    private val nonBarcodeItemAdapter = GridItemAdapter()
    private val notificationAdapter = RemindedItemAdapter()

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
            binding.lHomeEmptyStatePopular.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        itemViewModel.getNonBarcodeItemsLimited().observe(viewLifecycleOwner){
            nonBarcodeItemAdapter.submitList(it)
            binding.lHomeEmptyStateNonbarcode.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        }
        itemViewModel.getRemindedItems().observe(viewLifecycleOwner){
            notificationAdapter.submitList(it)
            binding.lNotificationDialog.lNotificationEmptyState.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            binding.cvHomeNotificationBadge.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.apply {
            expandAccordion(binding.lHomeRvAndEmpty, binding.ivHomeArrowPopular)
            cbHomePopularAccordian.setOnCheckedChangeListener { _, b ->
                if (b) expandAccordion(binding.lHomeRvAndEmpty, binding.ivHomeArrowPopular)
                else collapseAccordion(binding.lHomeRvAndEmpty, binding.ivHomeArrowPopular)
            }
            rvHomeOftenUsedItems.apply {
                adapter = popularAdapter
                addItemDecoration(GridSpacingItemDecoration(2, 32, false))
            }
            rvHomeNonbarcodeItems.apply {
                adapter = nonBarcodeItemAdapter
                addItemDecoration(GridSpacingItemDecoration(2, 32, false))
            }
            lNotificationDialog.rvNotificationRemindedItems.adapter = notificationAdapter
            srlHome.setOnRefreshListener { binding.srlHome.isRefreshing = false }
            fabHomeCreate.setOnClickListener {
                startActivity(Intent(context, CreateItemActivity::class.java))
            }
            cbHomeNotification.setOnClickListener {
                binding.cbHomeNotification.isChecked = !(it as CheckBox).isChecked
                showNotificationDialog()
            }
            lNotificationDialog.tvNotificationMarkRead.setOnClickListener {
                val remindedItems = notificationAdapter.currentList
                remindedItems.forEach { itemWithPrices ->
                    itemWithPrices.item.isReminded = false
                    itemViewModel.viewModelScope.launch(Dispatchers.IO){
                        itemViewModel.updateItem(itemWithPrices.item)
                    }
                }
                notificationAdapter.submitList(mutableListOf())
                binding.lNotificationDialog.lNotificationEmptyState.visibility = View.VISIBLE
            }
            mcvHomeScan.setOnClickListener {
                startActivity(Intent(context, BarcodeSearchActivity::class.java))
            }
        }

        val notificationParams = binding.lNotificationDialog.root.layoutParams
        notificationParams.width = (0.8 * Resources.getSystem().displayMetrics.widthPixels).toInt()
        notificationParams.height = (0.6 * Resources.getSystem().displayMetrics.heightPixels).toInt()
        binding.lNotificationDialog.root.layoutParams = notificationParams
        binding.lNotificationDialog.root.requestLayout()
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