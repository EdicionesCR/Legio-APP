package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.LegioDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Database(
    entities = [
        UserEntity::class,
        EventEntity::class,
        NewsEntity::class,
        DocumentEntity::class,
        AnnouncementEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class LegioDatabase : RoomDatabase() {

    abstract fun legioDao(): LegioDao

    companion object {
        @Volatile
        private var INSTANCE: LegioDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): LegioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LegioDatabase::class.java,
                    "legio_database"
                )
                    .addCallback(LegioDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class LegioDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.legioDao())
                }
            }
        }

        suspend fun populateDatabase(dao: LegioDao) {
            val calendar = Calendar.getInstance()
            val todayStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
            
            // Member with birthday today!
            calendar.add(Calendar.DAY_OF_YEAR, 2)
            val nearBirthdayStr = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)

            // Seed Users
            dao.insertUser(
                UserEntity(
                    fullName = "Comunidad Fotos CR",
                    avatarUrl = "", // Empty avatar to display golden royal initials badge
                    birthDate = "22-05-1995",
                    city = "Rosario, Santa Fe",
                    phone = "+54 9 341 555-0293",
                    email = "fotoscr@legioncristorey.com.ar",
                    memberType = MemberType.BROTHER.name,
                    ordinationDate = null,
                    hideMyData = false,
                    password = "22-Rosario-02931",
                    isAdmin = true,
                    isCurrentUser = true // Default logged-in user
                )
            )

            dao.insertUser(
                UserEntity(
                    fullName = "P. Juan Manuel Álvarez, LCR",
                    avatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&q=80&w=400",
                    birthDate = todayStr, // Birthday today!
                    city = "Buenos Aires, Argentina",
                    phone = "+54 9 11 4455-8822",
                    email = "jmalvarez@legioncristorey.org",
                    memberType = MemberType.PRIEST.name,
                    ordinationDate = "23-11-2014",
                    hideMyData = false,
                    isAdmin = false,
                    isCurrentUser = false
                )
            )

            dao.insertUser(
                UserEntity(
                    fullName = "P. Ignacio Bellver, LCR",
                    avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&q=80&w=400",
                    birthDate = nearBirthdayStr,
                    city = "Córdoba, Argentina",
                    phone = "+54 9 351 688-4421",
                    email = "ibellver@legioncristorey.org",
                    memberType = MemberType.PRIEST.name,
                    ordinationDate = "14-10-2018",
                    hideMyData = false,
                    isAdmin = false,
                    isCurrentUser = false
                )
            )

            dao.insertUser(
                UserEntity(
                    fullName = "Hno. Tomás Benítez, LCR",
                    avatarUrl = "", // Empty avatar to display automatic initials badge
                    birthDate = "15-10-1994",
                    city = "San Rafael, Mendoza",
                    phone = "+54 9 260 412-3344",
                    email = "tomas.benitez@legio.org",
                    memberType = MemberType.BROTHER.name,
                    ordinationDate = null,
                    hideMyData = false,
                    isAdmin = false,
                    isCurrentUser = false
                )
            )

            dao.insertUser(
                UserEntity(
                    fullName = "Gonzalo Fernández",
                    avatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&q=80&w=400",
                    birthDate = "04-11-1991",
                    city = "CABA, Buenos Aires",
                    phone = "+54 9 11 9988-7766",
                    email = "gonzalo.admin@legio.org",
                    memberType = MemberType.BROTHER.name, // Hermano so can access Thesaurus as well
                    ordinationDate = null,
                    hideMyData = false,
                    isAdmin = true,
                    isCurrentUser = false
                )
            )

            dao.insertUser(
                UserEntity(
                    fullName = "María Eugenia Soler",
                    avatarUrl = "", // Empty avatar to display automatic initials badge
                    birthDate = "08-12-1989",
                    city = "Salta, Argentina",
                    phone = "+54 9 387 411-9988",
                    email = "meugenia.soler@legio.org",
                    memberType = MemberType.CONSECRATED_LAITY.name,
                    ordinationDate = null,
                    hideMyData = true, // Hide phone and email for regular members
                    isAdmin = false,
                    isCurrentUser = false
                )
            )

            dao.insertUser(
                UserEntity(
                    fullName = "Santiago Rossi",
                    avatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&q=80&w=400",
                    birthDate = "20-01-1999",
                    city = "Rosario, Santa Fe",
                    phone = "+54 9 341 512-3344",
                    email = "santiago.rossi@legio.org",
                    memberType = MemberType.NONE.name,
                    ordinationDate = null,
                    hideMyData = false,
                    isAdmin = false,
                    isCurrentUser = false
                )
            )

            // Seed Events
            dao.insertEvent(
                EventEntity(
                    title = "Ejercicios Espirituales San Ignacio 2026",
                    startDate = "2026-10-16",
                    endDate = "2026-10-19",
                    time = "18:00 hs (Viernes) a 17:00 hs (Lunes)",
                    location = "Casa de Retiros Ntra. Sra. del Pilar, Luján, Bs. As.",
                    description = "Tres días de sagrado silencio, meditación profunda y dirección espiritual siguiendo el método de San Ignacio de Loyola. Destinado a legionarios mayores de 18 años. Se requiere ropa formal para la Santa Misa y cuaderno de apuntes.",
                    imageUrl = "https://images.unsplash.com/photo-1548625361-195fe57871b6?auto=format&fit=crop&q=80&w=800",
                    registrationUrl = "https://forms.gle/sampleLegioEjercicios2026",
                    requiresRegistration = true,
                    isFeatured = true
                )
            )

            dao.insertEvent(
                EventEntity(
                    title = "Solemne Fiesta de Cristo Rey y Congreso Anual",
                    startDate = "2026-11-20",
                    endDate = "2026-11-22",
                    time = "09:30 hs",
                    location = "Basílica y Convento San Francisco, Córdoba",
                    description = "Encuentro nacional de todos los miembros de la Legión de Cristo Rey. Conferencia magistral, adoración eucarística nocturna, renovación de promesas legionarias y banquete fraterno.",
                    imageUrl = "https://images.unsplash.com/photo-1519817650390-64a93db51149?auto=format&fit=crop&q=80&w=800",
                    registrationUrl = "https://forms.gle/sampleLegioCristoRey2026",
                    requiresRegistration = true,
                    isFeatured = true
                )
            )

            dao.insertEvent(
                EventEntity(
                    title = "Jornada de Formación Doctrinal y Apologética",
                    startDate = "2026-10-03",
                    endDate = "2026-10-03",
                    time = "09:00 - 18:00 hs",
                    location = "Parroquia Sagrado Corazón, CABA",
                    description = "Ciclo de conferencias teológicas sobre la Doctrina Social de la Iglesia y la defensa de la Fe en el mundo contemporáneo a cargo de sacerdotes formadores.",
                    imageUrl = "https://images.unsplash.com/photo-1519671482749-fd09be7ccebf?auto=format&fit=crop&q=80&w=800",
                    registrationUrl = null,
                    requiresRegistration = false,
                    isFeatured = false
                )
            )

            // Seed News
            dao.insertNews(
                NewsEntity(
                    title = "Carta Pastoral del Superior General: Fidelidad y Celo Apostólico",
                    date = "24 Sep 2026",
                    summary = "Mensaje oficial a toda la comunidad de la Legión para el nuevo ciclo de formación y apostolado.",
                    content = """
Queridos hermanos en Cristo Rey:

Al iniciar este nuevo trimestre del año litúrgico, nos dirigimos a cada uno de ustedes con profunda gratitud por la perseverancia y el ardor apostólico manifestado en cada una de nuestras sedes y comunidades.

La misión que la Providencia nos ha confiado no admite tibiezas: ser heraldos del Reino de Cristo en nuestras familias, lugares de trabajo y ambientes sociales. Los tiempos actuales reclaman una formación intelectual sólida, una vida sacramental vigorosa y una unión fraterna inquebrantable.

Los invitamos a intensificar la oración comunitaria, la devoción a nuestra Madre la Virgen Santísima bajo la advocación del Pilar y de Guadalupe, y a participar activamente de las jornadas de formación que la Legión organiza.

Que el Divino Rey de Reyes reine en nuestros corazones y en nuestra Patria.

En Cristo y María,
Consejo General de la Legión de Cristo Rey.
                    """.trimIndent(),
                    imageUrl = "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?auto=format&fit=crop&q=80&w=800",
                    category = "Institucional",
                    isHighlighted = true
                )
            )

            dao.insertNews(
                NewsEntity(
                    title = "Nuevas Ordenaciones Sacerdotales en la Fiesta de San Miguel Arcángel",
                    date = "20 Sep 2026",
                    summary = "Dos nuevos diáconos recibirán el Sagrado Orden del Presbiterado el próximo 29 de septiembre.",
                    content = """
Con inmenso júbilo anunciamos que el próximo 29 de septiembre, festividad de los Santos Arcángeles Miguel, Gabriel y Rafael, el Excmo. y Rvdmo. Obispo conferirá el Sagrado Orden del Presbiterado a nuestros hermanos diáconos.

La solemne Santa Misa tendrá lugar a las 18:30 hs en la Iglesia Prioral. Al término se entonará el tradicional Te Deum de acción de gracias y compartiremos un vino de honor.

Acompañemos con ferviente oración a nuestros nuevos sacerdotes para que sean celosos pastores según el Sagrado Corazón de Jesús.
                    """.trimIndent(),
                    imageUrl = "https://images.unsplash.com/photo-1543807535-eceef0bc6599?auto=format&fit=crop&q=80&w=800",
                    category = "Formación",
                    isHighlighted = false
                )
            )

            dao.insertNews(
                NewsEntity(
                    title = "Lanzamiento del Cancionero Litúrgico y Devocionario Oficial",
                    date = "15 Sep 2026",
                    summary = "Ya se encuentra disponible en la Biblioteca digital la versión revisada con cantos gregorianos y marchas tradicionales.",
                    content = """
La Comisión de Liturgia de la Legión de Cristo Rey pone a disposición de todos los miembros el nuevo 'Cancionero Litúrgico y Devocionario'. 

Esta cuidada edición digital incluye las notas musicales, textos en latín y castellano, y las oraciones propias de nuestra espiritualidad para el rezo cotidiano y las celebraciones comunitarias. Pueden descargarlo directamente desde la sección Biblioteca de esta aplicación.
                    """.trimIndent(),
                    imageUrl = "https://images.unsplash.com/photo-1445499348736-29b6cdfc03b9?auto=format&fit=crop&q=80&w=800",
                    category = "Liturgia",
                    isHighlighted = false
                )
            )

            // Seed Documents (Biblioteca)
            dao.insertDocument(
                DocumentEntity(
                    title = "Regla de Vida y Estatutos Fundacionales",
                    category = "Documentos institucionales",
                    description = "Normas canónicas, espíritu apostólico y deberes de los miembros de la Legión de Cristo Rey.",
                    fileUrl = "https://drive.google.com/drive/folders/sample-legio-estatutos",
                    fileType = "PDF",
                    pageCountOrSize = "48 págs. • 3.2 MB",
                    dateAdded = "2026",
                    keywords = "regla vida estatutos normas derecho canonico"
                )
            )

            dao.insertDocument(
                DocumentEntity(
                    title = "Plan Trienal de Formación Doctrinal y Espiritual",
                    category = "Planes de formación",
                    description = "Itinerario de estudio de la Suma Teológica, Sagrada Escritura y Magisterio Eclesiástico.",
                    fileUrl = "https://drive.google.com/drive/folders/sample-legio-plan-formacion",
                    fileType = "PDF",
                    pageCountOrSize = "32 págs. • 1.8 MB",
                    dateAdded = "2026",
                    keywords = "formacion teologia doctrina estudio catecismo"
                )
            )

            dao.insertDocument(
                DocumentEntity(
                    title = "Manual de Liturgia de las Horas y Salterio Legionario",
                    category = "Liturgia de las Horas",
                    description = "Guía para el rezo de Laudes, Vísperas y Completas adaptada a las celebraciones comunitarias.",
                    fileUrl = "https://drive.google.com/drive/folders/sample-legio-liturgia-horas",
                    fileType = "PDF",
                    pageCountOrSize = "64 págs. • 4.1 MB",
                    dateAdded = "2026",
                    keywords = "laudes visperas completas salmos oficio divino rezo"
                )
            )

            dao.insertDocument(
                DocumentEntity(
                    title = "Cancionero Sacro, Himnos y Marchas de la Legión",
                    category = "Cancioneros",
                    description = "Recopilación con partituras y letras: Cantos gregorianos, himnos a Cristo Rey y cánticos marianos.",
                    fileUrl = "https://drive.google.com/drive/folders/sample-legio-cancionero",
                    fileType = "PDF",
                    pageCountOrSize = "96 págs. • 8.5 MB",
                    dateAdded = "2026",
                    keywords = "cantos himnos musica gregoriano coro partituras"
                )
            )

            dao.insertDocument(
                DocumentEntity(
                    title = "Tratado de la Verdadera Devoción a la Santísima Virgen",
                    category = "Lecturas de profundización",
                    description = "Texto clásico de San Luis María Grignion de Montfort para la consagración mariana de los miembros.",
                    fileUrl = "https://drive.google.com/drive/folders/sample-legio-montfort",
                    fileType = "PDF",
                    pageCountOrSize = "180 págs. • 5.0 MB",
                    dateAdded = "2026",
                    keywords = "virgen maria montfort consagracion devocion espiritualidad"
                )
            )

            dao.insertDocument(
                DocumentEntity(
                    title = "Directorio de Prácticas de Piedad y Examen de Conciencia",
                    category = "Documentación",
                    description = "Método sistemático para el examen diario particular y general, y confesión sacramental bien preparada.",
                    fileUrl = "https://drive.google.com/drive/folders/sample-legio-piedad",
                    fileType = "PDF",
                    pageCountOrSize = "16 págs. • 950 KB",
                    dateAdded = "2026",
                    keywords = "examen conciencia piedad oracion confesion sacramento"
                )
            )

            // Seed Announcements (Avisos)
            dao.insertAnnouncement(
                AnnouncementEntity(
                    title = "Horario Especial de Santa Misa Comunitaria",
                    message = "Se informa a todos los miembros que este próximo sábado la Santa Misa mensual de comunidad se adelantará a las 10:00 hs en la Capilla Mayor por reformas en el atrio.",
                    date = "24 Sep 2026",
                    priority = "Alta",
                    isActive = true
                )
            )

            dao.insertAnnouncement(
                AnnouncementEntity(
                    title = "Nueva actualización de la Biblioteca Digital",
                    message = "Ya se encuentran incorporados los nuevos materiales de estudio para el Módulo II del Plan de Formación 2026.",
                    date = "21 Sep 2026",
                    priority = "Normal",
                    isActive = true
                )
            )
        }
    }
}
