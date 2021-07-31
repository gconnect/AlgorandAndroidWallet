package com.africinnovate.algorandandroidkotlin.di

import com.africinnovate.algorandandroidkotlin.repository.AccountRepository
import com.africinnovate.algorandandroidkotlin.repositoryImpl.AccountRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class AccountModule {
        @Singleton
        @Binds
        abstract fun bindCourse(impl: AccountRepositoryImpl): AccountRepository
}