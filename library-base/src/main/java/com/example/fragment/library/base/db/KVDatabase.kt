package com.example.fragment.library.base.db

import androidx.room.*
import com.example.fragment.library.base.provider.BaseContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 对RoomDatabase进行封装
 * 详细使用方法参考WanHelper.kt
 */
@Database(entities = [KV::class], version = 1, exportSchema = false)
abstract class KVDatabase : RoomDatabase() {

    companion object {

        @Volatile
        private var database: KVDatabase? = null

        private fun getDB() = database ?: synchronized(KVDatabase::class.java) {
            database ?: Room.databaseBuilder(
                BaseContent.get().applicationContext,
                KVDatabase::class.java,
                KVDatabase::class.java.simpleName
            ).build().also { db -> database = db }
        }

        fun set(key: String, value: String) {
            getDB().set(key, value)
        }

        fun get(key: String, result: (String) -> Unit) {
            getDB().get(key, result)
        }

        fun closeDatabase() {
            getDB().close()
        }

    }

    abstract fun getDao(): KVDao

    fun set(key: String, value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var kv = getDao().findByKey(key)
                if (kv == null) {
                    kv = KV(key = key, value = value)
                    getDao().insert(kv)
                } else {
                    kv.value = value
                    getDao().update(kv)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun get(key: String, result: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val value = try {
                getDao().findByKey(key)?.value ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }
            result.invoke(value)
        }
    }

    /**
     * 如果使用数据库频繁，则不建议每次操作后关闭
     */
    override fun close() {
        super.close()
        database = null
    }

}

@Dao
interface KVDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(kv: KV): Long

    @Delete
    suspend fun delete(kv: KV): Int

    @Update
    suspend fun update(kv: KV): Int

    @Query("SELECT * FROM kv_table WHERE `key` = :key ORDER BY id DESC LIMIT 1")
    suspend fun findByKey(key: String): KV?

}

@Entity(tableName = "kv_table")
data class KV(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "key")
    var key: String,

    @ColumnInfo(name = "value")
    var value: String

)
