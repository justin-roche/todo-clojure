(ns app.user
  (:require
   [clojure.test :as t]
   [mount.core :as mount :refer [defstate start stop]]
   ;; [app.server :as s]
;;    [sc.api]
;;    [sc.api.logging]
   [taoensso.timbre :as log]))

(defn refresh-and-run-tests []
  (stop)
  (t/run-tests 'app.test-utils)
  (t/run-tests 'app.user-tests)
  (t/run-tests 'app.auth-tests))

;; (defn log-spy-cs-with-timbre
;;   [cs-data]
;;   (log/info "At Code Site" (:sc.cs/id cs-data)
;;             "will save scope with locals" (:sc.cs/local-names cs-data)
;;             (str "(" (:sc.cs/file cs-data) "." (:sc.cs/line cs-data) ":" (:sc.cs/column cs-data) ")")))

;; (sc.api.logging/register-cs-logger
;;  ::log-spy-cs-with-timbre
;;  #(log-spy-cs-with-timbre %))

;; (defn log-spy-ep-with-timbre
;;   [ep-data]
;;   (let [cs-data (:sc.ep/code-site ep-data)]
;;     (log/info "At Execution Point"
;;               [(:sc.ep/id ep-data) (:sc.cs/id cs-data)]
;;               (:sc.cs/expr cs-data) "=>" (:sc.ep/value ep-data))))

;; (def my-spy-opts
;;   `{:sc/spy-cs-logger-id ::log-spy-cs-with-timbre
;;     :sc/spy-ep-post-eval-logger log-spy-ep-with-timbre})

;; (defmacro my-spy
;;   ([] (sc.api/spy-emit my-spy-opts nil &env &form))
;;   ([expr] (sc.api/spy-emit my-spy-opts expr &env &form))
;;   ([opts expr] (sc.api/spy-emit (merge my-spy-opts opts) expr &env &form)))

;; (println "evaled user")

;; ;; (try
;; ;; (rp/refresh :after 'app.user/refresh-and-run-tests)
;; ;;   (catch Exception e (clojure.pprint/pprint (str "caught exception: " (.toString e)))))
