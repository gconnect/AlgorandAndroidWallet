package com.africinnovate.algorandandroidkotlin.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.africinnovate.algorandandroidkotlin.Resource
import com.africinnovate.algorandandroidkotlin.UserAccount
import com.africinnovate.algorandandroidkotlin.model.AccountTransactions
import com.africinnovate.algorandandroidkotlin.model.Transactions
import com.africinnovate.algorandandroidkotlin.repository.TransactionRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception

class TransactionViewmodel @ViewModelInject constructor(val repository: TransactionRepository) :
    ViewModel() {
    private val _transactions: MutableLiveData<AccountTransactions> = MutableLiveData()
    val transactions: LiveData<AccountTransactions> get() = _transactions
    private val _showMessage: MutableLiveData<String> = MutableLiveData()
    val showMessage: LiveData<String> get() = _showMessage

    private val _showProgress: MutableLiveData<Boolean> = MutableLiveData()
    val showProgress: LiveData<Boolean> get() = _showProgress

    fun transferFund(amount: Long, receiverAddress: String) {
        try {
            viewModelScope.launch {
                repository.transferFund(amount, receiverAddress)
            }
        } catch (e: Exception) {
            Timber.d(e)
        }
    }

    fun getTransactions(address: String): LiveData<AccountTransactions> {
        try {
            viewModelScope.launch {
                _showProgress.value = true
                _transactions.value = repository.getTransactionsByAddress(address)
                _showProgress.value = false

            }
        } catch (e: Throwable) {
            _showMessage.postValue("Unable to connect to server!")
            _showProgress.postValue(false)
        }

        return transactions
    }


}
