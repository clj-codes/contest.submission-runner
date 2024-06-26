(ns integration.codes.clj.contest.submission-runner.db-test
  (:require [clojure.test :refer [use-fixtures]]
            [codes.clj.contest.submission-runner.db :as db]
            [com.stuartsierra.component :as component]
            [integration.codes.clj.contest.submission-runner.util :as util]
            [parenthesin.components.config.aero :as components.config]
            [parenthesin.components.db.jdbc-hikari :as components.database]
            [parenthesin.helpers.malli :as helpers.malli]
            [state-flow.api :refer [defflow]]
            [state-flow.assertions.matcher-combinators :refer [match?]]
            [state-flow.core :as state-flow :refer [flow]]
            [state-flow.state :as state]))

(use-fixtures :once helpers.malli/with-intrumentation)

(defn- create-and-start-components! []
  (component/start-system
   (component/system-map
    :config (components.config/new-config)
    :database (component/using (components.database/new-database)
                               [:config]))))

(defflow
  flow-integration-db-test
  {:init (util/start-system! create-and-start-components!)
   :cleanup util/stop-system!
   :fail-fast? true}
  (flow "creates a table, insert data and checks return in the database"
    [database (state-flow.api/get-state :database)]

    (state/invoke
     #(db/insert-wallet-transaction {:wallet/id #uuid "cd989358-af38-4a2f-a1a1-88096aa425a7"
                                     :wallet/btc_amount 2.0M
                                     :wallet/usd_amount_at 66000.00M}
                                    database))

    (flow "check transaction was inserted in db"
      (match? [#:wallet{:id #uuid "cd989358-af38-4a2f-a1a1-88096aa425a7"
                        :btc_amount 2.0M
                        :usd_amount_at 66000.00M
                        :created_at inst?}]
              (db/get-wallet-all-transactions database)))

    (flow "get current btc amount from db"
      (match? 2.0M
              (db/get-wallet-total database)))))
