package com.ekosoftware.secretdms.injection

import com.ekosoftware.secretdms.data.model.Chat
import com.ekosoftware.secretdms.data.model.DIRECTION_SENT
import com.ekosoftware.secretdms.data.model.Message
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import java.util.*
import kotlin.random.Random

object DummyData {

    private val times = arrayListOf(
        Calendar.getInstance()
            .apply { set(2021, 3, 21, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 22, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 22, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 22, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 23, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 23, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 24, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 24, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 25, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 25, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 25, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 25, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 25, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 30, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 31, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
        Calendar.getInstance()
            .apply { set(2021, 3, 31, Random.nextInt(0, 24), Random.nextInt(0, 60)) },
    )

    val messages: Array<Message>
        get() {
            val someMessages = times.mapIndexed { index, unit ->
                val randomStr = UUID.randomUUID().toString()
                /*val randomName = (1..15).map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinToString("")*/
                val randomName: (num: Int) -> String = {
                    when (it) {
                        0 -> "carlos"
                        1 -> "juan"
                        else -> "maria"
                    }
                }
                val direction = Random.nextInt(1, 3)
                val sent = if (direction == 1) times[index].timeInMillis else null
                val received = if (direction == 2) times[index].timeInMillis else null
                Message(
                    randomStr,
                    "Mensaje de prueba #$index",
                    direction,
                    randomName(Random.nextInt(0, 4)),
                    Random.nextInt(1, 21) * 1000L,
                    times[index].timeInMillis,
                    false
                )
            }
            val allMessages = mutableListOf<Message>()
            allMessages.addAll(fixedMessages)
            allMessages.addAll(someMessages)
            return allMessages.toTypedArray()
        }

    private val fixedMessages = listOf<Message>(
        Message(
            UUID.randomUUID().toString(),
            "Hola, soy tomas, sabés quién soy. Te hablo por acá, porque es una línea segura.",
            2,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 7, 15, 0, 31).toDate().time,
          false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Hola tomas, para confirmar recordame como nos conocimos",
            1,
            "tomas",
            Random.nextInt(1, 21) * 1000L,
            LocalDateTime(2021, 4, 7, 16, 21, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Ejercito de montaña, segundo pelotón",
            2,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 7, 16, 22, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Nos acorralaron",
            2,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 7, 16, 22, 34).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Yo disparé",
            2,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 7, 16, 22, 37).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Hola tomas, te escucho",
            1,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 7, 20, 1, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Necesito un favor, grande. Recordás la última vez que hablamos lo que te comenté que quería hacer?",
            2,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 7, 23, 57, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Si",
            1,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 8, 0, 20, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Entonces?",
            1,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 8, 8, 36, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Voy a realizar la operación. Necesito tu ayuda en ya sabes qué.",
            2,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 8, 8, 37, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Te veo en la estación de Retiro del Belgrano, a las 15.45 del martes.",
            1,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 8, 9, 41, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "A esa hora no puedo, es el bautismo de mi hija.",
            2,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 8, 9, 42, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Misma hora y lugar, día siguiente",
            1,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 8, 19, 7, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Perfecto, ahí nos vemos.",
            2,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 8, 23, 14, 31).toDate().time,
            false
        ),
        Message(
            UUID.randomUUID().toString(),
            "Confirmado",
            1,
            "tomas",
            10000L,
            LocalDateTime(2021, 4, 9, 10, 27, 31).toDate().time,
            false
        )
    )

    val chats = arrayOf(
        Chat("juan"),
        Chat("carlos"),
        Chat("maria"),
        Chat("tomas"),
    )
}