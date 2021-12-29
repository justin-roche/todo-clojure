(ns app.refresher
  (:require
   [mount.core :as mount :refer [defstate]]
   [clojure.tools.namespace.repl :as rp :refer [set-refresh-dirs refresh]]))

(set-refresh-dirs "src/app" "test/app")

(refresh :after 'app.user/refresh-and-run-tests)
