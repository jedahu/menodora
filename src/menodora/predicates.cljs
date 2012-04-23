(ns menodora.predicates)

(def eq
  {:test =
   :message #(str "Expected: " (pr-str %1)
                  ". Actual: " (pr-str %2) ".")})

(def truthy
  {:test boolean
   :message (constantly "Expected a truthy value.")})

(def type-eq
  {:test #(= %1 (type %2))
   :message #(str "Expected type: " %1
                  ". Actual type: " %2)})

(def is-a
  {:test instance?
   :message #(str %2 " not an instance of " %1 ".")})
