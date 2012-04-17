(ns menodora.test
  (:require
    [menodora.core :as mc]
    ;[menodora.test.core :as tc]
    ;[menodora.test.runners :as tr]
    [menodora.test.macros :as tm])
  (:use
    [menodora.runner.console :only (console-runner)]))

(defn ^:export -run-tests
  [finished print-fn]
  (set! *print-fn* print-fn)
  (mc/run-suites
    (console-runner :show :all)
    [;tc/core-tests
     ;tr/console-runner-tests
     tm/macro-tests]
    :finish finished
    :catch? false))

(defn ^:export -run-rhino
  []
  (-run-tests identity #(. java.lang.System/out print %)))
