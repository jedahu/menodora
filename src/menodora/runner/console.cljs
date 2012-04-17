(ns menodora.runner.console
  (:require
    [menodora.runner :as r]))

(defn prind [n & args]
  (apply println
         (str (apply str (repeat n \space))
              (first args))
         (rest args)))

(defrecord Console
  [suite describe should pass-fail unexpected]
  r/Runner
  (-finished [this]
    (println)
    (prind 0 "Suite pass fail")
    (doseq [[s p f] @pass-fail]
      (prind 0 s p f))
    (println)
    (let [[p f] (reduce
                  (fn [[p f] [_ p1 f1]]
                    [(+ p p1) (+ f f1)])
                  [0 0]
                  @pass-fail)]
      (prind 0 "total" p f)
      f))
  (-suite-start [this title]
    (reset! suite title)
    (swap! pass-fail conj [title 0 0]))
  (-suite-end [this])
  (-describe-start [this title]
    (reset! describe title))
  (-describe-end [this])
  (-should-start [this title]
    (reset! should title)
    (reset! unexpected nil))
  (-should-end [this]
    (swap! pass-fail
           update-in
           [(dec (count @pass-fail))
            (if (seq @unexpected) 2 1)]
           inc)
    (doseq [msg @unexpected]
      (prind 0 @describe "-" @should)
      (prind 4 "Fail." msg)))
  (-expected [this])
  (-unexpected [this msg args]
    (swap! unexpected conj msg)))

(defn console-runner
  [& opts]
  (Console. (atom nil) (atom nil) (atom nil)
            (atom []) (atom [])))
