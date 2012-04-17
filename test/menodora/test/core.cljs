(ns menodora.test.core
  (:use
    [menodora.core :only (run-tests)]
    [menodora.predicates :only (eq)]
    [menodora.runner.data :only (data-runner)])
  (:use-macros
    [menodora :only (defsuite describe should expect)]))

(defsuite core-test-tests
  (describe "two passes"
    (should "pass"
      (expect eq 1 1))
    (should "pass pass"
      (expect eq 2 2)
      (expect eq 3 3)))

  (describe "three fails"
    (should "pass fail"
      (expect eq 1 1)
      (expect eq 1 2))
    (should "fail pass"
      (expect eq 1 2)
      (expect eq 1 1))
    (should "fail fail"
      (expect eq 3 4)
      (expect eq 5 6)))

  (describe "three different"
    (should "pass"
      (expect eq 1 1))
    (should "fail"
      (expect eq 2 3))
    (should "pass"
      (expect eq 9 9)))

  (describe "describe exception"
    :let [x (throw "exception one")]
    (should "not be tested"
      (expect eq 1 1)))

  (describe "should exception"
    (should "pass"
      (expect eq 1 1))
    (should "throw an exception"
      (throw "exception two")
      (expect eq 1 1)))

  (describe "expect exception"
    (should "throw one exception"
      (expect eq 1 (throw "not a number"))
      (expect eq 3 3)))

  (describe "before"
    :let [shape (atom :line)]
    :before #(reset! shape :square)
    (should "be square"
      (expect eq :square @shape)))

  (describe "pre"
    :let [shapte (atom :line)]
    :pre #(reset! shape :circle)
    (should "be circle"
      (expect eq :circle @shape))))

(def data (atom nil))
(defn run-core-tests []
  (run-tests data-runner core-test-tests
             :finish #(reset! data %) :catch? false))

(defsuite core-tests
  (describe "test core-tests"
    :let [ts (subvec @data 1)]
    (should "two passes"
      (expect eq
        ["two passes"
         ["pass" :pass]
         ["pass pass" :pass :pass]]
        (nth ts 0)))
    (should "three fails"
      (expect eq
        ["three fails"
         ["pass fail" :pass [1 2]]
         ["fail pass" [1 2] :pass]
         ["fail fail" [3 4] [5 6]]]
        (nth ts 1)))
    (should "three different"
      (expect eq
        ["three different"
         ["pass" :pass]
         ["fail" [2 3]]
         ["pass" :pass]]
        (nth ts 2)))
    (should "describe exception"
      (expect eq
        ["describe exception"
         "exception one"]
        (nth ts 3)))
    (should "should exception"
      (expect eq
        ["should exception"
         [["pass" [false]]
          ["throw an exception" "exception two"]]]
        (nth ts 4)))
    (should "expect exception"
      (expect eq
        ["expect exception"
         [["throw one exception" "not a number"]]]
        (nth ts 5)))
    (should "before"
      (expect eq
        ["before"
         ["be square" :pass]]
        (nth ts 6)))
    (should "pre"
      (expect eq
        ["pre"
         ["be circle" :pass]]
        (nth ts 7)))))

;;. vim: set lispwords+=defsuite,describe,should,should*,expect:
