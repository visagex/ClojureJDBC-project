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
  (try
    (let [query (if (empty? (rest args))
                  (sql/format (h/select :* (h/from :EVENTS)))
                  (let [event-id (Integer/parseInt (first (rest args)))]
                    (sql/format (-> (h/select :*)
                                    (h/from :EVENTS)
                                    (h/where [:= :eventID event-id])))))
          results (jdbc/execute! ds query)]
      (if (empty? results)
        (println "No events found")
        (doseq [event results]
          (println (format "ID: %d, Name: %s, Start time: %s, Length: %s, Date: %s, Location: %s"
                           (:EVENTS/userID event)
                           (:EVENTS/name event)
                           (:EVENTS/startTime event)
                           (:EVENTS/length event)
                           (:EVENTS/date event)
                           (:EVENTS/location event))))))
    (catch Exception e
      (println "Error selecting events:" (.getMessage e)))))

(defmethod handle-command [:insert :events] [{:keys [args]}]
  )

(defmethod handle-command [:delete :events] [{:keys [args]}]
  )

(defmethod handle-command [:update :events] [{:keys [args]}]
  )
