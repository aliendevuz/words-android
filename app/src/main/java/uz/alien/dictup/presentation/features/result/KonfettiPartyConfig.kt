package uz.alien.dictup.presentation.features.result

import android.content.Context
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Shape
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

object KonfettiPartyConfig {

    /**
     * Yutuq uchun eng yaxshi konfetti animatsiyasi
     * Ikki chekkadan bir vaqtda otiladi
     */
    fun getWinnerParty(): List<Party> {
        return listOf(
            // Chap chekkadan
            Party(
                angle = 300,
                spread = 100,
                speed = 25f,
                maxSpeed = 35f,
                damping = 0.85f,
                size = listOf(Size.SMALL, Size.MEDIUM, Size.LARGE),
                colors = listOf(
                    0xFF0000, // Qizil
                    0x00AA00, // Yashil
                    0x0000FF, // Ko'k
                    0xFFD700, // Oltin
                    0xFF6600, // Oqro'q
                    0xFF1493  // Deep Pink
                ),
                shapes = listOf(Shape.Square, Shape.Circle),
                timeToLive = 3500L,
                fadeOutEnabled = true,
                position = Position.Relative(0.1, 0.5),
                emitter = Emitter(duration = 800, TimeUnit.MILLISECONDS).max(80),
                rotation = Rotation()
            ),

            // O'ng chekkadan
            Party(
                angle = 60,
                spread = 100,
                speed = 25f,
                maxSpeed = 35f,
                damping = 0.85f,
                size = listOf(Size.SMALL, Size.MEDIUM, Size.LARGE),
                colors = listOf(
                    0xFF0000, // Qizil
                    0x00AA00, // Yashil
                    0x0000FF, // Ko'k
                    0xFFD700, // Oltin
                    0xFF6600, // Oqro'q
                    0xFF1493  // Deep Pink
                ),
                shapes = listOf(Shape.Square, Shape.Circle),
                timeToLive = 3500L,
                fadeOutEnabled = true,
                position = Position.Relative(0.9, 0.5),
                emitter = Emitter(duration = 800, TimeUnit.MILLISECONDS).max(80),
                rotation = Rotation()
            ),

            // Yuqoridan
            Party(
                angle = 270,
                spread = 120,
                speed = 20f,
                maxSpeed = 30f,
                damping = 0.9f,
                size = listOf(Size.MEDIUM, Size.LARGE),
                colors = listOf(
                    0xFFD700, // Oltin
                    0xFFA500, // Apelsin
                    0xFF69B4  // Hot Pink
                ),
                shapes = listOf(Shape.Circle),
                timeToLive = 4000L,
                fadeOutEnabled = true,
                position = Position.Relative(0.5, 0.0),
                emitter = Emitter(duration = 1000, TimeUnit.MILLISECONDS).max(100),
                rotation = Rotation()
            )
        )

    }

    /**
     * Oddiy va tez konfetti
     */
    fun getQuickParty(): Party {
        return Party(
            angle = 270,
            spread = 360,
            speed = 15f,
            maxSpeed = 25f,
            damping = 0.9f,
            size = listOf(Size.MEDIUM),
            colors = listOf(
                0xFF0000,
                0x00AA00,
                0x0000FF,
                0xFFD700
            ),
            shapes = listOf(Shape.Square, Shape.Circle),
            timeToLive = 2000L,
            fadeOutEnabled = true,
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 500, TimeUnit.MILLISECONDS).max(100),
            rotation = Rotation()
        )
    }

    /**
     * Yuqoridan pastga tushayotgan konfetti
     */
    fun getRainParty(): Party {
        return Party(
            angle = 270,
            spread = 360,
            speed = 8f,
            maxSpeed = 12f,
            damping = 0.95f,
            size = listOf(Size.SMALL, Size.MEDIUM),
            colors = listOf(
                0xFF0000,
                0x00AA00,
                0x0000FF
            ),
            shapes = listOf(Shape.Square),
            timeToLive = 5000L,
            fadeOutEnabled = true,
            position = Position.Relative(0.5, 0.0),
            emitter = Emitter(duration = 3000, TimeUnit.MILLISECONDS).perSecond(50),
            rotation = Rotation()
        )
    }
}