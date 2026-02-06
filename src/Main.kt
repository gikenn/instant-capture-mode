// ================= IMPORTS =================
import java.io.File
import java.io.FileWriter

// ================= CAMERA ABSTRACTION =================

interface Camera {
    fun preload()
    fun capture(): Long
}

class SimulatedCamera : Camera {

    private var warm = false

    override fun preload() {
        println("📡 Camera preloading...")
        Thread.sleep(50) // simulate warm-up
        warm = true
    }

    override fun capture(): Long {
        val latency = if (warm) 30L else 500L
        Thread.sleep(latency)
        return latency
    }
}

// ================= MODE + GESTURE =================

enum class Mode {
    IDLE,
    INSTANT_CAPTURE
}

enum class Gesture {
    TAP,
    LONG_PRESS
}

// ================= CONTROLLER =================

class InstantCaptureController(
    private val camera: Camera
) {

    data class Shot(
        val timestamp: Long,
        val latency: Long,
        val totalTTFS: Long,
        val burstNumber: Int,
        val type: String // "COLD" or "WARM"
    )

    private var currentMode = Mode.IDLE
    private var lastActionTime = 0L
    private val debounceMs = 200L

    // Timing metrics
    private var modeEnteredAt = 0L
    private var firstShotTaken = false
    private var coldStart = true
    private var coldTTFS: Long? = null
    private var warmTTFS: Long? = null

    // Burst mode
    private val burstWindowMs = 800L
    private var burstCount = 0
    private var burstStartedAt = 0L

    // Shot log
    private val shots = mutableListOf<Shot>()

    // Handle gestures
    fun onGesture(gesture: Gesture) {
        when (gesture) {
            Gesture.LONG_PRESS -> handleLongPress()
            Gesture.TAP -> handleTap()
        }
    }

    private fun handleLongPress() {
        when (currentMode) {
            Mode.IDLE -> enterInstantCapture()
            Mode.INSTANT_CAPTURE -> exitInstantCapture()
        }
    }

    private fun handleTap() {
        val now = System.currentTimeMillis()

        if (currentMode != Mode.INSTANT_CAPTURE) return
        if (now - lastActionTime < debounceMs) return

        if (burstCount == 0) {
            burstStartedAt = now
            burstCount = 1
        } else if (now - burstStartedAt <= burstWindowMs) {
            burstCount++
        } else {
            println("📸 Burst finished ($burstCount shots)")
            burstCount = 1
            burstStartedAt = now
        }

        lastActionTime = now
        capturePhoto(now)
    }

    private fun enterInstantCapture() {
        currentMode = Mode.INSTANT_CAPTURE
        modeEnteredAt = System.currentTimeMillis()
        firstShotTaken = false
        coldStart = true
        burstCount = 0

        shots.clear() // reset shots for new session

        camera.preload()
        println("📸 Instant Capture Mode ON")
    }

    private fun exitInstantCapture() {
        if (burstCount > 0) {
            println("📸 Final burst count: $burstCount")
        }

        printSessionSummary()
        exportCSV() // auto-export directly to Downloads

        burstCount = 0
        currentMode = Mode.IDLE
        println("🛑 Instant Capture Mode OFF")
    }

    private fun capturePhoto(now: Long) {
        val latency = camera.capture()
        val totalTime = (now - modeEnteredAt) + latency

        val type = if (!firstShotTaken && coldStart) "COLD" else "WARM"

        if (!firstShotTaken) {
            if (coldStart) {
                coldTTFS = totalTime
                coldStart = false
            } else {
                warmTTFS = totalTime
            }
            firstShotTaken = true
        }

        shots.add(Shot(now, latency, totalTime, burstCount, type))

        println("📷 Photo captured (latency=${latency}ms, type=$type, burst=$burstCount)")
    }

    private fun printSessionSummary() {
        println("\n===== SESSION SUMMARY =====")
        println("Cold TTFS: ${coldTTFS ?: "N/A"} ms")
        println("Warm TTFS: ${warmTTFS ?: "N/A"} ms")
        println("Total shots in last burst: $burstCount")
        println("\nSHOT LOG (CSV format):")
        println("Timestamp,Latency(ms),TotalTTFS(ms),BurstNumber,Type")
        shots.forEach { shot ->
            println("${shot.timestamp},${shot.latency},${shot.totalTTFS},${shot.burstNumber},${shot.type}")
        }
        println("===========================\n")
    }

    private fun exportCSV(filename: String = "~/storage/shared/Download/shot_log.csv") {
        try {
            val file = File(filename)
            val writer = FileWriter(file)
            writer.append("Timestamp,Latency(ms),TotalTTFS(ms),BurstNumber,Type\n")
            shots.forEach { shot ->
                writer.append("${shot.timestamp},${shot.latency},${shot.totalTTFS},${shot.burstNumber},${shot.type}\n")
            }
            writer.flush()
            writer.close()
            println("✅ CSV exported to $filename")
        } catch (e: Exception) {
            println("❌ Error exporting CSV: ${e.message}")
        }
    }
}

// ================= MAIN =================

fun main() {
    val camera = SimulatedCamera()
    val controller = InstantCaptureController(camera)

    // Test gestures
    controller.onGesture(Gesture.TAP)          // ignored
    controller.onGesture(Gesture.LONG_PRESS)   // enter mode
    Thread.sleep(120)
    controller.onGesture(Gesture.TAP)          // first shot
    Thread.sleep(200)
    controller.onGesture(Gesture.TAP)          // second shot
    Thread.sleep(200)
    controller.onGesture(Gesture.TAP)          // third shot
    controller.onGesture(Gesture.LONG_PRESS)   // exit mode → CSV saved in Downloads
}
