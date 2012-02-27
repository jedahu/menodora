(defproject
  menodora "0.1.0-SNAPSHOT"
  :description "Jasmine-like test library for clojurescript."

  :dependencies
  [[org.clojure/clojure "1.3.0"]
   [org.clojure/tools.macro "0.1.1"]]
  
  :dev-dependencies
  [[lein-clojurescript "1.1.1-SNAPSHOT"]
   [lein-repljs "0.1.2-SNAPSHOT"]]

  :extra-classpath-dirs ["src"]

  :source-path "src"

  :cljs
  {:output-to "out/all.js"
   :output-dir "out"
   :optimizations :simple
   :pretty-print true
   :src-dir "src"
   :test-cmd ["d8" "out/all.js"]})
