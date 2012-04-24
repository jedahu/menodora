(ns menodora.test.core
  (:use
    [menodora.core :only (run-suites suite-runner test-seq)]
    [menodora.predicates :only (eq)]
    [menodora.runner.data :only (data-runner)])
  (:use-macros
    [menodora :only (defsuite describe should should* expect)]))

(defsuite pass-fail-tests
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
      (expect eq 9 9))))

(def after-val (atom nil))

(defsuite opts-tests
  (describe ":let"
    :let [foo 9]
    (should "make bindings available"
      (expect eq 9 foo)))

  (describe ":before"
    :let [shape (atom :line)]
    :before (reset! shape :square)
    (should "see bindings"
      (expect eq :square @shape)
      (reset! shape :triangle))
    (should "not be called again"
      (expect eq :triangle @shape)))

  (describe "after-val"
    :before (reset! after-val :a)
    :after (reset! after-val :b)
    (should "be :a"
      (expect eq :a @after-val)))
  (describe ":after"
    (should "be called after the previous 'describe'"
      (expect eq :b @after-val)))

  (describe ":pre"
    :let [shape (atom :line)]
    :pre (reset! shape :circle)
    (should "see bindings"
      (expect eq :circle @shape)
      (reset! shape :triangle))
    (should "be called before every 'should'"
      (expect eq :circle @shape)))

  (describe ":post"
    :let [shape (atom :line)]
    :post (reset! shape :square)
    (should "not be called before 'should'"
      (expect eq :line @shape))
    (should "be called after 'should'"
      (expect eq :square @shape))))

(defsuite async-tests
  (describe "should*"
    (should* "work"
      (expect eq 1 1)
      (<done>))
    (should "call the next test"
      (expect eq 1 1)))

  (describe ":before*"
    :let [shape (atom :line)]
    :before* (do (reset! shape :square) (<done>))
    (should "see bindings"
      (expect eq :square @shape)
      (reset! shape :triangle))
    (should "not be called again"
      (expect eq :triangle @shape)))

  (describe "after-val"
    :before (reset! after-val :a)
    :after* (do (reset! after-val :b) (<done>))
    (should "be :a"
      (expect eq :a @after-val)))
  (describe ":after*"
    (should "be called after the previous 'describe'"
      (expect eq :b @after-val)))

  (describe ":pre*"
    :let [shape (atom :line)]
    :pre* (do (reset! shape :circle) (<done>))
    (should "see bindings"
      (expect eq :circle @shape)
      (reset! shape :triangle))
    (should "be called before every 'should'"
      (expect eq :circle @shape)))

  (describe ":post*"
    :let [shape (atom :line)]
    :post* (do (reset! shape :square) (<done>))
    (should "not be called before 'should'"
      (expect eq :line @shape))
    (should "be called after 'should'"
      (expect eq :square @shape))))

(def global (js* "this"))

(defn test-menodora
  [_suites]
  (set! *print-fn* #(let [out (-> global
                                (aget "java")
                                (aget "lang")
                                (aget "System")
                                (aget "out"))
                          print (aget out "print")]
                      (. print call out %)))
  (let [expected-data
        [[["pass-fail-tests" "two passes" "pass"] :pass]
         [["pass-fail-tests" "two passes" "pass pass"] :pass :pass]
         [["pass-fail-tests" "three fails" "pass fail"] :pass [1 2]]
         [["pass-fail-tests" "three fails" "fail pass"] [1 2] :pass]
         [["pass-fail-tests" "three fails" "fail fail"] [3 4] [5 6]]
         [["pass-fail-tests" "three different" "pass"] :pass]
         [["pass-fail-tests" "three different" "fail"] [2 3]]
         [["pass-fail-tests" "three different" "pass"] :pass]
         [["opts-tests" ":let" "make bindings available"] :pass]
         [["opts-tests" ":before" "see bindings"] :pass]
         [["opts-tests" ":before" "not be called again"] :pass]
         [["opts-tests" "after-val" "be :a"] :pass]
         [["opts-tests" ":after" "be called after the previous 'describe'"] :pass]
         [["opts-tests" ":pre" "see bindings"] :pass]
         [["opts-tests" ":pre" "be called before every 'should'"] :pass]
         [["opts-tests" ":post" "not be called before 'should'"] :pass]
         [["opts-tests" ":post" "be called after 'should'"] :pass]
         [["async-tests" "should*" "work"] :pass]
         [["async-tests" "should*" "call the next test"] :pass]
         [["async-tests" ":before*" "see bindings"] :pass]
         [["async-tests" ":before*" "not be called again"] :pass]
         [["async-tests" "after-val" "be :a"] :pass]
         [["async-tests" ":after*" "be called after the previous 'describe'"] :pass]
         [["async-tests" ":pre*" "see bindings"] :pass]
         [["async-tests" ":pre*" "be called before every 'should'"] :pass]
         [["async-tests" ":post*" "not be called before 'should'"] :pass]
         [["async-tests" ":post*" "be called after 'should'"] :pass]]]
    (run-suites data-runner
                (fn [data]
                  (let [res (for [[expected actual]
                                  (map vector expected-data data)]
                              (or (= expected actual)
                                  (println
                                    "fail. Expected: " (pr-str expected)
                                    ". Actual: " (pr-str actual) ".")))
                        p (count (filter boolean res))
                        f (count (filter nil? res))]
                    (println "pass fail" p f)
                    (count (filter nil? res))))
                [pass-fail-tests
                 opts-tests
                 async-tests])))

;;. vim: set lispwords+=defsuite,describe,should,should*,expect:
