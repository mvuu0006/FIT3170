# Instructions for PI1 Demonstration
#### Produced by OnlineLearning

## Running the Modules
In order to run the front-end module, all that is needed is to run the following command:
```
npm start
```
In the directory ```./git-frontend```

In order to run the back-end module, you must begin the main() function within ```./git/src/main/java/monash/edu/git/GitApplication.java``` or build the application into a .jar and run it.

## Using the front-end
At the moment, the front-end supports adding projects and repositories via URL query strings as well as via the UI. The way to add projects and repositories to the system is using a URL of the form:
<pre>
http://localhost:3001/?project=<i>X</i>&gitId=<i>Y</i>
</pre>
Where X is the project ID and Y is the github ID of the repository.

Adding repositories through the UI is very simple. Just type in the GitHub username and Repository name into the boxes provided then click the button (note: make sure there is a project ID in the URL like above) and it will add it. If you're unsure of what to try to add, use the placeholder entries (make sure to still type them out though).

http://localhost:3001/contacts takes one to the contacts page but to get any data, projectId and email are needed: 
http://localhost:3001/contacts?project<i>X</i>&email=<i>Y</i>

## POSTing back to the user-project-service
Testing of the POST back to the user-project-service was unable to be tested, but it should work in theory. If there are any issues with this part of the module, you can make last-minute modifications to the POST. The POST request is contained in a function at ```./git/src/main/java/monash/edu/git/GitController.java``` lines **275-302**. It currently attempts to POST to the url.
<pre>
http://localhost:3000/user-project-service/save-git
</pre>