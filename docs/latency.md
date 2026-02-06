# Latency & Performance Model

The primary performance metric explored in this prototype is
Time-To-First-Shot (TTFS).

## Definitions

- Cold Start  
  First capture after entering Instant Capture Mode.
- Warm Capture  
  Subsequent captures after camera preload.

## Measurements

The system records:
- Capture latency per shot
- Total TTFS
- Burst grouping
- Cold vs warm classification

Metrics are exported as CSV for offline analysis.

## Why TTFS Matters

For wearable photography, moments are often lost within
hundreds of milliseconds. Reducing TTFS directly improves
the likelihood of capturing spontaneous events.

## Simulation Notes

A simulated camera backend is used to model:
- Camera warm-up cost
- Reduced latency after preload

This allows experimentation without physical hardware.
