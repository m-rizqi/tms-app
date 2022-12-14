package com.rizqi.tms.ui.createitem

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.animation.doOnEnd
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ActivityCreateItemBinding
import com.rizqi.tms.model.ItemWithPrices
import com.rizqi.tms.ui.dialog.success.SuccessDialog
import com.rizqi.tms.utility.Resource
import com.rizqi.tms.utility.hideLoading
import com.rizqi.tms.utility.showLoading
import com.rizqi.tms.viewmodel.ItemViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateItemActivity : AppCompatActivity(), OnStepChangedListener {
    private lateinit var binding : ActivityCreateItemBinding
    private lateinit var navHostFragment : NavHostFragment
    private val itemViewModel : ItemViewModel by viewModels()

    private lateinit var navController : NavController
    private var currentStep = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fcv_create_item) as NavHostFragment
        navController = navHostFragment.navController
        binding.btnCreateItemBack.setOnClickListener { onBackPressed() }
    }

    override fun onBackPressed() {
        if (currentStep == 1){
            finish()
        }else{
            changeToStep(currentStep - 1)
            when(currentStep){
                1 -> {
                    navController.navigate(Step2CreateItemFragmentDirections.step2ToStep1CreateItem())
                }
                2 -> {
                    navController.navigate(Step3CreateItemFragmentDirections.step3ToStep2CreateItem())
                }
            }
        }
    }

    override fun changeToStep(nextStep: Int) {
        if (nextStep > currentStep){
            when(nextStep){
                2 -> {
                    animateNextStep(binding.lpiCreateItemStep1ToStep2, binding.mcvCreateItemStep2, binding.tvCreateItem2, binding.tvCreateItemPrice)
                }
                3 -> {
                    animateNextStep(binding.lpiCreateItemStep2ToStep3, binding.mcvCreateItemStep3, binding.tvCreateItem3, binding.tvCreateItemMainPrice)
                }
            }
        }else{
            when(nextStep){
                2 -> {
                    animatePrevStep(binding.lpiCreateItemStep2ToStep3, binding.mcvCreateItemStep3, binding.tvCreateItem3, binding.tvCreateItemMainPrice)
                }
                1 -> {
                    animatePrevStep(binding.lpiCreateItemStep1ToStep2, binding.mcvCreateItemStep2, binding.tvCreateItem2, binding.tvCreateItemPrice)
                }
            }
        }
        currentStep = nextStep
    }

    override fun <T> onJourneyFinished(data: T) {
        val itemWithPrices = data as ItemWithPrices
        itemViewModel.insertItemWithPrices(itemWithPrices)
        itemViewModel.insertItemWithPrices.observe(this){result ->
            when(result){
                is Resource.Error -> {
                    hideLoading(binding.lCreateItemLoading)
                    Toast.makeText(this, result.message?.asString(this), Toast.LENGTH_SHORT).show()
                }
                is Resource.Success -> {
                    hideLoading(binding.lCreateItemLoading)
                    SuccessDialog(getString(R.string.success_add_item_description, itemWithPrices.item.name), {
                        finish()
                    }).show(supportFragmentManager, null)
                }
            }
        }
    }

    override fun showLoading() {
        showLoading(binding.lCreateItemLoading)
    }

    private fun animateNextStep(
        linearProgressIndicator: LinearProgressIndicator,
        nextCardView : MaterialCardView,
        nextStepTextView : TextView,
        nextTitleTextView : TextView
    ){
        val bgAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.track_color),
            resources.getColor(R.color.primary_100)
        ).apply {
            addUpdateListener {
                nextCardView.setCardBackgroundColor(it.animatedValue as Int)
            }
        }
        val stepAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.primary_60),
            resources.getColor(R.color.white)
        ).apply {
            addUpdateListener {
                nextStepTextView.setTextColor(it.animatedValue as Int)
            }
        }
        val titleAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.black_60),
            resources.getColor(R.color.black_100)
        ).apply {
            addUpdateListener {
                nextTitleTextView.setTextColor(it.animatedValue as Int)
            }
        }

        ValueAnimator.ofInt(0, 100).apply {
            addUpdateListener {
                linearProgressIndicator.progress = it.animatedValue as Int
            }
            doOnEnd {
                AnimatorSet().apply {
                    playTogether(bgAnim, stepAnim, titleAnim)
                    start()
                }
            }
            start()
        }
    }

    private fun animatePrevStep(
        linearProgressIndicator: LinearProgressIndicator,
        prevCardView : MaterialCardView,
        prevStepTextView : TextView,
        prevTitleTextView : TextView
    ){
        val bgAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.primary_100),
            resources.getColor(R.color.track_color)
        ).apply {
            addUpdateListener {
                prevCardView.setCardBackgroundColor(it.animatedValue as Int)
            }
        }
        val stepAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.white),
            resources.getColor(R.color.primary_60)
        ).apply {
            addUpdateListener {
                prevStepTextView.setTextColor(it.animatedValue as Int)
            }
        }
        val titleAnim = ValueAnimator.ofObject(
            ArgbEvaluator(),
            resources.getColor(R.color.black_100),
            resources.getColor(R.color.black_60)
        ).apply {
            addUpdateListener {
                prevTitleTextView.setTextColor(it.animatedValue as Int)
            }
        }

        AnimatorSet().apply {
            playTogether(bgAnim, stepAnim, titleAnim)
            doOnEnd {
                ValueAnimator.ofInt(100, 0).apply {
                    addUpdateListener {
                        linearProgressIndicator.progress = it.animatedValue as Int
                    }
                    start()
                }
            }
            start()
        }

    }

}