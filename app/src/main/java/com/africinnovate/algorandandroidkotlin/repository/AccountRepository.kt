package com.africinnovate.algorandandroidkotlin.repository

import com.africinnovate.algorandandroidkotlin.UserAccount
import com.africinnovate.algorandandroidkotlin.model.UserAccount2
import com.africinnovate.algorandandroidkotlin.model.WalletAccount
import com.algorand.algosdk.account.Account

interface AccountRepository {
    suspend fun getAccount(address : String) : WalletAccount?
    suspend fun generateAlgodPair(): Account
    suspend fun recoverAccount(passPhrase : String): Account
}