(ns app.router
  (:require
   [io.pedestal.http.route :as route]
   [app.utils :refer [with-mount log-through]]
   [app.auth :as auth]
   [app.db :as db]
   [app.errors :as errors]
   [app.users :as users]
   [io.pedestal.interceptor :as i]
   [reitit.http.coercion :as coercion]
   [reitit.http.interceptors.parameters :as parameters]
   [reitit.http.interceptors.exception :as exception]
   [reitit.http.interceptors.multipart :as multipart]
   [muuntaja.core :as m]
   [reitit.coercion.malli]
   [reitit.http.interceptors.muuntaja :as muuntaja]
   [io.pedestal.http.body-params :as bp]
   [aprint.core :refer [aprint]]
   [app.todos :as todos]))

(def body-parser (bp/body-params (bp/default-parser-map)))

(def route-data {:data {:coercion reitit.coercion.malli/coercion
                        :muuntaja m/instance
                        :interceptors [(muuntaja/format-request-interceptor)
                                       (parameters/parameters-interceptor)
                                       (muuntaja/format-negotiate-interceptor)
                                       (muuntaja/format-response-interceptor)
                                       (exception/exception-interceptor)
                                       (muuntaja/format-request-interceptor)
                                       (coercion/coerce-response-interceptor)
                                       (coercion/coerce-request-interceptor)
                                       (multipart/multipart-interceptor)]}})

(def routes [""
             {:interceptors [errors/errors-handler]}
             ["/login"
              {:post
               {:handler #(users/login (:body-params %1))
                :parameters {:body [:map
                                    [:name string?]]}}}]
             ["/me"
              {:get
               {:handler users/get-me
                :interceptors [(auth/verify-token)]}}]
             ["/todos"
              {:get {:handler #(todos/get-todos (:user %1))
                     :interceptors [(auth/verify-token)]}}]
             ["/todos/completion-report"
              {:get {:handler #(todos/get-completion-report (:user %1))
                     :interceptors [(auth/verify-token)]}}]
             ["/todos/burn-down-report"
              {:get {:handler #(todos/get-burn-down-report (:user %1))
                     :interceptors [(auth/verify-token)]}}]
             ["/todo"
              {:post {:handler #(todos/create-todo (:body-params %1) (:user %1))
                      :interceptors [(auth/verify-token)]}}]
             ["/todo/{id}"
              {:delete {:handler #(todos/delete-todo (:id (:path-params %1)) (:user %1))
                        :interceptors [(auth/verify-token)]}}]
             ["/todo/{id}/status"
              {:post {:handler #(todos/change-todo-status (:id (:path-params %1)) (:body-params %1) (:user %1))
                      :interceptors [(auth/verify-token)]}}]])

