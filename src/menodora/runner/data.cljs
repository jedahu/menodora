(ns menodora.runner.data
  (:require
    [menodora.runner :as r]))

(defn conj-expect
  [data x]
  (swap! data
         (fn [old]
           (let [n (dec (count old))
                 o (dec (count (old n)))
                 p (dec (count ((old n) o)))]
             (update-in old [n o p] conj x)))))

(defrecord Data
  [data]
  r/Runner
  (-finished [this]
    @data)
  (-suite-start [this title]
    (swap! data conj [title]))
  (-suite-end [this])
  (-describe-start [this title]
    (swap! data
           (fn [old]
             (update-in old [(dec (count old))] conj [title]))))
  (-describe-end [this])
  (-should-start [this title]
    (swap! data
           (fn [old]
             (let [n (dec (count old))
                   o (dec (count (old n)))]
               (update-in old [n o] conj [title])))))
  (-should-end [this])
  (-expected [this]
    (conj-expect data :pass))
  (-unexpected [this msg args]
    (conj-expect data args)))

(defn data-runner
  [& opts]
  (Data. (atom [])))
