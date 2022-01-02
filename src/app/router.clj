(ns app.router
  (:require
   [app.auth :as auth]
   [app.errors :as errors]
   [app.todos :as todos]
   [app.users :as users :refer [email-regexp]]
   [muuntaja.core :as m]
   [reitit.coercion.malli]
   [reitit.http.coercion :as coercion]
   [reitit.http.interceptors.exception :as exception]
   [reitit.http.interceptors.muuntaja :as muuntaja]))

(def route-data {:data {:coercion reitit.coercion.malli/coercion
                        :muuntaja m/instance
                        :interceptors [(muuntaja/format-negotiate-interceptor)
                                       (muuntaja/format-response-interceptor)
                                       (exception/exception-interceptor)
                                       (muuntaja/format-request-interceptor)
                                       (coercion/coerce-request-interceptor)]}})

(def routes [""
             {:interceptors [errors/errors-handler (auth/verify-token ["/login"])]}
             ["/login"
              {:post
               {:handler #(users/login (:body-params %1))
                :parameters {:body [:map
                                    [:name [:re email-regexp]]]}}}]
             ["/me"
              {:get {:handler users/get-me}}]
             ["/todos"
              {:get {:handler #(todos/get-todos (:user %1))}}]
             ["/todos/completion-report"
              {:get {:handler #(todos/get-completion-report (:user %1))}}]
             ["/todos/burn-down-report"
              {:get {:handler #(todos/get-burn-down-report (:user %1))}}]
             ["/todo"
              {:post {:handler #(todos/create-todo (:user %1) (:body-params %1))
                      :parameters {:body [:map
                                          [:name string?]]}}}]
             ["/todo/{id}"
              {:delete {:handler #(todos/delete-todo (:user %1) (:id (:path-params %1)))}}]
             ["/todo/{id}/status"
              {:post {:handler #(todos/change-todo-status (:user %1) (:id (:path-params %1)))}}]])

