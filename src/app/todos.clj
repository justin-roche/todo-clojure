(ns app.todos
  (:require
   [app.db :refer [insert-subdocument filter-subdocuments update-subdocument find-subdocument]]
   [app.utils :as utils]
   [monger.operators :as mo :refer :all]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn create-todo [todo user]
  (let [name (get-in user [:name])
        todo (merge {:status "incomplete"
                     :visibility "visible"
                     :createdAt (new java.util.Date)} todo)]
    (if (insert-subdocument "users"
                            {:name name}
                            "todos" todo)
      {:status 200}
      {:status 409})))

(defn _get-todo [username todo-id]
  (first (:todos (find-subdocument
                  "users"
                  {"todos" {"$elemMatch"
                            {"id" todo-id}}}
                  {"name" 1 "todos.$" 1}))))

(defn change-todo-status [todo-id data user]
  (let [name (get-in user [:name])
        todo (_get-todo name todo-id)
        complete (= "complete" (:status todo))]
    (if complete
      (if (update-subdocument "users"
                              {:name name "todos.id" todo-id}
                              {"todos.$.status" "incomplete"
                               "todos.$.completedAt" nil})
        {:status 200}
        {:status 409})
      (if (update-subdocument "users"
                              {:name name "todos.id" todo-id}
                              {"todos.$.status" "complete"
                               "todos.$.completedAt" (new java.util.Date)})
        {:status 200}
        {:status 409}))))

(defn delete-todo [todo-id user]
  (let [name (get-in user [:name])]
    (if (update-subdocument "users"
                            {:name name "todos.id" todo-id}
                            {"todos.$.visibility" "deleted"
                             "todos.$.deletedAt" (new java.util.Date)})
      {:status 200}
      {:status 409})))

(defn get-todos [user]
  (if-let [todos (filter-subdocuments "users"
                                      [{mo/$match (select-keys user [:name])}
                                       {mo/$unwind "$todos"}
                                       {mo/$match {"todos.visibility" {"$not" {"$eq" "deleted"}}}}
                                       {mo/$group {:_id "$name" :result {mo/$push "$todos"}}}])]
    {:status 200 :body {:data todos}}
    {:status 200 :body {:data []}}))



