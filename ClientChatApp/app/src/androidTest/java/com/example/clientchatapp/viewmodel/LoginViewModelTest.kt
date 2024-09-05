package com.example.clientchatapp.viewmodel

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.example.clientchatapp.setuptest.FakeChatService
import com.example.clientchatapp.setuptest.MainCoroutineRule
import com.example.serverchatapp.entities.User
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var fakeChatService: FakeChatService

    private val fakeObserver = Observer<LoginViewModel.LoginState> { }

    @Before
    fun setup() {
        runOnUiThread {
            val context = ApplicationProvider.getApplicationContext<Application>()
            fakeChatService = FakeChatService()
            loginViewModel = LoginViewModel(context).apply {
                chatManager = fakeChatService
                isServiceConnected = true // Ensure service is connected
            }
            loginViewModel.loginState.observeForever(fakeObserver)
        }
    }

    @After
    fun tearDown() {
        runOnUiThread {
            loginViewModel.loginState.removeObserver(fakeObserver)
        }
    }

    @Test
    fun `login with valid phone should succeed`() = runTest {
        loginViewModel.login("1234567890")

        advanceUntilIdle()
        delay(100)
        val expectedUser = User(1, "John Doe", "1234567890", "avatar")
        assertEquals(
            LoginViewModel.LoginState.Success(expectedUser),
            loginViewModel.loginState.value
        )
    }

    @Test
    fun `login with non-existent user should return error`() = runTest {
        loginViewModel.login("0987654321")

        // Use a delay to ensure LiveData has time to update
        advanceUntilIdle()
        delay(200)
        assertEquals(
            LoginViewModel.LoginState.Error("User not found"),
            loginViewModel.loginState.value
        )
    }

    @Test
    fun `login with invalid phone number should return error`() = runTest {
        loginViewModel.login("123")

        advanceUntilIdle()
        delay(100)
        assertEquals(
            LoginViewModel.LoginState.Error("Phone number must be 10 digits"),
            loginViewModel.loginState.value
        )
    }
}
