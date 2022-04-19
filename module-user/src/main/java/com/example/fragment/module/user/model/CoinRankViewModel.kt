package com.example.fragment.module.user.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fragment.library.base.http.HttpRequest
import com.example.fragment.library.base.http.get
import com.example.fragment.library.base.model.BaseViewModel
import com.example.fragment.library.common.bean.CoinRankBean
import kotlinx.coroutines.launch

class CoinRankViewModel : BaseViewModel() {

    val coinRankResult = MutableLiveData<CoinRankBean>()

    fun getCoinRank() {
        getCoinRank(getHomePage(1))
    }

    fun getCoinRankNext() {
        getCoinRank(getNextPage())
    }

    /**
     * 获取积分排行榜
     * page 1开始
     */
    private fun getCoinRank(page: Int) {
        //通过viewModelScope创建一个协程
        viewModelScope.launch {
            //构建请求体，传入请求参数
            val request = HttpRequest("coin/rank/{page}/json").putPath("page", page.toString())
            //以get方式发起网络请求
            val response = get<CoinRankBean>(request)
            //根据接口返回更新总页码
            response.data?.pageCount?.let { updatePageCont(it.toInt()) }
            //通过LiveData通知界面更新
            coinRankResult.postValue(response)
        }
    }

}