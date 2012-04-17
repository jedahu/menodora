(ns menodora.core
  (:require
    [menodora.runner :as r]))

(def suite-runner
  (atom nil))

(defn ?catch
  [f]
  (if (:catch? @run-opts)
    (try (f) (catch js/Object e e))
    (f)))

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

(defn ^:export run-tests [runner tests
                 & {:keys [finish] :or {:catch? true} :as opts}]
  (reset! suite-runner runner)
  ((reduce
     (fn [k1 f]
       #(f k1))
     #(finish (r/-finished runner))
     (reverse tests))))

(defn ^:export run-suites [runner suites
                  & {:keys [finish] :or {:catch? true} :as opts}]
  (apply run-tests runner (apply concat suites) (apply concat opts)))

(defn ^:export filter-tests
  [tests & names]
  (filter tests #(some (set names) (apply str (first %)))))
