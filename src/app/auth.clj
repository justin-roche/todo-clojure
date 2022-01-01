(ns app.auth
  (:require
   [app.config :refer [config-map]]
   [app.utils :as utils :refer [log-through]]
   [buddy.hashers :as buddy-hashers]
   [buddy.sign.jwt :as jwt]
   [clojure.string :as str]
   [app.db :as db]
   [taoensso.truss :as truss :refer [have]]
   [app.config :as config]))

(defn create-token
  "Creates a signed jwt-token with user data as payload. `valid-seconds` sets the expiration span. `name` is selected because the user id will change between test runs."
  [user & {:keys [valid-seconds] :or {valid-seconds 100000}}] ;; 2 hours
  (let [auth-key (get-in config-map [:auth :auth-key])
        payload (-> user
                    (select-keys [:name])
                    (assoc :exp (.plusSeconds
                                 (java.time.Instant/now) valid-seconds)))]
    (have string? auth-key)
    (jwt/sign payload auth-key {:alg :hs512})))

(defn unsign-token [token]
  (try (jwt/unsign (str/replace token "Bearer " "")
                   (get-in config-map [:auth :auth-key]) {:alg :hs512})
       (catch Exception e (throw e))))

(defn verify-token [exclusions]
  {:name ::verify-token
   :enter
   (fn [ctx]
     (if (some #(= (:uri (:request ctx)) %) exclusions)
       ctx
       (let [token (have string?
                         (get-in ctx [:request :headers "authorization"]))
             user-data (unsign-token token)]
         (utils/update-req ctx {:user user-data}))))})
