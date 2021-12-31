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
  (if-let [todos (:result (first (filter-subdocuments "users"
                                                      [{"$match" (select-keys user [:name])}
                                                       {"$unwind" "$todos"}
                                                       {"$match" {"todos.visibility" {"$not" {"$eq" "deleted"}}}}
                                                       {"$group" {:_id "$name" :result {"$push" "$todos"}}}])))]
    {:status 200 :body {:data todos}}
    {:status 200 :body {:data []}}))

(defn get-completion-report [user]
  (if-let [todos (filter-subdocuments "users"
                                      [{"$match" (select-keys user [:name])}
                                       {"$unwind" "$todos"}
                                       {"$match" {"todos.visibility" {"$not" {"$eq" "deleted"}}}}
                                       {"$group" {:_id "$todos.status" :result {mo/$push "$todos"}}}
                                       {"$sort" {"_id" 1}}])]

    {:status 200 :body {:data {:complete (:result (first todos))
                               :incomplete (:result (second todos))}}}
    {:status 200 :body {:data []}}))

(defn get-burn-down-report [user]
  (if-let [events (filter-subdocuments "users"
                                       [{"$match" (select-keys user [:name])}
                                        {"$unwind" "$todos"}
                                        {"$facet"
                                         {:creations [{"$project" {:_id "$todos.id" :type "creation" :eventDate "$todos.createdAt"}}]
                                          :completions [{"$match" {"todos.completedAt" {"$exists" true}}}
                                                        {"$project" {:_id "$todos.id" :type "completion" :eventDate "$todos.completedAt"}}]

                                          :deletions [{"$match" {"todos.deletedAt" {"$exists" true}}}
                                                      {"$project" {:_id "$todos.id" :deletedAt "$todos.deletedAt"}}]}}

                                        {"$project" {:activity {"$setUnion" ["$creations" "$completions"]}}}
                                        {"$unwind" "$activity"}
                                        {"$replaceRoot" {:newRoot "$activity"}}
                                        {"$sort" {"eventDate" 1}}])]
    {:status 200 :body {:data events}}
    {:status 200 :body {:data []}}))
