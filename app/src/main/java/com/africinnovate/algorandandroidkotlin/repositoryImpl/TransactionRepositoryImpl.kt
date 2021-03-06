package com.africinnovate.algorandandroidkotlin.repositoryImpl

import com.africinnovate.algorandandroidkotlin.ClientService.APIService
import com.africinnovate.algorandandroidkotlin.Resource
import com.africinnovate.algorandandroidkotlin.model.AccountTransactions
import com.africinnovate.algorandandroidkotlin.repository.TransactionRepository
import com.africinnovate.algorandandroidkotlin.utils.Constants
import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_API_TOKEN_KEY
import com.africinnovate.algorandandroidkotlin.utils.Constants.PASSPHRASE
import com.africinnovate.algorandandroidkotlin.utils.Constants.RECIEVER
import com.africinnovate.algorandandroidkotlin.utils.Constants.SENDER
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.transaction.Transaction
import com.algorand.algosdk.util.Encoder.encodeToMsgPack
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.common.Response
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse
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

    /**
     * utility function to wait on a transaction to be confirmed
     * the timeout parameter indicates how many rounds do you wish to check pending transactions for
     */
    override suspend fun waitForConfirmation(
        myclient: AlgodClient?,
        txID: String?,
        timeout: Int
    ): PendingTransactionResponse? {
        require(!(myclient == null || txID == null || timeout < 0)) { "Bad arguments for waitForConfirmation." }
        var resp = myclient.GetStatus().execute(headers, values)
        if (!resp.isSuccessful) {
            throw java.lang.Exception(resp.message())
        }
        val nodeStatusResponse = resp.body()
        val startRound = nodeStatusResponse.lastRound + 1
        var currentRound = startRound
        while (currentRound < startRound + timeout) {
            // Check the pending transactions
            val resp2 = myclient.PendingTransactionInformation(txID).execute(headers, values)
            if (resp2.isSuccessful) {
                val pendingInfo = resp2.body()
                if (pendingInfo != null) {
                    if (pendingInfo.confirmedRound != null && pendingInfo.confirmedRound > 0) {
                        // Got the completed Transaction
                        return pendingInfo
                    }
                    if (pendingInfo.poolError != null && pendingInfo.poolError.length > 0) {
                        // If there was a pool error, then the transaction has been rejected!
                        throw java.lang.Exception("The transaction has been rejected with a pool error: " + pendingInfo.poolError)
                    }
                }
            }
            resp = myclient.WaitForBlock(currentRound).execute(headers, values)
            if (!resp.isSuccessful) {
                throw java.lang.Exception(resp.message())
            }
            currentRound++
        }
        throw java.lang.Exception("Transaction not confirmed after $timeout rounds!")
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
                Resource.Success("Successfully sent tx with ID: $id")
                Timber.d("Successfully sent tx with ID: $id")
//                waitForConfirmation(id)
                waitForConfirmation(client, id, 4)
            }
        } catch (e: Exception) {
            e.message
        }
        Timber.d("id is $id")
        return id
    }

    override suspend fun getTransactionsByAddress(address: String): AccountTransactions? {
        val response = apiService.service.getAcccountTransactionsByAddress(address)
        try {
            if (response.isSuccessful) {
                response.body()
                Timber.d("trans $response")
                Timber.d("trans ${response.body()}")
            } else {
                response.errorBody()
            }
        } catch (t: Throwable) {
            t.message
        }
        return response.body()
    }
}
