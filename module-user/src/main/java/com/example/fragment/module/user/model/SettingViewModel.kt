package com.example.fragment.module.user.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fragment.library.base.http.HttpRequest
import com.example.fragment.library.base.http.HttpResponse
import com.example.fragment.library.base.http.download
import com.example.fragment.library.base.http.get
import com.example.fragment.library.base.model.BaseViewModel
import com.example.fragment.library.common.utils.WanHelper
import com.example.fragment.module.user.bean.UpdateBean
import kotlinx.coroutines.launch

class SettingViewModel : BaseViewModel() {

    val screenRecordResult = MutableLiveData<String>()
    val logoutResult = MutableLiveData<HttpResponse>()
    val updateResult = MutableLiveData<UpdateBean?>()
    val downloadApkResult = MutableLiveData<HttpResponse?>()

    fun getScreenRecord() {
        WanHelper.getScreenRecord {
            screenRecordResult.postValue(it)
        }
    }

    fun updateScreenRecord(status: String) {
        WanHelper.setScreenRecord(status)
        screenRecordResult.postValue(status)
    }

    /**
     * 退出登录
     */
    fun logout() {
        //通过viewModelScope创建一个协程
        viewModelScope.launch {
            //构建请求体，传入请求参数
            val request = HttpRequest("user/logout/json")
            //以get方式发起网络请求
            val response = get<HttpResponse>(request) { updateProgress(it) }
            //通过LiveData通知界面更新
            logoutResult.postValue(response)
        }
    }

    fun update() {
        //通过viewModelScope创建一个协程
        viewModelScope.launch {
            val url = "http://122.51.186.2:8080/update.json"
            //构建请求体，传入请求参数
            val request = HttpRequest(url)
            //以get方式发起网络请求
            val response = get<UpdateBean>(request) { updateProgress(it) }
            //通过LiveData通知界面更新
            updateResult.postValue(response)
        }
    }

    fun downloadApk(url: String, filePathName: String) {
        //通过viewModelScope创建一个协程
        viewModelScope.launch {
            //构建请求体，传入请求参数
            val request = HttpRequest(url)
            //以get方式发起网络请求
            val response = download(request, filePathName)
            response.errorMsg = filePathName
            //通过LiveData通知界面更新
            downloadApkResult.postValue(response)
        }
    }

}