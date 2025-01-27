package ktx.tiled

import com.badlogic.gdx.maps.MapLayer
import com.badlogic.gdx.maps.MapLayers
import com.badlogic.gdx.maps.MapProperties

/**
 * Extension method to directly access the [MapProperties] of a [MapLayer]. If the property
 * is not defined then this method throws a [MissingPropertyException].
 * @param key property name.
 * @return value of the property.
 * @throws MissingPropertyException If the property is not defined.
 */
inline fun <reified T> MapLayer.property(key: String): T = properties[key, T::class.java]
  ?: throw MissingPropertyException("Property $key does not exist for layer $name")

/**
 * Extension method to directly access the [MapProperties] of a [MapLayer]. The type is automatically
 * derived from the type of the given default value. If the property is not defined the [defaultValue]
 * will be returned.
 * @param key property name.
 * @param defaultValue default value in case the property is missing.
 * @return value of the property or defaultValue if property is missing.
 */
inline fun <reified T> MapLayer.property(key: String, defaultValue: T): T = properties[key, defaultValue, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [MapLayer]. If the property
 * is not defined then this method returns null.
 * @param key property name.
 * @return value of the property or null if the property is missing.
 */
inline fun <reified T> MapLayer.propertyOrNull(key: String): T? = properties[key, T::class.java]

/**
 * Extension method to directly access the [MapProperties] of a [MapLayer] and its
 * [containsKey][MapProperties.containsKey] method.
 * @param key property name.
 * @return true if the property exists. Otherwise false.
 */
fun MapLayer.containsProperty(key: String) = properties.containsKey(key)

/**
 * Returns **true** if and only if the [MapLayers] collection is empty.
 */
fun MapLayers.isEmpty() = this.count <= 0

/**
 * Returns **true** if and only if the [MapLayers] collection is not empty.
 */
fun MapLayers.isNotEmpty() = this.count > 0
