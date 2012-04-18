(ns menodora.runner.console
  (:require
    [menodora.runner :as r])
  (:use
    [menodora.core :only (make-run-suites)]))

(defn prind [n & args]
  (apply println
         (str (apply str (repeat n \space))
              (first args))
         (rest args)))

(defn ?print-descr
  [runner descr]
  (when-not (@(:descrs runner) descr)
    (swap! (:descrs runner) conj descr)
    (prind 0 descr)))

(defn print-text
  [text]
  (prind 4 text))

(defn show-all?
  [runner]
  (= :all (:show (:opts runner))))

(defrecord Console
  [opts suites descrs pass-fail unexpected]
  r/Runner
  (-finished [this]
    (println)
    (prind 0 "suite pass fail")
    (let [pf @(:pass-fail this)]
      (doseq [s @(:suites this)
              :let [[p f] (pf s)]]
        (prind 0 s p f))
      (println)
      (let [[p f] (apply map + (vals pf))]
        (prind 0 "total" p f)
        f)))
  (-test-start [this [suite descr text :as title]]
    (when-not (= suite (last @(:suites this)))
      (swap! (:suites this) conj suite)
      (swap! (:pass-fail this) assoc suite [0 0]))
    (when (show-all? this)
      (?print-descr this descr)
      (print-text text))
    (reset! (:unexpected this) nil))
  (-test-end [this [suite descr text :as title]]
    (swap! (:pass-fail this)
           update-in
           [suite (if (seq (remove nil? @(:unexpected this))) 1 0)]
           inc)
    (doseq [msg @(:unexpected this)]
      (when (or (show-all? this)
                msg)
        (?print-descr this descr)
        (when-not (show-all? this)
          (prind 4 text))
        (if msg
          (prind 8 "fail." msg)
          (prind 8 "pass.")))))
  (-expected [this]
    (swap! (:unexpected this) conj nil))
  (-unexpected [this msg args]
    (swap! (:unexpected this) conj msg)))

(defn ^:export console-runner
  [& {:as opts}]
  (when-let [pf (:print-fn opts)]
    (set! *print-fn* pf))
  (Console. opts (atom []) (atom #{}) (atom {}) (atom [])))

(def ^:export run-suites-v8
  (make-run-suites console-runner
                   #(js/quit %)
                   :print-fn #(js/write %)))

(def ^:export run-suites-rhino
  (make-run-suites console-runner
                   identity
                   :print-fn #(. java.lang.System/out print %)))

(def ^:export run-suites-phantom
  (make-run-suites console-runner
                   #(. js/phantom exit %)
                   :print-fn #(. js/console log %)))

(def pre-id "menodora-runner-console")

(defn browser-print-fn
  [s]
  (let [pre (or (. js/document getElementById pre-id)
                (let [pre1 (. js/document createElement "pre")]
                  (. pre1 setAttribute "id" pre-id)
                  (.. js/document -body (appendChild pre1))
                  pre1))]
    (. pre appendChild (. js/document createTextNode s))))

(def ^:export run-suites-browser
  (make-run-suites console-runner
                   identity
                   :print-fn browser-print-fn))
