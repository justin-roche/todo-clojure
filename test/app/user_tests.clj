(ns app.user-tests
  (:require
   [aprint.core :refer [aprint]]
   [buddy.hashers :as buddy-hashers]
   [app.db :as db]
   [clojure.tools.reader]
   [app.users :as users]
   [cheshire.core :as cheshire]

   [app.test-utils :as test-utils :refer [get-options post-options system-fixture]]
   [clj-http.client :as client]
   [clojure.data.json :as json]
   [app.test-data :as test-data :refer [auth-headers invalid-auth-headers non-admin-auth-headers]]
   [clojure.test :as t :refer [deftest use-fixtures]]
   [mount.core :as mount :refer [start stop]]
   [app.utils :as utils]))

(use-fixtures :each system-fixture)
