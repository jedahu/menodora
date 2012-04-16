(ns menodora.test
  (:require
    [menodora.core :as mc]
    [menodora.runners :as mr]
    [menodora.test.core :as tc]
    [menodora.test.runners :as tr]
    [menodora.test.macros :as tm]))

(defn ^:export -run-tests
  [finished print-fn]
  (binding [*print-fn* print-fn]
    (mc/run-tests
      (mr/console)
      [tc/core-tests
       tr/console-runner-tests
       tm/macro-tests]
      :finished finished
      :catch? false)))

(defn ^:export -run-rhino
  []
  (-run-tests identity #(. java.lang.System/out print %)))
