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
  (prind 4 "should" text))

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
    (let [fail? (seq (remove nil? @(:unexpected this)))]
      (swap! (:pass-fail this) update-in [suite (if fail? 1 0)] inc)
      (when (or (show-all? this) fail?)
        (?print-descr this descr)
        (when-not (show-all? this)
          (prind 4 text))
        (doseq [msg @(:unexpected this)]
          (when (or (show-all? this) msg)
            (if msg
              (prind 8 "fail." msg)
              (prind 8 "pass.")))))))
  (-expected [this]
    (swap! (:unexpected this) conj nil))
  (-unexpected [this msg args]
    (swap! (:unexpected this) conj msg)))

(defn ^:export console-runner
  [& {:as opts}]
  (when-let [pf (:print-fn opts)]
    (set! *print-fn* pf))
  (Console. opts (atom []) (atom #{}) (atom {}) (atom [])))

(def global (js* "this"))

(def ^:export run-suites-v8
  (make-run-suites console-runner
                   #((aget global "quit") %)
                   :print-fn #((aget global "write") %)))

(def ^:export run-suites-rhino
  (make-run-suites console-runner
                   identity
                   :print-fn #(let [out (-> global
                                          (aget "java")
                                          (aget "lang")
                                          (aget "System")
                                          (aget "out"))
                                    print (aget out "print")]
                                (. print call out %))))

(def browser-print-fn
  (let [buffer (atom [])]
    (fn [s]
      (let [console (aget global "console")
            log (aget console "log")]
        (swap! buffer
               (fn [buf]
                 (if (= \newline (last s))
                   (do
                     (. log call console
                        (apply str (conj buf (apply str (butlast s)))))
                     [])
                   (conj buf s))))))))

(def ^:export run-suites-browser
  (make-run-suites console-runner
                   #(.. js/document
                      -body
                      (setAttribute "data-menodora-final-fail-count" %))
                   :print-fn browser-print-fn))
