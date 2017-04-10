# Roomifer


### Roomifer is a great tool for managing errands and other tasks with your roommates, together.

![alt text](https://www.crestron.com/images/products/icons/android_download_logo.png "Roomifer is compatible with Android devices")

# Section Links
1. [Prerequisites](#prerequisites)
2. [App Description](#app-description)
3. [App Goal](#app-goal)
4. [General Explanation](#general-explanation)
5. [Relevant Knowledge Center](#relevant-knowledge-center)
6. [Possible Features for the Future](#possible-features-for-later-on)

___

#### Prerequisites:

The project should involve at least some experience with the following:
* Android development (using Android Studio)
* Java, using open source libraries (including Google Calendar API)
* Version control (Git/GitHub)
* Build automation (Gradle)
* A backend database server (SQL possibly)
___

#### App Description:

If you've ever lived off campus and had to deal with several roommates, you all may have struggled to stay organized and decide which roommate should buy a certain item for the house on a timely basis.

Let’s say four roommates agreed to share their dish soap. Who gets to decide what to buy

___

#### App Goal:

This app should help you and your roommates organize and set tasks by doing (simply) only the following:

You and your roommates are added to a group that holds an empty list. Anyone in the group can add items to the list. These lists may include items for purchase or may include chores for the roommates to complete

Roommates are assigned to certain items (tasks) on the list and are expected to meet the set deadline date for that particular item on the list.

Users will receive a notification once the task is completed and is up to the rotation stage

___

#### General Explanation: 

An item basically represents a task

Each roommate must have the app installed on their Android device, and once they open the app for the first time, they create an account (maybe we can use Google’s Sign-In API).

Once account/profile setup is completed by the user, they are brought to the main menu. The menu screen doesn’t show much to the user, but it will prompt them to create a group. (max 5 roommates)

The users will be able to invite each other to a group (if the users exist)

Once a group is created by a user, an empty list is created that can be manipulated by any user in the group. Any user in the group can add items/tasks to the list, and dismiss/delete them as well.

When adding an item to the list, the user can:
* Specify the name of the item
* Show who created it the item (enable/disable who created the item)
* Assign the item to specifically one person in the group, including themselves
* Assign the item to anyone in the group and it gets automatically rotates to someone else in the group to be assigned to them next once the due date is reached. (For example: a roommate alternates to buy dish soap every three weeks)
* Set due date (or to be reminded via Android push notifications) on a certain date. Once the due date is reached, whichever user that is assigned that item on the list must dismiss (delete) the item from the list to confirm that he completed the task.

___

## Relevant Knowledge Center

**UI Menu/Graphics:**
https://github.com/material-components/material-components-android


**Version Control:**
We can use GitHub for version control (Jordan can easily teach you all how to use it)

**Saving Data with Android (Java):**
https://developer.android.com/training/basics/data-storage/index.html

**SQL Database with Android (Java):**
https://developer.android.com/training/basics/data-storage/databases.html
* Basic tutorial for database modeling: https://www.getdonedone.com/building-the-optimal-user-database-model-for-your-application/



#### Google API’s:

Using Google Sign-In API for Android (Java): https://developers.google.com/identity/sign-in/android/start
* The implementation in Android (Java): https://developers.google.com/identity/sign-in/android/start-integrating

Using Google Calendar API for Java: https://developers.google.com/api-client-library/java/apis/calendar/v3
* Example implementation of Google Calendar for Android (Java): https://github.com/google/google-api-java-client-samples/tree/master/calendar-android-sample

___

#### Possible features for later on:
* User permissions in a group
* Privacy settings (to hide certain information from other users in a group)
