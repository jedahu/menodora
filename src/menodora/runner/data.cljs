(ns menodora.runner.data
  (:require
    [menodora.runner :as r]))

(defn conj-expect
  [data x]
  (swap! data update-in [(dec (count @data))] conj x))

(defrecord Data
  [data]
  r/Runner
  (-finished [this]
    @data)
  (-test-start [this title]
    (swap! (:data this) conj [title]))
  (-test-end [this title])
  (-expected [this]
    (conj-expect (:data this) :pass))
  (-unexpected [this msg args]
    (conj-expect (:data this) (vec args))))

(defn data-runner
  [& opts]
  (Data. (atom [])))
