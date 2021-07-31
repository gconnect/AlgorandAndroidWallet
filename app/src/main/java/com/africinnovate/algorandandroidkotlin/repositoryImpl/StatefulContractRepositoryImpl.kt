package com.africinnovate.algorandandroidkotlin.repositoryImpl

import android.os.Build
import com.africinnovate.algorandandroidkotlin.ClientService.APIService
import com.africinnovate.algorandandroidkotlin.repository.StatefulContractRepository
import com.africinnovate.algorandandroidkotlin.utils.Constants
import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_API_ADDR
import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_API_TOKEN
import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_PORT
import com.africinnovate.algorandandroidkotlin.utils.Constants.CREATOR_MNEMONIC
import com.africinnovate.algorandandroidkotlin.utils.Constants.USER_MNEMONIC
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.crypto.TEALProgram
import com.algorand.algosdk.logic.StateSchema
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.transaction.Transaction
import com.algorand.algosdk.transaction.Transaction.ApplicationOptInTransactionBuilder
import com.algorand.algosdk.util.Encoder.encodeToMsgPack
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.common.Response
import com.algorand.algosdk.v2.client.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.ArrayUtils
import timber.log.Timber
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import com.algorand.algosdk.v2.client.model.TransactionParametersResponse


class StatefulContractRepositoryImpl @Inject constructor(private val apiService: APIService) :
    StatefulContractRepository {

   private var client: AlgodClient = AlgodClient(
        ALGOD_API_ADDR,
        ALGOD_PORT,
        ALGOD_API_TOKEN,
    )

    // utility function to connect to a node
    private fun connectToNetwork(): AlgodClient {
        return client
    }

    var headers = arrayOf("X-API-Key")
    var values = arrayOf(Constants.ALGOD_API_TOKEN_KEY)

    val txHeaders: Array<String> = ArrayUtils.add(headers, "Content-Type")
    val txValues: Array<String> = ArrayUtils.add(values, "application/x-binary")

    // user declared account mnemonics
    val creatorMnemonic = CREATOR_MNEMONIC
    val userMnemonic = USER_MNEMONIC

    // declare application state storage (immutable)
    var localInts = 1
    var localBytes = 1
    var globalInts = 1
    var globalBytes = 0

    var clearProgramSource = """
        #pragma version 4
        int 1
        
        """.trimIndent()

    // get account from mnemonic
    var creatorAccount: Account = Account(creatorMnemonic)
    var sender: Address = creatorAccount.address

    // get node suggested parameters
    var params = client.TransactionParams().execute().body()

    // helper function to compile program source
    override suspend fun compileProgram(client: AlgodClient, programSource: ByteArray?): String? {
        lateinit var compileResponse: Response<CompileResponse>
        try {
            compileResponse = client.TealCompile().source(programSource).execute(
                headers,
                values
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Timber.d("compileResponse ${compileResponse.body().result}")
        return compileResponse.body().result
    }

    // utility function to wait on a transaction to be confirmed
    override suspend fun waitForConfirmation(txID: String?) {
        withContext(Dispatchers.IO) {
//            if (client == null) client = connectToNetwork()
            var lastRound: Long = client.GetStatus().execute(headers, values).body().lastRound
            while (true) {
                try {
                    // Check the pending transactions
                    val pendingInfo: Response<PendingTransactionResponse> =
                        client.PendingTransactionInformation(txID).execute(headers, values)
                    if (pendingInfo.body().confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
                        // Got the completed Transaction
                        Timber.d("Transaction   $txID +  confirmed in round  ${pendingInfo.body().confirmedRound}")
                        break
                    }
                    lastRound++
                    client.WaitForBlock(lastRound).execute(headers, values)
                } catch (e: Exception) {
                    throw e
                }
            }
        }
    }

    override suspend fun createApp(
        client: AlgodClient,
        creator: Account,
        approvalProgramSource: TEALProgram?,
        clearProgramSource: TEALProgram?,
        globalInts: Int,
        globalBytes: Int,
        localInts: Int,
        localBytes: Int
    ): Long? {

        // define sender as creator
        val sender: Address = creator.address

        // get node suggested parameters
        val params: TransactionParametersResponse? =
            client.TransactionParams().execute(headers, values).body()

        // create unsigned transaction
        val txn: Transaction = Transaction.ApplicationCreateTransactionBuilder()
            .sender(sender)
            .suggestedParams(params)
            .approvalProgram(approvalProgramSource)
            .clearStateProgram(clearProgramSource)
            .globalStateSchema(
                StateSchema(
                    globalInts,
                    globalBytes
                )
            )
            .localStateSchema(
                StateSchema(
                    localInts,
                    localBytes
                )
            )
            .build()

        // sign transaction
        val signedTxn: SignedTransaction = creator.signTransaction(txn)
        Timber.d("Signed transaction with txid: \" + ${signedTxn.transactionID}")

        // send to network
//        val encodedTxBytes: ByteArray = encodeToMsgPack(signedTxn)
//        val id: String =
//            client.RawTransaction().rawtxn(encodedTxBytes).execute(txHeaders, txValues).body().txId
//        Timber.d("Successfully sent tx with ID: $id")
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

        // await confirmation
//        waitForConfirmation(id)

        // display results
        val pTrx: PendingTransactionResponse? =
            client.PendingTransactionInformation(id).execute(headers, values).body()
        val appId = pTrx?.applicationIndex
        Timber.d("Created new app-id: $appId")
        return appId
    }

    override suspend fun optInApp(client: AlgodClient, account: Account, appId: Long?) {
        withContext(Dispatchers.IO) {
            // declare sender
            val sender: Address = account.address
            println("OptIn from account: $sender")

            // get node suggested parameters
            val params: TransactionParametersResponse =
                client.TransactionParams().execute(headers, values).body()

            // create unsigned transaction
            val txn: Transaction = ApplicationOptInTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .applicationId(appId)
                .build()

            // sign transaction
            val signedTxn: SignedTransaction = account.signTransaction(txn)

            // send to network
            val encodedTxBytes: ByteArray = encodeToMsgPack(signedTxn)
            val id: String =
                client.RawTransaction().rawtxn(encodedTxBytes).execute(txHeaders, txValues).body().txId

            // await confirmation
            waitForConfirmation(id)

       /*     var id = ""
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
            }*/

            // display results
            val pTrx: PendingTransactionResponse =
                client.PendingTransactionInformation(id).execute(headers, values).body()
            Timber.d("OptIn to app-id: %s", pTrx.txn.tx.applicationId)
        }
    }

    override suspend fun callApp(
        client: AlgodClient,
        account: Account,
        appId: Long?,
        appArgs: List<ByteArray>?
    ) {
        withContext(Dispatchers.IO) {
            // declare sender
            val sender: Address = account.address
            println("Call from account: $sender")
            val params: TransactionParametersResponse =
                client.TransactionParams().execute(headers, values).body()

            // create unsigned transaction
            val txn: Transaction = Transaction.ApplicationCallTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .applicationId(appId)
                .args(appArgs)
                .build()

            // sign transaction
            val signedTxn: SignedTransaction = account.signTransaction(txn)

            // save signed transaction to  a file
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Files.write(Paths.get("./callArgs.stxn"), encodeToMsgPack(signedTxn))
            }

            // send to network
          /*  val encodedTxBytes: ByteArray = encodeToMsgPack(signedTxn)
            val id: String =
                client.RawTransaction().rawtxn(encodedTxBytes).execute(headers, values).body().txId

            // await confirmation
            waitForConfirmation(id)*/

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

            // display results
            val pTrx: PendingTransactionResponse =
                client.PendingTransactionInformation(id).execute(headers, values).body()
            Timber.d("Called app-id: %s", pTrx.txn.tx.applicationId)
            if (pTrx.globalStateDelta != null) {
                Timber.d(" Global state: \" + pTrx.globalStateDelta.toString()")
            }
            if (pTrx.localStateDelta != null) {
                Timber.d("Local state: \" + pTrx.localStateDelta.toString()")
            }
        }

    }

    override suspend fun readLocalState(client: AlgodClient, account: Account, appId: Long?) {
        withContext(Dispatchers.IO) {
            val acctResponse: Response<com.algorand.algosdk.v2.client.model.Account> =
                client.AccountInformation(account.address).execute(headers, values)
            val applicationLocalState: List<ApplicationLocalState> =
                acctResponse.body().appsLocalState
            for (i in applicationLocalState.indices) {
                if (applicationLocalState[i].id == appId) {
                    Timber.d(
                        "User's application local state: %s",
                        applicationLocalState[i].keyValue.toString()
                    )
                }
            }
        }

    }

    override suspend fun readGlobalState(client: AlgodClient, account: Account, appId: Long?) {
        withContext(Dispatchers.IO) {
            val acctResponse: Response<com.algorand.algosdk.v2.client.model.Account> =
                client.AccountInformation(account.address).execute(headers, values)
            val createdApplications: List<Application> = acctResponse.body().createdApps
            for (i in createdApplications.indices) {
                if (createdApplications[i].id.equals(appId)) {
                    Timber.d("Application global state:  ${createdApplications[i].params.globalState.toString()}")
                }
            }
        }
    }

    override suspend fun updateApp(
        client: AlgodClient,
        creator: Account,
        appId: Long?,
        approvalProgramSource: TEALProgram?,
        clearProgramSource: TEALProgram?
    ) {
        withContext(Dispatchers.IO) {
            // define sender as creator
            val sender: Address = creator.address

            // get node suggested parameters
            val params: TransactionParametersResponse =
                client.TransactionParams().execute(headers, values).body()

            // create unsigned transaction
            val txn: Transaction = Transaction.ApplicationUpdateTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .applicationId(appId)
                .approvalProgram(approvalProgramSource)
                .clearStateProgram(clearProgramSource)
                .build()

            // sign transaction
            val signedTxn: SignedTransaction = creator.signTransaction(txn)

            // send to network
            val encodedTxBytes: ByteArray = encodeToMsgPack(signedTxn)
            val id: String =
                client.RawTransaction().rawtxn(encodedTxBytes).execute(txHeaders, txValues).body().txId

            // await confirmation
            waitForConfirmation(id)

            // display results
            val pTrx: PendingTransactionResponse =
                client.PendingTransactionInformation(id).execute(headers, values).body()
            Timber.d("Updated new app-id: $appId and $pTrx")
        }

    }


    override suspend fun closeOutApp(client: AlgodClient, userAccount: Account, appId: Long?) {
        withContext(Dispatchers.IO) {
            // define sender as creator
            val sender: Address = userAccount.address

            // get node suggested parameters
            val params: TransactionParametersResponse =
                client.TransactionParams().execute(headers, values).body()

            // create unsigned transaction
            val txn: Transaction = Transaction.ApplicationCloseTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .applicationId(appId)
                .build()

            // sign transaction
            val signedTxn: SignedTransaction = userAccount.signTransaction(txn)

            // send to network
            val encodedTxBytes: ByteArray = encodeToMsgPack(signedTxn)
            val id: String =
                client.RawTransaction().rawtxn(encodedTxBytes).execute(txHeaders, txValues).body().txId

            // await confirmation
            waitForConfirmation(id)

            // display results
            val pTrx: PendingTransactionResponse =
                client.PendingTransactionInformation(id).execute(headers, values).body()
            Timber.d("Closed out from app-id: $appId and $pTrx")
        }
    }

    override suspend fun clearApp(client: AlgodClient, userAccount: Account, appId: Long?) {
        withContext(Dispatchers.IO) {
            // define sender as creator
            val sender: Address = userAccount.address

            // get node suggested parameters
            val params: TransactionParametersResponse = client.TransactionParams().execute(
                headers,
                values
            ).body()

            // create unsigned transaction
            val txn: Transaction = Transaction.ApplicationClearTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .applicationId(appId)
                .build()

            // sign transaction
            val signedTxn: SignedTransaction = userAccount.signTransaction(txn)

            // send to network
            val encodedTxBytes: ByteArray = encodeToMsgPack(signedTxn)
            val id: String =
                client.RawTransaction().rawtxn(encodedTxBytes).execute(txHeaders, txValues).body().txId
            // await confirmation
            waitForConfirmation(id)


            // display results
            val pTrx: PendingTransactionResponse =
                client.PendingTransactionInformation(id).execute(headers, values).body()
            Timber.d("Cleared local state for app-id: $appId and $pTrx")
        }
    }

    override suspend fun deleteApp(client: AlgodClient, creatorAccount: Account, appId: Long?) {
        withContext(Dispatchers.IO) {
            // define sender as creator
            val sender: Address = creatorAccount.address

            // get node suggested parameters
            val params: TransactionParametersResponse = client.TransactionParams().execute(
                headers,
                values
            ).body()

            // create unsigned transaction
            val txn: Transaction = Transaction.ApplicationDeleteTransactionBuilder()
                .sender(sender)
                .suggestedParams(params)
                .applicationId(appId)
                .build()

            // sign transaction
            val signedTxn: SignedTransaction = creatorAccount.signTransaction(txn)

            // send to network
            val encodedTxBytes: ByteArray = encodeToMsgPack(signedTxn)
            val id: String =
                client.RawTransaction().rawtxn(encodedTxBytes).execute(txHeaders, txValues).body().txId
            // await confirmation
            waitForConfirmation(id)


            // display results
            val pTrx: PendingTransactionResponse =
                client.PendingTransactionInformation(id).execute(headers, values).body()
            Timber.d("Deleted app-id: $appId and $pTrx")
        }

    }

    override suspend fun statefulSmartContract() {
        // user declared account mnemonics
        val creatorMnemonic = CREATOR_MNEMONIC
        val userMnemonic = USER_MNEMONIC

        // declare application state storage (immutable)
        val localInts = 1
        val localBytes = 1
        val globalInts = 1
        val globalBytes = 0

        // user declared approval program (initial)
        val approvalProgramSourceInitial = """
            #pragma version 2
            ///// Handle each possible OnCompletion type. We don't have to worry about
            //// handling ClearState, because the ClearStateProgram will execute in that
            //// case, not the ApprovalProgram.
            txn OnCompletion
            int NoOp
            ==
            bnz handle_noop
            txn OnCompletion
            int OptIn
            ==
            bnz handle_optin
            txn OnCompletion
            int CloseOut
            ==
            bnz handle_closeout
            txn OnCompletion
            int UpdateApplication
            ==
            bnz handle_updateapp
            txn OnCompletion
            int DeleteApplication
            ==
            bnz handle_deleteapp
            //// Unexpected OnCompletion value. Should be unreachable.
            err
            handle_noop:
            //// Handle NoOp
            //// Check for creator
            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
            txn Sender
            ==
            bnz handle_optin
            //// read global state
            byte "counter"
            dup
            app_global_get
            //// increment the value
            int 1
            +
            //// store to scratch space
            dup
            store 0
            //// update global state
            app_global_put
            //// read local state for sender
            int 0
            byte "counter"
            app_local_get
            //// increment the value
            int 1
            +
            store 1
            //// update local state for sender
            int 0
            byte "counter"
            load 1
            app_local_put
            //// load return value as approval
            load 0
            return
            handle_optin:
            //// Handle OptIn
            //// approval
            int 1
            return
            handle_closeout:
            //// Handle CloseOut
            ////approval
            int 1
            return
            handle_deleteapp:
            //// Check for creator
            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
            txn Sender
            ==
            return
            handle_updateapp:
            //// Check for creator
            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
            txn Sender
            ==
            return
            
            """.trimIndent()

        // user declared approval program (refactored)
        val approvalProgramSourceRefactored = """
            #pragma version 2
            //// Handle each possible OnCompletion type. We don't have to worry about
            //// handling ClearState, because the ClearStateProgram will execute in that
            //// case, not the ApprovalProgram.
            txn OnCompletion
            int NoOp
            ==
            bnz handle_noop
            txn OnCompletion
            int OptIn
            ==
            bnz handle_optin
            txn OnCompletion
            int CloseOut
            ==
            bnz handle_closeout
            txn OnCompletion
            int UpdateApplication
            ==
            bnz handle_updateapp
            txn OnCompletion
            int DeleteApplication
            ==
            bnz handle_deleteapp
            //// Unexpected OnCompletion value. Should be unreachable.
            err
            handle_noop:
            //// Handle NoOp
            //// Check for creator
            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
            txn Sender
            ==
            bnz handle_optin
            //// read global state
            byte "counter"
            dup
            app_global_get
            //// increment the value
            int 1
            +
            //// store to scratch space
            dup
            store 0
            //// update global state
            app_global_put
            //// read local state for sender
            int 0
            byte "counter"
            app_local_get
            //// increment the value
            int 1
            +
            store 1
            //// update local state for sender
            //// update "counter"
            int 0
            byte "counter"
            load 1
            app_local_put
            //// update "timestamp"
            int 0
            byte "timestamp"
            txn ApplicationArgs 0
            app_local_put
            //// load return value as approval
            load 0
            return
            handle_optin:
            //// Handle OptIn
            //// approval
            int 1
            return
            handle_closeout:
            //// Handle CloseOut
            ////approval
            int 1
            return
            handle_deleteapp:
            //// Check for creator
            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
            txn Sender
            ==
            return
            handle_updateapp:
            //// Check for creator
            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
            txn Sender
            ==
            return
            
            """.trimIndent()

        // declare clear state program source
        val clearProgramSource = """
            #pragma version 2
            int 1
            
            """.trimIndent()
        withContext(Dispatchers.IO) {
            try {
                // Create an algod client
//                if (client == null) client = connectToNetwork()

                // get accounts from mnemonic
                val creatorAccount = Account(creatorMnemonic)
                val userAccount = Account(userMnemonic)

                // compile programs
                var approvalProgram = compileProgram(
                    client,
                    approvalProgramSourceInitial.toByteArray(charset("UTF-8"))
                )
                val clearProgram =
                    compileProgram(client, clearProgramSource.toByteArray(charset("UTF-8")))

                // create new application
                val appId = createApp(
                    client,
                    creatorAccount,
                    TEALProgram(approvalProgram),
                    TEALProgram(clearProgram),
                    globalInts,
                    globalBytes,
                    localInts,
                    localBytes
                )

                // opt-in to application
                optInApp(client, userAccount, appId)

                // call application without arguments
                callApp(client, userAccount, appId, null)

                // read local state of application from user account
                readLocalState(client, userAccount, appId)

                // read global state of application
                readGlobalState(client, creatorAccount, appId)

                // update application
                approvalProgram = compileProgram(
                    client,
                    approvalProgramSourceRefactored.toByteArray(charset("UTF-8"))
                )
                updateApp(
                    client,
                    creatorAccount,
                    appId,
                    TEALProgram(approvalProgram),
                    TEALProgram(clearProgram)
                )

                // call application with arguments
                val formatter = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss")
                val date = Date(System.currentTimeMillis())
                Timber.d("${formatter.format(date)}")
                val appArgs: MutableList<ByteArray> = ArrayList()
                appArgs.add(formatter.format(date).toString().toByteArray(charset("UTF8")))
                callApp(client, userAccount, appId, appArgs)

                // read local state of application from user account
                readLocalState(client, userAccount, appId)

                // close-out from application
                closeOutApp(client, userAccount, appId)

                // opt-in again to application
                optInApp(client, userAccount, appId)

                // call application with arguments
                callApp(client, userAccount, appId, appArgs)

                // read local state of application from user account
                readLocalState(client, userAccount, appId)

                // delete application
                deleteApp(client, creatorAccount, appId)

                // clear application from user account
                clearApp(client, userAccount, appId)
            } catch (e: Exception) {
                System.err.println("Exception raised: " + e.message)
            }
        }
    }

}