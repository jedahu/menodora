(ns menodora.core)

(def ^:dynamic *suite*
  (atom []))

(def ^:dynamic *descr-opts*)

(def ^:dynamic *run-opts*
  {})

(def ^:dynamic *should-results*)

(def ^:dynamic *describe-results*)

(defn ?catch
  [f]
  (if (:catch? *run-opts*)
    (try (f) (catch js/Object e e))
    (f)))

(defn should
  [text f]
  (swap! *describe-results*
         conj
         [text (?catch
                 (fn []
                   (binding [*should-results* (atom [])]
                     ((or (:pre *descr-opts*) #()))
                     (f)
                     ((or (:post *descr-opts*) #()))
                     @*should-results*)))]))
(defn expect
  [pred & args]
  (swap! *should-results*
         conj (?catch
                (fn []
                  (if (apply (:test pred) args)
                    false
                    (apply (:message pred) args))))))

(defn describe*
  [text opts f]
  (swap!
    *suite*
    conj
    [text (memoize
            (fn []
              (?catch
                (fn []
                  (binding [*descr-opts* opts
                            *describe-results* (atom [])]
                    ((or (:before *descr-opts*) #()))
                    (f)
                    ((or (:after *descr-opts*) #()))
                    @*describe-results*)))))]))

(defn describe
  [text & args]
  (assert (odd? (count args))
          (str "menodora.core/describe requires an even number of args: "
               text " " (pr-str args)))
  (describe* text (apply hash-map (butlast args)) (last args)))

(defn suite
  [title f]
  [title (binding [*suite* (atom [])]
           (f)
           @*suite*)])

(defn ^:export run-tests
  [runner suites & {:keys [finished] :or {:catch? true} :as opts}]
  (binding [*run-opts* opts]
    ((or finished (constantly nil))
       (runner suites))))

(defn ^:export filter-suite
  [suite & names]
  (update-in suite [1] (partial filter #(some (set names) (first %)))))

(defn expect-succ|fail
  [result]
  [(not result) (boolean result)])

(defn should-succ|fail
  [[_ result]]
  (cond
    (vector? result)
    [(count (filter not result))
     (count (filter boolean result))]

    result [0 1]

    :else [0 0]))

(defn describe-succ|fail
  [[title result]]
  (cond
    (vector? (result))
    (reduce
      (fn [[s f] [x y]]
        (if (< 0 y)
          [s (inc f)]
          [(inc s) f]))
      [0 0]
      (map should-succ|fail (result)))

    (result) [0 1]

    :else [0 0]))

(defn suite-succ|fail
  [[_ result]]
  (reduce
    (fn [[s f] [x y]]
      [(+ x s) (+ y f)])
    (map describe-succ|fail result)))
