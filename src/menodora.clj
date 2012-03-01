(ns menodora)

(defn opts-body-split
  [forms]
  (loop [forms forms opts {}]
    (if (< 2 (count forms))
      (let [[k v & more] forms]
        (recur more (assoc opts k
                          (if (list? v)
                            `(fn [] ~v)
                            v))))
      [opts forms])))

(defmacro ?wrap-binding
  [bindings & body]
  (if (seq bindings)
    `(binding ~(vec bindings) ~@body)
    `(do ~@body)))

(defmacro ?wrap-let
  [bindings & body]
  (if (seq bindings)
    `(let ~(vec bindings) ~@body)
    ~(do ~@body)))

(defmacro defsuite
  [sym & forms]
  (let [[opts body] (opts-body-split forms)]
    `(def ~sym
       ~(wrap-binding
          (:binding opts)
          (wrap-let
            (:let opts)
            [`(name '~sym)
             `(menodora.core/suite ~opts (fn [] ~@body))])))))

(defmacro describe
  [text & forms]
  (let [[opts body] (opts-body-split forms)]
    (wrap-binding
      (:binding opts)
      (wrap-let
        (:let opts)
        `(menodora.core/describe* ~text ~opts (fn [] ~@body))))))

(defmacro should
  [text & forms]
  (let [[opts body] (opts-body-split forms)]
    (wrap-binding
      (:binding opts)
      (wrap-let
        (:let opts)
        `(menodora.core/should* ~text (fn [] ~@body))))))

(defmacro expect
  [pred & args]
  `(menodora.core/expect ~pred ~@args))

;;. vim: set lispwords+=macrolet:
