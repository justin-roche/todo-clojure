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
                 [com.taoensso/timbre "5.1.2"] ; See CHANGELOG for details
                 [vvvvalvalval/scope-capture "0.3.2"]
                 [com.taoensso/truss "1.6.0"]
                 [aprint "0.1.3"]
                 [clj-http "3.12.3"]
                 ;; http://clojuremongodb.info/articles/misc.html
                 [org.slf4j/slf4j-nop "1.7.12"]
                 ;; [com.stuartsierra/component "1.0.0"]
                 [mount "0.1.16"]
                 [io.pedestal/pedestal.service       "0.5.9"]
                 [io.pedestal/pedestal.service-tools "0.5.9"] ;; Only needed for ns-watching; WAR tooling
                 [io.pedestal/pedestal.jetty         "0.5.9"]
                 [io.pedestal/pedestal.log         "0.5.9"] ;; Logging and runtime metrics
                 [io.pedestal/pedestal.interceptor "0.5.9"] ;; The Interceptor chain and the Interceptor API
                 [io.pedestal/pedestal.route       "0.5.9"] ;; Efficient routing algorithms and data structures
                 [metosin/malli "0.7.3"]
                 [metosin/reitit "0.5.15"]
                 [metosin/reitit-pedestal "0.5.15"]
                 [buddy "2.0.0"]
                 [com.novemberain/monger "3.1.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [com.jakemccrary/lein-test-refresh "0.25.0"]
                 ;; [com.mchange/c3p0 "0.9.5.2"]
                 ]
  :repl-options {:init-ns app.user}
  :test-refresh {;; Specifies a command to run on test
                 ;; failure/success. Short message is passed as the
                 ;; last argument to the command.
                 ;; Defaults to no command.
                 :notify-command ["-title" "Tests" "-message"]

                 ;; set to true to send notifications to growl
                 ;; Defaults to false.
                 :growl false

                 ;; only growl and use the notify command if there are
                 ;; failures.
                 ;; Defaults to true.
                 :notify-on-success false

                 ;; Stop clojure.test from printing
                 ;; "Testing namespace.being.tested". Very useful on
                 ;; codebases with many test namespaces.
                 ;; Defaults to false.
                 :quiet true

                 ;; If this is specified then only tests in namespaces
                 ;; that were just reloaded by tools.namespace
                 ;; (namespaces where a change was detected in it or a
                 ;; dependent namespace) are run. This can also be
                 ;; passed as a command line option: lein test-refresh :changes-only.
                 :changes-only true

                 ;; If specified, binds value to clojure.test/*stack-trace-depth*
                 :stack-trace-depth nil

                 ;; specifiy a custom clojure.test report method
                 ;; Specify the namespace and multimethod that will handle reporting
                 ;; from test-refresh.  The namespace must be available to the project dependencies.
                 ;; Defaults to no custom reporter
                 ;; :report  myreport.namespace/my-report

                 ;; If set to a truthy value, then lein test-refresh
                 ;; will only run your tests once. Also supported as a
                 ;; command line option. Reasoning for feature can be
                 ;; found in PR:
                 ;; https://github.com/jakemcc/lein-test-refresh/pull/48
                 :run-once false

                 ;; If given, watch for changes only in the given
                 ;; folders. By default, watches for changes on entire
                 ;; classpath.
                 :watch-dirs ["src" "test"]

                 ;; If given, only refresh code in the given
                 ;; directories. By default every directory on the
                 ;; classpath is refreshed. Value is passed through to clojure.tools.namespace.repl/set-refresh-dirs
                 ;; https://github.com/clojure/tools.namespace/blob/f3f5b29689c2bda53b4977cf97f5588f82c9bd00/src/main/clojure/clojure/tools/namespace/repl.clj#L164
                 :refresh-dirs ["src" "test"]

;; Use this flag to specify your own flag to add to
                 ;; cause test-refresh to focus. Intended to be used
                 ;; to let you specify a shorter flag than the default
                 ;; :test-refresh/focus.
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
