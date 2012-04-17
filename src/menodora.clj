(ns menodora)

(defn opts-body-split
  [forms]
  (loop [forms forms opts []]
    (if (keyword? (first forms))
      (let [[k v & more] forms]
        (recur more (conj opts
                          k (if (list? v)
                              `(fn [] ~v)
                              v))))
      [opts forms])))

(defn wrap-bindings
  [forms body]
  (reduce
    (fn [code [kind bindings]]
      (case kind
        :let `(let ~(vec bindings) ~code)
        :binding `(binding ~(vec bindings) ~code)
        code))
    body
    (reverse (partition 2 forms))))

(defn opts-map
  [opts]
  (apply hash-map
         (apply concat
                (filter #(not (#{:let :binding} (first %)))
                        (partition 2 opts)))))

(defmacro done!
  []
  `(menodora.core/done!))

(defmacro defsuite
  [sym & body]
  `(def ~sym
     (menodora.core/suite ~(name sym) ~@body)))

(defmacro describe
  [text & forms]
  (let [[opts body] (opts-body-split forms)
        fbody (wrap-bindings opts `[~@body])]
    `(menodora.core/describe* ~text ~(opts-map opts) (fn [] ~fbody))))

(defmacro should
  [text & forms]
  (let [[opts body] (opts-body-split forms)
        fbody (wrap-bindings opts `(do ~@body))]
    `(menodora.core/should* ~text (fn [] (do ~fbody (menodora.core/done!))))))

(defmacro should*
  [text & forms]
  (let [[opts body] (opts-body-split forms)
        fbody (wrap-bindings opts `(do ~@body))]
    `(menodora.core/should* ~text (fn [] ~fbody))))

(defmacro expect
  [pred & args]
  `(menodora.core/expect ~pred ~@args))

;;. vim: set lispwords+=macrolet,defsuite:
