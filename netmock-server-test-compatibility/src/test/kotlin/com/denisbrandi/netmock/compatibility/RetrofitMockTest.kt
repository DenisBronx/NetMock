package com.denisbrandi.netmock.compatibility

import com.denisbrandi.netmock.*
import com.denisbrandi.netmock.assets.readFromResources
import com.denisbrandi.netmock.server.NetMockServerRule
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import org.junit.*
import org.junit.Assert.*
import retrofit2.*
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*

class RetrofitMockTest {

    @get:Rule
    val netMock = NetMockServerRule()

    private val sut = Retrofit.Builder()
        .baseUrl(netMock.baseUrl)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(RetrofitApi::class.java)

    @Test
    fun `EXPECT GET response`() = runTest {
        netMock.addMock(EXPECTED_COMPLETE_REQUEST, EXPECTED_RESPONSE)

        val response = sut.get(headers = mapOf("a" to "b", "c" to "d"), params = mapOf("1" to "2", "3" to "4"))

        assertEquals("data", response.string())
    }

    @Test
    fun `EXPECT HEAD response`() = runTest {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = Method.Head)
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE.copy(body = ""))

        val response = sut.head(headers = mapOf("a" to "b", "c" to "d"), params = mapOf("1" to "2", "3" to "4"))

        assertEquals(listOf(expectedRequest), netMock.interceptedRequests)
        assertEquals(EXPECTED_RESPONSE.code, response.code())
        EXPECTED_RESPONSE.containsHeaders.forEach {
            assertTrue(response.headers().contains(it.key to it.value))
        }
    }

    @Test
    fun `EXPECT POST response`() = runTest {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = Method.Post, body = REQUEST_BODY)
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response =
            sut.post(headers = mapOf("a" to "b", "c" to "d"), params = mapOf("1" to "2", "3" to "4"), REQUEST_BODY)

        assertEquals("data", response.string())
    }

    @Test
    fun `EXPECT PUT response`() = runTest {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = Method.Put, body = REQUEST_BODY)
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response =
            sut.put(headers = mapOf("a" to "b", "c" to "d"), params = mapOf("1" to "2", "3" to "4"), REQUEST_BODY)

        assertEquals("data", response.string())
    }

    @Test
    fun `EXPECT DELETE response`() = runTest {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = Method.Delete, body = REQUEST_BODY)
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response =
            sut.delete(headers = mapOf("a" to "b", "c" to "d"), params = mapOf("1" to "2", "3" to "4"), REQUEST_BODY)

        assertEquals("data", response.string())
    }

    @Test
    fun `EXPECT PATCH response`() = runTest {
        val expectedRequest = EXPECTED_COMPLETE_REQUEST.copy(method = Method.Patch, body = REQUEST_BODY)
        netMock.addMock(expectedRequest, EXPECTED_RESPONSE)

        val response =
            sut.patch(headers = mapOf("a" to "b", "c" to "d"), params = mapOf("1" to "2", "3" to "4"), REQUEST_BODY)

        assertEquals("data", response.string())
    }

    private interface RetrofitApi {
        @GET("/somePath")
        suspend fun get(
            @HeaderMap headers: Map<String, String>,
            @QueryMap params: Map<String, String>
        ): ResponseBody

        @HEAD("/somePath")
        suspend fun head(
            @HeaderMap headers: Map<String, String>,
            @QueryMap params: Map<String, String>
        ): Response<Void>

        @POST("/somePath")
        suspend fun post(
            @HeaderMap headers: Map<String, String>,
            @QueryMap params: Map<String, String>,
            @Body data: String
        ): ResponseBody

        @PUT("/somePath")
        suspend fun put(
            @HeaderMap headers: Map<String, String>,
            @QueryMap params: Map<String, String>,
            @Body data: String
        ): ResponseBody

        @HTTP(
            method = "DELETE",
            path = "/somePath",
            hasBody = true
        ) // workaround as retrofit DELETE does not support body
        suspend fun delete(
            @HeaderMap headers: Map<String, String>,
            @QueryMap params: Map<String, String>,
            @Body data: String
        ): ResponseBody

        @PATCH("/somePath")
        suspend fun patch(
            @HeaderMap headers: Map<String, String>,
            @QueryMap params: Map<String, String>,
            @Body data: String
        ): ResponseBody
    }

    private companion object {
        val REQUEST_BODY = readFromResources("request_body.json")
        val EXPECTED_COMPLETE_REQUEST = NetMockRequest(
            path = "/somePath",
            method = Method.Get,
            containsHeaders = mapOf("a" to "b", "c" to "d"),
            params = mapOf("1" to "2", "3" to "4")
        )
        val EXPECTED_RESPONSE = NetMockResponse(code = 201, containsHeaders = mapOf("x" to "y"), body = "data")
    }
}