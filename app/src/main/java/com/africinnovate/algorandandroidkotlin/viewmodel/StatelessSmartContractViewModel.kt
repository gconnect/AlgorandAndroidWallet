package com.africinnovate.algorandandroidkotlin.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.africinnovate.algorandandroidkotlin.repository.StateLessContractRepository
import com.africinnovate.algorandandroidkotlin.repository.StatefulContractRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class StatelessSmartContractViewModel @ViewModelInject
constructor(val repository: StateLessContractRepository) : ViewModel() {

    fun compileTealSource() {
        viewModelScope.launch {
            try {
                repository.compileTealSource()
            } catch (e: Exception) {
                e.message
            }
        }
    }

    fun contractAccount() {
        viewModelScope.launch {
            try {
                repository.contractAccountExample()
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
    }

    fun accountDelegation() {
        viewModelScope.launch {
            try {
                repository.accountDelegationExample()
            }catch (e: Exception) {e.message}
        }
    }
}