package com.rizqi.tms.ui.itemdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityItemDetailBinding
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.utility.ITEM_ID
import com.rizqi.tms.utility.getBitmapFromPath
import com.rizqi.tms.utility.getDateString
import com.rizqi.tms.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ItemDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityItemDetailBinding
    private var itemId : Long? = null
    private var itemWithPrices : ItemWithPrices? = null
    private val itemViewModel : ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemId = intent?.getLongExtra(ITEM_ID, -1)
        if (itemId != null){
            itemViewModel.getItemById(itemId!!).observe(this){
                itemWithPrices = it
                binding.itemName = it.item.name
                binding.dateString = getDateString(it.item.lastUpdate)
                val priceAdapter = PriceDetailAdapter(it.prices.toMutableList())
                binding.rvItemDetailPriceList.adapter = priceAdapter
                CoroutineScope(Dispatchers.IO).launch{
                    val bitmap = it.item.imagePath?.let { it1 -> getBitmapFromPath(it1) }
                    withContext(Dispatchers.Main){
                        Glide.with(this@ItemDetailActivity).load(bitmap).placeholder(R.drawable.image_placeholder).into(binding.ivItemDetailImage)
                        binding.ivItemDetailImage.visibility = View.VISIBLE
                        binding.shimmerItemDetailImage.apply {
                            stopShimmer()
                            visibility = View.GONE
                        }
                    }
                }
            }
        }else{
            Toast.makeText(this, getString(R.string.item_not_found), Toast.LENGTH_SHORT).show()
            binding.cvItemDetailDelete.visibility = View.GONE
            binding.fabItemDetailEdit.visibility = View.GONE
        }
        binding.cvItemDetailBack.setOnClickListener { onBackPressed() }
    }
}