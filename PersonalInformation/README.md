# Abstract : 
---
A complete Android application that has following features :
*	Android application for storing user details. 
*	Fragments use for optimized use of ListView.  
*	Fetching data such as majors etc from assets that can be changed outside, thus not hardcoded in code.
* Demonstration of activity lifecycle and fragment lifecycle.

# App Description
---
The entire application is divided into two parts: 

## Personal Information Activity
The first or main activity collects the basic information about a person. This activity’s view will look like a basic form. The information we will collect is:
* First (given) name
* Family (last) name
* Age
* Email
* Phone
* Major
For each of the first five items (first name, last name, age, email and phone) use a text field to allow the user to enter the data. In each text field the appropriate keyboard is used. All items need a label indicating what information they contain (first name, last name, etc). All items are on a separate line. To select a major the user needs to go to a second activity.
Finally there is a done button. When the done button is pressed the user information is saved.
When the app is killed and restarted the saved data is displayed in this activity.


## Major Selection
To select a major the user is shown a second activity. When this activity is shown the user
sees a list of advanced degrees offered by SDSU. The list is shown using
a list fragment. When the user selects one of the advanced degrees, say Masters of Science,
they are then shown a list of all the majors in that advanced degree. The user can select a major in that list. When they are done selecting
the major bring them back to the Personal Information Activity, with the selected major
shown. For example MS Computer Science or Ph.D. Biology.
When the user selects an advanced degree type, say Master of Arts, they may not see the major
they are looking for. So they will want to go back to the list of advanced degree type to try
again.

