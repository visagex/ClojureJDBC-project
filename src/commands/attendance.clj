(ns commands.attendance
  (:require [commands.dispatch :refer [handle-command]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [db-tables :refer [data-source]]))

(defmethod handle-command [:select :attendance] [{:keys [args]}]
  (try
    (let [query (if (empty? (rest args))
                  (sql/format (h/select :* (h/from :ATTENDANCE)))
                  (let [user-id (Integer/parseInt (first (rest args)))
                        event-id (Integer/parseInt (second (rest args)))]
                    (sql/format (-> (h/select :*)
                                    (h/from :ATTENDANCE)
                                    (h/where [:and
                                              [:= :userID user-id]
                                              [:= :eventID event-id]])))))
          results (jdbc/execute! data-source query)]
      (if (empty? results)
        (println "Nothing found")
        (doseq [attend results]
          (println (format "userID: %d, eventID: %d, rsvpDate: %s"
                           (:ATTENDANCE/userID attend)
                           (:ATTENDANCE/eventID attend)
                           (:ATTENDANCE/rsvpDate attend))))))
    (catch Exception e
      (println "Error selecting attendance:" (.getMessage e)))))

(defmethod handle-command [:insert :attendance] [{:keys [args]}]
  ((try
     (let [remaining-args (rest args)]
       (if (< (count remaining-args) 3)
         (println "missing values, make sure you insert userID, eventID, and rsvpDate")
         (let [user-id (Integer/parseInt (first remaining-args))
               event-id (Integer/parseInt (second remaining-args))
               rsvp (nth remaining-args 2)
               query (sql/format (-> (h/insert-into :ATTENDANCE)
                                     (h/values [{:userID user-id
                                                 :eventID event-id
                                                 :rsvpDate rsvp}])))]
           (jdbc/execute! data-source query))))
     (catch Exception e
       (println "Error inserting attendance" (.getMessage e))))))

(defmethod handle-command [:delete :attendance] [{:keys [args]}]
  ((try
     (let [remaining-args ((rest args))
           user-id (Integer/parseInt (first remaining-args))
           event-id (Integer/parseInt (second remaining-args))]
       (if (and (int? user-id) (int? event-id))
         (let [query (sql/format (-> (h/delete-from :ATTENDANCE)
                                     (h/where [:and
                                               [:= :userID user-id]
                                               [:= :eventID event-id]])))]
           (jdbc/execute! data-source query)
           (println "successfully deleted"))
         (println "Make sure to input userID and eventID")))
     (catch Exception e
       (println "Error deleting attendance" (.getMessage e))))))

(defmethod handle-command [:update :attendance] [{:keys [args]}]
  ((try
     (let [remaining-args (rest args)]
       (let [user-id (Integer/parseInt (first remaining-args))
             event-id (Integer/parseInt (second remaining-args))
             update-col (nth remaining-args 2)
             new-value (nth remaining-args 3)
             query (sql/format (-> (h/update :USERS)
                                   (h/set {update-col new-value})
                                   (h/where [:and
                                             [:= :userID user-id]
                                             [:= :eventID event-id]])))]
         (jdbc/execute! data-source query)))
     (catch Exception e
       (println "Error updating attendance" (.getMessage e))))))