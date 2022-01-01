(ns app.utils
  (:require
   [aprint.core :refer [aprint]]
   [mount.core :refer [start stop]]))

(defn update-res [ctx v]
  (update-in ctx [:response] #(merge % v)))

(defn update-req [ctx v]
  (update-in ctx [:request] #(merge % v)))

(defn with-mount [test]
  (start)
  (test)
  (stop))

(defn log-through
  ([v]
   (aprint v) v)
  ([m v]
   (println m)
   (aprint v) v))

(defn xlog-through
  ([v]
   v)
  ([m v]
   v))

