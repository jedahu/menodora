(ns menodora.core)

(def ^:dynamic *tests*
  (atom []))

(def ^:dynamic *opts*)

(def ^:dynamic *should-results*)

(def ^:dynamic *describe-results*)

(defn should
  [text f]
  (swap! *describe-results*
         conj
         [text (try
                 (binding [*should-results* (atom [])]
                   ((or (:pre *opts*) #()))
                   (f)
                   ((or (:post *opts*) #()))
                   @*should-results*)
                 (catch js/Object e e))]))
(defn expect
  [pred & args]
  (swap! *should-results*
         conj (try
                (if (apply (:test pred) args)
                  false
                  (apply (:message pred) args))
                (catch js/Object e e))))

(defn describe
  [text & args]
  (assert (odd? (count args))
          "menodora.core/describe requires an even number of arguments")
  (let [{:keys [before after] :as opts} (apply hash-map (butlast args))
        f (last args)]
    (binding [*opts* opts]
      (swap!
        *tests*
        conj
        [text (delay
                (try
                  (binding [*describe-results* (atom [])]
                    ((or before #()))
                    (f)
                    ((or after #()))
                    @*describe-results*)
                  (catch js/Object e e)))]))))

(defn ^:export run-tests
  [runner & {:keys [print-fn names finished]}]
  (binding [*print-fn* print-fn]
    ((or finished (constantly nil))
       (runner
         (if-let [names (and (seq names) (set names))]
           (filter #(some names (first %)) @*tests*)
           @*tests*)))))

(defn should-succ|fail
  [[_ result]]
  (cond
    (vector? result)
    [(count (filter not result))
     (count (filter boolean result))]

    result [0 1]

    :else [0 0]))

(defn describe-succ|fail
  [[_ result]]
  (cond
    (vector? @result)
    (reduce
      (fn [[x y] [s f]]
        [(+ x s) (+ y f)])
      (map should-succ|fail @result))

    @result [0 1]

    :else [0 0]))
