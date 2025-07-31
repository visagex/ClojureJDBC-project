(ns commands.attendance
  (:require [commands.dispatch :refer [handle-command]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]))

(defmethod handle-command [:select :attendance] [{:keys [args]}]
  (println "selecting from attendance"))
