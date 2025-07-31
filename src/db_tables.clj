(ns db-tables
  (:require [next.jdbc :as jdbc]
            [honey.sql :as sql]
            [honey.sql.helpers :as h]))

(sql/set-dialect! :mysql)

;;(with-columns [:id :int [:not nil]] [:name [:varchar 32] [:default ""]])

(defn create-event-table [ds]
  (jdbc/execute! ds
                 (sql/format
                   (-> (h/create-table :EVENTS :if-not-exists)
                       (h/with-columns
                         [[:eventID :int [:not nil]]
                          [:name [:varchar 32] [:default ""]]
                          [:startTime :datetime]
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
