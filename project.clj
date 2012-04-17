(defproject
  menodora "0.1.0-SNAPSHOT"
  :description "Jasmine-like test library for clojurescript."

  :dependencies
  [[org.clojure/clojure "1.3.0"]
   [org.clojure/tools.macro "0.1.1"]]
  
  :plugins
  [[lein-cst "0.1.0-SNAPSHOT"]]

  :exclusions
  [org.apache.ant/ant]

  :extra-classpath-dirs ["src"]

  :source-path "src"

  :cst
  {:output-to "out/all.js"
   :output-dir "out"
   :optimizations :whitespace
   :pretty-print true
   :src-dir "src"
   ;:test-cmd "menodora.test._run_rhino()"})
   :test-cmd ["d8" "out/all.js" "-e" "menodora.test._run_tests(quit, write)"]})
