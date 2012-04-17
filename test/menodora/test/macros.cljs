(ns menodora.test.macros
  (:use
    [menodora.predicates :only (eq)])
  (:require
    [menodora.core :as mc])
  (:use-macros
    [menodora :only (defsuite describe should expect done!)]))

(defsuite macro-tests
  (describe "describe"
    :let [cat "tiger"]
    (should* "make bindings available"
      (expect eq "tiger" cat)
      (done!))
    (should* "shadow describe bindings"
      :let [cat "lion"]
      (expect eq "lion" cat)
      (done!))))

;;. vim: set lispwords+=defsuite,describe,should,expect:
