package com.ekosoftware.secretdms.injection

import com.ekosoftware.secretdms.data.model.Chat
import com.ekosoftware.secretdms.data.model.DIRECTION_SENT
import com.ekosoftware.secretdms.data.model.Message
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
            return times.mapIndexed { index, unit ->
                val randomStr = UUID.randomUUID().toString()
                /*val randomName = (1..15).map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinToString("")*/
                val randomName: (num: Int) -> String = {
                    when (it) {
                        0 ->"carlos"
                        1 -> "juan"
                        else -> "maria"
                    }
                }
                val direction = Random.nextInt(1,3)
                val sent = if (direction == 1) times[index].timeInMillis else null
                val received = if (direction == 2) times[index].timeInMillis else null
                Message(
                    randomStr,
                    "Mensaje de prueba #$index",
                    direction,
                    randomName(Random.nextInt(0,4)),
                    Random.nextInt(1, 21) * 1000L,
                    times[index].timeInMillis,
                    sent,
                    received,
                    showedTimeInMillis = sent ?: received
                )
            }.toTypedArray()
        }

    val chats = arrayOf(
        Chat("juan"),
        Chat("carlos"),
        Chat("maria"),
    )
}