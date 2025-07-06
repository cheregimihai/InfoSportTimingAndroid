*   **Status Indicator:** The timer display should visually indicate the current status of the match. This can be done through a text label below the timer or by changing the color of the timer itself. The possible statuses are:
    *   `Running`
    *   `Paused`
    *   `Stopped`
    *   `Half-time`
    *   `Full-time`

## State Management

*   **Persistence:** The app must remember the current match time and the state of the Shelly relay even if the user accidentally reloads the page or closes the browser. Use `localStorage` or `sessionStorage` for this.
*   **State to Persist:**
    *   Current time (in seconds).
    *   Current match status (`Running`, `Paused`, etc.).
    *   Shelly device configuration.

## Future Enhancements (for consideration)

*   **Sound Notifications:** A buzzer or sound notification at the end of each half.
*   **Offline First:** Use a service worker to make the app work offline.
*   **Dark Mode:** A dark mode for better visibility in different lighting conditions.

## Project Structure