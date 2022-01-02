(ns app.errors
  (:require
   [app.utils :as utils :refer [update-res]]
   [aprint.core :refer [aprint]]
   [io.pedestal.interceptor.error :as pe]))

(def errors-handler
  "( into ) is necessary to convert to reitit interceptor protocol"
  (into {} (pe/error-dispatch [ctx e]

                              [{:exception-type :malli.core/invalid-schema}]
                              (update-res ctx {:status 500 :body "Invalid schema"})

                              [{:interceptor :app.auth/verify-token}]
                              (do (aprint e) (update-res ctx {:status 401 :body "Invalid token"}))

                              [{:interceptor :app.auth/verify-role}]
                              (update-res ctx {:status 401 :body "Incorrect role"})

                              [{:exception-type :clojure.lang.ExceptionInfo}]
                              (update-res ctx {:status 500 :body "error"})

                              :else
                              (aprint "unhandled exception!!!" e))))

