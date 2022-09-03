package com.rizqi.tms.ui.updateitem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityUpdateItemBinding
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.model.PriceAndSubPrice
import com.rizqi.tms.ui.dialog.createprice.CreatePriceBottomSheet
import com.rizqi.tms.ui.dialog.success.SuccessDialog
import com.rizqi.tms.ui.dialog.warning.WarningDialog
import com.rizqi.tms.ui.itemdetail.PriceDetailAdapter
import com.rizqi.tms.ui.takeimage.TakeImageActivity
import com.rizqi.tms.utility.*
import com.rizqi.tms.viewmodel.ItemViewModel
import com.rizqi.tms.viewmodel.UnitViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class UpdateItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUpdateItemBinding
    private var itemId : Long? = null
    private var itemWithPrices : ItemWithPrices? = null
    private val itemViewModel : ItemViewModel by viewModels()
    private val viewModel : UpdateItemViewModel by viewModels()
    private val unitViewModel : UnitViewModel by viewModels()
    private var priceList = mutableListOf<PriceAndSubPrice>()
    private val priceAdapter = PriceDetailAdapter(priceList)

    private val takeImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == RESULT_OK && it.data != null){
            getAndDecodeBitmap(this)?.let { bitmap ->
                Glide.with(this).load(bitmap).placeholder(R.drawable.image_placeholder).into(binding.ivUpdateItemImage)
                viewModel.setImageBitmap(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        itemId = intent?.getLongExtra(ITEM_ID, -1)
        if (itemId != null){
            itemViewModel.getItemById(itemId!!).observe(this){
                itemWithPrices = it
                binding.tilUpdateItemName.editText.setText(it.item.name)
                setCheckedAdapter(binding.cbUpdateItemRemindMe, it.item.isReminded)
                viewModel.setReminded(it.item.isReminded)

                priceList = it.prices.toMutableList()
                priceAdapter.setList(priceList)
                priceAdapter.onItemClickListener = {priceAndSubPrice, i ->
                    showUpdatePriceDialog(priceAndSubPrice, i)
                }
                binding.rvItemDetailPriceList.adapter = priceAdapter

                CoroutineScope(Dispatchers.IO).launch{
                    val bitmap = it.item.imagePath?.let { it1 -> getBitmapFromPath(it1) }
                    withContext(Dispatchers.Main){
                        Glide.with(this@UpdateItemActivity).load(bitmap).into(binding.ivUpdateItemImage)
                        binding.ivUpdateItemImage.visibility = View.VISIBLE
                        binding.shimmerUpdateItemImage.apply {
                            stopShimmer()
                            visibility = View.GONE
                        }
                    }
                }

            }
        }else{
            Toast.makeText(this, getString(R.string.item_not_found), Toast.LENGTH_SHORT).show()
            binding.lUpdateItemSave.visibility = View.GONE
        }

        unitViewModel.getListUnit().observe(this){unitList ->
            priceList.forEach { priceSubPrice ->
                priceSubPrice.unit = unitList.firstOrNull{ u -> u.id == priceSubPrice.price.unitId}
            }
            priceAdapter.setList(priceList)
        }

        binding.apply {
            tilUpdateItemName.editText.doAfterTextChanged { viewModel.setName(it.toString()) }
            btnUpdateItemBack.setOnClickListener { onBackPressed() }
            tvUpdateItemCancel.setOnClickListener { onBackPressed() }
            cbUpdateItemRemindMe.setOnCheckedChangeListener { _, b ->
                setCheckedAdapter(binding.cbUpdateItemRemindMe, b)
                viewModel.setReminded(b)
            }
            btnUpdateItemTakeImage.setOnClickListener {
                takeImageLauncher.launch(Intent(this@UpdateItemActivity, TakeImageActivity::class.java))
            }
            fabUpdateItemCreatePrice.setOnClickListener { showCreatePriceDialog() }
            btnUpdateItemSave.setOnClickListener {
                if(viewModel.name.value.isNullOrBlank()){
                    binding.tilUpdateItemName.errorText = getString(R.string.field_must_be_filled, getString(R.string.item_name))
                    return@setOnClickListener
                }else{
                    binding.tilUpdateItemName.errorText = null
                }
                if (priceAdapter.itemCount == 0){
                    Toast.makeText(this@UpdateItemActivity, getString(R.string.create_min_1_price), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                showLoading(binding.lUpdateItemLoading)
                val previousImagePath = itemWithPrices?.item?.imagePath
                itemWithPrices?.let { item ->
                    item.item.name = viewModel.name.value!!
                    item.item.isReminded = viewModel.isReminded.value!!
                    if (viewModel.imageBitmap.value != null){
                        val newPath = saveBitmapToFolder(viewModel.imageBitmap.value)
                        item.item.imagePath = newPath
                    }
                }
                itemViewModel.updateItem(itemWithPrices!!, priceAdapter.getList())
                itemViewModel.updatedItemWithPrices.observe(this@UpdateItemActivity){res ->
                    when(res){
                        is Resource.Error -> {
                            hideLoading(binding.lUpdateItemLoading)
                            Toast.makeText(this@UpdateItemActivity, res.message?.asString(this@UpdateItemActivity), Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Success -> {
                            hideLoading(binding.lUpdateItemLoading)
                            SuccessDialog(
                                description = getString(R.string.success_update_item, itemWithPrices?.item?.name)
                            , onDismissListener = {
                                    previousImagePath?.let { path ->
                                        com.rizqi.tms.utility.deleteFile(path)
                                    }
                                    finish()
                                }
                            ).show(supportFragmentManager, null)
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        WarningDialog(
            onPositiveClickListener = {
                finish()
            },
            title = getString(R.string.item_is_not_changed),
            description = getString(R.string.are_you_sure_not_change_this_item)
        ).show(supportFragmentManager, null)
    }

    private fun showCreatePriceDialog(){
        UpdatePriceBottomSheet(
            priceList.isNotEmpty(),
            priceList.lastOrNull()?.unit,
            priceList.map { priceAndSubPrice ->  priceAndSubPrice.unit},
            false
        ){priceAndSubPrice ->
            priceAdapter.addPrice(priceAndSubPrice)
        }.show(supportFragmentManager, null)
    }

    private fun showUpdatePriceDialog(priceAndSubPrice: PriceAndSubPrice, position : Int){
        UpdatePriceBottomSheet(
            priceAndSubPrice.price.prevQuantityConnector != null,
            priceList.find { priceSubPrice ->  priceSubPrice.price.unitName == priceAndSubPrice.price.prevUnitName}?.unit,
            priceList.map { priceSubPrice ->  priceSubPrice.unit}.filter { unit -> unit?.name != priceAndSubPrice.price.unitName  },
            priceAndSubPrice.price.isMainPrice,
            priceAndSubPrice,
            CrudState.UPDATE
        ){updatedPriceSubPrice ->
            priceAdapter.updatePrice(updatedPriceSubPrice, position)
        }.apply {
            onDeleteListener = {
                priceAdapter.deletePrice(priceAndSubPrice.price.isMainPrice, position)
            }
        }.show(supportFragmentManager, null)
    }

}