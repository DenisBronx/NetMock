# NetMockServer
## Initialization

```kotlin
//the rule will start the server when the test starts and will shut down the server when the test is finished (@After)
@get:Rule 
val netMock = NetMockServerRule()
```
## Working with real URLs
If you are using a library compatible with `OkHttp`and you don't want to use `localhost` as base url for your requests, you can add the `netMock.server.interceptor` to your `OkHttpClient`.
```kotlin
@get:Rule 
val netMock = NetMockServerRule()

//OkHttp
private val okHttpClient = OkHttpClient.Builder().addInterceptor(netMock.interceptor).build()
okHttpClient.newCall(Request.Builder().get().url("https://google.com/requestPath").build())

//Retrofit
private val retrofitApi = Retrofit.Builder()
    .baseUrl("https://google.com/")
    .client(okHttpClient)
    .build()
    .create(RetrofitApi::class.java)
retrofitApi.getSomething()

//ktor
private val ktorClient = HttpClient(OkHttp) {
    engine {
        addInterceptor(netMock.interceptor)
    }
}
ktorClient.get("https://google.com/requestPath")
```
## Working with localhost
If you can't or don't want to work with real URLs you'll need to direct your requests to localhost.

Once initialized, NetMockServer will generate a `baseUrl` that you’ll need to use for your requests:
```kotlin
@get:Rule
val netMock = NetMockServerRule()

private val baseUrl = netMock.baseUrl

//OkHttp, somewhere in your code
private val okHttpClient = OkHttpClient.Builder().build()
okHttpClient.newCall(Request.Builder().get().url("${baseUrl}requestPath").build())

//Retrofit, somewhere in your code
private val retrofitApi = Retrofit.Builder().baseUrl(baseUrl).build().create(RetrofitApi::class.java)
retrofitApi.getSomething()

//Ktor, somewhere in your code
private val ktorClient = HttpClient(CIO.create()) {  }
ktorClient.get("${baseUrl}requestPath")
```
