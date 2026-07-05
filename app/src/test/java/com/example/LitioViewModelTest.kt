package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.ui.LitioViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LitioViewModelTest {

    @Test
    fun testSharedPreferencesPersistence() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LitioViewModel(app)

        // Initial default config should be empty or default
        assertEquals("", viewModel.googleSheetsWebhookUrl)

        // Set a new webhook URL
        val testUrl = "https://script.google.com/macros/s/ABC/exec"
        viewModel.updateGoogleSheetsWebhookUrl(testUrl)

        // Verify the value updated in current session
        assertEquals(testUrl, viewModel.googleSheetsWebhookUrl)

        // Instantiate a new ViewModel to verify it successfully loaded from SharedPreferences
        val secondViewModel = LitioViewModel(app)
        assertEquals(testUrl, secondViewModel.googleSheetsWebhookUrl)
    }

    @Test
    fun testWhatsappNumberPersistence() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = LitioViewModel(app)

        // Set a new WhatsApp number
        val testPhone = "+51999888777"
        viewModel.updateWhatsappNumber(testPhone)

        // Verify value
        assertEquals(testPhone, viewModel.whatsappNumber)

        // Verify loaded in new instance
        val secondViewModel = LitioViewModel(app)
        assertEquals(testPhone, secondViewModel.whatsappNumber)
    }
}
