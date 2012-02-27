(ns menodora.core
  (:refer-clojure
    :exclude (=))
  (:require
    [cljs.core :as cc]))

(def tests
  (atom []))

(def ^:dynamic *it-results*
  (atom []))

(def ^:dynamic *describe-results*
  (atom []))

(def =
  {:test cc/=
   :message #(str "Expected: " %1
                  ". Actual: " %2)})
