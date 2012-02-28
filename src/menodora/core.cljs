(ns menodora.core)

(def ^:dynamic *tests*
  (atom []))

(def ^:dynamic *opts*)

(def ^:dynamic *should-results*)

(def ^:dynamic *describe-results*)

(defn opts-body-split
  [forms]
  (let [[opts body] (split-with (comp not list?) forms)]
    [(apply hash-map opts) body]))

(defn should
  [text f]
  (swap! *describe-results*
         conj
         [text (try
                 (binding [*should-results* (atom [])]
                   ((or (:before *opts*) #()))
                   (f)
                   ((or (:after *opts*) #()))
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
  [text f]
  (let [[{:keys [before-all after-all] :as opts} body] (opts-body-split forms)]
    (binding [*opts* opts]
      (swap!
        *tests*
        conj
        [text (delay
                (try
                  (binding [*describe-results* (atom [])]
                    ((or before-all #()))
                    (f)
                    ((or after-all #()))
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
