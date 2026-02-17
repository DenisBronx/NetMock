package com.denisbrandi.netmock.server.mappers

import com.denisbrandi.netmock.interceptors.InterceptedRequest
import com.denisbrandi.netmock.server.INTERCEPTED_REQUEST_URL_HEADER
import java.nio.charset.Charset
import mockwebserver3.RecordedRequest
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrl
import okio.Buffer
import okio.ByteString
import org.junit.Assert.assertEquals
import org.junit.Test

class MockWebServerRequestMapperTest {
    private val sut = MockWebServerRequestMapper

    @Test
    fun `EXPECT mapped request WHEN has all fields`() {
        val recordedRequest = makeRecordedRequest(
            method = "GET",
            target = "/somePath?1=2&3=4",
            version = "HTTP/1.1",
            headers = RECORDED_HEADERS,
            body = Buffer().writeString("data", Charset.defaultCharset()).readByteString()
        )

        val result = sut.mapRequest(recordedRequest)

        assertEquals(EXPECTED_REQUEST, result)
    }

    @Test
    fun `EXPECT mapped request WHEN there are missing fields`() {
        val recordedRequest = makeRecordedRequest(
            method = "",
            target = "",
            version = "",
            headers = Headers.headersOf(),
            body = null
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
            requestUrl = "https://requesturl.com/",
            headers = emptyMap(),
            method = "",
            body = ""
        )

        fun makeRecordedRequest(
            method: String,
            target: String,
            version: String,
            headers: Headers,
            body: ByteString?,
        ): RecordedRequest {
            return RecordedRequest(
                method = method,
                target = target,
                version = version,
                headers = headers,
                chunkSizes = emptyList(),
                bodySize = 5,
                body = body,
                connectionIndex = 0,
                exchangeIndex = 0,
                handshake = null,
                handshakeServerNames = emptyList(),
                url = "https://requesturl.com".toHttpUrl()
            )
        }
    }
}
