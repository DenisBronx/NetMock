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
        val result = sut.mapRequest(makeRecordedRequest())

        assertEquals(EXPECTED_REQUEST, result)
    }

    @Test
    fun `EXPECT mapped request WHEN null request url`() {
        val result = sut.mapRequest(makeRecordedRequest(headers = RECORDER_HEADERS_NO_REDIRECT))

        assertEquals(EXPECTED_REQUEST.copy(requestUrl = null, headers = EXPECTED_HEADERS_NO_REDIRECT), result)
    }

    @Test
    fun `EXPECT mapped request WHEN empty body`() {
        val result = sut.mapRequest(makeRecordedRequest(body = Buffer()))

        assertEquals(EXPECTED_REQUEST.copy(body = ""), result)
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
        val RECORDER_HEADERS_NO_REDIRECT = Headers.headersOf(
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
        val EXPECTED_HEADERS_NO_REDIRECT = mapOf(
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

        fun makeRecordedRequest(
            requestLine: String = "GET /somePath?1=2&3=4 HTTP/1.1",
            headers: Headers = RECORDED_HEADERS,
            body: Buffer = Buffer().writeString("data", Charset.defaultCharset())
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
