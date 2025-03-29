**Mobile Development 2024/25 Portfolio**
# Requirements

Student ID: `23015833`

# Functional Requirements

Navigation:

The application must allow users to navigate between pages using a bottom navigation bar.
The bottom navigation bar should contain links to the following pages:
- Home
- Teams
- News

With additional subpages which accessed through general use of the app such as:
- Settings
- Statistics
- Schedules
- etc.

Personalization:
- The application must allow users to select their favorite NFL team for a personalized home page.
- The selected team should be saved using shared preferences.
- The application must remember the selected favorite team and display relevant information about the team on the home page.

Game Scheduling & Notifications:
- The application must interact with the device's calendar to allow users to "save the date" of an upcoming game.
- The application must notify users when a game involving their favorited team is about to start.
- Users should also be able to enable notifications for other games they are interested in.

Third-Party API Integration:
- The application must fetch NFL team and game-related data from a third-party API.
- The application must display real-time game scores, team statistics, and upcoming game schedules.
- Internet Connectivity:
- The application must check if the device is connected to the internet.
- If there is no internet connection, the app should display an appropriate message and provide limited offline functionality where possible.

News WebView:
- The application must allow users to view the latest NFL news by displaying a web view of NFL News when they click on the news tab.
(https://www.nfl.com/news/)

# Non-Functional Requirements

User Experience & Design:
- The application must be locked in portrait mode.
- The font used across the application should be consistent.
- The application should have a uniform theme and styling.
- The UI should be responsive and scale across different screen sizes.

App Lifecycle & Performance:
- The application should load directly to the home page upon launch/
- A loading screen should be displayed when fetching data from the API.
- On first launch, the application must prompt users to select their favorite NFL team before proceeding.


<!-- 

This is a good set of requirements. You've captured the overall essence of the app. I have a couple of suggestions to make:

- Which pages should they be able to navigate to from the bottom navigation bar? Be specific – imagine another engineer is going to take these and then implement them. Would you get back what you'd expected with these requirements, or would there be room for an engineer in a rush to claim they'd implemented all of them but the app was rather less than you'd hoped?

- You don't need to specify _how_ you will implement particular aspects in the functional requirements (this can go in the API description), it's sufficient just to specify the functionality and then leave the implementation up to the implementer. (You could also put this into the non-functional requirements.)

- What does the app need to pull from the third-party API? Specify.

– Checking whether the app has access to the internet sounds useful, but is it a 'function' of the app? I think this probably needs to be in the NFR section (perhaps expanded to explain what should happen based on whether it's available or not).

- Double-check for typos – there are a few to tidy up.

- You can lock the app in portrait mode, but remember that rotation is not the only thing that causes a configuration change, so you'll still have to do some state management.

Hope these are useful comments.

-->