(ns menodora.core
  (:refer-clojure
    :exclude (=))
  (:require
    [cljs.core :as cc]))

(def ^:dynamic *tests*
  (atom []))

(def ^:dynamic *should-results*)

(def ^:dynamic *describe-results*)

(def ^:dynamic *success*)

(def =
  {:test cc/=
   :message #(str "Expected: " %1
                  ". Actual: " %2)})

(defn opts-body-split
  [forms]
  (let [[opts body] (split-with (comp not list?) forms)]
    [(apply hash-map opts) body]))

(defn describe
  [text f]
  (let [[{:keys [before after] :as opts} body] (opts-body-split forms)]
    (defn should
      [text f]
      (swap! *describe-results*
             conj
             [text (try
                     (binding [*should-results* (atom [])]
                       (and before (apply before nil))
                       (f)
                       (and after (apply after nil))
                       @*should-results*)
                     (catch js/Object e e))]))
    (defn expect
      [pred & args]
      (swap! *should-results*
             conj (try
                    (if (apply (:test pred) args)
                      false
                      (apply (:message pred) args))
                    (catch js/Object e e))))
    (let [{:keys [before-all after-all]} opts]
      (swap!
        *tests*
        conj
        [text (delay
                (binding [*describe-results* (atom [])]
                  (and before-all (apply before-all nil))
                  (try (f) (catch js/Object e e))
                  (and after-all (apply after-all nil))
                  @*describe-results*))]))))

(defn ^:export run-tests
  [runner & {:keys [print-fn names finished]}]
  (binding [*print-fn* print-fn]
    ((or finished (constantly nil))
       (runner
         (if-let [names (and (seq names) (set names))]
           (filter #(some names (first %)) @*tests*)
           @*tests*)))))

(defn should-succ|fail
  [result]
  (cond
    (seq result)
    [(count (filter not result))
     (count (filter identity result))]

    result [0 1]

    :else [0 0]))

(defn describe-succ|fail
  [descr-test]
  (let [result @descr-test]
    (cond
      (seq result)
      (reduce
        (fn [[x y] [s f]]
          [(+ x s) (+ y f)])
        (map should-succ|fail result))

      result [0 1]

      :else [0 0])))

(defn ^:export console-runner
  [& {:keys [show]}]
  (fn [tests]
    (let [succ (atom [])
          fail (atom [])
          all (atom [])]
      (doseq [[descr test] tests
              :let [[s f] (describe-succ|fail test)]]
        (swap! all conj [descr @test])
        (if (= 0 f)
          (do
            (swap! succ conj [descr @test])
            (print "."))
          (do
            (swap! fail conj [descr @test])
            (print "F"))))
      (println)
      (doseq [[name should] (if (= :all show) @all @fail)]
        (println name)
        (if (seq should)
          (doseq [[text expects] should]
            (println "  should" text)
            (if (seq expects)
              (doseq [expect expects]
                (if expect
                  (println "    Fail." expect)
                  (println "    Pass.")))
              (println "    Threw:" expects)))
          (println "  Threw:" should)))
      (println)
      (count @fail))))
