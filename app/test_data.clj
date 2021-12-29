(ns app.test-data
  (:require
   [buddy.hashers :as buddy-hashers]
   [clojure.tools.reader]))

(def test-users [{:name "B" :password (buddy-hashers/encrypt "koira12")
                  :age 19
                  :roles    ["admin" "user"]}
                 {:name "A" :password (buddy-hashers/encrypt "kissa13")
                  :age 19
                  :roles    ["user"]}])

(def auth-headers {:headers {"Authorization" "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6MSwicm9sZXMiOlsiYWRtaW4iLCJ1c2VyIl0sImV4cCI6MTY0MDQwNjQ3MX0.K_FUbrjpPd2IJaOYnqmG8DtPe43omUueAoupCLC_c7OhCZ68jwm6DgaAlJCN1tPViFP9_-FOeZ6ofR8FxJAYMg"}})

(def invalid-auth-headers {:headers {"Authorization" "this is an invalid header"}})

(def non-admin-auth-headers {:headers {"Authorization" "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6Miwicm9sZXMiOlsidXNlciJdLCJleHAiOjE2NDA0MDcxMDF9.hElFSsbzZN_Q0hdvUXFsTsWHEhCTr75SBwiMBKQTTEujXIN91cfTQXk2nA2o553pZtPw0371H3X85pSj5S_jTg"}})
