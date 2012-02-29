(ns menodora)

(defn opts-body-split
  [forms]
  (loop [forms forms opts {}]
    (if (< 2 (count forms))
      (let [[k v & more] forms]
        (recur more (assoc opts k `(fn [] ~v))))
      [opts forms])))

(defmacro defsuite
  [sym & forms]
  (let [[opts body] (opts-body-split forms)]
    `(def ~sym
       [(name ~sym)
        (menodora.core/suite ~opts (fn [] ~@body))])))

(defmacro describe
  [text & forms]
  (let [[opts body] (opts-body-split forms)]
    `(menodora.core/describe* ~text ~opts (fn [] ~@body))))

(defmacro should
  [text & body]
  `(menodora.core/should ~text (fn [] ~@body)))

(defmacro expect
  [pred & args]
  `(menodora.core/expect ~pred ~@args))

;;. vim: set lispwords+=macrolet:
