package com.africinnovate.algorandandroidkotlin.ClientService

import com.africinnovate.algorandandroidkotlin.UserAccount
import com.africinnovate.algorandandroidkotlin.model.AccountTransactions
import com.africinnovate.algorandandroidkotlin.model.Transactions
import com.africinnovate.algorandandroidkotlin.model.UserAccount2
import com.africinnovate.algorandandroidkotlin.model.WalletAccount
import com.africinnovate.algorandandroidkotlin.utils.Constants.HEADER_VALUE
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface AlgorandRESTService {

    @Headers(HEADER_VALUE)
    @GET("v2/accounts/{account-id}")
    suspend fun getAccountByAddress(@Path("account-id") account_id: String?): Response<WalletAccount>

    @Headers(HEADER_VALUE)
    @GET("v2/accounts/{account-id}/transactions")
    suspend fun getAcccountTransactionsByAddress(@Path("account-id") account_id : String?): Response<AccountTransactions>

}
