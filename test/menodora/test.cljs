(ns menodora.test
  (:require
    [menodora.core :as mc]
    [menodora.runners :as mr]
    [menodora.test.core :as tc]
    [menodora.test.runners :as tr]))

(defn ^:export -run-tests
  [finished print-fn]
  (mc/run-tests
    (fn [suites]
      (js/print "runner...")
      (js/print (pr-str (seq? suites)))
      (doseq [suite suites
              descr suite
              [text should] descr]
        (js/print "<" "foo" ">")
        (js/print)
        (js/print)))
    ;(mr/console)
    [tc/core-tests tr/console-runner-tests]
    :finished finished
    :print-fn print-fn))
