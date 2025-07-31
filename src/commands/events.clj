(ns commands.events
  (:require [commands.dispatch :refer [handle-command]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]))


(def ds (jdbc/get-datasource {:dbtype "mysql"
                              :dbname "test_db"
                              :host "127.0.0.1"
                              :port 3306
                              :user "root"}))


(defmethod handle-command [:select :events] [{:keys [args]}]
  (println "selecting from events" (rest args)))

(defmethod handle-command [:insert :events] [{:keys [args]}]
  )

(defmethod handle-command [:delete :events] [{:keys [args]}]
  )

(defmethod handle-command [:update :events] [{:keys [args]}]
  )
