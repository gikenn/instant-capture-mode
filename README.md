# Instant Capture Mode Prototype

A gesture-driven, low-latency photo capture system inspired by smart
glasses (e.g. Meta Ray-Ban), designed to minimize time-to-first-shot
for photographers and mobile users who need to capture moments instantly.

This project explores how interaction design and system architecture
can reduce capture friction on wearable devices.

---

## Problem

On smart glasses and wearables, photo capture is often too slow:
- Users must wait for camera wake-up
- UI interactions add friction
- Accidental captures are common

For spontaneous photography, even small delays can mean missed moments.

This prototype focuses on **interaction speed, intent clarity, and
latency measurement** rather than UI polish.

---

## Core Idea

Introduce a dedicated **Instant Capture Mode**:

- A deliberate gesture enters capture mode
- While in this mode, photos can be taken instantly with a single tap
- Latency-sensitive paths are minimized
- Performance is measured and logged for analysis

---

## Key Features

- Mode-based capture system (IDLE / INSTANT_CAPTURE)
- Gesture abstraction (tap, long-press)
- Camera abstraction layer
- Cold vs warm capture modeling
- Burst detection
- Time-To-First-Shot (TTFS) measurement
- CSV telemetry export for offline analysis

---

## Architecture & Design Docs

Detailed design decisions are documented here:

- 📐 [Architecture Overview](docs/architecture.md)
- ✋ [Gesture Design](docs/gestures.md)
- ⚡ [Latency & Performance Model](docs/latency.md)

These documents explain *why* the system is structured this way,
not just how it works.

---

## Tech Stack

- Kotlin (JVM)
- Termux (mobile-only development environment)
- Simulated camera backend

No physical smart glasses hardware is required to run or evaluate
the prototype.

---

## Current Limitations

- Camera is simulated (no real sensor access)
- No UI layer
- Gesture input is mocked

These are intentional to keep the focus on interaction speed
and system behavior.

---

## Future Work

- Replace simulated camera with Android CameraX
- Evaluate blink or squeeze-based gestures
- Visualize TTFS and burst performance
- Integrate with real wearable hardware APIs

---

## Status

Prototype – v0. 1. 0
Built, tested, and published entirely from a mobile device.
