package com.africinnovate.algorandandroidkotlin.repository

import androidx.lifecycle.LiveData
import com.africinnovate.algorandandroidkotlin.Resource
import com.africinnovate.algorandandroidkotlin.model.AccountTransactions
import com.africinnovate.algorandandroidkotlin.model.Transactions
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse
import timber.log.Timber

interface TransactionRepository {
    suspend fun transferFund(amount: Long, receiverAddress: String)
    suspend fun submitTransaction(signedTxn: SignedTransaction): String
    suspend fun waitForConfirmation(
        myclient: AlgodClient?,
        txID: String?,
        timeout: Int
    ): PendingTransactionResponse?

    suspend fun getTransactionsByAddress(address: String): AccountTransactions?

}