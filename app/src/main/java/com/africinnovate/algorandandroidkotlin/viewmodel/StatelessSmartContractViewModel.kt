package com.africinnovate.algorandandroidkotlin.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.africinnovate.algorandandroidkotlin.repository.StateLessContractRepository
import com.africinnovate.algorandandroidkotlin.repository.StatefulContractRepository
import kotlinx.coroutines.launch

class StatelessSmartContractViewModel @ViewModelInject
constructor(val repository: StateLessContractRepository) : ViewModel() {

    fun compileTealSource(){
        viewModelScope.launch {
            repository.compileTealSource()
        }
    }

    fun contractAccount(){
        viewModelScope.launch {
            repository.contractAccountExample()
        }
    }

    fun accountDelegation(){
        viewModelScope.launch {
            repository.accountDelegationExample()
        }
    }
}