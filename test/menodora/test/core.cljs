(ns menodora.test.core
  (:require
    [menodora.core :as mc])
  (:use-macros
    [menodora :only (describe it expect)]))

(describe "menodora"
  (it "should pass"
    (expect mc/= 1 1))
  (it "should fail"
    (expect mc/= 1 2)))

;;. vim: set lispwords+=describe,it,expect:
