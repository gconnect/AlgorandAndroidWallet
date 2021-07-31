package com.africinnovate.algorandandroidkotlin.model

import com.google.gson.annotations.SerializedName

data class UserAccounts (
    @SerializedName( "next-token")
    val nextToken: String,
    @SerializedName( "current-round")
    val currentRound: Long,
    val accounts: List<Accounts>
)

data class Accounts (
    val participation: Participation,
    val amount: Long,
    val address: String,

    @SerializedName( "apps-local-state")
    val appsLocalState: List<AppsLocalState>,

    @SerializedName( "created-at-round")
    val createdAtRound: Long,

    @SerializedName( "apps-total-schema")
    val appsTotalSchema: Schema,

    @SerializedName( "created-assets")
    val createdAssets: List<CreatedAsset>,

    @SerializedName( "pending-rewards")
    val pendingRewards: Long,

    @SerializedName( "reward-base")
    val rewardBase: Long,

    @SerializedName( "created-apps")
    val createdApps: List<CreatedApp>,

    @SerializedName( "closed-at-round")
    val closedAtRound: Long,

    val assets: List<Asset>,
    val deleted: Boolean,
    val round: Long,

    @SerializedName( "amount-without-pending-rewards")
    val amountWithoutPendingRewards: Long,

    @SerializedName( "auth-addr")
    val authAddr: String,

    val rewards: Long,
    val status: String,

    @SerializedName( "sig-type")
    val sigType: String
)

data class AppsLocalState (
    val schema: Schema,
    val deleted: Boolean,

    @SerializedName( "closed-out-at-round")
    val closedOutAtRound: Long,

    @SerializedName( "key-value")
    val keyValue: List<KeyValue>,

    @SerializedName( "opted-in-at-round")
    val optedInAtRound: Long,

    val id: Long
)

data class KeyValue (
    val value: Value,
    val key: String
)

data class Value (
    val bytes: String,
    val type: Long,
    val uint: Long
)

data class Schema (
    @SerializedName( "num-uint")
    val numUint: Long,

    @SerializedName( "num-byte-slice")
    val numByteSlice: Long
)

data class Asset (
    val creator: String,
    val amount: Long,
    val deleted: Boolean,

    @SerializedName( "opted-out-at-round")
    val optedOutAtRound: Long,

    @SerializedName( "asset-id")
    val assetID: Long,

    @SerializedName( "is-frozen")
    val isFrozen: Boolean,

    @SerializedName( "opted-in-at-round")
    val optedInAtRound: Long
)

data class CreatedApp (
    @SerializedName( "deleted-at-round")
    val deletedAtRound: Long,

    val deleted: Boolean,

    @SerializedName( "created-at-round")
    val createdAtRound: Long,

    val id: Long,
    val params: CreatedAppParams
)

data class CreatedAppParams (
    @SerializedName( "global-state")
    val globalState: List<KeyValue>,

    @SerializedName( "clear-state-program")
    val clearStateProgram: String,

    val creator: String,

    @SerializedName( "local-state-schema")
    val localStateSchema: Schema,

    @SerializedName( "approval-program")
    val approvalProgram: String,

    @SerializedName( "global-state-schema")
    val globalStateSchema: Schema
)

data class CreatedAsset (
    @SerializedName( "destroyed-at-round")
    val destroyedAtRound: Long,
    val deleted: Boolean,
    @SerializedName( "created-at-round")
    val createdAtRound: Long,
    val index: Long,
    val params: CreatedAssetParams
)

data class CreatedAssetParams (
    val clawback: String,

    @SerializedName( "default-frozen")
    val defaultFrozen: Boolean,

    val creator: String,
    val total: Long,

    @SerializedName( "unit-name")
    val unitName: String,

    val freeze: String,
    val manager: String,
    val decimals: Long,
    val name: String,
    val reserve: String,

    @SerializedName( "metadata-hash")
    val metadataHash: String,

    val url: String
)

data class Participation (
    @SerializedName( "vote-participation-key")
    val voteParticipationKey: String,

    @SerializedName( "vote-last-valid")
    val voteLastValid: Long,

    @SerializedName( "vote-key-dilution")
    val voteKeyDilution: Long,

    @SerializedName( "vote-first-valid")
    val voteFirstValid: Long,

    @SerializedName( "selection-participation-key")
    val selectionParticipationKey: String
)