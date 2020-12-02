import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import mobi.waterdog.kgeojson.GeoJson
import mobi.waterdog.kgeojson.Geometry
import mobi.waterdog.kgeojson.GeometryCollection
import mobi.waterdog.kgeojson.LineString
import mobi.waterdog.kgeojson.MultiLineString
import mobi.waterdog.kgeojson.MultiPoint
import mobi.waterdog.kgeojson.Point
import mobi.waterdog.kgeojson.Polygon
import mobi.waterdog.kgeojson.PolygonBuilder
import mobi.waterdog.kgeojson.Position
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GeometryTests {

    /*************************************
     * Point tests
     *************************************/

    @Test
    fun `Encode & Decode Point`() {
        val point = Point(listOf(-8.600783, 40.349676))
        val pointString = mapper.encodeToString(point as Geometry)
        val pointDecoded = mapper.decodeFromString<Point>(pointString)

        point `should be equal to` pointDecoded
    }

    @Test
    fun `Instantiate invalid Point, with 3 coordinates`() {
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            Point(listOf(-8.600783, 40.349676, 3.0))
        }
        exception.message `should be equal to` "Point must have exactly 2 Double coordinates. Example: [lng, lat]."
    }

    /*************************************
     * MultiPoint tests
     *************************************/

    @Test
    fun `Encode & Decode MultiPoint`() {
        val multiPoint = MultiPoint(
            listOf(
                Position(-8.600783, 40.349676),
                Position(-9.020579, 38.852951),
            )
        )
        val multiPointString = mapper.encodeToString(multiPoint as Geometry)
        val multiPointDecoded = mapper.decodeFromString<MultiPoint>(multiPointString)

        multiPoint `should be equal to` multiPointDecoded
    }

    @Test
    fun `Instantiate invalid MultiPoint, with 1 coordinates`() {
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            MultiPoint(listOf(Position(-8.600783, 40.349676)))
        }
        exception.message `should be equal to` "MultiPoint must have at least 2 coordinates."
    }

    /*************************************
     * LineString tests
     *************************************/

    @Test
    fun `Encode & Decode LineString`() {
        val lineString = LineString(
            mutableListOf(
                Position(-8.600783, 40.349676),
                Position(-8.413182, 40.208091)
            )
        )

        val lineStringEncoded = mapper.encodeToString(lineString as Geometry)
        val lineStringDecoded = mapper.decodeFromString<Geometry>(lineStringEncoded)

        lineString `should be equal to` lineStringDecoded
    }

    @Test
    fun `Instantiate invalid LineString, with only 1 coordinates`() {
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            LineString(mutableListOf(Position(-8.600783, 40.349676)))
        }
        exception.message `should be equal to` "LineString must have at least 2 coordinated."
    }

    /*************************************
     * MultiLineString tests
     *************************************/

    @Test
    fun `Encode & Decode MultiLineString`() {
        val multiLineString = MultiLineString(
            mutableListOf(
                mutableListOf(
                    Position(-8.600783, 40.349676),
                    Position(-8.413182, 40.208091)
                ),
                mutableListOf(
                    Position(-8.332041, 39.427420),
                    Position(-7.979837, 39.375901)
                )
            )
        )

        val multiLineStringEncoded = mapper.encodeToString(multiLineString as Geometry)
        val multiLineStringDecoded = mapper.decodeFromString<Geometry>(multiLineStringEncoded)

        multiLineString `should be equal to` multiLineStringDecoded
    }

    @Test
    fun `Instantiate invalid MultiLineString, with only 1 coordinates`() {
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            MultiLineString(mutableListOf(mutableListOf(Position(-8.600783, 40.349676))))
        }
        exception.message `should be equal to` "Each LineString must have at least 2 coordinated."
    }

    /*************************************
     * Polygon tests
     *************************************/

    @Test
    fun `Encode & Decode Polygon`() {
        val outerRing = mutableListOf(
            Position(-8.600783, 40.349676),
            Position(-8.413182, 40.208091),
            Position(-8.276604, 40.281916),
            Position(-8.562161, 40.393168),
            Position(-8.600783, 40.349676)
        )
        val hole = mutableListOf(
            Position(-8.447788, 40.289855),
            Position(-8.417629, 40.292596),
            Position(-8.412887, 40.275433),
            Position(-8.456202, 40.280261),
            Position(-8.447788, 40.289855)
        )
        val polygon = Polygon(mutableListOf(outerRing, hole))

        val polygonEncoded = mapper.encodeToString(polygon as Geometry)
        val polygonDecoded = mapper.decodeFromString<Polygon>(polygonEncoded)

        polygonDecoded `should be equal to` polygonDecoded
    }

    @Test
    fun `Instantiate invalid Polygon - no outer or inner rings`() {
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            Polygon(mutableListOf())
        }
        exception.message `should be equal to` "Polygon must contain at least one linear ring."
    }

    @Test
    fun `Instantiate invalid Polygon - outer ring with only 3 coordinates`() {
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            val outerRing = mutableListOf(
                Position(-8.600783, 40.349676),
                Position(-8.413182, 40.208091),
                Position(-8.600783, 40.349676)
            )
            Polygon(mutableListOf(outerRing))
        }
        exception.message `should be equal to` "All linear rings must have at least four or more positions."
    }

    @Test
    fun `Instantiate invalid Polygon - non-matching first and last position of linear ring`() {
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            val outerRing = mutableListOf(
                Position(-8.600783, 40.349676),
                Position(-8.413182, 40.208091),
                Position(-8.276604, 40.281916),
                Position(-8.562161, 40.393168)
            )
            Polygon(mutableListOf(outerRing))
        }
        exception.message `should be equal to` "First and last position of a linear ring must match."
    }

    /**************************************
     * PolygonBuilder tests
     **************************************/

    @Test
    fun `Create solid polygon using PolygonBuilder`() {
        val polygonExteriorRing = listOf(
            Position(-8.600783, 40.349676),
            Position(-8.413182, 40.208091),
            Position(-8.276604, 40.281916),
            Position(-8.562161, 40.393168),
            Position(-8.600783, 40.349676)
        )

        val polygon = PolygonBuilder()
            .exteriorRing(*polygonExteriorRing.toTypedArray())
            .build()

        polygon.exteriorRing `should be equal to` polygonExteriorRing
        polygon.interiorRings `should be equal to` null
    }

    @Test
    fun `Create polygon with a hole using PolygonBuilder`() {
        val polygonExteriorRing = listOf(
            Position(-8.600783, 40.349676),
            Position(-8.413182, 40.208091),
            Position(-8.276604, 40.281916),
            Position(-8.562161, 40.393168),
            Position(-8.600783, 40.349676)
        )
        val polygonHole = listOf(
            Position(-8.447788, 40.289855),
            Position(-8.417629, 40.292596),
            Position(-8.412887, 40.275433),
            Position(-8.456202, 40.280261),
            Position(-8.447788, 40.289855)
        )

        val polygon = PolygonBuilder()
            .exteriorRing(*polygonExteriorRing.toTypedArray())
            .addHole(*polygonHole.toTypedArray())
            .build()

        polygon.exteriorRing `should be equal to` polygonExteriorRing
        polygon.interiorRings!![0] `should be equal to` polygonHole
    }

    /*************************************
     * GeometryCollection tests
     *************************************/

    @Test
    fun `Encode & Decode GeometryCollection`() {
        val geometryCollection = GeometryCollection(
            mutableListOf(
                Point(mutableListOf(-8.600783, 40.349676)),
                LineString(
                    mutableListOf(
                        Position(-8.600783, 40.349676),
                        Position(-8.413182, 40.208091)
                    )
                )
            )
        )
        val geometryCollectionEncoded = mapper.encodeToString(geometryCollection as GeoJson)
        val geometryCollectionDecoded = mapper.decodeFromString<GeometryCollection>(geometryCollectionEncoded)

        geometryCollection `should be equal to` geometryCollectionDecoded
    }
}
