package com.africinnovate.algorandandroidkotlin.model

import com.google.gson.annotations.SerializedName

class WalletAccount(
    val account: Account,
    @SerializedName("current-round")
    val currentRound: Long
)


data class Account(
    val address: String,
    val amount: Long,

    @SerializedName("amount-without-pending-rewards")
    val amountWithoutPendingRewards: Long,

    @SerializedName("created-at-round")
    val createdAtRound: Long,

    val deleted: Boolean,

    @SerializedName("pending-rewards")
    val pendingRewards: Long,

    @SerializedName("reward-base")
    val rewardBase: Long,

    val rewards: Long,
    val round: Long,

    @SerializedName("sig-type")
    val sigType: String,

    val status: String
)
