# Echo Framework

Simple event-based framework for developing high concurrent applications wth event-based domain model.

> TODO: publish to maven central

## Usage

### Events

There are two main concepts: `Event` and `Bus`. You can publish event on bus and then subscribe to the type of that event to somehow handle it. It is not required to handle every event on the bus.

Let's start with simple event named `SignInInitiated`:

```kotlin
class SignInInitiated(
    val username: String, 
    val password: String
) : Event()
```

Then we may create the bus and add subscription to it. Subscription method (handling method) will be called each time when event of type `SignInInitiated` will be published onto bus:

```kotlin
fun main(args: Array<String>) = runBlocking {
    val bus = Bus()

    bus.subscribeAsync(
        SignInInitiated::class,
        { e -> println("User '${e.username}' signing in...") },
        { e, ex -> println("Something go wrong!") }
    )
    
    bus.publish(SignInInitiated("user", "passwd"))
}
```

Method `Bus::subscribeAsync` is non-blocking and all handles will be executed in different threads. Indeed, there exists blocking version of this method: `Bus::subscribe` - you can use this if you want to manually setup dispatchers for coroutines.

So `Event` is just a portion of data which can be handled asynchronously.

If there is a hierarchy of classes, interfaces and events it is possible to subscribe to one of the super-types. Let `SignInInitiated` implement interface `Auditable`:

```kotlin
interface Auditable

class SignInInitiated(
    val username: String, 
    val password: String
) : Event(), Auditable
```

And then you can subscribe to all events which implements such an interface:

```kotlin
fun main(args: Array<String>) = runBlocking {
    val bus = Bus()

    bus.subscribeAsync(
        Auditable::class,
        { e -> println("Audit: $e") },
        { e, ex -> println("Something go wrong!") }
    )
    
    bus.publish(SignInInitiated("user", "passwd"))
}
```
