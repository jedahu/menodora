(ns menodora.test.macros
  (:use
    [menodora.predicates :only (eq)])
  (:require
    [menodora.core :as mc])
  (:use-macros
    [menodora :only (defsuite describe should should* expect)]))

(defsuite macro-tests
  (describe "bindings"
    :let [cat "tiger"]
    (should "be available"
      (expect eq "tiger" cat)))

  (describe "before"
    :let [shape (atom :line)]
    :before (reset! shape :square)
    (should "see bindings"
      (expect eq :square @shape)
      (reset! shape :triangle))
    (should "not be called again"
      (expect eq :triangle @shape)))

  (describe "pre"
    :let [shape (atom :line)]
    :pre (reset! shape :circle)
    (should "see bindings"
      (expect eq :circle @shape)
      (reset! shape :triangle))
    (should "be called before every 'should'"
      (expect eq :circle @shape))))

;;. vim: set lispwords+=defsuite,describe,should,expect:
