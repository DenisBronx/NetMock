package com.denisbrandi.netmock.matchers

import kotlinx.serialization.json.*

object RequestBodyMatcher {
    fun isMatchingTheBody(recordedRequestBody: String, netMockRequestBody: String): Boolean {
        return if (recordedRequestBody == netMockRequestBody) {
            true
        } else {
            val recordedJsonObject = asJsonObject(recordedRequestBody)
            if (recordedJsonObject != null) {
                recordedJsonObject == asJsonObject(netMockRequestBody)
            } else {
                val recordedJsonArray = asJsonArray(recordedRequestBody)
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