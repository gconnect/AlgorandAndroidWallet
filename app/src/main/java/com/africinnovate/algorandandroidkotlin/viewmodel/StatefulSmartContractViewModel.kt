package com.africinnovate.algorandandroidkotlin.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.africinnovate.algorandandroidkotlin.repository.StatefulContractRepository
import kotlinx.coroutines.launch

class StatefulSmartContractViewModel @ViewModelInject
constructor(val repository: StatefulContractRepository) : ViewModel() {

    fun statefulSmartContract() {
        viewModelScope.launch {
            try {
                repository.statefulSmartContract()
            } catch (e: Exception) {
                e.message
            }
        }
    }
}