(ns app.todos
  (:require
   [app.db :refer [find-by-id find-document get-collection insert-multiple
                   update-by-id insert]]
   [cheshire.core :refer :all]
   [monger.result :refer :all]
   [app.utils :as utils]))

(defn create-todo [todo user]
  (if (insert "todos" (merge todo {:username (:username user)}))
    {:status 200}
    {:status 409}))

(defn delete-todo [todo-id]
  ;; (if (insert "todos" (merge todo {:user user})) {:status 200} {:status 409})
  )

(defn get-todos [req user]
  (if-let [todos (get-collection "todos")]
    {:status 200 :body {:data todos}}
    {:status 409}))
;; (defn update-user [body id]
;;   (if-let [user (update-by-id "users" id body)]
;;     (if-let [r (xlog-through (str "found with id " id) (find-by-id "users" id))]
;;       (do
;;         {:status 200 :body {:data r}}))
;;     {:status 409}))

;; (comment (app.utils/with-mount (fn [])))


