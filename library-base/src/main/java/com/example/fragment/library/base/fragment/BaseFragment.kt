package com.example.fragment.library.base.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.example.fragment.library.base.activity.BaseActivity
import com.example.fragment.library.base.model.BaseViewModel

abstract class BaseFragment : Fragment() {

    private var isVisibleToUser = false
    var delayedLoad = 300L

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = true
        view.isFocusable = true
        initView()
        initViewModel()?.progressState(
            viewLifecycleOwner,
            { showProgress() },
            { dismissProgress() }
        )
        view.postDelayed({
            // 在转场动画结束后加载数据，
            // 用于解决过度动画卡顿问题。
            initLoad()
        }, delayedLoad)
    }

    override fun onResume() {
        super.onResume()
        isVisibleToUser = true
    }

    override fun onPause() {
        super.onPause()
        isVisibleToUser = false
        hideInputMethod()
    }

    abstract fun initView()
    abstract fun initViewModel(): BaseViewModel?

    /**
     * 请在这里初始化数据！
     */
    abstract fun initLoad()

    private fun showProgress() {
        if (isVisibleToUser) {
            (requireActivity() as BaseActivity).showProgress()
        }
    }

    private fun dismissProgress() {
        (requireActivity() as BaseActivity).dismissProgress()
    }

    private fun hideInputMethod() {
        val imm = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val view = requireActivity().currentFocus ?: return
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}