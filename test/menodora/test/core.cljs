(ns menodora.test.core
  (:require
    [menodora.core :as mc])
  #_(:use-macros
    [menodora :only (describe it expect)]))

(def tests (atom []))

(binding [mc/*tests* tests]
  (mc/describe "menodora"
    (fn []
      (mc/should "pass"
        #(mc/expect mc/= 1 1))
      (mc/should "fail"
        #(mc/expect mc/= 1 2)))))

(mc/describe "menodora tests"
  (fn []
    (let [ts (vec (map (fn [[x y]] [x (deref y)]) @tests))]
      (mc/should "pass"
        #(mc/expect mc/=
           ["pass" [false]]
           (get-in ts [0 1 0])))
      (mc/should "fail"
        #(mc/expect mc/=
           ["fail" ["Expected: 1. Actual: 2"]]
           (get-in ts [0 1 1]))))))

(defn ^:export -run-tests
  [finished print-fn]
  (mc/run-tests (mc/console-runner)
                :finished finished
                :print-fn print-fn))

;;. vim: set lispwords+=describe,should,expect:
