package com.datangic.api

import com.datangic.api.login.LoginApi
import com.datangic.api.reqresIn.ReqresIn
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations


class LoginApiTest {
    @Mock
    val mockList = mutableListOf<String>()

    @Before
    fun beforeTest() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    @Throws(Exception::class)
    fun getUserTest() {
        //使用mock对象执行方法
        mockList.add("one")
        mockList.clear()

        //检验方法是否调用
        verify(mockList).add("one")
        verify(mockList).clear()
        println(LoginApi.LoginType.RegisterORLogin.str)
    }

}