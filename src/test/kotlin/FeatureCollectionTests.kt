import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mobi.waterdog.kgeojson.Feature
import mobi.waterdog.kgeojson.FeatureCollection
import mobi.waterdog.kgeojson.GeoJson
import mobi.waterdog.kgeojson.LineString
import mobi.waterdog.kgeojson.Position
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should not be equal to`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeatureCollectionTests {

    @Test
    fun `Encode & Decode FeatureCollection`() {
        val lineString = LineString(
            mutableListOf(
                Position(-8.600783, 40.349676),
                Position(-8.413182, 40.208091),
            )
        )
        val featureCollection = FeatureCollection(mutableListOf(Feature(lineString)))

        val featureCollectionSerialized = mapper.encodeToString(featureCollection)
        val decodedFeatureCollection = mapper.decodeFromString<FeatureCollection>(featureCollectionSerialized)

        featureCollection `should be equal to` decodedFeatureCollection
    }

    @Test
    fun `Compare 2 different feature collections - same geometry but different properties`() {
        val lineString = LineString(
            mutableListOf(
                Position(-8.600783, 40.349676),
                Position(-8.413182, 40.208091),
            )
        )
        val featureCollection = FeatureCollection(mutableListOf(Feature(lineString, mutableMapOf("mySpecialProp" to "hi!"))))
        val featureCollection2 = FeatureCollection(mutableListOf(Feature(lineString)))

        featureCollection `should not be equal to` featureCollection2
    }

    @Test
    fun `Check "type" field encoding FeatureCollection (as GeoJson)`() {
        val lineString = LineString(
            mutableListOf(
                Position(-8.600783, 40.349676),
                Position(-8.413182, 40.208091),
            )
        )
        val featureCollection = FeatureCollection(mutableListOf(Feature(lineString)))

        val featureCollectionSerialized = mapper.encodeToString(featureCollection as GeoJson)

        Assertions.assertTrue { featureCollectionSerialized.contains("\"type\":\"FeatureCollection\"") }
    }
}
