(ns app.errors
  (:require
   [clojure.test :refer :all]
   [io.pedestal.log :as pl]
   [io.pedestal.test :refer :all]
   [io.pedestal.interceptor.error :as pe]
   [aprint.core :refer [aprint]]
   [malli.core :as m]
   [malli.error :as me]))

(def error-messages {401 "Unauthorized"})

(defn update-res [ctx v]
  (update-in ctx [:response] #(merge % v)))

(defn handle-unknown-error [e]
  (pl/error "unknown error" e)
  {:status 501})

(def errors-handler
  "( into ) is necessary to convert to reitit interceptor protocol"
  (into {} (pe/error-dispatch [ctx e]

                              [{:exception-type :malli.core/invalid-schema}]
                              (update-res ctx {:status 500 :body "Invalid schema, wow"})

                              [{:interceptor :app.auth/verify-token}]
                              (update-res ctx {:status 401 :body "Invalid token"})

                              [{:interceptor :app.auth/verify-role}]
                              (update-res ctx {:status 401 :body "Incorrect role"})

                              ;; fallback cases
                              [{:exception-type :clojure.lang.ExceptionInfo}]
                              (update-res ctx {:status 500 :body "error"})

                              :else
                              (aprint "unhandled exception!!!" e))))

(defn v [schema input]
  (let [r (m/validate schema input)]
    (if (not r)
      (aprint "validation error:" (me/humanize (m/explain schema input)));   (do
      nil)))

(defn throw [status]
  (throw (new Exception)))
