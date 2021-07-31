package com.africinnovate.algorandandroidkotlin.di

import com.africinnovate.algorandandroidkotlin.repository.TransactionRepository
import com.africinnovate.algorandandroidkotlin.repositoryImpl.TransactionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class TransactionModule {
        @Singleton
        @Binds
        abstract fun bindCourse(impl: TransactionRepositoryImpl): TransactionRepository
}