package com.rizqi.tms.ui.dashboard.cashier

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rizqi.tms.R
import com.rizqi.tms.databinding.FragmentCashierBinding
import com.rizqi.tms.ui.cashiersystem.CashierSystemActivity

class CashierFragment : Fragment() {

    private var _binding : FragmentCashierBinding? = null
    private val binding : FragmentCashierBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCashierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnCashierCreateTransaction.setOnClickListener {
                Intent(context, CashierSystemActivity::class.java).also {itn ->
                    startActivity(itn)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}