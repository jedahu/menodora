(ns menodora.server
  (:require
    [ring.adapter.jetty :as jetty])
  (:import
    [java.io File]))

(defn- serve-cljs-
  [{:keys [output-dir output-to] :as build-opts}
   & {:keys [test-uri handler port] :as opts :or {test-uri "/" port 8765}}]
  (println "    running jetty")
  (println (str "    test url: http://localhost:" port test-uri))
  (jetty/run-jetty
    (fn [req]
      (cond
        (= test-uri (:uri req))
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body
         (str "<html><head><meta charset='UTF-8'/></head></body>"
              (when (.exists (File. (str output-dir "/goog/base.js")))
                "<script src='"
                (str output-dir "/goog/base.js")
                "'></script>")
              "<script src='/" output-to "'></script>")}

        (.exists (File. (str "." (:uri req))))
        {:status 200
         :body (slurp (str "." (:uri req)))}

        handler
        (handler req)
        
        :else
        {:status 404}))
    {:port port :join? false}))

(def serve-cljs (memoize serve-cljs-))
