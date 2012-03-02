(ns menodora.test.macros
  (:use
    [menodora.predicates :only (eq)])
  (:require
    [menodora.core :as mc])
  (:use-macros
    [menodora :only (defsuite describe should expect)]))

(def ^:dynamic *animal*)
(def ^:dynamic *tree*)

(defsuite macro-tests
  (describe "describe"
    :binding [*tree* "redwood"]
    :let [cat "tiger"
          tree+ (str *tree* " tree")]
    :binding [*animal* (str "indian " cat)]
    (should "make bindings available"
      (expect eq "tiger" cat)
      (expect eq "redwood" *tree*))
    (should "bind in order"
      (expect eq "redwood tree" tree+)
      (expect eq "indian tiger" *animal*))
    (should "shadow describe bindings"
      :let [cat "lion"]
      :binding [*tree* "pine"]
      (expect eq "pine" *tree*)
      (expect eq "lion" cat))))

;;. vim: set lispwords+=defsuite,describe,should,expect:
