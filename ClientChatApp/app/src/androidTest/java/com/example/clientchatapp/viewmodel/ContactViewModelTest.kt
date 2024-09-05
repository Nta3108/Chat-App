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
class ContactViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: ContactViewModel
    private lateinit var fakeChatService: FakeChatService
    private val observer = Observer<ContactViewModel.ContactsState> {}

    @Before
    fun setup() {
        runOnUiThread {
            fakeChatService = FakeChatService()
            val application = ApplicationProvider.getApplicationContext<Application>()
            viewModel = ContactViewModel(application).apply {
                chatManager = fakeChatService
                isServiceConnected = true // Assume the service is connected for testing
            }
            viewModel.contactsState.observeForever(observer)
        }
    }

    @After
    fun tearDown() {
        runOnUiThread {
            viewModel.contactsState.removeObserver(observer)
        }
    }


    @Test
    fun `load contacts successfully should return contacts`() = runTest {
        // Setup
        val users = listOf(
            User(1, "Jane Doe", "0987654321", "avatar1"),
            User(2, "John Smith", "0123456789", "avatar2")
        )
        fakeChatService.users.addAll(users)

        // Action
        viewModel.loadContacts()

        // Ensure all coroutines have completed
        advanceUntilIdle()
        delay(300)

        // Verify
        assertEquals(ContactViewModel.ContactsState.Success(users), viewModel.contactsState.value)
    }

    @Test
    fun `search contacts successfully should return matching users`() = runTest {
        // Setup
        val users = listOf(
            User(1, "Jane Doe", "0987654321", "avatar1"),
            User(2, "John Smith", "0123456789", "avatar2")
        )
        fakeChatService.users.addAll(users)

        // Action
        viewModel.searchContacts("Jane")

        // Ensure all coroutines have completed
        advanceUntilIdle()
        delay(200)

        // Verify
        val expectedUsers = listOf(users[0])
        assertEquals(
            ContactViewModel.ContactsState.Success(expectedUsers),
            viewModel.contactsState.value
        )
    }

    @Test
    fun `search contacts with no matches should return error`() = runTest {
        // Setup
        val users = listOf(
            User(1, "Jane Doe", "0987654321", "avatar1"),
            User(2, "John Smith", "0123456789", "avatar2")
        )
        fakeChatService.users.addAll(users)

        // Action
        viewModel.searchContacts("NonExistent")

        // Ensure all coroutines have completed
        advanceUntilIdle()
        delay(100)
        // Verify
        assertEquals(
            ContactViewModel.ContactsState.Error("No users found"),
            viewModel.contactsState.value
        )
    }
}