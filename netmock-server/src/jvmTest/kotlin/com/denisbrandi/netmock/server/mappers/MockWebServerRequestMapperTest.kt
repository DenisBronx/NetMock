package com.denisbrandi.netmock.server.mappers

import com.denisbrandi.netmock.interceptors.InterceptedRequest
import com.denisbrandi.netmock.server.INTERCEPTED_REQUEST_URL_HEADER
import java.net.Socket
import java.nio.charset.Charset
import okhttp3.Headers
import okhttp3.mockwebserver.RecordedRequest
import okio.Buffer
import org.junit.Assert.assertEquals
import org.junit.Test

class MockWebServerRequestMapperTest {
    private val sut = MockWebServerRequestMapper

    @Test
    fun `EXPECT mapped request WHEN has all fields`() {
        val recordedRequest = makeRecordedRequest(
            requestLine = "GET /somePath?1=2&3=4 HTTP/1.1",
            headers = RECORDED_HEADERS,
            body = Buffer().writeString("data", Charset.defaultCharset())
        )

        val result = sut.mapRequest(recordedRequest)

        assertEquals(EXPECTED_REQUEST, result)
    }

    @Test
    fun `EXPECT mapped request WHEN there are missing fields`() {
        val recordedRequest = makeRecordedRequest(
            requestLine = "",
            headers = Headers.headersOf(),
            body = Buffer()
        )

        val result = sut.mapRequest(recordedRequest)

        assertEquals(EXPECTED_REQUEST_ALL_FIELDS_MISSING, result)
    }

    private companion object {
        val RECORDED_HEADERS = Headers.headersOf(
            INTERCEPTED_REQUEST_URL_HEADER,
            "https://google.com/somePath?1=2&3=4",
            "a",
            "b",
            "c",
            "d",
            "Host",
            "localhost:60000"
        )
        val EXPECTED_HEADERS = mapOf(
            INTERCEPTED_REQUEST_URL_HEADER to "https://google.com/somePath?1=2&3=4",
            "a" to "b",
            "c" to "d",
            "Host" to "localhost:60000"
        )
        val EXPECTED_REQUEST = InterceptedRequest(
            requestUrl = "https://google.com/somePath?1=2&3=4",
            headers = EXPECTED_HEADERS,
            method = "GET",
            body = "data"
        )

        val EXPECTED_REQUEST_ALL_FIELDS_MISSING = InterceptedRequest(
            requestUrl = "",
            headers = emptyMap(),
            method = "",
            body = ""
        )

        fun makeRecordedRequest(
            requestLine: String,
            headers: Headers,
            body: Buffer
        ): RecordedRequest {
            return RecordedRequest(
                requestLine = requestLine,
                headers = headers,
                chunkSizes = emptyList(),
                bodySize = 5,
                body = body,
                sequenceNumber = 9,
                socket = Socket()
            )
        }
    }
}
