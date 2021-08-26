package com.africinnovate.algorandandroidkotlin.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.africinnovate.algorandandroidkotlin.UserAccount
import com.africinnovate.algorandandroidkotlin.repository.StatefulContractRepository
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.TEALProgram
import com.algorand.algosdk.v2.client.common.AlgodClient
import com.algorand.algosdk.v2.client.model.CompileResponse
import com.algorand.algosdk.v2.client.model.PendingTransactionResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class StatefulSmartContractViewModel @ViewModelInject
constructor(val repository: StatefulContractRepository) : ViewModel() {
    private val _response : MutableLiveData<String> = MutableLiveData()
    val response : LiveData<String> get() = _response

    private val _appid : MutableLiveData<Long> = MutableLiveData()
    val appid : LiveData<Long> get() = _appid

    private val _pTrx : MutableLiveData<PendingTransactionResponse> = MutableLiveData()
    val pTrx : LiveData<PendingTransactionResponse> get() = _pTrx

    fun statefulSmartContract() {
        viewModelScope.launch {
            try {
                repository.statefulSmartContract()
            } catch (e: Exception) {
                e.message
            }
        }
    }

    fun compileProgram(client: AlgodClient, programSource: ByteArray?) : LiveData<String> {
        viewModelScope.launch {
            try {
                _response.value = repository.compileProgram(client, programSource)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return response
    }

    fun createApp(client: AlgodClient,
                  creator: Account,
                  approvalProgramSource: TEALProgram?,
                  clearProgramSource: TEALProgram?,
                  globalInts: Int,
                  globalBytes: Int,
                  localInts: Int,
                  localBytes: Int
    ): LiveData<Long>?{
        viewModelScope.launch {
            try {
                _appid.value = repository.createApp(client,creator, approvalProgramSource, clearProgramSource,globalInts,globalBytes,localInts,localBytes)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return appid
    }

    fun optInApp(client: AlgodClient, userAccount: Account, apppId: Long?) : LiveData<Long>{
        viewModelScope.launch {
            try {
                _appid.value = repository.optInApp(client,userAccount, apppId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return appid
    }

    fun callApp(client: AlgodClient,
                userAccount: Account,
                appId: Long?,
                appArgs: List<ByteArray>?) : LiveData<Long>{
        viewModelScope.launch {
            try {
                _appid.value = repository.callApp(client,userAccount, appId, appArgs)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return appid
    }

    fun readLocalState(client: AlgodClient, userAccount: Account, appId: Long?) : LiveData<String>{
        viewModelScope.launch {
            try {
                _response.value = repository.readLocalState(client,userAccount, appId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return response
    }

    fun readGlobalState(client: AlgodClient, userAccount: Account, appId: Long?) : LiveData<String>{
        viewModelScope.launch {
            try {
                _response.value = repository.readGlobalState(client,userAccount, appId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return response
    }

    fun updateApp(client: AlgodClient,
                  creator: Account,
                  appId: Long?,
                  approvalProgramSource: TEALProgram?,
                  clearProgramSource: TEALProgram?) : LiveData<PendingTransactionResponse>{
        viewModelScope.launch {
            try {
                _pTrx.value = repository.updateApp(client,creator, appId, approvalProgramSource, clearProgramSource)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return pTrx
    }

    fun closeOutApp(client: AlgodClient, userAccount: Account, appId: Long?) : LiveData<PendingTransactionResponse>{
        viewModelScope.launch {
            try {
                _pTrx.value = repository.closeOutApp(client,userAccount, appId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return pTrx
    }

    fun clearApp(client: AlgodClient, userAccount: Account, appId: Long?) : LiveData<PendingTransactionResponse>{
        viewModelScope.launch {
            try {
                _pTrx.value = repository.clearApp(client,userAccount, appId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return pTrx
    }

    fun deleteApp(client: AlgodClient, userAccount: Account, appId: Long?) : LiveData<PendingTransactionResponse>{
        viewModelScope.launch {
            try {
                _pTrx.value = repository.deleteApp(client,userAccount, appId)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return pTrx
    }

}