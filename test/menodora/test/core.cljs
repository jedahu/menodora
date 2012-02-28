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
        #(expect eq 9 9))))

  (describe "describe exception"
    (fn []
      (throw "exception one")
      (should "not be tested"
        #(expect eq 1 1))))

  (describe "should exception"
    (fn []
      (should "pass"
        #(expect eq 1 1))
      (should "throw an exception"
        #(do
           (throw "exception two")
           (expect eq 1 1)))))

  (describe "expect exception"
    (fn []
      (should "throw one exception"
        #(do
           (expect eq 1 (throw "not a number"))
           (expect eq 3 3)))))

  (let [shape (atom :line)]
    (describe "before"
      :before #(reset! shape :square)
      :after #(reset! shape :line)
      (fn []
        (should "be square"
          #(expect eq :square @shape))))

    (describe "after"
      (fn []
        (should "be line"
          #(expect eq :line @shape))))

    (describe "pre"
      :pre #(reset! shape :circle)
      :post #(reset! shape :line)
      (fn []
        (should "be circle"
          #(expect eq :circle @shape))))

    (describe "post"
      (fn []
        (should "be line"
          #(expect eq :line @shape))))))

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
           (nth ts 2)))
      (should "describe exception"
        #(expect eq
           ["describe exception"
            "exception one"]
           (nth ts 3)))
      (should "should exception"
        #(expect eq
           ["should exception"
            [["pass" [false]]
             ["throw an exception" "exception two"]]]
           (nth ts 4)))
      (should "expect exception"
        #(expect eq
           ["expect exception"
            [["throw one exception" "not a number"]]]
           (nth ts 5)))
      (should "before"
        #(expect eq
           ["before"
            [["be square" [false]]]]
           (nth ts 6)))
      (should "after"
        #(expect eq
           ["after"
            [["be line" [false]]]]
           (nth ts 7)))
      (should "pre"
        #(expect eq
           ["pre"
            [["be circle" [false]]]]
           (nth ts 8)))
      (should "post"
        #(expect eq
           ["post"
            [["be line" [false]]]]
           (nth ts 9))))))

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
             [".FFFFF...."
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
              "describe exception"
              "  Threw: exception one"
              "should exception"
              "  should throw an exception"
              "    Threw: exception two"
              "expect exception"
              "  should throw one exception"
              "    Threw: not a number"
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
             [".FFFFF...."
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
              "describe exception"
              "  Threw: exception one"
              "should exception"
              "  should pass"
              "    Pass."
              "  should throw an exception"
              "    Threw: exception two"
              "expect exception"
              "  should throw one exception"
              "    Threw: not a number"
              "before"
              "  should be square"
              "    Pass."
              "after"
              "  should be line"
              "    Pass."
              "pre"
              "  should be circle"
              "    Pass."
              "post"
              "  should be line"
              "    Pass."
              ""])
           (apply str @strings))))))

(defn ^:export -run-tests
  [finished print-fn]
  (mc/run-tests (mr/console)
                :finished finished
                :print-fn print-fn))

;;. vim: set lispwords+=describe,should,expect:
