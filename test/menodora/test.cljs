(ns menodora.test
  (:require
    [menodora.core :as mc]
    [menodora.test.core :as tc]
    [menodora.test.runners :as tr]
    [menodora.test.macros :as tm])
  (:use
    [menodora.runner.console :only (console-runner)]))

(defn ^:export -run-tests
  [finished print-fn]
  (set! *print-fn* print-fn)
  (mc/run-tests
    console-runner
    [;tc/core-tests
     ;tr/console-runner-tests
     tm/macro-tests]
    :finish finished
    :catch? false))

(defn ^:export -run-rhino
  []
  (-run-tests (fn [x] (println "foo" x) "bar") #(. java.lang.System/out print %)))
