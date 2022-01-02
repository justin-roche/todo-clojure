(ns app.users
  (:require
   [app.auth :as auth :refer [create-token]]
   [app.db :refer [find-document insert-document]]
   [app.user :as user]))

(def email-regexp #".+\@.+\..+")

(defn verify-login [name]
  (if-let [user (find-document "users" {:name name})]
    user
    nil))

(defn login [{:keys [name]}]
  (if-let [user (or (verify-login name)
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

