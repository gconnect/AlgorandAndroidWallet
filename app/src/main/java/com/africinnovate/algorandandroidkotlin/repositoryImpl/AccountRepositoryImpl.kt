package com.africinnovate.algorandandroidkotlin.repositoryImpl

import com.africinnovate.algorandandroidkotlin.ClientService.APIService
import com.africinnovate.algorandandroidkotlin.UserAccount
import com.africinnovate.algorandandroidkotlin.model.UserAccount2
import com.africinnovate.algorandandroidkotlin.model.WalletAccount
import com.africinnovate.algorandandroidkotlin.repository.AccountRepository
import com.africinnovate.algorandandroidkotlin.utils.Constants
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.v2.client.common.AlgodClient
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(private val apiService: APIService) :
    AccountRepository {

    override suspend fun getAccount(address: String): WalletAccount? {
        val response = apiService.service.getAccountByAddress(address)
        try {
            val accountInfo = response.body()
            if (response.isSuccessful) {
                val account = response.body()?.account
                Timber.d("acctResponse $response")
                Timber.d("address is $address and account is $account")
                Timber.d("account info ${accountInfo.toString()}")
            } else {
                Timber.d("Error ${response.errorBody()}")

            }
        }catch (e : Exception){
            Timber.d(e)
        }
        return response.body()
    }

    override suspend fun generateAlgodPair() : Account {
        return Account()
    }

    override suspend fun recoverAccount(passPhrase : String): Account {
        val account = Account(passPhrase)
        Timber.d("Account $account")
        return  account
    }


}
