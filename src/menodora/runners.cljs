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

(defn console-detail
  [suites]
  (doseq [[suite-name results] suites
          :let [[s f] (mc/suite-succ|fail [suite-name results])]
          ;:when (or (= :all show) (< 0 f))
          ]
    (when (or (= :all show) (< 0 f))
      (println "Suite:" suite-name)
      (doseq [[text shoulds] results
              :let [[s f] (mc/describe-succ|fail [text shoulds])]
              ;:when (or (= :all show) (< 0 f))
              ]
        (when (or (= :all show) (< 0 f))
          (println "  " text)
          (if (vector? @shoulds)
            (doseq [[text expects :as should] @shoulds
                    :let [[s f] (mc/expect-succ|fail should)]
                    ;:when (or (= :all show) (< 0 f))
                    ]
              (when (or (= :all show) (< 0 f))
                (println "    should" text)
                (if (vector? expects)
                  (doseq [expect expects]
                    (if expect
                      (println "      Fail." expect)
                      (println "      Pass.")))
                  (println "      Threw:" expects))))
            (println "    Threw:" @shoulds)))))))

(defn ^:export console
  [& {:keys [show]}]
  (fn [suites]
    ;(console-dots suites)
    ;(console-detail suites)
    (apply + (map (comp second mc/suite-succ|fail) suites))))
