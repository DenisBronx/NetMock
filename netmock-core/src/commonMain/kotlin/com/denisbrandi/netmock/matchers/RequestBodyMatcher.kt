package com.denisbrandi.netmock.matchers

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

internal object RequestBodyMatcher {
    fun isMatchingTheBody(recordedRequestBody: String?, netMockRequestBody: String): Boolean {
        val sanitizedRecordedRequestBody = recordedRequestBody.orEmpty()
        return if (sanitizedRecordedRequestBody == netMockRequestBody) {
            true
        } else {
            val recordedJsonObject = asJsonObject(sanitizedRecordedRequestBody)
            if (recordedJsonObject != null) {
                recordedJsonObject == asJsonObject(netMockRequestBody)
            } else {
                val recordedJsonArray = asJsonArray(sanitizedRecordedRequestBody)
                if (recordedJsonArray != null) {
                    recordedJsonArray == asJsonArray(netMockRequestBody)
                } else {
                    false
                }
            }
        }
    }

    private fun asJsonObject(jsonString: String): JsonObject? {
        return try {
            Json.decodeFromString<JsonObject>(jsonString)
        } catch (t: Throwable) {
            null
        }
    }

    private fun asJsonArray(jsonString: String): JsonArray? {
        return try {
            Json.decodeFromString<JsonArray>(jsonString)
        } catch (t: Throwable) {
            null
        }
    }
}
