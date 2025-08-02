(ns db-tables
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]
            [commands.dispatch :refer [handle-command]]))

(sql/set-dialect! :mysql)

;;(with-columns [:id :int [:not nil]] [:name [:varchar 32] [:default ""]])
(def db
  {:dbtype "mysql"
   :dbname "test_db"
   :host "127.0.0.1"
   :port 3306
   :user "root"})

(def data-source (jdbc/get-datasource db))

(defn create-event-table [ds]
  (jdbc/execute! ds
                 (sql/format
                   (-> (h/create-table :EVENTS :if-not-exists)
                       (h/with-columns
                         [[:eventID :int [:not nil]]
                          [:name [:varchar 32] [:default ""]]
                          [:startTime :time]
                          [:length :time]
                          [:date :date]
                          [:location :text]
                          [:private :boolean]
                          [:userID :int]
                          [[:foreign-key :userID][:references :USER :userID]]
                          [[:primary-key :eventID]]])))))

(defn create-user-table [ds]
  (jdbc/execute! ds
                 (sql/format
                   (-> (h/create-table :USER :if-not-exists)
                       (h/with-columns
                         [[:userID :int [:not nil]]
                          [:name [:varchar 32] [:default ""]]
                          [:email [:varchar 60] [:unique [:not nil]]]
                          [:password [:varchar 32] [:not nil]]
                          [[:primary-key :userID]]])))))


(defn create-attendance-table [ds]
  (jdbc/execute! ds
                 (sql/format
                   (-> (h/create-table :ATTENDANCE :if-not-exists)
                       (h/with-columns
                         [[:userID :int]
                          [:eventID :int]
                          [:rsvpDate :date]
                          [[:foreign-key :userID][:references :USER :userID]]
                          [[:foreign-key :eventID][:references :EVENTS :eventID]]
                          [[:primary-key :userID :eventID]]])))))


(defn create-all [ds]
  (create-user-table ds)
  (create-event-table ds)
  (create-attendance-table ds))


;;make it explicit if you want to create tables
(defmethod handle-command [:init :table] [{:keys [args]}]
  (create-all data-source)
  (println "initialized tables"))

(defmethod handle-command [:drop :table] [{:keys [args]}]
  (try
    (let [table-name (keyword (second args))
          query (sql/format (-> (h/drop-table table-name)))]
      (jdbc/execute! data-source query)
      (println table-name))
    (catch Exception e
      (println "Error dropping table" (.getMessage e)))))