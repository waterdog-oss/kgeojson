package mobi.waterdog.kgeojson

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class GeoJsonCoordinates


@Serializable
class Position(val lng: Double, val lat: Double) : GeoJsonCoordinates() {

    @Serializer(forClass = Position::class)
    companion object : KSerializer<Position> {

        private val serializer = ListSerializer(Double.serializer())
        override val descriptor: SerialDescriptor = serializer.descriptor

        override fun serialize(encoder: Encoder, obj: Position) {
            serializer.serialize(encoder, listOf(obj.lng, obj.lat))
        }

        override fun deserialize(decoder: Decoder): Position {
            val geoJsonCoordinatesList = decoder.decodeSerializableValue(serializer)
            return Position(geoJsonCoordinatesList[0], geoJsonCoordinatesList[1])
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (lat != other.lat) return false
        if (lng != other.lng) return false

        return true
    }

    override fun hashCode(): Int {
        var result = lat.hashCode()
        result = 31 * result + lng.hashCode()
        return result
    }
}