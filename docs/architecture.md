# Architecture Overview

This project models an instant photo capture system inspired by
smart glasses (e.g. Meta Ray-Ban), optimized for low latency and
minimal user interaction.

## High-Level System Diagram

+------------------+
|   User Gesture   |
| (Tap / LongPress)|
+--------+---------+
         |
         v
+------------------+
|  Gesture Layer   |
| (Abstracted)     |
+--------+---------+
         |
         v
+---------------------------+
|   Mode Controller         |
|  (Finite State Machine)   |
|                           |
|  IDLE <-> INSTANT_CAPTURE |
+--------+------------------+
         |
         v
+------------------+
| Camera Interface |
|    (Camera)      |
+--------+---------+
         |
         v
+-------------------------+
| Camera Implementation  |
| (SimulatedCamera now)  |
| (Real hardware later)  |
+--------+----------------+
         |
         v
+--------------------------+
| Capture Metrics & Logs   |
| (TTFS, burst, latency)  |
+--------+-----------------+
         |
         v
+--------------------------+
|   CSV Export (Analysis)  |
+--------------------------+

This flow shows how user intent is separated from hardware details.
Gestures are interpreted based on the current mode, and only valid
actions reach the camera layer. Performance data is captured alongside
each photo for analysis.
## Core Components

### Gesture Layer
User input is abstracted as high-level gestures:
- TAP
- LONG_PRESS

This allows future hardware (touch sensors, blink detection, EMG)
to plug into the same system without changing core logic.

### Mode Controller
The system operates as a finite state machine:

- IDLE  
  Safe state. No photos can be taken.
- INSTANT_CAPTURE  
  Optimized capture state where latency is minimized.

Gestures are interpreted differently depending on the current mode.

### Camera Abstraction
A camera interface decouples capture logic from hardware:

- SimulatedCamera (current)
- Real hardware / CameraX (future)

This allows performance testing without physical devices.

## Design Principle
Latency-sensitive paths are kept minimal and predictable.
Non-essential work is moved outside the capture path.
