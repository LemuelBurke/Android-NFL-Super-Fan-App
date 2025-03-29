**Mobile Development 2024/25 Portfolio**
# API Description

Student ID: `23015833`

For navigation within the application, I'm using a Bottom Navigation Bar for seamless transitions between primary pages: Home, Teams, and News. Bottom Navigation was the most intuitive option, aligning with Wroblewski’s (Wroblewski 2015) principle that “Obvious Always Wins,” ensuring crucial features remain accessible rather than hidden behind menus. Tabs were considered but not chosen, as they better suit closely related content rather than distinct sections. Bottom Navigation bar's also offer ergonomic advantages, being positioned within thumb reach for most users.

For personalisation, the application lets users select their favorite NFL team, storing this preference using Shared Preferences. This approach was chosen over a database solution like Room, as Shared Preferences provide a lightweight and efficient way to persist small amounts of user-specific data(Android [no date]). This ensures the application remembers the user's favorite team and tailors the Home page to display relevant information without unnecessary database overhead.

To handle game scheduling and notifications, I integrated the application with the device’s calendar API, allowing users to “save the date” for upcoming games. Notifications are managed using Android’s AlarmManager and NotificationManager, ensuring timely alerts before a favorited team’s game starts. WorkManager was considered but was not ideal since its main function is to manage deferrable, long-running background tasks rather than real-time scheduling.

For third-party API integration, the application fetches NFL team and game-related data from "BallDontLie" NFL API, ensuring users receive up-to-date scores, statistics, and schedules. The data retrieval process is optimized with Retrofit.

To enhance user experience, the application checks for internet connectivity and displays an appropriate message if offline. Limited offline functionality is available where feasible, ensuring users can still access stored data. Lastly, for news content, a WebView loads the latest NFL updates directly from the official NFL website, maintaining an in-app browsing experience without unnecessary redirections.

References:
Wroblewski, L. 2015. LukeW | Obvious Always Wins. Available at: https://www.lukew.com/ff/entry.asp?1945 [Accessed: 29 March 2025].

Android. [no date]. Save key-value data. Available at: https://developer.android.com/training/data-storage/shared-preferences [Accessed: 29 March 2025].
