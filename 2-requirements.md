**Mobile Development 2024/25 Portfolio**
# Requirements

Student ID: `23015833`

# Functional Requirements
- The application must allow users to navigate to and from pages via the use of a bottom navigation bar.
- The application must allow users to select their facourite NFL team for a personalised 'home' page, the selected team should be saved in shared_prefs.
- The application must "remember" a users selected favourite team and display information regarding their favourite team on the homepage.
- The application must interact with the calender to allow users to "save the date" of an upcoming game.
- The application must notify users of when a game involving their favourited team is about to start or any game they've enabled notification for is about to start
- The application must be able to pull from the third-party api.
- The application must check if the device is connected to the internet.
- The application must be able to display a webview of https://www.nfl.com/news/ to users when they click on the news tab


# Non-Functional Requirements

- The application must be locked in portrait mode
- The font used across the application should be consistant
- The application should have consistant themes and styling
- The application should scale between screen sizes
- The application should upon launch should load to home
- The application should display a loading screen when trying to reach the api
- The application should upon its first launch on a device should prompt a user to select their favourite NFL team

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