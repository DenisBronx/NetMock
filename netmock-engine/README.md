# NetMockEngine
## Initialization

```kotlin
private val netMock = NetMockEngine()
//pass the netMock instance instead of MockEngine 
private val ktorClient = HttpClient(netMock) { 
    install(ContentNegotiation) {
        json()
    }
}
```
