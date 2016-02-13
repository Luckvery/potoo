(ns potoo.datomic
  (:require [datomic.api :as d]
            [com.stuartsierra.component :as component]
            [clojure.java.io :as io]
            [taoensso.timbre :as log])
  (:import datomic.Util))


;; Queries

(defn find-potoos [conn]
  (d/q '[:find ?text ?author ?created
         :where [?p :pot/text ?text]
                [?p :pot/author ?author]
                [?p :pot/created ?created]]
       (d/db conn)))

;; Component

(defrecord DatomicDatabase [uri schema initial-data db-conn]
  component/Lifecycle
  (start [component]
    (log/info "Creating database connection to" uri)
    (d/create-database uri)
    (let [c (d/connect uri)]
      @(d/transact c schema)
      @(d/transact c initial-data)
      (assoc component :db-conn c)))
  (stop [component]
    component))

(defn new-database [db-uri]
  (DatomicDatabase.
    db-uri
    (first (Util/readAll (io/reader (io/resource "datomic/schema.edn"))))
    (first (Util/readAll (io/reader (io/resource "datomic/initial.edn"))))
    nil))

(comment
  (let [datomic (new-database "datomic:mem://localhost:4334/potoos")
        conn (-> (.start datomic) :db-conn)]
       (find-potoos conn)))
