(ns app.errors
  (:require
   [app.utils :as utils :refer [update-res]]
   [aprint.core :refer [aprint]]
   [io.pedestal.interceptor.error :as pe]))

(def errors-handler
  "( into ) is necessary to convert to reitit interceptor protocol"
  (into {} (pe/error-dispatch [ctx e]

                              [{:exception-type :malli.core/invalid-schema}]
                              (update-res ctx {:status 500 :body "Invalid request"})

                              [{:interceptor :app.auth/verify-token}]
                              (do (println "error verifying token")
                                  (update-res ctx {:status 401 :body "Invalid token"}))

                              [{:exception-type :clojure.lang.ExceptionInfo}]
                              (update-res ctx {:status 500 :body "error"})

                              :else
                              (aprint "unhandled exception" e))))

