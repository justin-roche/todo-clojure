(ns app.todos
  (:require
   [app.db :refer [insert-subdocument filter-subdocuments update-subdocument]]
   [app.utils :as utils]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn create-todo [todo user]
  (let [name (get-in user [:name])
        todo (merge {:status "incomplete" :visibility "visible"} todo)]
    (if (insert-subdocument "users" {:name name} "todos" todo)
      {:status 200}
      {:status 409})))

(defn update-todo [todo-id data user]
  (let [name (get-in user [:name])]
    (if (update-subdocument "users" {:name name "todos.id" todo-id}
                            {"todos.$.status" (:status data)})
      {:status 200}
      {:status 409})))

(defn delete-todo [todo-id user]
  (let [name (get-in user [:name])]
    (if (update-subdocument "users" {:name name "todos.id" todo-id} {"todos.$.visibility" "deleted"})
      {:status 200}
      {:status 409})))

(defn get-todos [user]
  (if-let [todos (filter-subdocuments (:name user))]
    {:status 200 :body {:data todos}}
    {:status 200 :body {:data []}}))



