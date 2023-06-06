# NetMock
NetMock is a powerful testing library that makes it incredibly easy to unit test your network requests. 
It is compatible with `Java`, `Kotlin`, and `Android`, making it a versatile option for developers working on different platforms.

The library offers a variety of features that can help you test your network requests against any network library, including `OkHttp`, `Ktor`, and `Retrofit`. 
You can use a mock-like API to test your requests, making the test code much simpler and more readable.

NetMock comes in two different flavors: `netmock-server` and `netmock-engine`. 

The `netmock-server` flavor is compatible with Java/Kotlin/Android and it is library independent as it allows you to mock network requests by redirecting requests to a [localhost](http://localhost/) web server using [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver). 
This flavor is perfect for developers who want to test their network requests without having to worry about setting up a separate server.

The `netmock-engine` flavor, on the other hand, is designed specifically for developers using `Ktor`. 
It allows you to use `MockEngine` instead of a [localhost](http://localhost/) server, making it a more lightweight option for those working with `Ktor`.
If in your project you are using a variety of libraries and you don't want to import both flavors, just use `netmock-server` as it is compatible with all the libraries including `Ktor`.

`netmock-engine` will soon available for multiplatform projects!

# Install
Netmock is available on Jitpack.

For Gradle users, add the following to your root build.gradle

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

and the following to your module’s build.gradle

```groovy
dependencies {
    //compatible with all libraries
    testImplementation "com.github.DenisBronx.netmock:netmock-server:0.1.1" 
    //lighter weight option for ktor only library users
    testImplementation "com.github.DenisBronx.netmock:netmock-engine:0.1.1"
}
```

# Examples
#### `NetMockServer` initialization

```kotlin
//the rule will start the server when the test starts and will shutdown the server when the test is finished (@After)
@get:Rule 
val netMock = NetMockServerRule()
```

#### `NetMockEngine` initialization

```kotlin
private val netMock = NetMockEngine()
//pass the netMock instance instead of MockEngine 
private val ktorClient = HttpClient(netMock) { 
    install(ContentNegotiation) {
        json()
    }
}
```

Once initialized, NetMock will generate a baseUrl that you’ll need to use for your requests:

```kotlin
private val baseUrl = netMock.baseUrl

//OkHttp, somewhere in your code
private val okHttp = OkHttpClient.Builder().build()
okHttp.newCall(Request.Builder().get().url("${baseUrl}requestPath").build())

//Retrofit, somewhere in your code
private val retrofitApi = Retrofit.Builder().baseUrl(baseUrl).build().create(RetrofitApi::class.java)

//Ktor, somewhere in your code
private val ktorClient = HttpClient(CIO.create()) /*or HttpClient(netMock) if you use NetMockEngine*/ {
    install(ContentNegotiation) {
        json()
    }
}
ktorClient.get("${baseUrl}requestPath")
```

## Mock requests and responses

```kotlin
@Test
fun `my test`() {
    netMock.addMock(
        request = {
            //exact method
            method = Method.Post
            //exact path excluding query parameters
            path = "/somePath"
            //exact query parameters" "?paramKey1=paramValue1&paramKey2=paramValue2"
            params = mapOf("paramKey1" to "paramValue1", "paramKey2" to "paramValue2")
            //must-have headers, as some clients add extra headers you may not want to check them all
            //if you are using ktor and your response body is a json, you must have "Content-Type: application/json" as header
            containsHeaders = mapOf("a" to "b", "b" to "c")
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
            containsHeaders = mapOf("a" to "b", "b" to "c")
            //response body, must be a String (this allows you to test your parsing)
            body = """{"data": "text"}"""
            //or, you can read a file in "test/resources"
            //body = readFromResources("responses/response_body.json")
        }
    )
    
    //...
}
```

Or if you want to define requests and responses outside the test function for reusability:

```kotlin
private val request = NetMockRequest(
    method = Method.Post, 
    path = "/somePath",
    params = mapOf("paramKey1" to "paramValue1", "paramKey2" to "paramValue2"), 
    containsHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("requests/request_body.json")
)
private val response = NetMockResponse(
    code = 200,
    containsHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("responses/response_body.json")
)

@Test
fun `my test`() {
    netMock.addMock(request, response)
    
    //...
}
```

You can also use templates and override the fields that need to change:

```kotlin
private val templateRequest = NetMockRequest(
    method = Method.Post, 
    path = "/somePath",
    params = mapOf("paramKey1" to "paramValue1", "paramKey2" to "paramValue2"), 
    containsHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("requests/request_body.json")
)
private val templateResponse = NetMockResponse(
    code = 200,
    containsHeaders = mapOf("a" to "b", "b" to "c"),
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

Each mock will intercept only 1 request, once a request is intercepted `NetMock` will remove the mock from the queue.
This allows you test what your code exactly does.
If your code is making the same request multiple times (i.e polling) you would need to add a mock for each expected request:

```kotlin
@Test
fun `your test`() {
    netMock.addMock(request, response)
    netMock.addMock(request, response)
    netMock.addMock(request, response)
    
    //...
}
```

If you want to verify that a request has been intercepted you can use `netMock.interceptedRequests`, which will return a list of all the interceptedRequests by `NetMock`:

```kotlin
@Test
fun `your test`() {
    netMock.addMock(request, response)
    
    //...
    
    assertEquals(listOf(request), netMock.interceptedRequests)
}
```

You can also check the requests that have not been intercepted yet with:

```kotlin
netMock.allowedMocks
```
This will return a `NetMockRequestResponse` which is the pair of the `NetMockRequest:NetMockResponse` you previously mocked.

## Not mocked requests
By default requests that are not mocked will produce a `400 Bad Request` response and will be logged as errors in the JUnit console.
You can override this behaviour by setting a default response:
```kotlin
netMock.defaultResponse = NetMockResponse(
    code = 200,
    containsHeaders = mapOf("a" to "b", "b" to "c"),
    body = readFromResources("responses/response_body.json")
)
```
by doing so, all the not mocked requests will return the specified response and no logs will be printed in the JUnit console.
