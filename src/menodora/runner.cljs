(ns menodora.runner)

(defprotocol Runner
  (-suite-start [this title])
  (-suite-end [this])
  (-describe-start [this title])
  (-describe-end [this])
  (-should-start [this title])
  (-should-end [this])
  (-expected [this])
  (-unexpected [this msg args])
  (-finished [this]))
