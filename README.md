# NetMock

NetMock is a powerful testing library that makes it incredibly easy to unit test your network
requests.
It is compatible with `Java`, `Kotlin`, `Android`, and `Kotlin-Multiplatform` making it a versatile
option for developers working on different platforms.

The library offers a variety of features that can help you test your network requests against any
network library, including `OkHttp`, `Ktor`, and `Retrofit`.
You can use a mock-like API to test your requests, making the test code much simpler and more
readable.

`NetMock` comes in two different flavors: `netmock-server` and `netmock-engine`.

The `netmock-server` flavor is compatible with `Java`, `Kotlin`, and `Android`, and it is **library
independent** as it allows you to mock network requests by redirecting requests to
a [localhost](http://localhost/) web server
using [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver).
This flavor is perfect for developers that work on non-Multiplatform projects and that want to test
their network requests without having to worry about setting up a separate server.

The `netmock-engine` flavor, on the other hand, is designed specifically for developers using `Ktor`
or working with `Kotlin Multiplatform`.
It allows you to use `MockEngine` instead of a localhost server, making it a more lightweight and
multiplatform option for those working with `Ktor`.

If your project is not a Kotlin multiplatform project, and you are using a variety of libraries, and
you don't want to import both flavors, just use `netmock-server` as it is compatible with all the
libraries including `Ktor`.

# Install

`Netmock` is available on Maven Central.

For Gradle users, add the following to your module’s `build.gradle`

```groovy
dependencies {
    //compatible with all libraries
    testImplementation "io.github.denisbronx.netmock:netmock-server:0.9.0"
    //mutliplatform and lighter weight option for ktor only library users
    testImplementation "io.github.denisbronx.netmock:netmock-engine:0.9.0"
    //library for accessing local json files in the test folder
    testImplementation "io.github.denisbronx.netmock:netmock-resources:0.9.0"
}
```

# Examples

## Initialization

[netmock-server initialization](netmock-server/README.md)

[netmock-engine initialization](netmock-engine/README.md)

## Mock requests and responses

```kotlin
@Test
fun `my test`() {
    netMock.addMock(
        request = {
            //exact method
            method = Method.Post
            //exact request url: scheme, base url, path, params...
            requestUrl = "https://google.com/somePath?paramKey1=paramValue1"
            //must-have headers, as some clients add extra headers you may not want to check them all
            //if you are using ktor and your response body is a json, you must have "Content-Type: application/json" as header
            mandatoryHeaders = mapOf("a" to "b", "b" to "c")
            //request body, must be a String (this allows you to test your parsing)
            body = """{"id": "2"}"""
            //or, you can read a file in "test/resources"
            //body = readFromResources("requests/request_body.json")
        },
        response = {
            //status code
            code = 200
            //must-have headers
            //if you are using ktor and your response body is a json, you must have "Content-Type: application/json" as header
            mandatoryHeaders = mapOf("a" to "b", "b" to "c")
            //response body, must be a String (this allows you to test your parsing)
            body = """{"data": "text"}"""
            //or, you can read a file in "test/resources"
            //body = readFromResources("responses/response_body.json")
        }
    )

    //...
}
```

### Reuse requests and responses

```kotlin
private val request = NetMockRequest(
    method = Method.Post,
    requestUrl = "https://google.com/somePath?paramKey1=paramValue1",
    mandatoryHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("requests/request_body.json")
)
private val response = NetMockResponse(
    code = 200,
    mandatoryHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("responses/response_body.json")
)

@Test
fun `my test`() {
    netMock.addMock(request, response)

    //...
}
```

### Templates

```kotlin
private val templateRequest = NetMockRequest(
    method = Method.Post,
    requestUrl = "https://google.com/somePath?paramKey1=paramValue1",
    mandatoryHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("requests/request_body.json")
)
private val templateResponse = NetMockResponse(
    code = 200,
    mandatoryHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("responses/response_body.json")
)

@Test
fun `my test`() {
    netMock.addMock(
        request = {
            fromRequest(templateRequest)
            method = Method.Put
        },
        response = {
            fromResponse(templateResponse)
            code = 201
        }
    )

    //...
}
```

### Custom Request Matchers

If you want to use more relaxed matching criteria you can add your own request matcher by using
`addMockWithCustomMatcher` instead of `addMock`:

```kotlin
private val response = NetMockResponse(
    code = 200,
    mandatoryHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("responses/response_body.json")
)

@Test
fun `my test`() {
    netMock.addMockWithCustomMatcher(
        requestMatcher = { interceptedRequest ->
            interceptedRequest.requestUrl.contains("https://google.com/somePath") && method == Method.Post
        },
        response = response
    )

    //...
}
```

### Mock the same request multiple times

By default, each mock in `NetMock` intercepts only **one request**. After a request is intercepted,
the mock is automatically removed from the queue. This behavior ensures precise control over your
tests, allowing you to verify exactly how your code handles each request.

However, if your code makes the same request multiple times (e.g., polling or retries), you can add
multiple mocks for each expected request:

```kotlin
@Test
fun `my test`() {
    netMock.addMock(request, response) // Mock for the first request
    netMock.addMock(request, response) // Mock for the second request
    netMock.addMock(request, response) // Mock for the third request

    // ...
}
```

Alternatively, if the exact number of requests is not a concern in your test scenario, you can use
the `retainMock = true` flag to create a persistent mock. This mock will remain in the queue and
intercept all matching requests, even after the first interception:

```kotlin
@Test
fun `my test`() {
    netMock.addMock(request, response, retainMock = true) // Persistent mock
    // or
    // netMock.addMockWithCustomMatcher(requestMatcher, response, retainMock = true)

    // ...
}
```
This approach is particularly useful when:
* Testing polling mechanisms or retry logic.
* The number of requests is dynamic or unknown.
* You want to simplify test setup by avoiding repetitive mock definitions.

## Verify intercepted requests

If you want to verify that a request has been intercepted you can use `netMock.interceptedRequests`,
which will return a list of all the interceptedRequests by `NetMock`:

```kotlin
@Test
fun `my test`() {
    netMock.addMock(request, response)

    //...

    assertEquals(listOf(request), netMock.interceptedRequests)
}
```

You can also check the requests that have not been intercepted yet with:

```kotlin
netMock.allowedMocks
```

This will return a `NetMockRequestResponse` which is the pair of the`NetMockRequest:NetMockResponse`
you previously mocked.

## Not mocked requests

By default, requests that are not mocked will produce a `400 Bad Request` response and will be
logged as errors in the console.
You can override this behaviour by setting a default response:

```kotlin
netMock.defaultResponse = NetMockResponse(
    code = 200,
    mandatoryHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("responses/response_body.json")
)
```

by doing so, all the not mocked requests will return the specified response and no logs will be
printed in the console.

## Resources

When working with request and response bodies, it may not be ideal to create string constants in
your tests (i.e. long JSONs that compromise tests readability, sharing bodies between test
classes...).
You can instead read from a local file in your test `resources` folder:

```kotlin
//Reads the text from the module's "src/test/resources/responses/products_response_body.json" file
val responseBody = readFromResources("responses/products_response_body.json")
```

By doing so, the IDE will properly format the file for you and you can even copy/paste HTTP request
and response bodies from your real web server to create representative test cases.

### Multiplatform Resources

If you are working on a multiplatform project, your `resources` folder will be located in a
different path.
Use the following methods for reading the correct files:

| Method                    | Resolved path              |
|---------------------------|----------------------------|
| `readFromCommonResources` | `src/commonTest/resources` |
| `readFromJvmResources`    | `src/jvmTest/resources`    |
| `readFromNativeResources` | `src/nativeTest/resources` |
