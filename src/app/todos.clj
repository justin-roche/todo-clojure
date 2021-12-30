(ns app.todos
  (:require
   [app.db :refer [insert-subdocument filter-subdocuments update-subdocument]]
   [app.utils :as utils]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn create-todo [todo user]
  (let [username (get-in user [:username])
        todo (merge {:status "incomplete" :visibility "visible"} todo)]
    (if (insert-subdocument "users" {:name username} "todos" todo)
      {:status 200}
      {:status 409})))

(defn update-todo [todo-id user]
  (if (update-subdocument "users" {:name (get-in user [:username]) "todos.id" todo-id} {"todos.$.status" "completed"})
    {:status 200}
    {:status 409}))

(defn delete-todo [todo-id user]
  (if (update-subdocument "users" {:name "A" "todos.id" todo-id} {"todos.$.visibility" "deleted"})
    {:status 200}
    {:status 409}))

(defn get-todos [req user]
  (if-let [todos (filter-subdocuments (:username user))]
    {:status 200 :body {:data todos}}
    {:status 200 :body {:data []}}))



