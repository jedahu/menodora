(ns menodora.runner)

(defprotocol Runner
  (-test-start [this title])
  (-test-end [this title])
  (-expected [this])
  (-unexpected [this msg args])
  (-finished [this]))
