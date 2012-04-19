(ns menodora)

(defn opts-body-split
  [forms]
  (loop [forms forms opts []]
    (if (keyword? (first forms))
      (let [[k v & more] forms]
        (recur more (conj opts k v)))
      [opts forms])))

(defn wrap-bindings*
  [opts body]
  (if-let [binds (:let opts)]
    `(let ~(vec binds) ~body)
    body))

(defn wrap-bindings
  [opts body]
  (wrap-bindings* (apply hash-map opts) body))

(defmacro wrap-bindings-m
  [opts body]
  (wrap-bindings* opts body))

(defmacro wrap-bindings-m
  [forms body]
  (wrap-bindings forms body))

(defmacro defsuite
  [sym & body]
  `(def ~sym
     (let [suite-name# ~(name sym)]
       (vec (map (fn [f] (f suite-name#)) ~(vec body))))))

(defn rflatten
  [xs]
  (letfn [(seqize [x]
            (if (or (seq? x)
                    (vector? x)
                    (map? x))
              (map seqize x)
              x))]
    (flatten (seqize xs))))

(defn verify-<done>
  [form]
  (spit "/tmp/flat" (pr-str (rflatten form)))
  (if (some #{'<done>} (rflatten form))
    form
    (throw (Error. "form must include a call to <done>:" (pr-str form)))))

(defn opt-form-fn
  [[kw form]]
  `(fn [opts-unused# ~'<done>]
     ~(if (= "*" (last (str kw)))
        ~(verify-<done> form)
        `(do ~form (~'<done>)))))

(defn wrap-test-fn
  [pairs test-fn]
  (let [pres (filter (comp #{:pre :pre*} first) pairs)
        posts (filter (comp #{:post :post*} first) pairs)]
    (concat
      (map opt-form-fn pres)
      [test-fn]
      (map opt-form-fn posts))))

(defn wrap-test-fns
  [opts test-fns]
  (let [pairs (partition 2 opts)
        befores (filter (comp #{:before :before*} first) pairs)
        afters (filter (comp #{:after :after*} first) pairs)]
    (concat
      (map opt-form-fn befores)
      (apply concat (map (partial wrap-test-fn pairs) test-fns))
      (map opt-form-fn afters))))

(defmacro describe
  [text & forms]
  (let [[opts body] (opts-body-split forms)
        omap (apply hash-map opts)
        k (gensym "k-")
        o (gensym "opts-")]
    `(fn [suite-name#]
       (fn [~k]
         (let [~o {:descr ~text :suite suite-name#}]
           ~(wrap-bindings
              opts
              `((reduce
                  (fn [k1# f#]
                    #(f# ~o k1#))
                  ~k
                  (reverse ~(vec (wrap-test-fns opts body)))))))))))

(defmacro should*
  [text & body]
  `(menodora.core/should*
     ~text
     (fn [~'<done>]
       ~@(verify-<done> body))))

(defmacro should
  [text & body]
  `(menodora.core/should*
     ~text
     (fn [k#]
       ~@body
       (k#))))

(defmacro expect
  [pred & args]
  `(menodora.core/expect ~pred ~@args))

;;. vim: set lispwords+=macrolet,defsuite:
