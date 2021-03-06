(defproject
  menodora "0.1.5-SNAPSHOT"
  :description "Jasmine-like test library for clojurescript."

  :dependencies
  [[org.clojure/clojure "1.4.0"]
   [ring/ring-jetty-adapter "1.1.0"]]

  :plugins
  [[lein-cst "0.2.5"]]

  :exclusions
  [org.apache.ant/ant]

  :extra-classpath-dirs ["src"]

  :source-path "src"

  :cst
  {:runners
   {:console-rhino {:cljs menodora.test.core/test-menodora
                    :proc :rhino
                    :build :single}}
   :runner :console-rhino}

  :min-lein-version "2.0.0")
