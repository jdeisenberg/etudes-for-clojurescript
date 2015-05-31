(ns servertest.core
  (:require-macros [hiccups.core :as hiccups])
  (:require [cljs.nodejs :as nodejs]
            [hiccups.runtime :as hiccupsrt]))

(nodejs/enable-util-print!)

(def express (nodejs/require "express"))

(defn generate-page! [request response]
  ;; retrieve user name (if any) that was sent to server
  (let [query (.-query request)
        user-name (if query (.-userName query) "")]
    (.send response
           (hiccups/html
             [:html
              [:head [:title "Server Example"]
               [:meta {:http-equiv "Content-type" :content "text/html"
                       :charset "utf-8"}]]
              [:body
               [:p "Enter your name:"]
               [:form {:action "/"
                       :method "get"}
                [:input {:name "userName" :value user-name}]
                [:input {:type "submit" :value "Send Data"}]]
               [:p (if (and user-name (not= user-name ""))
                     (str "Pleased to meet you, " user-name ".") "")]]]))))

(defn -main []
  (let [app (express)]
    (.get app "/" generate-page!)
    (.listen app 3000
             (fn []
               (println "Server started on port 3000")))))

(set! *main-cli-fn* -main)