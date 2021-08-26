package com.africinnovate.algorandandroidkotlin.repository

import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.TEALProgram
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse

interface StatefulContractRepository {
    suspend fun statefulSmartContract()
    suspend fun compileProgram(client: AlgodClient, programSource: ByteArray?): String?
    suspend fun waitForConfirmation(txID: String?)
    suspend fun createApp(
        client: AlgodClient,
        creator: Account,
        approvalProgramSource: TEALProgram?,
        clearProgramSource: TEALProgram?,
        globalInts: Int,
        globalBytes: Int,
        localInts: Int,
        localBytes: Int
    ): Long?
    suspend fun optInApp(client: AlgodClient, account: Account, appId: Long?) : Long
    suspend fun callApp(
        client: AlgodClient,
        account: Account,
        appId: Long?,
        appArgs: List<ByteArray>?
    ) : Long
    suspend fun readLocalState(client: AlgodClient, account: Account, appId: Long?) : String
    suspend fun readGlobalState(client: AlgodClient, account: Account, appId: Long?) : String
    suspend fun updateApp(
        client: AlgodClient,
        creator: Account,
        appId: Long?,
        approvalProgramSource: TEALProgram?,
        clearProgramSource: TEALProgram?
    ) : PendingTransactionResponse
    suspend fun closeOutApp(client: AlgodClient, userAccount: Account, appId: Long?) : PendingTransactionResponse
    suspend fun clearApp(client: AlgodClient, userAccount: Account, appId: Long?) : PendingTransactionResponse
    suspend fun deleteApp(client: AlgodClient, creatorAccount: Account, appId: Long?) : PendingTransactionResponse
}
