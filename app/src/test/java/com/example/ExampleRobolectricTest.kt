package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.ApiFilter
import com.example.model.HttpMethod
import com.example.model.StatusCategory
import com.example.network.ApiRequestDto
import com.example.network.toDomain
import com.example.repository.FakeApiRepository
import com.example.utils.JsonFormatter
import com.example.viewmodel.ApiViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Network Inspector", appName)
    }

    @Test
    fun `fake api repository returns realistic sample requests`() = runTest {
        val repo = FakeApiRepository()
        val requests = repo.getRequests()
        assertTrue(requests.isNotEmpty())
        assertTrue(requests.size >= 15)
        assertTrue(requests.any { it.method == HttpMethod.POST && it.path == "/post" })
        assertTrue(requests.any { it.isError })
    }

    @Test
    fun `dto mapping produces valid domain model`() {
        val dto = ApiRequestDto(
            id = "test-123",
            method = "POST",
            endpoint = "/api/payment",
            status = 201,
            message = "Created",
            duration = 142
        )
        val domain = dto.toDomain()
        assertEquals(HttpMethod.POST, domain.method)
        assertEquals("/api/payment", domain.path)
        assertEquals(201, domain.statusCode)
        assertEquals(142L, domain.durationMs)
        assertEquals(StatusCategory.SUCCESS, domain.statusCategory)
    }

    @Test
    fun `api viewmodel can be instantiated with default constructor by viewmodel provider`() {
        // Verifies no NoSuchMethodException <init> []
        val viewModel = ApiViewModel()
        assertNotNull(viewModel)
        assertNotNull(viewModel.uiState.value)
    }

    @Test
    fun `json formatter handles json objects and arrays`() {
        val raw = "{\"name\":\"Maya\",\"age\":28}"
        val formatted = JsonFormatter.formatJson(raw)
        assertTrue(formatted.contains("\"name\": \"Maya\""))
        assertTrue(formatted.contains("\"age\": 28"))
    }
}
