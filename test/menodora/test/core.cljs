(ns menodora.test.core
  (:require
    [menodora.core :as mc])
  (:use-macros
    [menodora :only (describe it expect)]))

(describe "menodora"
  (it "should pass"
    (expect mc/= 1 1))
  (it "should fail"
    (expect mc/= 1 2)))

(binding [*print-fn* js/print]
  (let [ts (vec (map (fn [[x y]] [x (deref y)]) @mc/tests))]
    (println (pr-str ts))
    (when-not (and
                (= ["should pass" [false]]
                   (get-in ts [0 1 0]))
                (= ["should fail" ["Expected: 1. Actual: 2"]]
                   (get-in ts [0 1 1])))
      (js/quit 1))))

;;. vim: set lispwords+=describe,it,expect:
