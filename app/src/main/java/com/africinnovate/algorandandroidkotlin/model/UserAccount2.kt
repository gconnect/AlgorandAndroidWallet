package com.africinnovate.algorandandroidkotlin.model

import com.google.gson.annotations.SerializedName


data class UserAccount2 (
        val account: Account2,

        @SerializedName( "current-round")
        val currentRound: Long
    )

    data class Account2 (
        val address: String,
        val amount: Long,

        @SerializedName( "amount-without-pending-rewards")
        val amountWithoutPendingRewards: Long,

        @SerializedName( "apps-local-state")
        val appsLocalState: List<AppsLocalState2>,

        @SerializedName( "apps-total-schema")
        val appsTotalSchema: Schema2,

        @SerializedName( "created-at-round")
        val createdAtRound: Long,

        val deleted: Boolean,

        @SerializedName( "pending-rewards")
        val pendingRewards: Long,

        @SerializedName( "reward-base")
        val rewardBase: Long,

        val rewards: Long,
        val round: Long,

        @SerializedName( "sig-type")
        val sigType: String,

        val status: String
    )

    data class AppsLocalState2 (
        @SerializedName( "closed-out-at-round")
        val closedOutAtRound: Long? = null,

        val deleted: Boolean,
        val id: Long,

        @SerializedName( "key-value")
        val keyValue: List<KeyValue2>,

        @SerializedName( "opted-in-at-round")
        val optedInAtRound: Long,

        val schema: Schema
    )

    data class KeyValue2 (
        val key: Key2,
        val value: Value2
    )

    enum class Key2(val value: String)

    data class Value2 (
        val bytes: String,
        val type: Long,
        val uint: Long
    )

    data class Schema2 (
        @SerializedName( "num-byte-slice")
        val numByteSlice: Long,

        @SerializedName( "num-uint")
        val numUint: Long
    )

