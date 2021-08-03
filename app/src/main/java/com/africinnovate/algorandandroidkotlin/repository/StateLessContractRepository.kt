package com.africinnovate.algorandandroidkotlin.repository

import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.TEALProgram
import com.algorand.algosdk.v2.client.common.AlgodClient

interface StateLessContractRepository {
    suspend fun compileTealSource()
    suspend fun waitForConfirmation(txID: String)
    suspend fun contractAccountExample()
    suspend fun accountDelegationExample()
}
