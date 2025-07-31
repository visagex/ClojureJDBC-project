(ns processer
  (:require [next.jdbc :as jdbc]
            [db-tables :as db-tables]
            [commands.dispatch :refer [handle-command]]
            [commands.user]
            [commands.events]
            [commands.attendance])
  (:gen-class :main true))

(def db
  {:dbtype "mysql"
   :dbname "test_db"
   :host "127.0.0.1"
   :port 3306
   :user "root"})

(def ds (jdbc/get-datasource db))



(defn parse-input [input]
  (let [tokens (clojure.string/split input #"\s+")
        cmd (keyword (first tokens))
        args (rest tokens)]
    {:cmd cmd :args args}))

(defn -main
  [& args]
  (db-tables/create-all ds)
  (loop []
    (let [input (read-line)]
      (if (= (str input) "exit")
        (println "exiting")
        (do
          (handle-command (parse-input input))
          (recur))))))




