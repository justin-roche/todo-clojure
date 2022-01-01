(ns app.core
  (:require
   [app.auth :as auth]
   [app.db :as db]
   [app.test-data :as test-data]
   [app.users :as users]
   [app.server :as server]
   [clojure.tools.reader]
   [mount.core :as mount :refer [start stop]]))

(defn main []
  (start))



