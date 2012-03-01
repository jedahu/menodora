(ns menodora.runners
  (:require
    [menodora.core :as mc]))

(defn console-dots
  [suites]
  (doseq [[suite-name results] suites
          [text shoulds] results
          :let [[s f] (mc/describe-succ|fail [text shoulds])]]
    (print (if (= 0 f) "." "F")))
  (println))

(defn indentln [n & args]
  (apply println
         (str (apply str (repeat n \space))
              (first args))
         (rest args)))

(defn console-detail
  [suites all?]
  (doseq [[suite-name results] suites
          :let [[s f] (mc/suite-succ|fail [suite-name results])]
          ;:when (or (= :all show) (< 0 f))
          ]
    (when (or all? (< 0 f))
      (println "Suite:" suite-name)
      (doseq [[text shoulds] results
              :let [[s f] (mc/describe-succ|fail [text shoulds])]
              ;:when (or (= :all show) (< 0 f))
              ]
        (when (or all? (< 0 f))
          (indentln 2 text)
          (if (vector? (shoulds))
            (doseq [[text expects :as should] (shoulds)
                    :let [[s f] (mc/should-succ|fail should)]
                    ;:when (or (= :all show) (< 0 f))
                    ]
              (when (or all? (< 0 f))
                (indentln 4 "should" text)
                (if (vector? expects)
                  (doseq [expect expects]
                    (if expect
                      (indentln 6 "Fail." expect)
                      (indentln 6 "Pass.")))
                  (indentln 6 "Threw:" expects))))
            (indentln 4 "Threw:" (shoulds))))))))

(defn ^:export console
  [& {:keys [show]}]
  (fn [suites]
    (console-dots suites)
    (console-detail suites (= :all show))
    (let [[s f] (reduce (fn [[x y] [s f]] [(+ x s) (+ y f)])
                        (map mc/suite-succ|fail suites))]
      (println (str "Passed: " s ". Failed: " f "."))
      f)))
