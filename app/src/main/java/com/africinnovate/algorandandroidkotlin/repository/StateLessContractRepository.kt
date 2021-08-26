package com.africinnovate.algorandandroidkotlin.repository

import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.TEALProgram
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.model.CompileResponse

interface StateLessContractRepository {
    suspend fun compileTealSource() : CompileResponse
    suspend fun waitForConfirmation(txID: String)
    suspend fun contractAccountExample() : CompileResponse
    suspend fun accountDelegationExample() : CompileResponse
}
