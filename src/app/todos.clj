(ns app.todos
  (:require
   [app.db :refer [find-documents insert-subdocument filter-subdocuments]]
   [app.utils :as utils]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn create-todo [todo user]
  (let [username (get-in user [:username])
        todo (merge {:status "incomplete" :visibility "visible"} todo)]
    (if (insert-subdocument "users" {:name username} "todos" todo)
      {:status 200}
      {:status 409})))

(defn delete-todo [todo-id]
  ;; (if (insert "todos" (merge todo {:user user})) {:status 200} {:status 409})
  )

(defn get-todos [req user]
  (if-let [todos
           (filter-subdocuments (:username user))]
    {:status 200 :body {:data todos}}
    {:status 409}))

(comment (app.utils/with-mount (fn [])))


