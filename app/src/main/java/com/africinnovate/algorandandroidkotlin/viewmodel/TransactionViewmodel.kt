package com.africinnovate.algorandandroidkotlin.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.africinnovate.algorandandroidkotlin.UserAccount
import com.africinnovate.algorandandroidkotlin.model.AccountTransactions
import com.africinnovate.algorandandroidkotlin.model.Transactions
import com.africinnovate.algorandandroidkotlin.repository.TransactionRepository
import kotlinx.coroutines.launch

class TransactionViewmodel @ViewModelInject constructor(val repository: TransactionRepository) : ViewModel() {
    private val _transactions : MutableLiveData<AccountTransactions> = MutableLiveData()
    val transactions : LiveData<AccountTransactions> get() = _transactions


    fun transferFund() {
        viewModelScope.launch {
           repository.transferFund()
        }
    }

    fun getTransactions(address: String) : LiveData<AccountTransactions>{
        viewModelScope.launch {
           _transactions.value = repository.getTransactionsByAddress(address)
        }
        return transactions
    }


}
