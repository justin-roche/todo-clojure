# Introduction

A Clojure backend for a todo application.

## Usage

Ensure Docker is installed on your machine. Start the database by navigating to the db folder and executing: 

```
bash docker-up.sh

```

Create a profiles.clj file at the root level of the project, and add the following:

```
{:profiles/dev  {:env {:auth-key ***KEY***}}}

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



## License

MIT
