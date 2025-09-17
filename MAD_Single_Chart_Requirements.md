
# MAD Chart Control – Single Chart Baseline
**Version:** 1.0  
**Status:** In Progress  

---

## ✅ Active Requirements

1. **Mic Input Capture**
   - Real-time audio stream from microphone
   - Split into 10 frequency bands (CH1–CH10)
   - Source: Web Audio API
   - System must not crash if mic access denied

2. **Sampling Interval**
   - Adjustable in ms via control panel
   - Applies to live stream
   - Range: 100ms–10000ms

3. **Window Size**
   - Sliding window of N points
   - Adjustable via control panel
   - Oldest points removed when full

4. **Chart Mode**
   - Toggle between:
     - Stacked 10-channel bar (CH1–CH10)
     - Total Sum of channels (single line or bar)
   - No bar/line toggle unless explicitly reapproved

5. **Chart Display**
   - Zoom on X-axis via mouse wheel and touch (pinch)
   - Pan with drag
   - No redraw when scrolling backward
   - Maintain bar width/aspect ratio across window sizes

6. **Average Line**
   - Optional dotted line (toggleable)
   - Displays average amplitude (sum or per-channel mean depending on mode)
   - Does not interfere with zoom/pan

7. **Title Toggle**
   - Checkbox to show/hide title
   - Default: visible
   - Applies live

8. **Background Color**
   - Control panel color picker
   - Applies to full page and chart area

9. **Export**
   - Button to download full captured dataset as JSON
   - Filename: `mad_capture_<timestamp>.json`

10. **Clear**
    - Clears chart display and memory buffer
    - Does NOT reset controls or reload session

11. **Tick Formatting**
    - X-axis ticks match sampling rate & window
    - Avoid label spam (e.g., every second on a 1hr chart)
    - Format examples:
      - 1s interval: show HH:MM:SS
      - 1min interval: show HH:MM

12. **UI Structure**
    - Sliding control panel on left
    - Title: "Chart Settings"
    - Control inputs:
      - Background color
      - Interval
      - Window size
      - Toggle: Title
      - Toggle: Chart mode (Sum vs Channels)
      - Toggle: Avg Line
    - Top row (fixed): Start, Stop, Clear, Download

13. **Layout Stability**
    - Chart fits panel (320px height minimum)
    - No scrolling glitches or overflow
    - Works on mobile & desktop

14. **Visual Smoothness**
    - Tracer-like flow (no jumpy redraws)
    - Transitions smooth over time
    - No page flickering or resizing artifacts

15. **Persistent State**
    - If refresh occurs, reset to baseline defaults
    - No IndexedDB or local storage unless reapproved

---

## ✅ Done/Verified
_None yet_

## ❓ Open Issues
_None yet_

## ❓ Questions to Clarify
_None yet_
