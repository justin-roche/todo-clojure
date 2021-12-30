(ns app.auth
  (:require
   [app.utils :as utils :refer [log-through]]
   [buddy.hashers :as buddy-hashers]
   [buddy.sign.jwt :as jwt]
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
           user-data (unsign-token token)]
       (utils/update-req ctx {:user user-data})))})

(defn get-user-from-login [username password]
  (let [user (db/find-document "users" {:name username})]
    (if (and user (buddy-hashers/check password (:password user)))
      user
      nil)))

(comment (app.utils/with-mount (fn []
                                 (log-through "user token" (create-token {:username "A"})))))





