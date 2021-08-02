package com.africinnovate.algorandandroidkotlin.repositoryImpl

import android.os.Build
import androidx.annotation.RequiresApi
import com.africinnovate.algorandandroidkotlin.ClientService.APIService
import com.africinnovate.algorandandroidkotlin.repository.StateLessContractRepository
import com.africinnovate.algorandandroidkotlin.utils.Constants
import com.africinnovate.algorandandroidkotlin.utils.Constants.CREATOR_MNEMONIC
import com.africinnovate.algorandandroidkotlin.utils.Constants.USER_ADDRESS
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.algod.client.ApiException
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.crypto.LogicsigSignature
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.transaction.Transaction
import com.algorand.algosdk.util.Encoder
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.common.Response
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse
import org.apache.commons.lang3.ArrayUtils
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files.readAllBytes
import java.nio.file.Paths
import java.util.*
import javax.inject.Inject


class StateLessSmartContractRepositoryImpl @Inject constructor(private val apiService: APIService) : StateLessContractRepository {
    private var client: AlgodClient = AlgodClient(
        Constants.ALGOD_API_ADDR,
        Constants.ALGOD_PORT,
        Constants.ALGOD_API_TOKEN,
    )

    // utility function to connect to a node
    private fun connectToNetwork(): AlgodClient {
        return client
    }

    var headers = arrayOf("X-API-Key")
    var values = arrayOf(Constants.ALGOD_API_TOKEN_KEY)

    val txHeaders: Array<String> = ArrayUtils.add(headers, "Content-Type")
    val txValues: Array<String> = ArrayUtils.add(values, "application/x-binary")

  @RequiresApi(Build.VERSION_CODES.O)
  override suspend fun compileTealSource() {
        // Initialize an algod client
        if (client == null) client = connectToNetwork()

        // read file - int 0
//        val data: ByteArray =
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                Files.readAllBytes(Paths.get("sample.teal"))
//            } else {
//                TODO("VERSION.SDK_INT < O")
//            }
//      val data = readAllBytes(Paths.get("./sample.teal"))

//      val file = File("./sample.teal")
//      val bytes = ByteArray(file.length().toInt())
//      val bis = BufferedInputStream(FileInputStream(file))
//      val dis = DataInputStream(bis)
//      dis.readFully(bytes)

      // compile
//        val response = client.TealCompile().source(data).execute().body()
//        // print results
//        println("response: $response")
//        println("Hash: " + response.hash)
//        println("Result: " + response.result)
    }

  override suspend fun waitForConfirmation(txID: String) {
        if (client == null) client = connectToNetwork()
        var lastRound = client.GetStatus().execute(headers, values).body().lastRound
        while (true) {
            try {
                // Check the pending transactions
                val pendingInfo: Response<PendingTransactionResponse> =
                    client.PendingTransactionInformation(txID).execute()
                if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
                    // Got the completed Transaction
                    println(
                        "Transaction " + txID + " confirmed in round " + pendingInfo.body().confirmedRound
                    )
                    break
                }
                lastRound++
                client.WaitForBlock(lastRound).execute(headers, values)
            } catch (e: Exception) {
                throw e
            }
        }
    }

   override suspend fun contractAccountExample() {
        // Initialize an algod client
        if (client == null) client = connectToNetwork()

        // Set the receiver
        val RECEIVER = "UVBYHRZIHUNUELDO6HWUAHOZF6G66W6T3JOXIIUSV3LDSBWVCFZ6LM6NCA"

        // Read program from file samplearg.teal
        val source = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            readAllBytes(Paths.get("./samplearg.teal"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        // compile
        val response = client.TealCompile().source(source).execute(headers, values).body()
        // print results
        println("response: $response")
        println("Hash: " + response.hash)
        println("Result: " + response.result)
        val program = Base64.getDecoder().decode(response.result.toString())

        // create logic sig
        // integer parameter
        val teal_args = ArrayList<ByteArray>()
        val arg1 = byteArrayOf(123)
        teal_args.add(arg1)
        val lsig = LogicsigSignature(program, teal_args)
        // For no args use null as second param
        // LogicsigSignature lsig = new LogicsigSignature(program, null);
        println("lsig address: " + lsig.toAddress())
        val params = client.TransactionParams().execute(headers, values).body()
        // create a transaction
        val note = "Hello World"
        val txn: Transaction = Transaction.PaymentTransactionBuilder()
            .sender(
                lsig
                    .toAddress()
            )
            .note(note.toByteArray())
            .amount(100000)
            .receiver(Address(RECEIVER))
            .suggestedParams(params)
            .build()
        try {
            // create the LogicSigTransaction with contract account LogicSig
            val stx: SignedTransaction = Account.signLogicsigTransaction(lsig, txn)
            // send raw LogicSigTransaction to network
            val encodedTxBytes: ByteArray = Encoder.encodeToMsgPack(stx)
            val id = client.RawTransaction().rawtxn(encodedTxBytes).execute(txHeaders, txValues)
                .body().txId
            // Wait for transaction confirmation
            waitForConfirmation(id)
            println("Successfully sent tx with id: $id")
            // Read the transaction
            val pTrx = client.PendingTransactionInformation(id).execute(headers, values).body()
            val jsonObj = JSONObject(pTrx.toString())
            println("Transaction information (with notes): " + jsonObj.toString(2)) // pretty print
            println("Decoded note: " + String(pTrx.txn.tx.note))
        } catch (e: ApiException) {
            System.err.println("Exception when calling algod#rawTransaction: " + e.getResponseBody())
        }
    }

  override suspend fun accountDelegationExample() {
        // Initialize an algod client
        if (client == null) client = connectToNetwork()
        // import your private key mnemonic and address
        val SRC_ACCOUNT = CREATOR_MNEMONIC
        val src = Account(SRC_ACCOUNT)
        // Set the receiver
        val RECEIVER = USER_ADDRESS

        // Read program from file samplearg.teal
        val source = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            readAllBytes(Paths.get("./samplearg.teal"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }


        // compile
        val response = client.TealCompile().source(source).execute().body()
        // print results
        println("response: $response")
        println("Hash: " + response.hash)
        println("Result: " + response.result)
        val program = Base64.getDecoder().decode(response.result.toString())

        // create logic sig

        // string parameter
        // ArrayList<byte[]> teal_args = new ArrayList<byte[]>();
        // String orig = "my string";
        // teal_args.add(orig.getBytes());
        // LogicsigSignature lsig = new LogicsigSignature(program, teal_args);

        // integer parameter
        val teal_args = ArrayList<ByteArray>()
        val arg1 = byteArrayOf(123)
        teal_args.add(arg1)
        val lsig = LogicsigSignature(program, teal_args)
        //    For no args use null as second param
        //    LogicsigSignature lsig = new LogicsigSignature(program, null);
        // sign the logic signature with an account sk
        src.signLogicsig(lsig)
        val params = client.TransactionParams().execute(headers, values).body()
        // create a transaction
        val note = "Hello World"
        val txn = Transaction.PaymentTransactionBuilder()
            .sender(src.address)
            .note(note.toByteArray())
            .amount(100000)
            .receiver(Address(RECEIVER))
            .suggestedParams(params)
            .build()
        try {
            // create the LogicSigTransaction with contract account LogicSig
            val stx = Account.signLogicsigTransaction(lsig, txn)
            // send raw LogicSigTransaction to network
            val encodedTxBytes = Encoder.encodeToMsgPack(stx)
            val id = client.RawTransaction().rawtxn(encodedTxBytes).execute(txHeaders, txValues).body().txId
            // Wait for transaction confirmation
            waitForConfirmation(id)
            println("Successfully sent tx with id: $id")
            // Read the transaction
            val pTrx = client.PendingTransactionInformation(id).execute(headers, values).body()
            val jsonObj = JSONObject(pTrx.toString())
            println("Transaction information (with notes): " + jsonObj.toString(2)) // pretty print
            println("Decoded note: " + String(pTrx.txn.tx.note))
        } catch (e: ApiException) {
            System.err.println("Exception when calling algod#rawTransaction: " + e.responseBody)
        }
    }


}