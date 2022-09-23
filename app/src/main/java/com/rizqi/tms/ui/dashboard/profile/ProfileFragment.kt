package com.rizqi.tms.ui.dashboard.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rizqi.tms.R
import com.rizqi.tms.TMSPreferences.Companion.getUserId
import com.rizqi.tms.TMSPreferences.Companion.isAnonymous
import com.rizqi.tms.TMSPreferences.Companion.setAnonymous
import com.rizqi.tms.TMSPreferences.Companion.setFirebaseUserId
import com.rizqi.tms.TMSPreferences.Companion.setLogin
import com.rizqi.tms.TMSPreferences.Companion.setUserId
import com.rizqi.tms.databinding.FragmentProfileBinding
import com.rizqi.tms.model.Setting
import com.rizqi.tms.ui.dashboard.DashboardActivity
import com.rizqi.tms.ui.onboarding.OnBoardingActivity
import com.rizqi.tms.utility.getInitialBitmap
import com.rizqi.tms.utility.getInitialPlaceholder
import com.rizqi.tms.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding : FragmentProfileBinding? = null
    private val binding : FragmentProfileBinding
        get() = _binding!!
    private val userViewModel : UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =  FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (context?.isAnonymous() == true){
            binding.tvProfileUsername.text = getString(R.string.anonymous)
            binding.tvProfileEdit.visibility = View.GONE
            Glide.with(binding.root).load(context?.getInitialBitmap(getString(R.string.anonymous))).into(binding.ivProfileImage)
        }else{
            userViewModel.getUserById(context?.getUserId() ?: -1).observe(viewLifecycleOwner){
                binding.apply {
                    tvProfileUsername.text = it.name
                    tvProfileEmail.text = it.email
                }
                Glide.with(binding.root)
                    .load(Firebase.auth.currentUser?.photoUrl).
                    placeholder(context?.getInitialPlaceholder(it.name))
                    .into(binding.ivProfileImage)
            }
        }

        val settingAdapter = SettingsAdapter{
            Firebase.auth.signOut()
            context?.setLogin(false)
            context?.setAnonymous(false)
            context?.setFirebaseUserId("")
            context?.setUserId(0)
            val intent = Intent(context, OnBoardingActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            activity?.finish()
        }
        settingAdapter.submitList(Setting.getSettings(resources))
        binding.rvProfileSettings.adapter = settingAdapter

    }

}