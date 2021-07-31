package com.africinnovate.algorandandroidkotlin.repositoryImpl

import com.africinnovate.algorandandroidkotlin.utils.Constants
import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_API_ADDR
import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_API_TOKEN
import com.africinnovate.algorandandroidkotlin.utils.Constants.ALGOD_PORT
import com.algorand.algosdk.crypto.Address
import com.algorand.algosdk.crypto.TEALProgram
import com.algorand.algosdk.transaction.SignedTransaction
import com.algorand.algosdk.util.Encoder
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.common.Response
import com.algorand.algosdk.v2.client.model.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class StatefulSmartContract {
//    val client: AlgodClient = AlgodClient(
//        ALGOD_API_ADDR,
//        ALGOD_PORT,
//        ALGOD_API_TOKEN,
//    )
//
//    // utility function to connect to a node
//    private fun connectToNetwork(): AlgodClient {
//
////            // Initialize an algod client
////            val ALGOD_API_ADDR = "localhost"
////            val ALGOD_PORT = 4001
////            val ALGOD_API_TOKEN = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
//        return AlgodClient(
//            ALGOD_API_ADDR,
//            ALGOD_PORT,
//            ALGOD_API_TOKEN
//        )
//    }
//
//    //        private val client: AlgodClient = AlgodClient(
////            Constants.ALGOD_API_ADDR,
////            Constants.ALGOD_PORT,
////            Constants.ALGOD_API_TOKEN,
////        )
//    // utility function to wait on a transaction to be confirmed
//    fun waitForConfirmation(txID: String?) {
//        if (client == null) connectToNetwork()
//        var lastRound: Long = client.GetStatus().execute().body().lastRound
//        while (true) {
//            try {
//                // Check the pending transactions
//                val pendingInfo: Response<PendingTransactionResponse>? =
//                    client?.PendingTransactionInformation(txID)?.execute()
//                if (pendingInfo?.body()?.confirmedRound != null && pendingInfo.body().confirmedRound > 0) {
//                    // Got the completed Transaction
//                    println(
//                        "Transaction " + txID + " confirmed in round " + pendingInfo.body().confirmedRound
//                    )
//                    break
//                }
//                lastRound++
//                client?.WaitForBlock(lastRound)?.execute()
//            } catch (e: Exception) {
//                throw e
//            }
//        }
//    }
//
//    // helper function to compile program source
//    fun compileProgram(client: AlgodClient?, programSource: ByteArray?): String? {
//        var compileResponse: Response<CompileResponse?>? = null
//        try {
//            compileResponse = client?.TealCompile()?.source(programSource)?.execute()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        System.out.println(compileResponse?.body()?.result)
//        return compileResponse?.body()?.result
//    }
//
//    fun createApp(
//        client: AlgodClient?,
//        creator: Account,
//        approvalProgramSource: TEALProgram?,
//        clearProgramSource: TEALProgram?,
//        globalInts: Int,
//        globalBytes: Int,
//        localInts: Int,
//        localBytes: Int
//    ): Long? {
//        // define sender as creator
//        val sender: Address = creator.address
//
//        // get node suggested parameters
//        val params: TransactionParametersResponse? = client?.TransactionParams()?.execute()?.body()
//
//        // create unsigned transaction
//        val txn: Transaction = Transaction.ApplicationCreateTransactionBuilder()
//            .sender(sender)
//            .suggestedParams(params)
//            .approvalProgram(approvalProgramSource)
//            .clearStateProgram(clearProgramSource)
//            .globalStateSchema(StateSchema(globalInts, globalBytes))
//            .localStateSchema(StateSchema(localInts, localBytes))
//            .build()
//
//        // sign transaction
//        val signedTxn: SignedTransaction = creator.signTransaction(txn)
//        println("Signed transaction with txid: " + signedTxn.transactionID)
//
//        // send to network
//        val encodedTxBytes: ByteArray = Encoder.encodeToMsgPack(signedTxn)
//        val id: String? = client?.RawTransaction()?.rawtxn(encodedTxBytes)?.execute()?.body()?.txId
//        println("Successfully sent tx with ID: $id")
//
//        // await confirmation
//        waitForConfirmation(id)
//
//        // display results
//        val pTrx: PendingTransactionResponse? =
//            client?.PendingTransactionInformation(id)?.execute()?.body()
//        val appId = pTrx?.applicationIndex
//        println("Created new app-id: $appId")
//        return appId
//    }
//
//    @Throws(Exception::class)
//    fun optInApp(client: AlgodClient?, account: Account, appId: Long?) {
//        // declare sender
//        val sender: Address = account.address
//        println("OptIn from account: $sender")
//
//        // get node suggested parameters
//        val params: TransactionParametersResponse = client.TransactionParams().execute().body()
//
//        // create unsigned transaction
//        val txn: Transaction = Transaction.ApplicationOptInTransactionBuilder()
//            .sender(sender)
//            .suggestedParams(params)
//            .applicationId(appId)
//            .build()
//
//        // sign transaction
//        val signedTxn: SignedTransaction = account.signTransaction(txn)
//
//        // send to network
//        val encodedTxBytes: ByteArray = Encoder.encodeToMsgPack(signedTxn)
//        val id: String? = client?.RawTransaction()?.rawtxn(encodedTxBytes)?.execute()?.body()?.txId
//
//        // await confirmation
//        waitForConfirmation(id)
//
//        // display results
//        val pTrx: PendingTransactionResponse? =
//            client?.PendingTransactionInformation(id)?.execute()?.body()
//        println("OptIn to app-id: " + pTrx?.txn?.tx?.applicationId)
//    }
//
//    @Throws(Exception::class)
//    fun callApp(
//        client: AlgodClient?,
//        account: Account,
//        appId: Long?,
//        appArgs: List<ByteArray>?
//    ) {
//        // declare sender
//        val sender: Address = account.address
//        println("Call from account: $sender")
//        val params: TransactionParametersResponse = client.TransactionParams().execute().body()
//
//        // create unsigned transaction
//        val txn: Transaction = Transaction.ApplicationCallTransactionBuilder()
//            .sender(sender)
//            .suggestedParams(params)
//            .applicationId(appId)
//            .args(appArgs)
//            .build()
//
//        // sign transaction
//        val signedTxn: SignedTransaction = account.signTransaction(txn)
//
//        // save signed transaction to  a file
//        Files.write(Paths.get("./callArgs.stxn"), Encoder.encodeToMsgPack(signedTxn))
//
//        // send to network
//        val encodedTxBytes: ByteArray = Encoder.encodeToMsgPack(signedTxn)
//        val id: String = client.RawTransaction().rawtxn(encodedTxBytes).execute().body().txId
//
//        // await confirmation
//        waitForConfirmation(id)
//
//        // display results
//        val pTrx: PendingTransactionResponse =
//            client.PendingTransactionInformation(id).execute().body()
//        println("Called app-id: " + pTrx.txn.tx.applicationId)
//        if (pTrx.globalStateDelta != null) {
//            println("    Global state: " + pTrx.globalStateDelta.toString())
//        }
//        if (pTrx.localStateDelta != null) {
//            println("    Local state: " + pTrx.localStateDelta.toString())
//        }
//    }
//
//    @Throws(Exception::class)
//    fun readLocalState(client: AlgodClient?, account: Account, appId: Long) {
//        val acctResponse: Response<Account>? =
//            client?.AccountInformation(account.address)?.execute()
//        val applicationLocalState: List<ApplicationLocalState>? =
//            acctResponse?.body()?.appsLocalState
//        for (i in applicationLocalState?.indices) {
//            if (applicationLocalState[i].id == appId) {
//                println("User's application local state: " + applicationLocalState[i].keyValue.toString())
//            }
//        }
//    }
//
//    @Throws(Exception::class)
//    fun readGlobalState(client: AlgodClient?, account: Account, appId: Long?) {
//        val acctResponse: Response<Account>? =
//            client?.AccountInformation(account.address)?.execute()
//        val createdApplications: List<Application>? = acctResponse?.body()?.createdApps
//        for (i in createdApplications?.indices) {
//            if (createdApplications[i].id.equals(appId)) {
//                System.out.println("Application global state: " + createdApplications[i].params.globalState.toString())
//            }
//        }
//    }
//
//    @Throws(Exception::class)
//    fun updateApp(
//        client: AlgodClient,
//        creator: Account,
//        appId: Long,
//        approvalProgramSource: TEALProgram?,
//        clearProgramSource: TEALProgram?
//    ) {
//        // define sender as creator
//        val sender: Address = creator.address
//
//        // get node suggested parameters
//        val params: TransactionParametersResponse = client.TransactionParams().execute().body()
//
//        // create unsigned transaction
//        val txn: Transaction = Transaction.ApplicationUpdateTransactionBuilder()
//            .sender(sender)
//            .suggestedParams(params)
//            .applicationId(appId)
//            .approvalProgram(approvalProgramSource)
//            .clearStateProgram(clearProgramSource)
//            .build()
//
//        // sign transaction
//        val signedTxn: SignedTransaction = creator.signTransaction(txn)
//
//        // send to network
//        val encodedTxBytes: ByteArray = Encoder.encodeToMsgPack(signedTxn)
//        val id: String = client.RawTransaction().rawtxn(encodedTxBytes).execute().body().txId
//
//        // await confirmation
//        waitForConfirmation(id)
//
//        // display results
//        val pTrx: PendingTransactionResponse =
//            client.PendingTransactionInformation(id).execute().body()
//        println("Updated new app-id: $appId")
//    }
//
//    @Throws(Exception::class)
//    fun closeOutApp(client: AlgodClient, userAccount: Account, appId: Long) {
//        // define sender as creator
//        val sender: Address = userAccount.address
//
//        // get node suggested parameters
//        val params: TransactionParametersResponse = client.TransactionParams().execute().body()
//
//        // create unsigned transaction
//        val txn: Transaction = Transaction.ApplicationCloseTransactionBuilder()
//            .sender(sender)
//            .suggestedParams(params)
//            .applicationId(appId)
//            .build()
//
//        // sign transaction
//        val signedTxn: SignedTransaction = userAccount.signTransaction(txn)
//
//        // send to network
//        val encodedTxBytes: ByteArray = Encoder.encodeToMsgPack(signedTxn)
//        val id: String = client.RawTransaction().rawtxn(encodedTxBytes).execute().body().txId
//
//        // await confirmation
//        waitForConfirmation(id)
//
//        // display results
//        val pTrx: PendingTransactionResponse =
//            client.PendingTransactionInformation(id).execute().body()
//        println("Closed out from app-id: $appId")
//    }
//
//    @Throws(Exception::class)
//    fun clearApp(client: AlgodClient, userAccount: Account, appId: Long) {
//        // define sender as creator
//        val sender: Address = userAccount.address
//
//        // get node suggested parameters
//        val params: TransactionParametersResponse = client.TransactionParams().execute().body()
//
//        // create unsigned transaction
//        val txn: Transaction = Transaction.ApplicationClearTransactionBuilder()
//            .sender(sender)
//            .suggestedParams(params)
//            .applicationId(appId)
//            .build()
//
//        // sign transaction
//        val signedTxn: SignedTransaction = userAccount.signTransaction(txn)
//
//        // send to network
//        val encodedTxBytes: ByteArray = Encoder.encodeToMsgPack(signedTxn)
//        val id: String = client.RawTransaction().rawtxn(encodedTxBytes).execute().body().txId
//
//        // await confirmation
//        waitForConfirmation(id)
//
//        // display results
//        val pTrx: PendingTransactionResponse =
//            client.PendingTransactionInformation(id).execute().body()
//        println("Cleared local state for app-id: $appId")
//    }
//
//    @Throws(Exception::class)
//    fun deleteApp(client: AlgodClient, creatorAccount: Account, appId: Long) {
//        // define sender as creator
//        val sender: Address = creatorAccount.address
//
//        // get node suggested parameters
//        val params: TransactionParametersResponse = client.TransactionParams().execute().body()
//
//        // create unsigned transaction
//        val txn: Transaction = Transaction.ApplicationDeleteTransactionBuilder()
//            .sender(sender)
//            .suggestedParams(params)
//            .applicationId(appId)
//            .build()
//
//        // sign transaction
//        val signedTxn: SignedTransaction = creatorAccount.signTransaction(txn)
//
//        // send to network
//        val encodedTxBytes: ByteArray = Encoder.encodeToMsgPack(signedTxn)
//        val id: String = client.RawTransaction().rawtxn(encodedTxBytes).execute().body().txId
//
//        // await confirmation
//        waitForConfirmation(id)
//
//        // display results
//        val pTrx: PendingTransactionResponse =
//            client.PendingTransactionInformation(id).execute().body()
//        println("Deleted app-id: $appId")
//    }
//
//    @Throws(Exception::class)
//    fun statefulSmartContract() {
//        // user declared account mnemonics
//        val creatorMnemonic = "Your 25-word mnemonic goes here"
//        val userMnemonic = "A second distinct 25-word mnemonic goes here"
//
//        // declare application state storage (immutable)
//        val localInts = 1
//        val localBytes = 1
//        val globalInts = 1
//        val globalBytes = 0
//
//        // user declared approval program (initial)
//        val approvalProgramSourceInitial = """
//            #pragma version 2
//            ///// Handle each possible OnCompletion type. We don't have to worry about
//            //// handling ClearState, because the ClearStateProgram will execute in that
//            //// case, not the ApprovalProgram.
//            txn OnCompletion
//            int NoOp
//            ==
//            bnz handle_noop
//            txn OnCompletion
//            int OptIn
//            ==
//            bnz handle_optin
//            txn OnCompletion
//            int CloseOut
//            ==
//            bnz handle_closeout
//            txn OnCompletion
//            int UpdateApplication
//            ==
//            bnz handle_updateapp
//            txn OnCompletion
//            int DeleteApplication
//            ==
//            bnz handle_deleteapp
//            //// Unexpected OnCompletion value. Should be unreachable.
//            err
//            handle_noop:
//            //// Handle NoOp
//            //// Check for creator
//            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
//            txn Sender
//            ==
//            bnz handle_optin
//            //// read global state
//            byte "counter"
//            dup
//            app_global_get
//            //// increment the value
//            int 1
//            +
//            //// store to scratch space
//            dup
//            store 0
//            //// update global state
//            app_global_put
//            //// read local state for sender
//            int 0
//            byte "counter"
//            app_local_get
//            //// increment the value
//            int 1
//            +
//            store 1
//            //// update local state for sender
//            int 0
//            byte "counter"
//            load 1
//            app_local_put
//            //// load return value as approval
//            load 0
//            return
//            handle_optin:
//            //// Handle OptIn
//            //// approval
//            int 1
//            return
//            handle_closeout:
//            //// Handle CloseOut
//            ////approval
//            int 1
//            return
//            handle_deleteapp:
//            //// Check for creator
//            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
//            txn Sender
//            ==
//            return
//            handle_updateapp:
//            //// Check for creator
//            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
//            txn Sender
//            ==
//            return
//
//            """.trimIndent()
//
//        // user declared approval program (refactored)
//        val approvalProgramSourceRefactored = """
//            #pragma version 2
//            //// Handle each possible OnCompletion type. We don't have to worry about
//            //// handling ClearState, because the ClearStateProgram will execute in that
//            //// case, not the ApprovalProgram.
//            txn OnCompletion
//            int NoOp
//            ==
//            bnz handle_noop
//            txn OnCompletion
//            int OptIn
//            ==
//            bnz handle_optin
//            txn OnCompletion
//            int CloseOut
//            ==
//            bnz handle_closeout
//            txn OnCompletion
//            int UpdateApplication
//            ==
//            bnz handle_updateapp
//            txn OnCompletion
//            int DeleteApplication
//            ==
//            bnz handle_deleteapp
//            //// Unexpected OnCompletion value. Should be unreachable.
//            err
//            handle_noop:
//            //// Handle NoOp
//            //// Check for creator
//            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
//            txn Sender
//            ==
//            bnz handle_optin
//            //// read global state
//            byte "counter"
//            dup
//            app_global_get
//            //// increment the value
//            int 1
//            +
//            //// store to scratch space
//            dup
//            store 0
//            //// update global state
//            app_global_put
//            //// read local state for sender
//            int 0
//            byte "counter"
//            app_local_get
//            //// increment the value
//            int 1
//            +
//            store 1
//            //// update local state for sender
//            //// update "counter"
//            int 0
//            byte "counter"
//            load 1
//            app_local_put
//            //// update "timestamp"
//            int 0
//            byte "timestamp"
//            txn ApplicationArgs 0
//            app_local_put
//            //// load return value as approval
//            load 0
//            return
//            handle_optin:
//            //// Handle OptIn
//            //// approval
//            int 1
//            return
//            handle_closeout:
//            //// Handle CloseOut
//            ////approval
//            int 1
//            return
//            handle_deleteapp:
//            //// Check for creator
//            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
//            txn Sender
//            ==
//            return
//            handle_updateapp:
//            //// Check for creator
//            addr 5XWY6RBNYHCSY2HK5HCTO62DUJJ4PT3G4L77FQEBUKE6ZYRGQAFTLZSQQ4
//            txn Sender
//            ==
//            return
//
//            """.trimIndent()
//
//        // declare clear state program source
//        val clearProgramSource = """
//            #pragma version 2
//            int 1
//
//            """.trimIndent()
//        try {
//            // Create an algod client
//            if (client == null) client = connectToNetwork()
//
//            // get accounts from mnemonic
//            val creatorAccount = Account(creatorMnemonic)
//            val userAccount = Account(userMnemonic)
//
//            // compile programs
//            var approvalProgram = compileProgram(
//                client,
//                approvalProgramSourceInitial.toByteArray(charset("UTF-8"))
//            )
//            val clearProgram =
//                compileProgram(client, clearProgramSource.toByteArray(charset("UTF-8")))
//
//            // create new application
//            val appId = createApp(
//                client,
//                creatorAccount,
//                TEALProgram(approvalProgram),
//                TEALProgram(clearProgram),
//                globalInts,
//                globalBytes,
//                localInts,
//                localBytes
//            )
//
//            // opt-in to application
//            optInApp(client, userAccount, appId)
//
//            // call application without arguments
//            callApp(client, userAccount, appId, null)
//
//            // read local state of application from user account
//            readLocalState(client, userAccount, appId)
//
//            // read global state of application
//            readGlobalState(client, creatorAccount, appId)
//
//            // update application
//            approvalProgram = compileProgram(
//                client,
//                approvalProgramSourceRefactored.toByteArray(charset("UTF-8"))
//            )
//            updateApp(
//                client,
//                creatorAccount,
//                appId,
//                TEALProgram(approvalProgram),
//                TEALProgram(clearProgram)
//            )
//
//            // call application with arguments
//            val formatter = SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss")
//            val date = Date(System.currentTimeMillis())
//            System.out.println(formatter.format(date))
//            val appArgs: MutableList<ByteArray> = ArrayList()
//            appArgs.add(formatter.format(date).toString().getBytes("UTF8"))
//            callApp(client, userAccount, appId, appArgs)
//
//            // read local state of application from user account
//            readLocalState(client, userAccount, appId)
//
//            // close-out from application
//            closeOutApp(client, userAccount, appId)
//
//            // opt-in again to application
//            optInApp(client, userAccount, appId)
//
//            // call application with arguments
//            callApp(client, userAccount, appId, appArgs)
//
//            // read local state of application from user account
//            readLocalState(client, userAccount, appId)
//
//            // delete application
//            deleteApp(client, creatorAccount, appId)
//
//            // clear application from user account
//            clearApp(client, userAccount, appId)
//        } catch (e: Exception) {
//            System.err.println("Exception raised: " + e.message)
//        }
//
//    }
}
