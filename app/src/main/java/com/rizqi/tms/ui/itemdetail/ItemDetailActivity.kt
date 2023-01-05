package com.rizqi.tms.ui.itemdetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityItemDetailBinding
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.ui.dialog.warning.WarningDialog
import com.rizqi.tms.ui.updateitem.UpdateItemActivity
import com.rizqi.tms.utility.*
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
                if (it == null) {
                    return@observe
                }
                itemWithPrices = it
                binding.itemName = it.item.name
                binding.dateString = getDateString(it.item.lastUpdate)
                val priceAdapter = PriceDetailAdapter(it.prices.toMutableList())
                binding.rvItemDetailPriceList.adapter = priceAdapter
                CoroutineScope(Dispatchers.IO).launch{
                    val bitmap = it.item.imagePath?.let { it1 -> getBitmapFromPath(it1) }
                    withContext(Dispatchers.Main){
                        Glide.with(this@ItemDetailActivity).load(bitmap).into(binding.ivItemDetailImage)
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
        binding.fabItemDetailEdit.setOnClickListener{
            val intent = Intent(this, UpdateItemActivity::class.java)
            intent.putExtra(ITEM_ID, itemId)
            startActivity(intent)
        }
        binding.cvItemDetailDelete.setOnClickListener {
            WarningDialog(
                onPositiveClickListener = {
                    itemWithPrices?.let { item ->
                        showLoading(binding.lItemDetailLoading)
                        itemViewModel.deleteItem(item.item)
                        itemViewModel.deleteItem.observe(this){res ->
                            when(res){
                                is Resource.Error -> {
                                    hideLoading(binding.lItemDetailLoading)
                                    Toast.makeText(this, res.stringResource?.asString(this), Toast.LENGTH_SHORT).show()
                                }
                                is Resource.Success -> {
                                    hideLoading(binding.lItemDetailLoading)
                                    finish()
                                }
                            }
                        }
                    }
            },
                title = getString(R.string.delete_this_item),
                description = getString(R.string.are_you_sure_delete_this_item)
            ).show(supportFragmentManager, null)
        }
    }
}