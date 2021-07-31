package com.africinnovate.algorandandroidkotlin

import com.google.gson.annotations.SerializedName

data class UserAccount(
    val address: String,
    val amount: Long,
    @SerializedName("amount-without-pending-rewards")
    val amountWithoutPendingRewards: Long,
    @SerializedName("apps-local-state")
    val appsLocalState: List<AppsLocalState>,
    @SerializedName("apps-total-schema")
    val appsTotalSchema: AppsTotalSchema,
    val assets: List<Assets>,
    @SerializedName("created-apps")
    val createdApps: List<CreatedApps>,
    @SerializedName("created-assets")
    val createdAssets: List<CreatedAssets>,
    @SerializedName("pending-rewards")
    val pendingRewards: Long,
    @SerializedName("reward-base")
    val rewardBase: Long,
    val rewards: Long,
    val round: Long,
    val status: String
)

class Assets {

}

class CreatedApps {

}

class CreatedAssets ()


data class AppsTotalSchema(
    @SerializedName("num-byte-slice")
    val numByteSlice: Int,
    @SerializedName("num-uint")
    val numUnit: Int
)

data class AppsLocalState(
    val id: Int,
    @SerializedName("key-value")
    val keyValue: List<KeyValue>
)

data class KeyValue(val key: String, val value: Value)

data class Value(val bytes: String, val type: Int, val unit: Int)
