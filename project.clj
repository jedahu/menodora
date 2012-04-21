(defproject
  menodora "0.1.1-SNAPSHOT"
  :description "Jasmine-like test library for clojurescript."

  :dependencies
  [[org.clojure/clojure "1.3.0"]
   [org.clojure/tools.macro "0.1.1"]
   [ring/ring-jetty-adapter "1.1.0-RC1"]]

  :plugins
  [[lein-cst "0.2.1"]]

  :exclusions
  [org.apache.ant/ant]

  :extra-classpath-dirs ["src"]

  :source-path "src"

  :cst
  {:suites [menodora.test.core/core-tests]})
