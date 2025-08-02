(ns commands.user
  (:require [commands.dispatch :refer [handle-command]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [db-tables :refer [data-source]]))


(defmethod handle-command [:select :user] [{:keys [args]}]
  (try
    (let [query (if (empty? (rest args))
                  (sql/format (-> (h/select :*)
                                  (h/from :USER)))
                  (let [user-id (Integer/parseInt (first (rest args)))]
                    (sql/format (-> (h/select :*)
                                    (h/from :USER)
                                    (h/where [:= :userID user-id])))))
          results (jdbc/execute! data-source query)]
      (if (empty? results)
        (println "No users found")
        (doseq [user results]
          (println (format "ID: %d, Name: %s, Email: %s" 
                          (:user/userID user)
                          (:user/name user)
                          (:user/email user))))))
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
          (jdbc/execute! data-source query)
          (println (format "User inserted successfully: ID=%d, Name=%s, Email=%s" 
                          user-id name email)))))
    (catch Exception e
      (println "Error inserting user:" (.getMessage e)))))

(defmethod handle-command [:delete :user] [{:keys [args]}]
  (try
    (let [user-id (Integer/parseInt (second args))]  ; second gets the element after :user
      (let [query (sql/format (-> (h/delete-from :USER)
                                  (h/where [:= :userID user-id])))]
        (jdbc/execute! data-source query)
        (println "User deleted successfully")))
    (catch NumberFormatException e
      (println "Invalid user ID format"))
    (catch Exception e
      (println "Error deleting user:" (.getMessage e)))))

(defmethod handle-command [:update :user] [{:keys [args]}]
  ;;get user id as first arg and then get what they wish to update and third value being the new value
  (try
     (let [remaining-args (rest args)]
       (let [id (Integer/parseInt (first remaining-args))
             update-col (second remaining-args)
             new-value (nth remaining-args 2)
             query (sql/format (-> (h/update :USER)
                                   (h/set {update-col new-value})
                                   (h/where [:= :userID id])))]
         (jdbc/execute! data-source query)))
     (catch Exception e
       (println "Error updating user: " (.getMessage e)))))

(defmethod handle-command [:help :user] [{:keys [args]}]
  (println "FOR USE: ")
  (println "insert user : user-id, name, email, password")
  (println "select user : blank or user-id")
  (println "delete user : user-id")
  (println "update user : user-id, name of column you wish to update, new value"))

