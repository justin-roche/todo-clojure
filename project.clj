(defproject server "0.1.0-SNAPSHOT"
  :description ""
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main app.core/main
  :plugins [[lein-environ "1.2.0"]]
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.namespace "1.2.0"]
                 [org.clojure/tools.reader "1.3.6"]
                 [cheshire "5.10.1"]
                 [org.clojure/data.json "2.4.0"]
                 [environ "1.2.0"]
                 [com.taoensso/timbre "5.1.2"]
                 [vvvvalvalval/scope-capture "0.3.2"]
                 [com.taoensso/truss "1.6.0"]
                 [aprint "0.1.3"]
                 [clj-http "3.12.3"]
                 ;; http://clojuremongodb.info/articles/misc.html
                 [org.slf4j/slf4j-nop "1.7.12"]
                 [mount "0.1.16"]
                 [io.pedestal/pedestal.service       "0.5.9"]
                 [io.pedestal/pedestal.service-tools "0.5.9"]
                 [io.pedestal/pedestal.jetty         "0.5.9"]
                 [io.pedestal/pedestal.log         "0.5.9"]
                 [io.pedestal/pedestal.interceptor "0.5.9"]
                 [io.pedestal/pedestal.route       "0.5.9"]
                 [metosin/malli "0.7.3"]
                 [metosin/reitit "0.5.15"]
                 [metosin/reitit-pedestal "0.5.15"]
                 [buddy "2.0.0"]
                 [com.novemberain/monger "3.1.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [com.jakemccrary/lein-test-refresh "0.25.0"]]
  :repl-options {:init-ns app.user}
  :test-refresh {:notify-command ["-title" "Tests" "-message"]
                 :growl false
                 :notify-on-success false
                 :quiet true
                 :changes-only true
                 :stack-trace-depth nil
                 :run-once false
                 :watch-dirs ["src" "test"]
                 :refresh-dirs ["src" "test"]
                 :focus-flag :test-refresh/focus}
  :profiles {:test {:plugins [[com.jakemccrary/lein-test-refresh "0.24.1"]]
                    :env {:db-host "localhost"
                          :db "mongo-test"
                          :cred-user "root"
                          :cred-password "rootpassword"
                          :cred-db "admin"}}
             :dev {:plugins [[com.jakemccrary/lein-test-refresh "0.24.1"]]
                   :env {:db-host "localhost"
                         :db "mongo-test"
                         :cred-user "root"
                         :cred-password "rootpassword"
                         :cred-db "admin"}}})
