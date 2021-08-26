package com.africinnovate.algorandandroidkotlin.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.africinnovate.algorandandroidkotlin.repository.StateLessContractRepository
import com.africinnovate.algorandandroidkotlin.repository.StatefulContractRepository
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.v2.client.model.CompileResponse
import kotlinx.coroutines.launch
import timber.log.Timber

class StatelessSmartContractViewModel @ViewModelInject
constructor(val repository: StateLessContractRepository) : ViewModel() {
    private val _response : MutableLiveData<CompileResponse> = MutableLiveData()
    val response : LiveData<CompileResponse> get() = _response

    fun compileTealSource() : LiveData<CompileResponse> {
        viewModelScope.launch {
            try {
            _response.value = repository.compileTealSource()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return response
    }

    fun contractAccount() : LiveData<CompileResponse> {
        viewModelScope.launch {
            try {
              _response.value = repository.contractAccountExample()
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return response
    }

    fun accountDelegation() : LiveData<CompileResponse> {
        viewModelScope.launch {
            try {
               _response.value = repository.accountDelegationExample()
            }catch (e: Exception) {
                Timber.e(e)
            }
        }
        return response
    }
}