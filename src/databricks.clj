(ns databricks
  (:require [cheshire.core :refer :all]
            [babashka.curl :as curl]
            [babashka.process :refer [sh shell]]))


(defn fs-ls-2
  "List dbfs folders (dbfs:/)
  Argument
  - profile: Configuration profile (string)
  - dir: directory
  eg. (dbfs-ls \"poc\" \"\")"
  [profile dir]
  (-> (sh "databricks -o" "json" "-p" profile "fs" "ls" (str "dbfs:/" dir))
      :out
      (parse-string true)))

(defn list-clusters-2
  "List databricks clusters.
  Argument
  - profile: Configuration profile (string)
  - keywords: Select keys
  eg. (list-clusters \"poc\")
  eg. (list-clusters \"poc\" :cluster_id :cluster_name :state)"
  ([profile]
   (-> (shell {:out :string} "databricks" "-o" "json" "-p" profile "clusters"  "list")
       :out
       (parse-string true)))
  ([profile & keywords]
   (map #(select-keys % (vec keywords)) (list-clusters profile))))

(defn start-cluster
  "Start databricks cluster"
  [profile cluster-id]
  (shell "databricks" "-p" profile "clusters" "start" cluster-id))
(defn stop-cluster
  "Start databricks cluster"
  [profile cluster-id]
  (shell "databricks" "-p" profile "clusters" "delete" cluster-id))

(defn current-users
  [profile]
  (-> (sh "databricks" "-o" "json" "-p" profile "current-user" "me")
      :out
      (parse-string true)))

(defn list-tables
  [profile catalog database]
  (-> (sh "databricks" "-o" "json" "-p" profile "tables" "list" catalog database)
      :out
      (parse-string true)))

(defn create-headers [profile]
  {:headers {"Authorization" (str "Bearer " (:token profile))}})

(defn list-clusters [profile]
  (let [resp (curl/get (str (:host profile) "/api/2.0/clusters/list")
                       (create-headers profile)
                       )]
    (parse-string (:body resp) true)))

(defn fs-ls [profile path]
  (let [resp (curl/get (str (:host (:poc profiles)) "/api/2.0/dbfs/list?path=" path)
                       (create-headers profile)
                       )]
    (parse-string (:body resp) true)))


(defn list-users [scim-profile]
  (let [resp (curl/get (str (:host scim-profile) "/Users")
                       (create-headers scim-profile)
                       )]
    (parse-string (:body resp) true)))

(defn list-groups [scim-profile]
  (let [resp (curl/get (str (:host scim-profile) "/Groups")
                       (create-headers scim-profile)
                       )]
    (parse-string (:body resp) true)))

;(list-groups (:scim profiles))

(defn list-metastores [account-profile]
  (let [resp (curl/get (str (:host account-profile) "/metastores")
                       (create-headers account-profile)
                       )]
    resp))
;    (parse-string (:body resp) true)))