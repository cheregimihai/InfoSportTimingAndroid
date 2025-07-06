
# Prompt for AI: Recreate the Handball Timekeeping App

## High-Level Overview

You are tasked with recreating a Handball Timekeeping App. This is an Android, fullscreen application designed to run on an Android phone in landscape mode. The primary user is a timekeeper for a handball match. The app's main purpose is to display and manage the official match time, and to trigger external hardware (goal-side lights) at the end of each half.

## Target Platform and Technology Stack

*   **Platform:** Android App.
*   **Framework:** 
*   **Language:** 
*   **Styling:** Material Design
*   **State Management:** 
*   **Hardware Integration:** The app will communicate with a Shelly relay device over the local network.

## User Interface (UI) and User Experience (UX)

The UI should be simple, intuitive, and easy to read from a distance. The screen is divided into two main sections: the timer display and the controls.

### 1. Timer Display (Upper Half of the Screen)

*   **Layout:** Occupies the top 50% of the screen.
*   **Content:** A large, digital-style clock displaying the match time.
*   **Format:** `MM:SS` (e.g., `29:59`).
*   **Font:** A clear, bold, and easily readable font (e.g., a sans-serif font like Roboto, Open Sans, or a monospace font).


### 2. Controls (Bottom Half of the Screen)

*   **Layout:** Occupies the bottom 30% of the screen. The controls should be large and easy to tap.
*   **Components:**
    *   **Time Adjustment Buttons:**
        *   `-5 sec`: Decrements the time by 5 seconds.
        *   `-1 sec`: Decrements the time by 1 second.
        *   `+1 sec`: Increments the time by 1 second.
        *   `+5 sec`: Increments the time by 5 seconds.
    *   **Manual Time Input:**
        *   An input field or a modal that allows the user to manually set the time. This should be a simple and quick interaction.
    *   **Main Action Buttons:**
        *   `Start / Pause`: A toggle button that starts or pauses the timer. The button's label should change to reflect the current action (e.g., "Pause" when the timer is running, and "Start" when it's paused).

## Core Functionality

### 1. Timer Logic

*   **Match Duration:** The match consists of two 30-minute halves. Afterwards, there are a maximum of 4 rounds of overtime, 5 minutes each.
*   **Counting:** The timer should count up from `00:00` to `80:00`. The timer should never be allowed to go over "80:00"
*   **Auto-Stop:** The timer must automatically stop when it reaches `30:00`, `60:00`, `65:00`, `70:00`, `75:00`, `80:00`. The user must press "Start" to begin the second half, and overtime rounds.
*   **Time Adjustment Constraints:**
    *   The `+1 sec` and `+5 sec` buttons should not allow the timer to go beyond `30:00` if the current time is `29:59` or less. It should not allow the timer to go over '60:00' if current time is '59:59'. In general, it should not allow you to progress past an automatic stop.
    *   The `-1 sec` and `-5 sec` buttons should not allow the timer to go below `00:00`.

### 2. Shelly Relay Integration

*   **Purpose:** To trigger goal-side LED lights at the end of each half.
*   **Trigger Events:**
    *   At `30:00` (end of the first half).
    *   At `60:00` (end of the second half).
    *   At `65:00` (end of first overtime round)
    *   At `70:00` (end of second overtime round)
    *   At `75:00` (end of third overtime round)
    *   At `80:00` (end of last overtime round)       
*   **Action:** When triggered, the app should send an HTTP request to the Shelly relay device to turn it on and off in a flashing pattern.
*   **Flash Pattern:** A configurable flash pattern (e.g., 3 flashes of 1 second each).
*   **Configuration:** The app should have a settings area (which can be a simple, non-UI configuration file or a dedicated settings component) where the user can:
    *   Enter the IP address and any necessary credentials for the Shelly device.
    *   Configure the flash duration and pattern.
    *   Disable the relay control for a "test mode."


Please organize the code in a logical and maintainable way. 

Please begin by creating the basic project structure and then implementing the UI and core functionality as described above.