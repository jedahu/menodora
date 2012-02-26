(ns menodora.core)

(def tests
  (atom []))

(def ^:dynamic *it-results*
  (atom []))

(def =
  {:test =
   :message #(str "Expected: " %1
                  ". Actual: " %2)})
