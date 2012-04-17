(ns menodora.test.runners
  (:use
    [menodora.predicates :only (eq)])
  (:use-macros
    [menodora :only (defsuite describe should expect)])
  (:require
    [menodora.core :as mc]
    [menodora.runners :as mr]
    [menodora.test.core :as tc]
    [clojure.string :as string]))

(defsuite console-runner-tests
  (describe "test console runner"
    (should "write output for fails"
      (let [strings (atom [])]
        (binding [*print-fn* (fn [& s]
                               (swap! strings conj (if (string? s)
                                                     s
                                                     (string/join " " s))))]
          ((mr/console) [tc/core-test-tests]))
        (expect eq
          (string/join
            "\n"
            ["  three fails - should pass fail"
             "    Fail. Expected: 1. Actual: 2."
             "  three-fails - should fail pass"
             "    Fail. Expected: 1. Actual: 2."
             "  three-fails - should fail fail"
             "    Fail. Expected: 3. Actual: 4."
             "    Fail. Expected: 5. Actual: 6."
             "  three different - should fail"
             "    Fail. Expected: 2. Actual: 3."
             ""
             "Suite pass fail"
             "core-test-tests 9 7"
             ""
             "total 9 7"
             ""])
          (apply str @strings))))
    #_(should "write all output"
      (let [strings (atom [])]
        (binding [*print-fn* (fn [& s] (swap! strings conj (if (string? s)
                                                             s
                                                             (string/join " " s))))]
          ((mr/console :show :all) [tc/core-test-tests]))
        (expect eq
          (string/join
            "\n"
            [".FFFFF...."
             "Suite: core-test-tests"
             "  two passes"
             "    should pass"
             "      Pass."
             "    should pass pass"
             "      Pass."
             "      Pass."
             "  three fails"
             "    should pass fail"
             "      Pass."
             "      Fail. Expected: 1. Actual: 2."
             "    should fail pass"
             "      Fail. Expected: 1. Actual: 2."
             "      Pass."
             "    should fail fail"
             "      Fail. Expected: 3. Actual: 4."
             "      Fail. Expected: 5. Actual: 6."
             "  three different"
             "    should pass"
             "      Pass."
             "    should fail"
             "      Fail. Expected: 2. Actual: 3."
             "    should pass"
             "      Pass."
             "  describe exception"
             "    Threw: exception one"
             "  should exception"
             "    should pass"
             "      Pass."
             "    should throw an exception"
             "      Threw: exception two"
             "  expect exception"
             "    should throw one exception"
             "      Threw: not a number"
             "  before"
             "    should be square"
             "      Pass."
             "  after"
             "    should be line"
             "      Pass."
             "  pre"
             "    should be circle"
             "      Pass."
             "  post"
             "    should be line"
             "      Pass."
             "Passed: 9. Failed: 7."
             ""])
          (apply str @strings))))))

;;. vim: set lispwords+=defsuite,describe,should,expect:
