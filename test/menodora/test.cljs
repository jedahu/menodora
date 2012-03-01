(ns menodora.test
  (:require
    [menodora.core :as mc]
    [menodora.runners :as mr]
    [menodora.test.core :as tc]
    [menodora.test.runners :as tr]))

(defn ^:export -run-tests
  [finished print-fn]
  (mc/run-tests
    (mr/console)
    [tc/core-tests tr/console-runner-tests]
    :finished finished
    :print-fn print-fn
    :catch? false))
