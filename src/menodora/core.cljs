(ns menodora.core
  (:require
    [menodora.runner :as r]))

(def suite-runner
  (atom nil))

(defn expect
  [pred & args]
  (if (apply (:test pred) args)
    (r/-expected @suite-runner)
    (r/-unexpected @suite-runner (apply (:message pred) args) args)))

(defn should*
  [text f]
  (fn [opts k]
    (let [title [(:suite opts) (:descr opts) text]]
      (r/-test-start @suite-runner title)
      (f (fn []
           (r/-test-end @suite-runner title)
           (k))))))

(defn run-tests [runner tests & runner-opts]
  (reset! suite-runner (apply runner runner-opts))
  ((reduce
     (fn [k1 f]
       #(f k1))
     #(r/-finished @suite-runner)
     (reverse tests))))

(defn ^:export run-suites
  [runner finish suites & runner-opts]
  (finish
    (try
      (apply run-tests
             runner
             (apply concat suites)
             runner-opts)
      (catch js/Object e
        e))))

(defn ^:export filter-tests
  [tests & names]
  (filter tests #(some (set names) (apply str (first %)))))

(defn make-run-suites
  [runner finish & {:as runner-opts}]
  (fn [suites & {:as runner-opts1}]
    (apply run-suites runner finish suites
           (apply concat (merge runner-opts runner-opts1)))))
