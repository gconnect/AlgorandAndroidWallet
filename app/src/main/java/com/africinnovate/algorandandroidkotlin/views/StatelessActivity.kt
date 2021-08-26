package com.africinnovate.algorandandroidkotlin.views

    import android.os.Bundle
    import androidx.activity.viewModels
    import androidx.appcompat.app.AppCompatActivity
    import androidx.databinding.DataBindingUtil
    import com.africinnovate.algorandandroidkotlin.R
    import com.africinnovate.algorandandroidkotlin.databinding.ActivityStatelessBinding
    import com.africinnovate.algorandandroidkotlin.viewmodel.StatelessSmartContractViewModel
    import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatelessActivity : AppCompatActivity() {
    private val viewModel: StatelessSmartContractViewModel by viewModels()
    private lateinit var binding: ActivityStatelessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_stateless)

        binding.compile.setOnClickListener {
            viewModel.compileTealSource().observe(this, { response ->
                binding.output.text = " response :$response\n  hash: ${response.hash}\n  result: ${response.result}  "
            })
        }

        binding.contractAccount.setOnClickListener {
            viewModel.contractAccount().observe(this, { response ->
                binding.output.text = " response :$response\n  hash: ${response.hash}\n  result: ${response.result}  "
            })
        }

        binding.accountDelegation.setOnClickListener {
            viewModel.accountDelegation().observe(this, { response ->
                binding.output.text = " response :$response\n  hash: ${response.hash}\n  result: ${response.result}  "
            })
        }
    }

}