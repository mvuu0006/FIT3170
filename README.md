# Student Project Management Dashboard - Git Module
#### Produced by OnlineLearning

## Module Description
This module includes a back-end component created using Spring Boot and a front-end created with React. The back-end component is an interface through which the Student Project Management Dashboard can interact with the GitHub API (with plans to introduce GitLab compatability in the future). The front-end is merely an example of how to interact with the back-end using React. 

As of 12/06/2020, the front-end is configured to run on port 3001 and the back-end is configured to run on port 5001. To view both components together, you should navigate to the directory (all directories listed are relative to the directory of this .md file) ```/git-frontend/``` and run the following command using a Node.js configured command line:
```
npm start
```
For the backend, the Java runtime should be used to execute the main function, located at ```/git/src/main/java/monash/edu/git/GitApplication.java```

## Users
Currently the module does not have any authentication for restricting access based off of the logged-in user. This will be fixed in the next Product Increment. 

## Projects
Projects can be created by making a PUT request and project information can be retrieved by making a GET request to the following URL:
```
localhost:5001/git/project/{projectname}
```
A 404 error will be returned if the project does not exist in the system when a GET request is made. When creating a new project, it is recommended to first make a GET request to see if the project exists, then make a PUT request as a response to a 404 error on the GET request (this is shown in the accompanying front-end). GET requests for this API will return a JSON containing basic project information (git-specific).

## Repositories
In this module, repositories only exist as components of a Project. Due to this, a projects reposotories can be retirieved from the following URL (using a GET request):
```
localhost:5001/git/project/{projectname}/repos
```
This link returns a JSON object containing basic info on all repositories attached to the given Project. Basic info for a specific repository can be gained from the following URL (using a GET request):
```
localhost:5001/git/project/{projectname}/repos/{githubusername}/{reponame}
```
Additionally, the above URL can be used to add a repo to a project by using a PUT request.

### Repository Contributors
The back-end also support requests for finding out the contributors to a given repository via the following URL (using a GET request):
```
localhost:5001/git/project/{projectname}/repos/{githubusername}/{reponame}/contributors
```
This returns a JSON object that contains information on each GitHub user that has contributed to the given repository.

### Repository Commits
Similarly to the contributors, the back-end can also return a list of commits to a given repository via the following URL:
```
localhost:5001/git/project/{projectname/repos/{githubusername}/{reponame}/commits
```
This merely returns a JSON object containing a list of commits (to the master branch only?) and basic information on each commit.