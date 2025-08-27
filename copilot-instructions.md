## Coding Conventions

### Test Patterns
```kotlin
@Test
fun `should generate valid PNG when given valid request`() = testApplication {
    application { module() }
    val response = client.post("/snap") {
        contentType(ContentType.Application.Json)
        setBody(validSnapRequest)
    }
    assertEquals(HttpStatusCode.OK, response.status)
    assertTrue(response.contentType()?.match(ContentType.Image.PNG) == true)
}

@Test
fun `should reject request with invalid preset`() = testApplication {
    application { module() }
    val response = client.post("/snap") {
        contentType(ContentType.Application.Json)
        setBody(invalidPresetRequest)
    }
    assertEquals(HttpStatusCode.BadRequest, response.status)
}
```

### Test Naming Conventions
- **Positive Tests**: `should [expected behavior] when [condition]` (e.g., `should generate valid image when given valid input`)
- **Negative Tests**: `should [error behavior] when [invalid condition]` (e.g., `should reject request when preset is invalid`)
- **Feature Tests**: `should [feature behavior] for [specific case]` (e.g., `should produce larger images for presentation preset`)
- **Validation Tests**: `should validate [rule] and [expected result]` (e.g., `should validate input and return error details`)

## Local Development

To run the app with an in-memory H2 database (no MySQL required):

```
DB_TYPE=h2 ./gradlew run
```

This is useful for local development and testing. By default, the app tries to connect to MySQL. If you see a database connection error, use the above command.
