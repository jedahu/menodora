(ns menodora.test.core
  (:use
    [menodora.core :only (suite describe should expect *run-opts*)]
    [menodora.predicates :only (eq)]))

(def core-test-tests
  ["core-test-tests"
   (suite nil
     (fn []
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
               #(expect eq :line @shape)))))))])

(def core-tests
  ["core-tests"
   (suite nil
     (fn []
       (describe "test menodora"
         (fn []
           (let [ts (binding [*run-opts* (assoc *run-opts* :catch? true)]
                      (vec (map (fn [[x y]] [x (y)]) (second core-test-tests))))]
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
                  (nth ts 9))))))))])

;;. vim: set lispwords+=defsuite,describe,should,expect:
