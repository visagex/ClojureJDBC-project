(ns commands.events
  (:require [commands.dispatch :refer [handle-command]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [db-tables :refer [data-source]]))


(defmethod handle-command [:select :events] [{:keys [args]}]
  (try
    (let [query (if (empty? (rest args))
                  (sql/format (-> (h/select :*)
                                  (h/from :EVENTS)))
                  (let [event-id (Integer/parseInt (first (rest args)))]
                    (sql/format (-> (h/select :*)
                                    (h/from :EVENTS)
                                    (h/where [:= :eventID event-id])))))
          results (jdbc/execute! data-source query)]
      (if (empty? results)
        (println "No events found")
        (doseq [event results]
          (println (format "ID: %d, Name: %s, Start time: %s, Length: %s, Date: %s, Location: %s"
                           (:events/eventID event)
                           (:events/name event)
                           (:events/startTime event)
                           (:events/length event)
                           (:events/date event)
                           (:events/location event)
                           (:events/userID event))))))
    (catch Exception e
      (println "Error selecting events:" (.getMessage e)))))

(defmethod handle-command [:insert :events] [{:keys [args]}]
  (try
    (let [remaining-args (rest args)]
      (if (< (count remaining-args) 8)
        (println "To insert an event you need, eventID, name, startTime, length, date,
        location, private and userID")
        (let [id (Integer/parseInt (first remaining-args))
              name (second remaining-args)
              startTime (nth remaining-args 2)
              length (nth remaining-args 3)
              date (nth remaining-args 4)
              location (nth remaining-args 5)
              private (Integer/parseInt (nth remaining-args 6))
              userID (Integer/parseInt (last remaining-args))
              query (sql/format (-> (h/insert-into :EVENTS)
                                    (h/values [{:eventID id
                                                :name name
                                                :startTime startTime
                                                :length length
                                                :date date
                                                :location location
                                                :private private
                                                :userID userID}])))]
          (jdbc/execute! data-source query)
          (println (format "Event listed successfully! eventID=%d, name=%s, startTime=%s, date=%s,location=%s"
                           id name startTime date location)))))
    (catch Exception e
      (println "Error inserting event: " (.getMessage e)))))

(defmethod handle-command [:delete :events] [{:keys [args]}]
  (try
    (let [remaining-arg (Integer/parseInt (second args))]
        (let [id remaining-arg
              query (sql/format (-> (h/delete-from :EVENTS)
                                    (h/where [:= :eventID id])))]
          (jdbc/execute! data-source query)
          (println "successfully deleted")))
     (catch Exception e
       (println "Error deleting event" (.getMessage e)))))

(defmethod handle-command [:update :events] [{:keys [args]}]
  (try
     (let [remaining-args (rest args)]
       (let [id (Integer/parseInt (first remaining-args))
             update-col (second remaining-args)
             new-val (nth remaining-args 2)
             query (sql/format (-> (h/update :EVENTS)
                                   (h/set {update-col new-val})
                                   (h/where [:= :eventID id])))]
         (jdbc/execute! data-source query)))
     (catch Exception e
       (println "Error updating event" (.getMessage e)))))

(defmethod handle-command [:help :events] [{:keys [args]}]
  (println "FOR USE: ")
  (println "insert events : event-id, name, startTime, length, date, location,
  private (0 or 1) * boolean, user-id")
  (println "select events : blank or event-id")
  (println "delete events : event-id")
  (println "update events : event-id, name of column you wish to update, new value"))