(ns menodora.test.macros
  (:use
    [menodora.predicates :only (eq)])
  (:require
    [menodora.core :as mc])
  (:use-macros
    [menodora :only (defsuite describe should expect)]))

(def ^:dynamic *animal*)
(def ^:dynamic *tree*)

(defsuite test-macros
  :binding [*animal* "bonobo"
            *tree* "pine"]
  :let [cat "lion"]
  (describe "defsuite"
    (should "make bindings visible"
      (expect eq "bonobo" *animal*)
      (expect eq "pine" *tree*)
      (expect eq "lion" cat)))
  (describe "describe"
    :binding [*tree* "redwood"]
    :let [cat "tiger"
          tree+ (str *tree* " tree")]
    (should "shadow suite bindings"
      (expect eq "redwood" *tree*)
      (expect eq "tiger" cat))
    (should "make dynamic bindings visible to let bindings"
      (expect eq "redwood tree" tree+))))

;;. vim: set lispwords+=defsuite,describe,should,expect:
