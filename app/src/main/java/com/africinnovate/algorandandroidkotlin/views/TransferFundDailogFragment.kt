package com.africinnovate.algorandandroidkotlin.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.africinnovate.algorandandroidkotlin.R
import com.africinnovate.algorandandroidkotlin.Resource
import com.africinnovate.algorandandroidkotlin.databinding.TransferFundBinding
import com.africinnovate.algorandandroidkotlin.viewmodel.TransactionViewmodel
import dagger.hilt.android.AndroidEntryPoint


/**
 * A simple [Fragment] subclass.
 * Use the [TransferFundDailogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class TransferFundDailogFragment : DialogFragment() {
    private lateinit var binding: TransferFundBinding
    private val transactionViewModel: TransactionViewmodel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.transfer_fund, container, false
        )
        binding.transferFund.setOnClickListener {
            validateTransfer()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun validateTransfer() {
        val amount = binding.amount.text.toString().trim()
        val receiverAddress: String = binding.address.text.toString()

        if (amount.isEmpty() && receiverAddress.isEmpty()) {
            Toast.makeText(context, "Fields must be filled", Toast.LENGTH_LONG).show()
            return
        } else {
            transactionViewModel.transferFund(amount.toLong(), receiverAddress)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        fun newInstance(amount: Long, address: String): TransferFundDailogFragment? {
            val dialog = TransferFundDailogFragment()
            val bundle = Bundle()
            bundle.putString("address", address)
            bundle.putLong("amount", amount)
            dialog.arguments = bundle
            return dialog
        }
    }


}