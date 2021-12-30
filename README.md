# Introduction

A Clojure backend for a todo application.

## Usage

Ensure Docker is installed on your machine. Start the database by navigating to the db folder and executing: 

```
bash docker-up.sh

```

Run lein deps.

"Log in" with any email address (only used for identification; no password)

All the following requirements are in the context of the logged-in user:
View list of existing to-do tasks 
Add a new to-do task
Mark a task as complete (or incomplete, if it was already marked complete)
Delete an existing task
View a chart comparing the number of complete vs. incomplete tasks
View a burn-down chart showing the addition and completion/deletion of tasks over time (that is, at every moment a task was added, the chart should step up by one, and every moment a task was completed or deleted, the chart should step down by one)


Do deleted items show in the list of existing todos?

Should login also create a user, or should it be restricted to users already in the database?

To mark a todo as complete/incomplete, should there be a single endpoint that toggles the completion status? Typically for REST I would have a post where the client sends the updated field and value, like todo/{id}. But from the description it sounds more like there's an endpoint todo/{id}/status, and the client posts to that and then the server handles the logic of toggling it.

What should be returned from successfull API operations? Should it be a success message alone, or also the altered document?




## License

MIT
