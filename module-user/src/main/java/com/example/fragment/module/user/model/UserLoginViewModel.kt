package com.example.fragment.module.user.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.fragment.library.base.http.HttpRequest
import com.example.fragment.library.base.http.post
import com.example.fragment.library.base.model.BaseViewModel
import com.example.fragment.library.common.bean.LoginBean
import com.example.fragment.library.common.bean.RegisterBean
import kotlinx.coroutines.launch

class UserLoginViewModel : BaseViewModel() {

    val loginResult = MutableLiveData<LoginBean>()
    val registerResult = MutableLiveData<RegisterBean>()

    fun login(username: String, password: String) {
        //通过viewModelScope创建一个协程
        viewModelScope.launch {
            //构建请求体，传入请求参数
            val request = HttpRequest("user/login")
                .putParam("username", username)
                .putParam("password", password)
            //以post方式发起网络请求
            val response = post<LoginBean>(request) { updateProgress(it) }
            //通过LiveData通知界面更新
            loginResult.postValue(response)
        }
    }

    fun register(username: String, password: String, repassword: String) {
        viewModelScope.launch {
            val request = HttpRequest("user/register")
                .putParam("username", username)
                .putParam("password", password)
                .putParam("repassword", repassword)
            val response = post<RegisterBean>(request) { updateProgress(it) }
            registerResult.postValue(response)
        }
    }

}