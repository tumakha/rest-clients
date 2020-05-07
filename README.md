## rest-clients

[![Java-Gradle-CI](https://github.com/tumakha/rest-clients/workflows/Java-Gradle-CI/badge.svg)](https://github.com/tumakha/rest-clients/actions)

REST clients performance test

#### Prerequisites

Java 13+, Gradle 6 or gradle-wrapper

#### Run/Debug main class

    restclients.RestClientsApp
    
#### Build without tests

    gradle build -x test

#### Run app

    cd ./build/libs
    java -jar rest-clients.jar
    
Report will be saved as rest-clients-performance.csv

#### Test results

The fastest clients are:
- Java 11 java.net.http.HttpClient
- AsyncHttpClient (org.asynchttpclient:async-http-client)
- Spring Reactive WebClient (org.springframework.boot:spring-boot-starter-webflux)
- Jetty (org.eclipse.jetty:jetty-client)

They all use NIO, Reactive architecture and can return response wrapped into CompletableFuture or Callback.

To achieve the best performance requests should be passed to rest client by several threads.
Also, none of threads should wait for response by calling 
CompletableFuture::join or CompletableFuture::get.

Instead of that callback should be used to handle response like:
    
    future.thenApply(response -> {});        
    future.thenRun(() -> {});
    future.thenCompose(...)

