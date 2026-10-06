package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.BrandEntity
import com.example.data.models.FavoriteEntity
import com.example.data.models.MotorcycleEntity
import com.example.data.models.RecentlyViewedEntity
import com.example.data.models.SavedComparisonEntity
import com.example.data.models.VariantEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BrandEntity::class,
        MotorcycleEntity::class,
        VariantEntity::class,
        FavoriteEntity::class,
        RecentlyViewedEntity::class,
        SavedComparisonEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MotoScopeDatabase : RoomDatabase() {

    abstract fun motoDao(): MotoDao

    companion object {
        @Volatile
        private var INSTANCE: MotoScopeDatabase? = null

        fun getInstance(context: Context): MotoScopeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MotoScopeDatabase::class.java,
                    "motoscope_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate seed data on initial creation
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val dao = database.motoDao()
                                    dao.insertBrands(PreloadedData.brands)
                                    dao.insertMotorcycles(PreloadedData.motorcycles)
                                    dao.insertVariants(PreloadedData.variants)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
