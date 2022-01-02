(ns app.auth
  (:require
   [app.config :refer [config-map]]
   [app.utils :as utils]
   [buddy.hashers :as buddy-hashers]
   [buddy.sign.jwt :as jwt]
   [clojure.string :as str]
   [app.db :as db]
   [taoensso.truss :as truss :refer [have]]
   [app.config :as config]
   [app.user :as user]))

(defn create-token
  "Creates a signed jwt-token with user data as payload. `valid-seconds` sets the expiration span. `name` is selected because the user id will change between test runs."
  [user]
  (let [auth-key (get-in config-map [:auth :auth-key])
        payload (-> user
                    (select-keys [:name])
                    (assoc :exp (.plusSeconds
                                 (java.time.Instant/now) 1000000)))]
    (have string? auth-key)
    (jwt/sign payload auth-key {:alg :hs512})))

(defn unsign-token "Returns user data saved in token"
  [token]
  (jwt/unsign (str/replace token "Bearer " "")
              (get-in config-map [:auth :auth-key]) {:alg :hs512}))

(defn verify-token
  "Adds user data from token to request. `exclusions` is a list of routes that do not need authenticated."
  [exclusions]
  {:name ::verify-token
   :enter
   (fn [ctx]
     (if (some #(= (:uri (:request ctx)) %) exclusions)
       ctx
       (let [token (have string?
                         (get-in ctx [:request :headers "authorization"]))
             token-user (unsign-token token)
             db-user (db/find-document "users" {:name (:name token-user)})]
         (if db-user (utils/update-req ctx {:user db-user})
             (throw (new Error))))))})
