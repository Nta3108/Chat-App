package com.example.clientchatapp.viewmodel

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.example.clientchatapp.setuptest.FakeChatService
import com.example.clientchatapp.setuptest.MainCoroutineRule
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
class RegisterViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var fakeChatService: FakeChatService

    private val fakeObserver = Observer<RegisterViewModel.RegisterState> { }

    @Before
    fun setup() {
        runOnUiThread {
            val context = ApplicationProvider.getApplicationContext<Application>()
            fakeChatService = FakeChatService()
            registerViewModel = RegisterViewModel(context).apply {
                chatManager = fakeChatService
                isServiceConnected = true // Ensure service is connected
            }
            registerViewModel.registerState.observeForever(fakeObserver)
        }
    }

    @After
    fun tearDown() {
        runOnUiThread {
            registerViewModel.registerState.removeObserver(fakeObserver)
        }
    }

    @Test
    fun `register with valid information should succeed`() = runTest {
        // Setup
        val name = "Jane Doe"
        val phone = "0987654321"
        val avatar = "avatar"

        // Action
        registerViewModel.registerUser(name, phone, avatar)

        // Ensure all coroutines have completed
        advanceUntilIdle()
        delay(100)

        // Verify
        assertEquals(RegisterViewModel.RegisterState.Success, registerViewModel.registerState.value)
    }

    @Test
    fun `register with already registered phone should return error`() = runTest {
        // Setup
        val name = "John Doe"
        val phone = "1234567890" // Phone number already exists in FakeChatService
        val avatar = "avatar"

        // Action
        registerViewModel.registerUser(name, phone, avatar)

        // Ensure all coroutines have completed
        advanceUntilIdle()
        delay(200)

        // Verify
        assertEquals(
            RegisterViewModel.RegisterState.Error("Phone number already registered"),
            registerViewModel.registerState.value
        )
    }

    @Test
    fun `register with invalid phone number should return error`() = runTest {
        // Setup
        val name = "Jane Doe"
        val phone = "123"
        val avatar = "avatar"

        // Action
        registerViewModel.registerUser(name, phone, avatar)

        // Ensure all coroutines have completed
        advanceUntilIdle()
        delay(50)

        // Verify
        assertEquals(
            RegisterViewModel.RegisterState.Error("Phone number must be 10 digits"),
            registerViewModel.registerState.value
        )
    }

    @Test
    fun `register when service is not connected should return error`() = runTest {
        // Setup
        val name = "Jane Doe"
        val phone = "0987654321"
        val avatar = "avatar"

        registerViewModel.isServiceConnected = false

        // Action
        registerViewModel.registerUser(name, phone, avatar)

        advanceUntilIdle()
        delay(50)

        // Verify
        assertEquals(
            RegisterViewModel.RegisterState.Error("Service not connected"),
            registerViewModel.registerState.value
        )
    }
}


