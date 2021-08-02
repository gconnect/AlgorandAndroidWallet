package com.africinnovate.algorandandroidkotlin.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.africinnovate.algorandandroidkotlin.UserAccount
import com.africinnovate.algorandandroidkotlin.model.UserAccount2
import com.africinnovate.algorandandroidkotlin.model.WalletAccount
import com.africinnovate.algorandandroidkotlin.repository.AccountRepository
import com.algorand.algosdk.account.Account
import kotlinx.coroutines.launch

class AccountViewmodel @ViewModelInject constructor(val repository: AccountRepository) : ViewModel() {
    private val _account : MutableLiveData<WalletAccount> = MutableLiveData()
    val account : LiveData<WalletAccount> get() = _account

    private val _algodPair : MutableLiveData<Account> = MutableLiveData()
    val algodPair : LiveData<Account> get() = _algodPair

    fun getAccount(address: String) : LiveData<WalletAccount>{
        viewModelScope.launch {
            _account.value  = repository.getAccount(address)
        }
        return account
    }

    fun generateAlgodPair() : LiveData<Account> {
        viewModelScope.launch {
            _algodPair.value = repository.generateAlgodPair()
        }
        return algodPair
    }

    fun recoverAccount(passsPhrase : String) : LiveData<Account>{
        viewModelScope.launch {
           _algodPair.value = repository.recoverAccount(passsPhrase)
        }
        return algodPair
    }

}
