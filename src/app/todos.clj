(ns app.todos
  (:require
   [app.db :refer [find-documents insert]]
   [app.utils :as utils]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn create-todo [todo user]
  (if (insert "todos" (merge todo (select-keys user [:username])))
    {:status 200}
    {:status 409}))

(defn delete-todo [todo-id]
  ;; (if (insert "todos" (merge todo {:user user})) {:status 200} {:status 409})
  )

(defn get-todos [req user]
  (if-let [todos (find-documents "todos" (select-keys user [:username]))]
    {:status 200 :body {:data todos}}
    {:status 409}))

(comment (app.utils/with-mount (fn [])))


