package com.africinnovate.algorandandroidkotlin.views

import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.africinnovate.algorandandroidkotlin.R
import com.africinnovate.algorandandroidkotlin.databinding.ActivityMainBinding
import com.africinnovate.algorandandroidkotlin.utils.Constants.CREATOR_MNEMONIC
import com.africinnovate.algorandandroidkotlin.utils.Constants.SRC_MNEMONIC
import com.africinnovate.algorandandroidkotlin.viewmodel.AccountViewmodel
import com.africinnovate.algorandandroidkotlin.viewmodel.StatefulSmartContractViewModel
import com.africinnovate.algorandandroidkotlin.viewmodel.StatelessSmartContractViewModel
import com.africinnovate.algorandandroidkotlin.viewmodel.TransactionViewmodel
import dagger.hilt.android.AndroidEntryPoint
import org.bouncycastle.jce.provider.BouncyCastleProvider
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.security.Security
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: AccountViewmodel by viewModels()
    private val transactionViewModel: TransactionViewmodel by viewModels()
    private val statefulSmartContractViewModel: StatefulSmartContractViewModel by viewModels()
    private val statelessSmartContractViewModel: StatelessSmartContractViewModel by viewModels()
   lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 0)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        binding.dashboard.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }
        binding.stflContract.setOnClickListener {
            val intent = Intent(this, StatefulActivity::class.java)
            startActivity(intent)
        }

        binding.stlssContract.setOnClickListener {
            val intent = Intent(this, StatelessActivity::class.java)
            startActivity(intent)
        }
        binding.createAccount.setOnClickListener {
            createAccount()
            if (binding.recoveryConstraint.visibility == View.VISIBLE){
                binding.createConstraint.visibility = View.VISIBLE
                binding.recoveryConstraint.visibility = View.GONE
            }else{
                binding.createConstraint.visibility = View.VISIBLE
            }

        }
        binding.recoveryAccount.setOnClickListener {
            recoverAccount()
            if (binding.createConstraint.visibility == View.VISIBLE){
                binding.createConstraint.visibility = View.GONE
                binding.recoveryConstraint.visibility = View.VISIBLE
            }else{
                binding.recoveryConstraint.visibility = View.VISIBLE
            }
        }

        binding.root

    }

    private fun createAccount(){
       viewModel.generateAlgodPair().observe(this, {
           binding.publicKeyAddress.text = it.address.toString()
           binding.newPassphrase.text = it.toMnemonic()
           Timber.d("address ${it.address}")
           Timber.d("phrase ${it.toMnemonic()}")
       })
    }

    private fun recoverAccount(){
        viewModel.recoverAccount(SRC_MNEMONIC).observe(this, Observer {
            binding.recoveryPublicKeyAddress.text = it.address.toString()
            binding.recoveryPassphrase.text = it.toMnemonic()
        })
    }

}