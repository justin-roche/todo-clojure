# Introduction

A Clojure backend for a todo application.

## Usage

Ensure Docker is installed and running on your machine. Start the database by navigating to the db folder and executing: 

```
bash docker-up.sh

```

Create a profiles.clj file at the root level of the project, and add the following to it, including your own value for auth-key:

```
{:profiles/dev  {:env {:auth-key ***KEY***}}}

```

Run lein deps followed by lein run at the root level of the project.

## License

MIT
