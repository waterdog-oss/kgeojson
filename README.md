![badge](https://maven-badges.herokuapp.com/maven-central/mobi.waterdog.kgeojson/kgeojson/badge.svg)

# kgeojson

A simple GeoJson library written in Kotlin.

**kgeojson** rely on kotlinx serialization to encode and decode GeoJson data.

### Usage

There's a simple example of encoding a feature collection with a single Point. 

    val featureCollection = 
      FeatureCollection(mutableListOf(
        Feature(
          Point(listOf(-74.001202, 40.713336)))))
    val fcEncoded = Json.encodeToString(featureCollection as GeoJson)
    println(fcEncoded)
    
Will result in:

    {
      "type": "FeatureCollection",
      "features":[
      {
        "type":"Feature",
        "geometry":{
          "type":"Point",
          "coordinates": [ -74.001202, 40.713336 ]
         },
         "properties":{}
      }
     ]
    }

Notice the `as GeoJson` in the `encodeToString` call. When encoding, we need to tell kotlinx to use the parent sealed class, so he puts the `type` discriminator field in the serialized object.

That's part of a kotlinx serialization limitation discussed [here](https://github.com/Kotlin/kotlinx.serialization/issues/1194) (maybe fixed in a future release). If you don't pass the target object as the parent sealed class, the object will be serialized **without the `type` discriminator field**. This will result in a non RFC7946 compliant GeoJson.

So it's recommended to always pass the target as the parent, in **kgeojson** it means to add `as GeoJson` on every `encodeToString` call, that's it.

### Supported types
- FeatureCollection
- Feature
- Point
- MultiPoint
- LineString
- MultiLineString
- Polygon
- GeometryCollection
