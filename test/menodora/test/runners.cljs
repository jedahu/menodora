(ns menodora.test.runners
  (:use
    [menodora.core :only (suite describe should expect)]
    [menodora.predicates :only (eq)])
  (:require
    [menodora.runners :as mr]
    [menodora.test.core :as tc]
    [clojure.string :as string]))

(def console-runner-tests
  ["console-runner-tests"
   (suite nil
     (fn []
       (describe "test console runner"
         (fn []
           (should "write output for fails"
             #(let [strings (atom [])]
                (binding [*print-fn* (fn [& s] (swap! strings conj (if (string? s)
                                                                     s
                                                                     (string/join " " s))))]
                  ((mr/console) tc/core-test-tests))
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
                  ((mr/console :show :all) tc/core-test-tests))
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
                  (apply str @strings))))))))])
