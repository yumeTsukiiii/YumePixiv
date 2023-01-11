package fan.yumetsuki.yumepixiv.utils

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class SharedPreferencesDelegate<T>(
    private val valueType: KClass<*>,
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: T
) : ReadWriteProperty<Any, T> {

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return when(valueType) {
            String::class -> sharedPreferences.getString(key, defaultValue as? String)
            Boolean::class -> sharedPreferences.getBoolean(key, defaultValue as? Boolean ?: false)
            Int::class -> sharedPreferences.getInt(key, defaultValue as? Int ?: -1)
            Float::class -> sharedPreferences.getFloat(key, defaultValue as? Float ?: -1f)
            Long::class -> sharedPreferences.getLong(key, defaultValue as? Long ?: -1)
            else -> error("valueType must be String or Boolean or Int or Float or Long")
        } as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        sharedPreferences.edit {
            when(valueType) {
                String::class -> putString(key, value as String)
                Boolean::class -> putBoolean(key, value as Boolean)
                Int::class -> putInt(key, value as Int)
                Float::class -> putFloat(key, value as Float)
                Long::class -> putLong(key, value as Long)
                else -> error("valueType must be String or Boolean or Int or Float or Long")
            }
        }
    }

}

@Suppress("unused")
fun SharedPreferences.string(key: String, defaultValue: String = ""): SharedPreferencesDelegate<String> {
    return SharedPreferencesDelegate(String::class, this, key, defaultValue)
}

@Suppress("unused")
fun SharedPreferences.stringOrNull(key: String, defaultValue: String? = null): SharedPreferencesDelegate<String?> {
    return SharedPreferencesDelegate(String::class, this, key, defaultValue)
}

@Suppress("unused")
fun SharedPreferences.int(key: String, defaultValue: Int = -1): SharedPreferencesDelegate<Int> {
    return SharedPreferencesDelegate(Int::class, this, key, defaultValue)
}

@Suppress("unused")
fun SharedPreferences.float(key: String, defaultValue: Float = -1f): SharedPreferencesDelegate<Float> {
    return SharedPreferencesDelegate(Int::class, this, key, defaultValue)
}

@Suppress("unused")
fun SharedPreferences.double(key: String, defaultValue: Double = -1.0): SharedPreferencesDelegate<Double> {
    return SharedPreferencesDelegate(Int::class, this, key, defaultValue)
}

@Suppress("unused")
fun SharedPreferences.boolean(key: String, defaultValue: Boolean = false): SharedPreferencesDelegate<Boolean> {
    return SharedPreferencesDelegate(Int::class, this, key, defaultValue)
}