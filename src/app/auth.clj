(ns app.auth
  (:require
   [app.utils :as utils :refer [log-through xlog-through]]
   [buddy.hashers :as buddy-hashers]
   [buddy.sign.jwt :as jwt]
   [clojure.set :refer [subset?]]
   [io.pedestal.log :as pl]
   [malli.core :as m]
   [app.db :as db]))

(def private-key
  "Used for signing and verifying JWT-tokens In real world you'd read
  this from an environment variable or some other configuration that's
  not included in the source code."
  "kana15")

(defn create-token
  "Creates a signed jwt-token with user data as payload. `valid-seconds` sets the expiration span. `username` is selected because the user id will change between test runs."
  [user & {:keys [valid-seconds] :or {valid-seconds 777200}}] ;; 2 hours
  (let [payload (-> user
                    (select-keys [:username])
                    (assoc :exp (.plusSeconds
                                 (java.time.Instant/now) valid-seconds)))]
    (jwt/sign payload private-key {:alg :hs512})))

(defn unsign-token [token]
  (try (jwt/unsign token private-key {:alg :hs512})
       (catch Exception e (throw e))))

(defn verify-token []
  {:name ::verify-token
   :enter
   (fn [ctx]
     (let [token (get-in ctx [:request :headers "authorization"])
           user-data (log-through (unsign-token token))]
       (utils/update-req ctx {:user user-data})))})

(defn login [rq]
  (let [username (get-in rq [:body-params :username])
        password (get-in rq [:body-params :password])
        user (db/find-document "users" {:name username})]
    (if (and user (buddy-hashers/check password (:password user)))
      {:status 200
       :token (create-token user)
       :body
       {:message "Authorization success"}}
      {:status 401})))

(comment (app.utils/with-mount (fn []
                                 (log-through "user token" (create-token {:username "A"})))))





