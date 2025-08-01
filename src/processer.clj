(ns processer
  (:require [next.jdbc :as jdbc]
            [db-tables :as db-tables]
            [commands.dispatch :refer [handle-command]]
            [commands.user]
            [commands.events]
            [commands.attendance])
  (:gen-class :main true))

(defn parse-input [input]
  (let [tokens (clojure.string/split input #"\s+")
        cmd (keyword (first tokens))
        args (rest tokens)]
    {:cmd cmd :args args}))

(defn -main
  [& args]
  (loop []
    (let [input (read-line)]
      (if (= (str input) "exit")
        (println "exiting")
        (do
          (handle-command (parse-input input))
          (recur))))))




