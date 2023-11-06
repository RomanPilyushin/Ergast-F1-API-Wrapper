# Ergast F1 API Wrapper

This project provides a Java wrapper for the Ergast Developer API, which is an experimental web service that provides a historical record of motor racing data for non-commercial purposes.

## Description

The Ergast API wrapper allows for easy access to the Ergast web service. It simplifies the process of sending requests to the API and parsing the returned data by handling the construction of URLs, making HTTP requests, and parsing the returned JSON into Java objects.

## Features

- Simple interface to access Formula 1 data
- Supports fetching data about drivers, circuits, constructors, seasons, and race results
- Handles HTTP requests and JSON parsing
- Custom exceptions for error handling
- Configurable limit and offset for result pagination

## Maven Configuration

To use this wrapper in your Maven project, you'll need to include the following in your `pom.xml` file:

### Step 1: Add the JitPack repository

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Step 2: Add the dependency
```xml
<dependency>
    <groupId>com.github.RomanPilyushin</groupId>
    <artifactId>Ergast-F1-API-Wrapper</artifactId>
    <version>master-SNAPSHOT</version>
</dependency>
```

### Fetching Data
#### Initialization
```java
// Initializing with specific season, limit, and offset
Ergast ergast = new Ergast(2016, 100, 2);

// Initializing with the current season, default limit, and default offset
Ergast ergast = new Ergast();

```


#### Drivers
```java
try {
    List<Driver> drivers = ergast.getDrivers();
    // Process the list of drivers
} catch (IOException e) {
    // Handle exceptions
}
```

#### Seasons
```java
try {
    List<Season> seasons = ergast.getSeasons();
    // Process the list of seasons
} catch (IOException e) {
    // Handle exceptions
}
```

