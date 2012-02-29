(ns menodora.core)

(def ^:dynamic *suite*
  (atom []))

(def ^:dynamic *opts*)

(def ^:dynamic *suite-opts*
  {})

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

(defn describe*
  [text opts f]
    (binding [*opts* opts]
      (swap!
        *suite*
        conj
        [text (delay
                (try
                  (binding [*describe-results* (atom [])]
                    ((or (:pre *suite-opts*) #()))
                    ((or (:before *opts*) #()))
                    (f)
                    ((or (:after *opts*) #()))
                    ((or (:post *suite-opts*) #()))
                    @*describe-results*)
                  (catch js/Object e e)))])))

(defn describe
  [text & args]
  (assert (odd? (count args))
          (str "menodora.core/describe requires an even number of args: "
               text " " (pr-str args)))
  (describe* text (apply hash-map (butlast args)) (last args)))

(defn suite
  [opts f]
  (let [{:keys [before after]} opts]
    (binding [*suite-opts* opts
              *suite* (atom [])]
      ((or before #()))
      (f)
      ((or after #()))
      @*suite*)))

(defn ^:export run-tests
  [runner suites & {:keys [print-fn finished]}]
  (binding [*print-fn* print-fn]
    (println (pr-str suites))
    (println "done")
    ((or finished (constantly nil))
       (runner
         (vec
           (for [s suites
                 :let [suite (if (map? s) s (first s))
                       names (if (map? s) nil (rest s))]]
             (if-let [names (and (seq names) (set names))]
               (update-in suite [1] (partial filter #(some names (first %))))
               suite)))))))

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

(defn suite-succ|fail
  [[_ result]]
  (reduce
    (fn [[x y] [s f]]
      [(+ x s) (+ y f)])
    (map describe-succ|fail result)))
