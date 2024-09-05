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
class EditProfileViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var editProfileViewModel: EditProfileViewModel
    private lateinit var fakeChatService: FakeChatService

    private val fakeObserver = Observer<EditProfileViewModel.UpdateProfileState> { }

    @Before
    fun setup() {
        runOnUiThread {
            val context = ApplicationProvider.getApplicationContext<Application>()
            fakeChatService = FakeChatService()
            editProfileViewModel = EditProfileViewModel(context).apply {
                chatManager = fakeChatService
                isServiceConnected = true // Ensure service is connected
            }
            editProfileViewModel.updateProfileState.observeForever(fakeObserver)
        }
    }

    @After
    fun tearDown() {
        runOnUiThread {
            editProfileViewModel.updateProfileState.removeObserver(fakeObserver)
        }
    }

    @Test
    fun `update profile with valid user should succeed`() = runTest {
        val updatedUser = User(1, "Jane Doe", "1234567890", "newAvatar")

        editProfileViewModel.updateProfile(updatedUser)

        advanceUntilIdle()
        delay(200)
        assertEquals(
            EditProfileViewModel.UpdateProfileState.Success(updatedUser),
            editProfileViewModel.updateProfileState.value
        )
    }

    @Test
    fun `update profile with service not connected should return error`() = runTest {
        editProfileViewModel.isServiceConnected = false
        val user = User(1, "Jane Doe", "1234567890", "newAvatar")

        editProfileViewModel.updateProfile(user)

        advanceUntilIdle()
        delay(100)
        assertEquals(
            EditProfileViewModel.UpdateProfileState.Error("Service not connected"),
            editProfileViewModel.updateProfileState.value
        )
    }
}