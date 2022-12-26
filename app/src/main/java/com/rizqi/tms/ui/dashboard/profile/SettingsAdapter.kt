package com.rizqi.tms.ui.dashboard.profile

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizqi.tms.R
import com.rizqi.tms.databinding.ItemSettingBinding
import com.rizqi.tms.model.Setting

class SettingsAdapter(
    private val onLogoutListener : () -> Unit
) : ListAdapter<Setting, SettingsAdapter.SettingViewHolder>(DiffCallback){

    class SettingViewHolder(val binding : ItemSettingBinding) : RecyclerView.ViewHolder(binding.root)

    companion object DiffCallback : DiffUtil.ItemCallback<Setting>(){
        override fun areItemsTheSame(oldItem: Setting, newItem: Setting): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Setting, newItem: Setting): Boolean {
            return oldItem.name == newItem.name && oldItem.description == newItem.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        return SettingViewHolder(
            ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        val binding = holder.binding
        val setting = getItem(position)

        Glide.with(binding.root.context).load(setting.icon).into(binding.ivItemSetting)
        binding.tvItemSettingTitle.text = setting.name
        binding.tvItemSettingDescription.text = setting.description
        binding.root.setOnClickListener {
            if (position != itemCount - 1){
                setting.activityAction?.let { action ->
                    val intent = Intent(binding.root.context, action)
                    it.context.startActivity(intent)
                }
            }else{
                onLogoutListener()
            }
        }

        if (position == itemCount-1){
            binding.tvItemSettingTitle.setTextColor(binding.root.context.resources.getColor(R.color.danger_main))
            binding.tvItemSettingDescription.visibility = View.GONE
        }

    }
}