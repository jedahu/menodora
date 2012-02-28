(ns menodora.predicates)

(def eq
  {:test =
   :message #(str "Expected: " (pr-str %1)
                  ". Actual: " (pr-str %2) ".")})
