(ns app.todos
  (:require
   [app.db :refer [find-by-id find-document get-collection insert-multiple
                   update-by-id insert]]
   [app.utils :refer [log-through xlog-through]]
   [cheshire.core :refer :all]
   [monger.result :refer :all]))

(defn add-todo-for-user [req]
  (if (insert "todos" {}) {:status 200} {:status 409}))

(defn get-todos-for-user [req]
  ;; (if-let [users (get-collection "todos")]
  ;;   {:status 200 :body {:data users}}
  ;;   {:status 409})
  )
;; (defn update-user [body id]
;;   (if-let [user (update-by-id "users" id body)]
;;     (if-let [r (xlog-through (str "found with id " id) (find-by-id "users" id))]
;;       (do
;;         {:status 200 :body {:data r}}))
;;     {:status 409}))

(comment (app.utils/with-mount (fn [])))


