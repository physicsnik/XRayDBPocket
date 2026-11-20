package ch.psi.xraydbpocket

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName="elements") data class Element(
    @PrimaryKey val z: Int,
    val symbol: String,
    val name: String,
    val mass: Double,
    val configuration: String = ""
)
@Entity(tableName="spectral_entries", indices=[Index("z"),Index("energyEv")]) data class SpectralEntry(
 @PrimaryKey(autoGenerate=true) val id:Int=0, val z:Int, val category:String, val label:String,
 val energyEv:Double, val uncertaintyEv:Double?=null, val environment:String="Free atom",
 val source:String, val reference:String, val note:String="")

@Suppress("unused")
@Dao interface XrayDao {
 @Query("SELECT * FROM elements ORDER BY z") fun elements():Flow<List<Element>>
 @Query("SELECT * FROM elements WHERE z=:z") fun element(z:Int):Flow<Element?>
 @Query("SELECT * FROM spectral_entries WHERE z=:z ORDER BY energyEv") fun entries(z:Int):Flow<List<SpectralEntry>>
 @Query("SELECT * FROM spectral_entries WHERE energyEv BETWEEN :lo AND :hi ORDER BY ABS(energyEv-:target)") fun near(target:Double,lo:Double,hi:Double):Flow<List<SpectralEntry>>
 @Query("SELECT * FROM spectral_entries WHERE category='X-ray emission' AND energyEv BETWEEN :lo AND :hi ORDER BY ABS(energyEv-:target)") fun nearEmission(target:Double,lo:Double,hi:Double):Flow<List<SpectralEntry>>
 @Query("SELECT e.* FROM spectral_entries e JOIN elements a ON a.z=e.z WHERE a.symbol LIKE :q OR a.name LIKE :q OR e.label LIKE :q OR e.category LIKE :q ORDER BY e.z,e.energyEv LIMIT 100") fun search(q:String):Flow<List<SpectralEntry>>
}

@Database(entities=[Element::class,SpectralEntry::class],version=17,exportSchema=false)
abstract class XrayDb:RoomDatabase(){ abstract fun dao():XrayDao
 companion object { @Volatile private var I:XrayDb?=null
  fun get(c:Context)=I?: synchronized(this){ I?:Room.databaseBuilder(c,XrayDb::class.java,"xray.db").createFromAsset("xray.db").fallbackToDestructiveMigration().build().also{I=it} }
 }
}
