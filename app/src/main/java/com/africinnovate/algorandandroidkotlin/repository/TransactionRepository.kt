package com.africinnovate.algorandandroidkotlin.repository

import androidx.lifecycle.LiveData
import com.africinnovate.algorandandroidkotlin.model.AccountTransactions
import com.africinnovate.algorandandroidkotlin.model.Transactions
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.transaction.SignedTransaction
import timber.log.Timber

interface TransactionRepository {
    suspend fun transferFund()
    suspend fun submitTransaction(signedTxn: SignedTransaction): String
    suspend fun waitForConfirmation(txID: String)
    suspend fun getTransactionsByAddress(address : String) : AccountTransactions?

}