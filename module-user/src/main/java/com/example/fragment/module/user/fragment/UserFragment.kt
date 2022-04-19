package com.example.fragment.module.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.fragment.library.base.model.BaseViewModel
import com.example.fragment.library.base.utils.loadCircleCrop
import com.example.fragment.library.base.utils.loadRoundedCorners
import com.example.fragment.library.common.constant.Router
import com.example.fragment.library.common.fragment.RouterFragment
import com.example.fragment.module.user.R
import com.example.fragment.module.user.databinding.UserFragmentBinding
import com.example.fragment.module.user.model.UserViewModel
import java.io.File

class UserFragment : RouterFragment() {

    companion object {
        @JvmStatic
        fun newInstance(): UserFragment {
            return UserFragment()
        }
    }

    private val viewModel: UserViewModel by activityViewModels()
    private var _binding: UserFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun initView() {
        binding.avatar.loadCircleCrop(R.drawable.avatar_1_raster)
        binding.avatar.setOnClickListener { activity.navigation(Router.USER_LOGIN) }
        binding.username.setOnClickListener { activity.navigation(Router.USER_LOGIN) }
        binding.myCoin.setOnClickListener { activity.navigation(Router.MY_COIN) }
        binding.myCollection.setOnClickListener { activity.navigation(Router.MY_COLLECT) }
        binding.myShare.setOnClickListener { activity.navigation(Router.MY_SHARE) }
        binding.setting.setOnClickListener { activity.navigation(Router.SETTING) }
    }

    override fun initViewModel(): BaseViewModel {
        viewModel.userResult.observe(viewLifecycleOwner) { userBean ->
            if (userBean.id.isNotBlank()) {
                if (userBean.avatar.isNotBlank()) {
                    binding.avatar.loadCircleCrop(File(userBean.avatar))
                }
                binding.username.text = "欢迎回来！${userBean.username}"
                binding.avatar.setOnClickListener { activity.navigation(Router.USER_INFO) }
                binding.username.setOnClickListener { activity.navigation(Router.USER_INFO) }
            } else {
                binding.avatar.loadCircleCrop(R.drawable.avatar_1_raster)
                binding.username.text = "去登录"
                binding.avatar.setOnClickListener { activity.navigation(Router.USER_LOGIN) }
                binding.username.setOnClickListener { activity.navigation(Router.USER_LOGIN) }
            }
        }
        return viewModel
    }

    override fun initLoad() {
        if (viewModel.userResult.value == null) {
            viewModel.getUser()
        }
    }

}