package com.africinnovate.algorandandroidkotlin.views

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.africinnovate.algorandandroidkotlin.R
import com.africinnovate.algorandandroidkotlin.databinding.ActivityStatefulBinding
import com.africinnovate.algorandandroidkotlin.utils.Constants
import com.africinnovate.algorandandroidkotlin.viewmodel.StatefulSmartContractViewModel
import com.algorand.algosdk.account.Account
import com.algorand.algosdk.crypto.TEALProgram
import com.algorand.algosdk.v2.client.common.AlgodClient
import dagger.hilt.android.AndroidEntryPoint
import org.apache.commons.lang3.ArrayUtils
import timber.log.Timber


@AndroidEntryPoint
class StatefulActivity : AppCompatActivity() {
    private val viewModel: StatefulSmartContractViewModel by viewModels()
    private lateinit var binding: ActivityStatefulBinding
    private var client: AlgodClient = AlgodClient(
        Constants.ALGOD_API_ADDR,
        Constants.ALGOD_PORT,
        Constants.ALGOD_API_TOKEN,
    )

    // compile programs
    var approvalProgram: String = ""
    var approvalProgram2: String = ""

    var clearProgram: String = ""
    var appId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stateful)
        binding.compile.setOnClickListener {
            viewModel.compileProgram(
                client,
                approvalProgramSourceInitial.toByteArray(charset("UTF-8"))
            ).observe(this, {
                binding.stflOutput.text = it
            })
        }

        viewModel.compileProgram(
            client,
            approvalProgramSourceRefactored.toByteArray(charset("UTF-8"))
        ).observe(this, {
            approvalProgram = it
        })

        viewModel.compileProgram(
            client,
            approvalProgramSourceInitial.toByteArray(charset("UTF-8"))
        ).observe(this, {
            approvalProgram2 = it
        })

        viewModel.compileProgram(
            client,
            clearProgramSource.toByteArray(charset("UTF-8"))
        ).observe(this, {
            clearProgram = it
        })

        binding.createApp.setOnClickListener {
            createApp()
        }
        binding.optInApp.setOnClickListener { optionApp() }
        binding.callApp.setOnClickListener { callApp() }
        binding.readLocalState.setOnClickListener { readLocalState() }
        binding.readGlobalState.setOnClickListener { readGlobalState() }
        binding.updateApp.setOnClickListener { updateApp() }
        binding.closeOutApp.setOnClickListener { closeOutApp() }
        binding.clearApp.setOnClickListener { clearApp() }
        binding.deleteApp.setOnClickListener { deleteApp() }
        binding.stateful.setOnClickListener {
            viewModel.statefulSmartContract()
        }
    }


    private fun createApp() {
        val approve = intent.getStringExtra("approval")
        val clear = intent.getStringExtra("clear")
        Timber.d("program1 $approve")
        Timber.d("clear1 $clear")

        viewModel.createApp(
            client, creatorAccount, TEALProgram(approvalProgram),
            TEALProgram(clearProgram),
            globalInts,
            globalBytes,
            localInts,
            localBytes
        )?.observe(this, {
            appId = it
            Timber.d("appId $appId")
            binding.stflOutput.text = "Created app id: $it"

        })
    }

    private fun optionApp() {
        viewModel.optInApp(client, userAccount, appId).observe(this, { appId ->
            binding.stflOutput.text =
                "OptIn from account: ${userAccount.address}\n OptIn to app-id: $appId"
        })
    }

    private fun callApp() {
        viewModel.callApp(client, userAccount, appId, null).observe(this, { appId ->
            binding.stflOutput.text =
                " Call from account: ${userAccount.address}\n Called app-id: $appId"
        })
    }

    private fun readLocalState() {
        viewModel.readLocalState(client, userAccount, appId).observe(this, { appId ->
            binding.stflOutput.text = " User's Application Local State: $appId"
        })
    }

    private fun readGlobalState() {
        viewModel.readGlobalState(client, userAccount, appId).observe(this, { appId ->
            binding.stflOutput.text = "Application Global State: $appId"
        })
    }

    private fun updateApp() {
        viewModel.updateApp(
            client, creatorAccount, appId, TEALProgram(approvalProgram2),
            TEALProgram(clearProgram)
        ).observe(this, { ptr ->
            binding.stflOutput.text = "Updated new app-id: $appId and $ptr"
        })
    }

    private fun closeOutApp() {
        viewModel.closeOutApp(
            client, userAccount, appId
        ).observe(this, { ptr ->
            binding.stflOutput.text = "Closed out from  app-id: $appId and $ptr"
        })
    }

    private fun clearApp() {
        viewModel.clearApp(
            client, userAccount, appId
        ).observe(this, { ptr ->
            binding.stflOutput.text = "Cleared local state for  app-id: $appId and ${ptr}"
        })
    }

    private fun deleteApp() {
        viewModel.deleteApp(
            client, creatorAccount, appId
        ).observe(this, { ptr ->
            binding.stflOutput.text = "Deleted app-id: $appId and $ptr"
        })
    }


    companion object {
        // user declared account mnemonics
        val creatorMnemonic = Constants.CREATOR_MNEMONIC
        val userMnemonic = Constants.USER_MNEMONIC

        // get accounts from mnemonic
        val creatorAccount = Account(creatorMnemonic)
        val userAccount = Account(userMnemonic)

        // declare application state storage (immutable)
        val localInts = 1
        val localBytes = 1
        val globalInts = 1
        val globalBytes = 0

        // user declared approval program (initial)
        val approvalProgramSourceInitial = """
            #pragma version 2
            ///// Handle each possible OnCompletion type. We don't have to worry about
            //// handling ClearState, because the ClearStateProgram will execute in that
            //// case, not the ApprovalProgram.
            txn OnCompletion
            int NoOp
            ==
            bnz handle_noop
            txn OnCompletion
            int OptIn
            ==
            bnz handle_optin
            txn OnCompletion
            int CloseOut
            ==
            bnz handle_closeout
            txn OnCompletion
            int UpdateApplication
            ==
            bnz handle_updateapp
            txn OnCompletion
            int DeleteApplication
            ==
            bnz handle_deleteapp
            //// Unexpected OnCompletion value. Should be unreachable.
            err
            handle_noop:
            //// Handle NoOp
            //// Check for creator
            addr FR23WI5ZTTRNYTXHA73GIJNS6BDXR3PZA6WETQF7IO6YBBYBS27TXUDNPI
            txn Sender
            ==
            bnz handle_optin
            //// read global state
            byte "counter"
            dup
            app_global_get
            //// increment the value
            int 1
            +
            //// store to scratch space
            dup
            store 0
            //// update global state
            app_global_put
            //// read local state for sender
            int 0
            byte "counter"
            app_local_get
            //// increment the value
            int 1
            +
            store 1
            //// update local state for sender
            int 0
            byte "counter"
            load 1
            app_local_put
            //// load return value as approval
            load 0
            return
            handle_optin:
            //// Handle OptIn
            //// approval
            int 1
            return
            handle_closeout:
            //// Handle CloseOut
            ////approval
            int 1
            return
            handle_deleteapp:
            //// Check for creator
            addr FR23WI5ZTTRNYTXHA73GIJNS6BDXR3PZA6WETQF7IO6YBBYBS27TXUDNPI
            txn Sender
            ==
            return
            handle_updateapp:
            //// Check for creator
            addr FR23WI5ZTTRNYTXHA73GIJNS6BDXR3PZA6WETQF7IO6YBBYBS27TXUDNPI
            txn Sender
            ==
            return
            
            """.trimIndent()

        // user declared approval program (refactored)
        val approvalProgramSourceRefactored = """
            #pragma version 2
            //// Handle each possible OnCompletion type. We don't have to worry about
            //// handling ClearState, because the ClearStateProgram will execute in that
            //// case, not the ApprovalProgram.
            txn OnCompletion
            int NoOp
            ==
            bnz handle_noop
            txn OnCompletion
            int OptIn
            ==
            bnz handle_optin
            txn OnCompletion
            int CloseOut
            ==
            bnz handle_closeout
            txn OnCompletion
            int UpdateApplication
            ==
            bnz handle_updateapp
            txn OnCompletion
            int DeleteApplication
            ==
            bnz handle_deleteapp
            //// Unexpected OnCompletion value. Should be unreachable.
            err
            handle_noop:
            //// Handle NoOp
            //// Check for creator
            addr FR23WI5ZTTRNYTXHA73GIJNS6BDXR3PZA6WETQF7IO6YBBYBS27TXUDNPI
            txn Sender
            ==
            bnz handle_optin
            //// read global state
            byte "counter"
            dup
            app_global_get
            //// increment the value
            int 1
            +
            //// store to scratch space
            dup
            store 0
            //// update global state
            app_global_put
            //// read local state for sender
            int 0
            byte "counter"
            app_local_get
            //// increment the value
            int 1
            +
            store 1
            //// update local state for sender
            //// update "counter"
            int 0
            byte "counter"
            load 1
            app_local_put
            //// update "timestamp"
            int 0
            byte "timestamp"
            txn ApplicationArgs 0
            app_local_put
            //// load return value as approval
            load 0
            return
            handle_optin:
            //// Handle OptIn
            //// approval
            int 1
            return
            handle_closeout:
            //// Handle CloseOut
            ////approval
            int 1
            return
            handle_deleteapp:
            //// Check for creator
            addr FR23WI5ZTTRNYTXHA73GIJNS6BDXR3PZA6WETQF7IO6YBBYBS27TXUDNPI
            txn Sender
            ==
            return
            handle_updateapp:
            //// Check for creator
            addr FR23WI5ZTTRNYTXHA73GIJNS6BDXR3PZA6WETQF7IO6YBBYBS27TXUDNPI
            txn Sender
            ==
            return
            
            """.trimIndent()

        // declare clear state program source
        val clearProgramSource = """
            #pragma version 2
            int 1
            
            """.trimIndent()
    }
}