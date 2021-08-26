package com.africinnovate.algorandandroidkotlin.views

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.africinnovate.algorandandroidkotlin.R
import com.africinnovate.algorandandroidkotlin.adapter.BaseRecyclerAdapter
import com.africinnovate.algorandandroidkotlin.databinding.ActivityDashboardBinding
import com.africinnovate.algorandandroidkotlin.model.Transactions
import com.africinnovate.algorandandroidkotlin.utils.Constants
import com.africinnovate.algorandandroidkotlin.utils.Constants.CREATOR_ADDRESS
import com.africinnovate.algorandandroidkotlin.utils.Constants.SRC_ADDRESS
import com.africinnovate.algorandandroidkotlin.viewmodel.AccountViewmodel
import com.africinnovate.algorandandroidkotlin.viewmodel.TransactionViewmodel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class Dashboard : AppCompatActivity() {
    private val myAdapter: BaseRecyclerAdapter = BaseRecyclerAdapter()
    private var transactions: List<Transactions> = ArrayList()
    private lateinit var binding: ActivityDashboardBinding
    private val transactionViewModel: TransactionViewmodel by viewModels()
    private val viewModel: AccountViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dashboard)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);

        getWalletBalance()
        initializeRecyclerView()
        setData()
        showMessage()
        showProgress()

        myAdapter.layoutId = R.layout.transactions_list_items
        myAdapter.items = transactions
        myAdapter.onCustomClickItemListner = { view, position ->
            // To perform onclick to detail page
        }
        binding.send.setOnClickListener {
            TransferFundDailogFragment().show(supportFragmentManager, "Transfer")
        }
        binding.receive.setOnClickListener { fundAccount() }
//        binding.walletAddressTv.text = CREATOR_ADDRESS

//        binding.copyIcon.setOnClickListener { Viewutil.copy(binding.walletAddressTv, this) }

        binding.copy.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", SRC_ADDRESS)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, SRC_ADDRESS, Toast.LENGTH_LONG).show()
        }

        binding.root
    }

    fun initializeRecyclerView() {
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BaseRecyclerAdapter()
            setHasFixedSize(true)
            adapter = myAdapter
        }
    }

    /**
     * The [setData] sets the data on the recyclerview
     */
    private fun setData() {
        transactionViewModel.getTransactions(Constants.SRC_ADDRESS).observe(this, Observer {
            transactions = it.transactions
            Timber.d("Transactions ${transactions.size}")
            try {
                if (transactions.isNotEmpty()) {
                    binding.recyclerview.visibility = View.VISIBLE
                    myAdapter.setData(transactions)
                    binding.emptyState.visibility = View.GONE

                } else {
                    binding.recyclerview.visibility = View.GONE
                    binding.emptyState.visibility = View.VISIBLE
                }
            }catch (e : java.lang.Exception){
                Timber.d(e)
            }

        })
    }

    private fun getWalletBalance() {
        viewModel.getAccount(Constants.SRC_ADDRESS)
            .observe(this, {
                try {
                    binding.balance.text = it.account.amount.toString()
                    Timber.d("amount  ${it.account.amount}")
                } catch (e: Exception) {
                    e.message
                }
            })
    }

    private fun fundAccount() {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.FUND_ACCOUNT))
        try {
            if (!Constants.FUND_ACCOUNT.startsWith("http://") && !Constants.FUND_ACCOUNT.startsWith(
                    "https://"
                )
            )
                Constants.FUND_ACCOUNT = "http://" + Constants.FUND_ACCOUNT;
            startActivity(browserIntent)
        } catch (e: Exception) {
            Timber.d("Host not available ${e.message}")
        }
    }

    /**
     * This shows the progressbar when loading the transactions list data
     */
    private fun showProgress() {
        transactionViewModel.showProgress.observe(this, Observer {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    /**
     * This shows a toast message when an error occurs
     */
    private fun showMessage() {
        transactionViewModel.showMessage.observe(this, Observer {
            val message = it
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.dashboard_options, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.stateless_smart_contract -> {
                startActivity(Intent(this, StatelessActivity::class.java))
                true
            }
            R.id.stateful_smart_contract -> {
                startActivity(Intent(this, StatefulActivity::class.java))
                true
            }
            R.id.logout -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}