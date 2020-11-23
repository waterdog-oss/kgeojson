import kotlinx.serialization.json.Json

val mapper: Json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}