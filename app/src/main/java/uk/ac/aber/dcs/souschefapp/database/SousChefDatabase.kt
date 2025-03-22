package uk.ac.aber.dcs.souschefapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uk.ac.aber.dcs.souschefapp.database.models.Account
import uk.ac.aber.dcs.souschefapp.database.models.Converter
import uk.ac.aber.dcs.souschefapp.database.models.Ingredient
import uk.ac.aber.dcs.souschefapp.database.models.Log
import uk.ac.aber.dcs.souschefapp.database.models.Note
import uk.ac.aber.dcs.souschefapp.database.models.Product
import uk.ac.aber.dcs.souschefapp.database.models.Recipe

@Database(
    entities = [
        Account::class,
        Product::class,
        Recipe::class,
        Ingredient::class,
        Log::class,
        Note::class,
    ],
    version = 1,
)
@TypeConverters(Converter::class)
abstract class SousChefDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun productDao(): ProductDao
    abstract fun recipeDao(): RecipeDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun logDao(): LogDao
    abstract fun noteDao(): NoteDao

    companion object{
        private var INSTANCE: SousChefDatabase? = null

        @Synchronized
        fun getInstance(context: Context): SousChefDatabase {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SousChefDatabase::class.java,
                    "sous_chef_database"
                ).build().also { INSTANCE = it }
            }
        }
    }

}
