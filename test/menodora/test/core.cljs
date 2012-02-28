(ns menodora.test.core
  (:require
    [menodora.core :as mc]
    [menodora.runners :as mr]
    [clojure.string :as string])
  (:use
    [menodora.core :only (describe should expect)]
    [menodora.predicates :only (eq)])
  #_(:use-macros
    [menodora :only (describe it expect)]))

(def tests (atom []))

(binding [mc/*tests* tests]
  (describe "two passes"
    (fn []
      (should "pass"
        #(expect eq 1 1))
      (should "pass pass"
        #(do
           (expect eq 2 2)
           (expect eq 3 3)))))

  (describe "three fails"
    (fn []
      (should "pass fail"
        #(do
           (expect eq 1 1)
           (expect eq 1 2)))
      (should "fail pass"
        #(do
           (expect eq 1 2)
           (expect eq 1 1)))
      (should "fail fail"
        #(do
           (expect eq 3 4)
           (expect eq 5 6)))))

  (describe "three different"
    (fn []
      (should "pass"
        #(expect eq 1 1))
      (should "fail"
        #(expect eq 2 3))
      (should "pass"
        #(expect eq 9 9)))))

(describe "test menodora"
  (fn []
    (let [ts (vec (map (fn [[x y]] [x (deref y)]) @tests))]
      (should "two passes"
        #(expect eq
           ["two passes"
            [["pass" [false]]
             ["pass pass" [false false]]]]
           (nth ts 0)))
      (should "three fails"
        #(expect eq
           ["three fails"
            [["pass fail" [false "Expected: 1. Actual: 2."]]
             ["fail pass" ["Expected: 1. Actual: 2." false]]
             ["fail fail" ["Expected: 3. Actual: 4." "Expected: 5. Actual: 6."]]]]
           (nth ts 1)))
      (should "three different"
        #(expect eq
           ["three different"
            [["pass" [false]]
             ["fail" ["Expected: 2. Actual: 3."]]
             ["pass" [false]]]]
           (nth ts 2))))))

(describe "test console runner"
  (fn []
    (should "write output for fails"
      #(let [strings (atom [])]
         (binding [*print-fn* (fn [& s] (swap! strings conj (if (string? s)
                                                              s
                                                              (string/join " " s))))]
           ((mr/console) @tests))
         (expect eq
           (string/join
             "\n"
             [".FF"
              "three fails"
              "  should pass fail"
              "    Pass."
              "    Fail. Expected: 1. Actual: 2."
              "  should fail pass"
              "    Fail. Expected: 1. Actual: 2."
              "    Pass."
              "  should fail fail"
              "    Fail. Expected: 3. Actual: 4."
              "    Fail. Expected: 5. Actual: 6."
              "three different"
              "  should fail"
              "    Fail. Expected: 2. Actual: 3."
              ""])
           (apply str @strings))))
    (should "write all output"
      #(let [strings (atom [])]
         (binding [*print-fn* (fn [& s] (swap! strings conj (if (string? s)
                                                              s
                                                              (string/join " " s))))]
           ((mr/console :show :all) @tests))
         (expect eq
           (string/join
             "\n"
             [".FF"
              "two passes"
              "  should pass"
              "    Pass."
              "  should pass pass"
              "    Pass."
              "    Pass."
              "three fails"
              "  should pass fail"
              "    Pass."
              "    Fail. Expected: 1. Actual: 2."
              "  should fail pass"
              "    Fail. Expected: 1. Actual: 2."
              "    Pass."
              "  should fail fail"
              "    Fail. Expected: 3. Actual: 4."
              "    Fail. Expected: 5. Actual: 6."
              "three different"
              "  should pass"
              "    Pass."
              "  should fail"
              "    Fail. Expected: 2. Actual: 3."
              "  should pass"
              "    Pass."
              ""])
           (apply str @strings))))))

(defn ^:export -run-tests
  [finished print-fn]
  (mc/run-tests (mr/console)
                :finished finished
                :print-fn print-fn))

;;. vim: set lispwords+=describe,should,expect:
