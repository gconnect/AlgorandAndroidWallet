package com.africinnovate.algorandandroidkotlin.repositoryImpl

import com.africinnovate.algorandandroidkotlin.ClientService.APIService
import com.africinnovate.algorandandroidkotlin.model.AccountTransactions
import com.africinnovate.algorandandroidkotlin.repository.TransactionRepository
import com.africinnovate.algorandandroidkotlin.utils.Constants
import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_API_TOKEN_KEY
import com.africinnovate.algorandandroidkotlin.utils.Constants.CREATOR_ADDRESS
import com.africinnovate.algorandandroidkotlin.utils.Constants.CREATOR_MNEMONIC
import com.africinnovate.algorandandroidkotlin.utils.Constants.PASSPHRASE
import com.africinnovate.algorandandroidkotlin.utils.Constants.RECIEVER
import com.africinnovate.algorandandroidkotlin.utils.Constants.SENDER
import com.africinnovate.algorandandroidkotlin.utils.Constants.USER_ADDRESS
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.transaction.Transaction
import com.algorand.algosdk.util.Encoder.encodeToMsgPack
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.common.Response
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.ArrayUtils
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TransactionRepositoryImpl @Inject constructor(private val apiService: APIService) :
    TransactionRepository {
        private val client: AlgodClient = AlgodClient(
            Constants.ALGOD_API_ADDR,
            Constants.ALGOD_PORT,
            Constants.ALGOD_API_TOKEN,
        )
    var headers = arrayOf("X-API-Key")
    var values = arrayOf(ALGOD_API_TOKEN_KEY)

    override suspend fun transferFund(amount: Long, receiverAddress: String) {
        val passPhrase = PASSPHRASE
        val myAccount = Account(passPhrase)
        // Construct the transaction
        val RECEIVER = RECIEVER
        val sender = SENDER
        withContext(Dispatchers.IO) {
            val resp: Response<TransactionParametersResponse> = client.TransactionParams().execute(
                headers,
                values
            )
            try {
                if (!resp.isSuccessful) {
                    Timber.d("message ${resp.message()}")
                    throw Exception(resp.message())
                }
                val params: TransactionParametersResponse = resp.body()
                    ?: throw Exception("Params retrieval error")
                val txn: Transaction = Transaction.PaymentTransactionBuilder()
                    .sender(sender)
                    .amount(amount)
                    .receiver(Address(receiverAddress))
                    .suggestedParams(params)
                    .build()

                //Sign the transaction
                val signedTxn: SignedTransaction = myAccount.signTransaction(txn)
                Timber.d("Signed transaction with txid:: \" + ${signedTxn.transactionID}")
                submitTransaction(signedTxn)
            } catch (e: Exception) {
                Timber.d("Error ${e.message}")
            }

        }
    }

    override suspend fun waitForConfirmation(txID: String) {
        var lastRound = client.GetStatus().execute(headers, values).body().lastRound
        while (true) {
            try {
                // Check the pending tranactions
                val pendingInfo = client.PendingTransactionInformation(txID).execute(
                    headers,
                    values
                )
                if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
                    // Got the completed Transaction
                    Timber.d("Transaction + $txID +  confirmed in round  + ${pendingInfo.body().confirmedRound}")
                    break
                }
                lastRound++
                client.WaitForBlock(lastRound).execute(headers, values)
            } catch (e: Exception) {
                Timber.d("confirm  + $e ")
                throw e
            }
        }
    }

    override suspend fun submitTransaction(signedTxn: SignedTransaction): String {
        val txHeaders: Array<String> = ArrayUtils.add(headers, "Content-Type")
        val txValues: Array<String> = ArrayUtils.add(values, "application/x-binary")
        var id = ""
        try {
            // Submit the transaction to the network
            val encodedTxBytes: ByteArray = encodeToMsgPack(signedTxn)
            val rawtxresponse = client.RawTransaction().rawtxn(encodedTxBytes).execute(
                txHeaders,
                txValues
            )
            if (!rawtxresponse.isSuccessful) {
                Timber.d("raw ${rawtxresponse.message()}")
                throw Exception(rawtxresponse.message())
            } else {
                 id = rawtxresponse.body().txId
                Timber.d("Successfully sent tx with ID: $id")
                waitForConfirmation(id)
            }
        }catch (e : Exception){
            e.message
        }
        Timber.d("id is $id")
        return id
    }

    override suspend fun getTransactionsByAddress(address: String): AccountTransactions? {
        val response = apiService.service.getAcccountTransactionsByAddress(address)
        try {
            if (response.isSuccessful){
                response.body()
                Timber.d("trans $response")
                Timber.d("trans ${response.body()}")
            }else{
                response.errorBody()
            }
        }catch (t : Throwable){
            t.message
        }
        return response.body()
    }



}
