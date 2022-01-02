(ns app.users
  (:require
   [app.auth :as auth :refer [create-token]]
   [app.db :refer [find-document insert-document]]))

(def email-regexp #".+\@.+\..+")

(defn login [{:keys [name]}]
  (if-let [user (or (find-document "users" {:name name})
                    (insert-document "users" {:name name}))]
    {:status 200
     :body
     {:token (create-token {:name name})
      :message "Login success"}}))

(defn get-me [req]
  (if-let [user (find-document "users"
                               {:name (get-in req [:user :name])})]
    {:status 200 :body {:data user}}
    {:status 409}))

