package com.africinnovate.algorandandroidkotlin.views

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.africinnovate.algorandandroidkotlin.R
import com.africinnovate.algorandandroidkotlin.databinding.ActivityMainBinding
import com.africinnovate.algorandandroidkotlin.utils.Constants.CREATOR_ADDRESS
import com.africinnovate.algorandandroidkotlin.utils.Constants.CREATOR_MNEMONIC
import com.africinnovate.algorandandroidkotlin.utils.Constants.FUND_ACCOUNT
import com.africinnovate.algorandandroidkotlin.viewmodel.AccountViewmodel
import com.africinnovate.algorandandroidkotlin.viewmodel.StatefulSmartContractViewModel
import com.africinnovate.algorandandroidkotlin.viewmodel.StatelessSmartContractViewModel
import com.africinnovate.algorandandroidkotlin.viewmodel.TransactionViewmodel
import dagger.hilt.android.AndroidEntryPoint
import org.bouncycastle.jce.provider.BouncyCastleProvider
import timber.log.Timber
import java.nio.file.Path
import java.nio.file.Paths
import java.security.Security


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: AccountViewmodel by viewModels()
    private val transactionViewModel: TransactionViewmodel by viewModels()
    private val statefulSmartContractViewModel: StatefulSmartContractViewModel by viewModels()
    private val statelessSmartContractViewModel: StatelessSmartContractViewModel by viewModels()
   lateinit var binding : ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        val text = findViewById<TextView>(R.id.home)
//        val stflBtn = findViewById<Button>(R.id.stateful_smart_contract)
//        val stlesBtn = findViewById<Button>(R.id.stateless_smart_contract)
//        val dashboard = findViewById<Button>(R.id.dasboard)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        Security.removeProvider("BC")
        Security.insertProviderAt(BouncyCastleProvider(), 0)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        binding.dashboard.setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        val thread = Thread {
            try {
                //Your code goes here
               binding.stflContract.setOnClickListener {
                    statefulSmartContractViewModel.statefulSmartContract()
                }

                binding.stlssContract.setOnClickListener {
                    statelessSmartContractViewModel.compileTealSource()
                    statelessSmartContractViewModel.contractAccount()
                    statelessSmartContractViewModel.accountDelegation()
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        thread.start()
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
        val filename = "sample1.teal"
        val pathToFile: Path = Paths.get(filename)
        System.out.println( pathToFile.toAbsolutePath())

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
        viewModel.recoverAccount(CREATOR_MNEMONIC).observe(this, Observer {
            binding.recoveryPublicKeyAddress.text = it.address.toString()
            binding.recoveryPassphrase.text = it.toMnemonic()
        })
    }

}