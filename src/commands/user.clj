(ns commands.user
  (:require [commands.dispatch :refer [handle-command]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]))

(def ds (jdbc/get-datasource {:dbtype "mysql"
                              :dbname "test_db" 
                              :host "127.0.0.1"
                              :port 3306
                              :user "root"}))

(defmethod handle-command [:select :user] [{:keys [args]}]
  (try
    (let [query (if (empty? (rest args))
                  (sql/format (h/select :* (h/from :USER)))
                  (let [user-id (Integer/parseInt (first (rest args)))]
                    (sql/format (-> (h/select :*)
                                    (h/from :USER)
                                    (h/where [:= :userID user-id])))))
          results (jdbc/execute! ds query)]
      (if (empty? results)
        (println "No users found")
        (doseq [user results]
          (println (format "ID: %d, Name: %s, Email: %s" 
                          (:USER/userID user)
                          (:USER/name user)
                          (:USER/email user))))))
    (catch Exception e
      (println "Error selecting users:" (.getMessage e)))))

(defmethod handle-command [:insert :user] [{:keys [args]}]
  (try
    (let [remaining-args (rest args)]
      (if (< (count remaining-args) 4)
        (println "Usage: insert user <userID> <name> <email> <password>")
        (let [user-id (Integer/parseInt (first remaining-args))
              name (second remaining-args)
              email (nth remaining-args 2)
              password (nth remaining-args 3)
              query (sql/format (-> (h/insert-into :USER)
                                    (h/values [{:userID user-id
                                                :name name
                                                :email email
                                                :password password}])))]
          (jdbc/execute! ds query)
          (println (format "User inserted successfully: ID=%d, Name=%s, Email=%s" 
                          user-id name email)))))
    (catch Exception e
      (println "Error inserting user:" (.getMessage e)))))

(defmethod handle-command [:delete :user] [{:keys [args]}]
  ;;first option delete from user-id then check for where clauses. for example
  ;; delete * FROM user where name is ___
  ((try
     (let [remaining-args (rest args)]
       (if (int? (first remaining-args))
         (let [id (first remaining-args)
               query (sql/format (-> (h/delete-from :USER)
                                     (h/where [:= :userID id])))]
           (jdbc/execute! ds query))
         (if (string? (first remaining-args))
           (let [name (first remaining-args)
                 query (sql/format (-> (h/delete-from :USER)
                                       (h/where [:= :name name])))]
             (jdbc/execute! ds query)
             (println "successfully deleted!")))))
     (catch Exception e
       (println "Error deleting user: " (.getMessage e))))))

(defmethod handle-command [:update :user] [{:keys [args]}]
  ;;get user id as first arg and then get what they wish to update and third value being the new value
  ((try
     (let [remaining-args (rest args)]
       (let [id (first remaining-args)
             update-col (second remaining-args)
             new-value (nth remaining-args 2)
             query (sql/format (-> (h/update :USERS)
                                   (h/set {update-col new-value})
                                   (h/where [:= :userID id])))]
         (jdbc/execute! ds query)))
     (catch Exception e
       (println "Error updating user: " (.getMessage e))))))

(defmethod handle-command [:help :user]
  (println ""))

