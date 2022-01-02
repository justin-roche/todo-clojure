(ns app.todos
  (:require
   [app.db :refer [insert-subdocument aggregate-subdocuments update-subdocument find-subdocument]]
   [app.utils :as utils]
   [monger.operators :as mo :refer :all]
   [monger.result :refer :all]))

(defn _get-todo [username todo-id]
  (first (:todos (find-subdocument
                  "users"
                  {"name" username
                   "todos" {"$elemMatch"
                            {"id" todo-id}}}
                  {"name" 1 "todos.$" 1}))))

(defn create-todo [user todo]
  (let [username (get-in user [:name])
        todo (merge {:status "incomplete"
                     :visibility "visible"
                     :createdAt (new java.util.Date)} todo)]
    (if-let [inserted-id (insert-subdocument "users"
                                             {:name username}
                                             "todos" todo)]
      {:status 200 :body {:data (_get-todo username inserted-id)}}
      {:status 409})))

(defn change-todo-status [user todo-id]
  (let [name (get-in user [:name])
        todo (_get-todo name todo-id)
        completeNow (= "incomplete" (:status todo))
        newStatus (if completeNow "complete" "incomplete")
        completedAt (if completeNow (new java.util.Date) nil)]
    (if-let [updatedTodo (and (update-subdocument "users"
                                                  {:name name "todos.id" todo-id}
                                                  {"todos.$.status" newStatus
                                                   "todos.$.completedAt" completedAt})
                              (_get-todo name todo-id))]
      {:status 200 :body {:data updatedTodo}}
      {:status 409})))

(defn delete-todo [user todo-id]
  (let [name (get-in user [:name])]
    (if (update-subdocument "users"
                            {:name name "todos.id" todo-id}
                            {"todos.$.visibility" "deleted"
                             "todos.$.deletedAt" (new java.util.Date)})
      {:status 200}
      {:status 409})))

(defn get-todos [user]
  (let [pipeline [{"$match" (select-keys user [:name])}
                  {"$unwind" "$todos"}
                  {"$match" {"todos.visibility" {"$not" {"$eq" "deleted"}}}}
                  {"$group" {:_id "$name" :result {"$push" "$todos"}}}]]
    (if-let [todos (:result (first (aggregate-subdocuments "users" pipeline)))]
      {:status 200 :body {:data todos}}
      {:status 200 :body {:data []}})))

(defn get-completion-report [user]
  (let [pipeline [{"$match" (select-keys user [:name])}
                  {"$unwind" "$todos"}
                  {"$match" {"todos.visibility" {"$not" {"$eq" "deleted"}}}}
                  {"$group" {:_id "$todos.status" :result {"$push" "$todos"}}}
                  {"$sort" {"_id" 1}}]]
    (if-let [todos (aggregate-subdocuments "users" pipeline)]
      {:status 200 :body {:data {:complete (or (:result (first todos)) [])
                                 :incomplete (or (:result (second todos)) [])}}}
      {:status 200 :body {:data []}})))

(defn get-burn-down-report [user]
  (let [pipeline [{"$match" (select-keys user [:name])}
                  {"$unwind" "$todos"}
                  {"$facet"
                   {:creations [{"$project" {:_id "$todos.id" :type "creation" :eventDate "$todos.createdAt"}}]
                    :completions [{"$match" {"todos.completedAt" {"$exists" true}}}
                                  {"$project" {:_id "$todos.id" :type "completion" :eventDate "$todos.completedAt"}}]
                    :deletions [{"$match" {"todos.deletedAt" {"$exists" true}}}
                                {"$project" {:_id "$todos.id" :type "deletion" :eventDate "$todos.deletedAt"}}]}}
                  {"$project" {:activity {"$setUnion" ["$creations" "$completions" "$deletions"]}}}
                  {"$unwind" "$activity"}
                  {"$replaceRoot" {:newRoot "$activity"}}
                  {"$sort" {"eventDate" 1}}]]
    (if-let [events (aggregate-subdocuments "users" pipeline)]
      {:status 200 :body {:data events}}
      {:status 200 :body {:data []}})))
