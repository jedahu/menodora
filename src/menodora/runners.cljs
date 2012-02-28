(ns menodora.runners
  (:require
    [menodora.core :as mc]))

(defn ^:export console
  [& {:keys [show]}]
  (fn [tests]
    (let [succ (atom [])
          fail (atom [])
          all (atom [])]
      (doseq [[text test :as descr] tests
              :let [[s f] (mc/describe-succ|fail descr)]]
        (swap! all conj [text @test])
        (if (= 0 f)
          (do
            (swap! succ conj [text @test])
            (print "."))
          (do
            (swap! fail conj [text @test])
            (print "F"))))
      (println)
      (doseq [[name shoulds] (if (= :all show) @all @fail)]
        (println name)
        (if (seq shoulds)
          (doseq [[text expects :as should] shoulds]
            (when (or (= :all show)
                      (< 0 (second (mc/should-succ|fail should))))
              (println "  should" text)
              (if (seq expects)
                (doseq [expect expects]
                  (if expect
                    (println "    Fail." expect)
                    (println "    Pass.")))
                (println "    Threw:" expects))))
          (println "  Threw:" should)))
      (count @fail))))
