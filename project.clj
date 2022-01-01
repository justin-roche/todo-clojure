(defproject server "0.1.0-SNAPSHOT"
  :description "todo app"
  :url ""
  :license {:name "MIT License"}
  :main app.core/main
  :plugins [[lein-environ "1.2.0"]]
  :dependencies [[buddy "2.0.0"]
                 [cheshire "5.10.1"]
                 [com.novemberain/monger "3.1.0"]
                 [com.taoensso/truss "1.6.0"]
                 [environ "1.2.0"]
                 [io.pedestal/pedestal.interceptor "0.5.9"]
                 [io.pedestal/pedestal.jetty         "0.5.9"]
                 [io.pedestal/pedestal.log         "0.5.9"]
                 [io.pedestal/pedestal.route       "0.5.9"]
                 [io.pedestal/pedestal.service       "0.5.9"]
                 [io.pedestal/pedestal.service-tools "0.5.9"]
                 [metosin/malli "0.7.3"]
                 [metosin/reitit "0.5.15"]
                 [metosin/reitit-pedestal "0.5.15"]
                 [mount "0.1.16"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [org.clojure/tools.reader "1.3.6"]]
  :repl-options {:init-ns app.user}
  :profiles {:dev [:profiles/dev :project/dev]
             :test [:profiles/dev :project/dev]
             :profiles/dev  {}
             :project/dev {:dependencies [[aprint "0.1.3"]
                                          [clj-http "3.12.3"]
                                          [org.clojure/tools.namespace "1.2.0"]
                                          [org.slf4j/slf4j-nop "1.7.12"]
                                          [vvvvalvalval/scope-capture "0.3.2"]]
                           :env {:db-host "localhost"
                                 :db-port 27017
                                 :http-port 8890
                                 :db "mongo-test"
                                 :cred-user "root"
                                 :cred-password "rootpassword"
                                 :cred-db "admin"}}})
