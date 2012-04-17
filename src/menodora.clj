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

(defmacro describe
  [text & forms]
  (let [[opts body] (opts-body-split forms)
        omap (apply hash-map opts)
        k (gensym "k-")
        o (gensym "opts-")]
    `(fn [suite-name#]
       (fn [~k]
         (let [~o (assoc
                    ~(dissoc
                       (apply hash-map opts)
                       :before :after :pre :post)
                    :descr ~text
                    :suite suite-name#)]
           ~(wrap-bindings
              opts
              `((reduce
                  (fn [k1# f#]
                    (fn []
                      ~(:pre omap)
                      (f# ~o
                         (fn []
                           ~(:post omap)
                           (k1#)))))
                  (fn []
                    ~(:after omap)
                    (~k))
                  (do
                    ~(:before omap)
                    (reverse ~(vec body)))))))))))

(defmacro should*
  [text kname & body]
  `(menodora.core/should* ~text (fn [~kname] ~@body)))

(defmacro should
  [text & body]
  `(menodora.core/should* ~text (fn [k#] ~@body (k#))))

(defmacro expect
  [pred & args]
  `(menodora.core/expect ~pred ~@args))

;;. vim: set lispwords+=macrolet,defsuite:
