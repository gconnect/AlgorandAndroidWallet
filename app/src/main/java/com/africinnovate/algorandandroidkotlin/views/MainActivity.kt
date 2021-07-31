package com.africinnovate.algorandandroidkotlin.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.os.StrictMode
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.africinnovate.algorandandroidkotlin.R
import com.africinnovate.algorandandroidkotlin.utils.Constants.FUND_ACCOUNT
import com.africinnovate.algorandandroidkotlin.viewmodel.AccountViewmodel
import com.africinnovate.algorandandroidkotlin.viewmodel.StatefulSmartContractViewModel
import com.africinnovate.algorandandroidkotlin.viewmodel.TransactionViewmodel
import dagger.hilt.android.AndroidEntryPoint
import org.bouncycastle.jce.provider.BouncyCastleProvider
import timber.log.Timber
import java.security.Security


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: AccountViewmodel by viewModels()
    private val transactionViewModel: TransactionViewmodel by viewModels()
    private val statefulSmartContractViewModel: StatefulSmartContractViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val text = findViewById<TextView>(R.id.home)
        val stflBtn = findViewById<TextView>(R.id.stateful_smart_contract)
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 0)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        viewModel.generateAlgodPair().observe(this, {
            text.text = it.address.toString()
            Timber.d("address ${it.address}")
            Timber.d("phrase ${it.toMnemonic()}")
        })
        getWalletBalance()
        transferFund()
        text.setOnClickListener { fundAccount() }

        val thread = Thread {
            try {
                //Your code goes here
                stflBtn.setOnClickListener {
                    statefulSmartContractViewModel.statefulSmartContract()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        thread.start()

        transactionViewModel.getTransactions("RPP7BXWZ3TVFR2FRWAFX5TH6GNP5PZNGCVCZPMFOP5MX376Q7Q2MA2YEDU")
            .observe(this, {
                try {
                    Timber.d("transactions ${it.transactions}")
                } catch (e: Exception) {
                    e.message
                }
            })
    }

    private fun getWalletBalance() {
        viewModel.getAccount("LY2MVEUGJ2Q73NY3FMG36SKSTECCV2W5CB33ZWLNGOLDJKOQJXPPPCPLV4")
            .observe(this, {
                try {
                    it.account.amount
                    Timber.d("amount ${it.account.amount}")
                } catch (e: Exception) {
                    e.message
                }
            })
    }

    private fun transferFund() {
        transactionViewModel.transferFund()
    }

    private fun fundAccount() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(FUND_ACCOUNT))
        try {
            if (!FUND_ACCOUNT.startsWith("http://") && !FUND_ACCOUNT.startsWith("https://"))
                FUND_ACCOUNT = "http://" + FUND_ACCOUNT;
            startActivity(browserIntent)
        } catch (e: Exception) {
            Timber.d("Host not available ${e.message}")
        }
    }


}