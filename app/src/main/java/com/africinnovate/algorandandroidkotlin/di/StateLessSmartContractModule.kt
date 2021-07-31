package com.africinnovate.algorandandroidkotlin.di

import com.africinnovate.algorandandroidkotlin.repository.StateLessContractRepository
import com.africinnovate.algorandandroidkotlin.repositoryImpl.StateLessSmartContractRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class StateLessSmartContractModule {
        @Singleton
        @Binds
        abstract fun bindCourse(impl: StateLessSmartContractRepositoryImpl): StateLessContractRepository
}