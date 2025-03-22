package uk.ac.aber.dcs.souschefapp.database

enum class CookingUnits (val abbreviation: String, val type: String){
    // Volume Units
    TEASPOON("tsp", "Volume"),
    TABLESPOON("tbsp", "Volume"),
    CUP("cup", "Volume"),
    MILLILITER("mL", "Volume"),
    LITER("L", "Volume"),
    FLUID_OUNCE("fl oz", "Volume"),
    PINT("pt", "Volume"),
    QUART("qt", "Volume"),
    GALLON("gal", "Volume"),

    // Weight Units
    GRAM("g", "Weight"),
    KILOGRAM("kg", "Weight"),
    OUNCE("oz", "Weight"),
    POUND("lb", "Weight"),

    // Length Units (for specific measurements like pasta)
    INCH("in", "Length"),
    CENTIMETER("cm", "Length");

    fun description(): String {
        return "$name ($abbreviation) - $type unit"
    }

    fun value(): String {
        return abbreviation
    }

    fun type(): String {
        return type
    }
}