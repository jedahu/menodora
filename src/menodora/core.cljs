(ns menodora.core
  (:require
    [menodora.runner :as r]))

(def descr-opts
  (atom {}))

(def run-opts
  (atom {}))

(def suite-list
  (atom ()))
(def describe-list
  (atom ()))
(def should-list
  (atom ()))

(def suite-runner
  (atom nil))

(defn ?catch
  [f]
  (if (:catch? @run-opts)
    (try (f) (catch js/Object e e))
    (f)))

(defn all-done!
  []
  ((:finish @run-opts) (r/-finished @suite-runner)))

(defn suite-done!
  []
  (r/-suite-end @suite-runner)
  (swap! suite-list rest)
  (if-let [x (first @suite-list)]
    ((second x))
    (all-done!)))

(defn describe-done!
  []
  ((or (:after @descr-opts) #()))
  (r/-describe-end @suite-runner)
  (swap! describe-list rest)
  (if-let [x (first @describe-list)]
    (?catch (second x))
    (suite-done!)))

(defn should-done!
  []
  ((or (:post @descr-opts) #()))
  (r/-should-end @suite-runner)
  (swap! should-list rest)
  (if-let [x (first @should-list)]
    (?catch (second x))
    (describe-done!)))

(def done! should-done!)

(defn opts-body-split
  [forms]
  (loop [forms forms opts []]
    (if (keyword? (first forms))
      (let [[k v & more] forms]
        (recur more (conj opts k v)))
      [opts forms])))

(defn expect
  [pred & args]
  (if (apply (:test pred) args)
    (r/-expected @suite-runner)
    (r/-unexpected @suite-runner (apply (:message pred) args) args)))

(defn should*
  [text f]
  [text
   (fn []
     (r/-should-start @suite-runner text)
     ((or (:pre @descr-opts) #()))
     (f))])

(defn should
  [text f]
  (should* text
           (fn []
             (f)
             (should-done!))))

(defn describe*
  [text opts f]
  [text
   (fn []
     (let [shs (f)]
       (reset! descr-opts opts)
       (reset! should-list shs)
       (r/-describe-start @suite-runner text)
       ((or (:before @descr-opts) #()))
       ((second (first shs)))))])

(defn describe
  [text & args]
  (let [[opts forms] (opts-body-split args)]
    (describe* text (apply hash-map opts) forms)))

(defn suite
  [title & describes]
  [title
   (fn []
     (reset! describe-list describes)
     (r/-suite-start @suite-runner title)
     ((second (first describes))))])

(defn run-suites [runner suites]
  (reset! suite-runner runner)
  (reset! suite-list suites)
  ((second (first suites))))

(defn ^:export run-tests
  [runner suites & {:keys [finish] :or {:catch? true} :as opts}]
  (reset! run-opts opts)
  (run-suites (runner) suites))

(defn ^:export filter-suite
  [suite & names]
  (update-in suite [1] (partial filter #(some (set names) (first %)))))
