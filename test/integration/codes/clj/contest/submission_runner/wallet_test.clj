(ns integration.codes.clj.contest.submission-runner.wallet-test
  (:require [clojure.test :refer [use-fixtures]]
            [integration.codes.clj.contest.submission-runner.util :as util]
            [matcher-combinators.matchers :as matchers]
            [parenthesin.helpers.malli :as helpers.malli]
            [parenthesin.helpers.state-flow.http :as state-flow.http]
            [parenthesin.helpers.state-flow.server.pedestal :as state-flow.server]
            [state-flow.api :refer [defflow]]
            [state-flow.assertions.matcher-combinators :refer [match?]]
            [state-flow.core :as state-flow :refer [flow]]))

(use-fixtures :once helpers.malli/with-intrumentation)

(defflow
  flow-integration-wallet-test
  {:init util/start-system!
   :cleanup util/stop-system!
   :fail-fast? true}
  (flow "should interact with system"

    (flow "prepare system with http-out mocks"
      (state-flow.http/set-http-out-responses! {"https://api.coindesk.com/v1/bpi/currentprice.json"
                                                {:body {:bpi {:USD {:rate_float 30000.00}}}
                                                 :status 200}})

      (flow "should insert deposit into wallet"
        (match? (matchers/embeds {:status 201
                                  :body  {:id string?
                                          :btc-amount 2
                                          :usd-amount-at 60000.0}})
                (state-flow.server/request! {:method :post
                                             :uri    "/wallet/deposit"
                                             :body   {:btc 2M}})))

      (flow "should insert withdrawal into wallet"
        (match? (matchers/embeds {:status 201
                                  :body  {:id string?
                                          :btc-amount -1
                                          :usd-amount-at -30000.0}})
                (state-flow.server/request! {:method :post
                                             :uri    "/wallet/withdrawal"
                                             :body   {:btc -1M}})))

      (flow "shouldn't insert deposit negative values into wallet"
        (match? {:status 400
                 :body  "btc deposit amount can't be negative."}
                (state-flow.server/request! {:method :post
                                             :uri    "/wallet/deposit"
                                             :body   {:btc -2M}})))

      (flow "shouldn't insert withdrawal positive values into wallet"
        (match? {:status 400
                 :body  "btc withdrawal amount can't be positive."}
                (state-flow.server/request! {:method :post
                                             :uri    "/wallet/withdrawal"
                                             :body   {:btc 2M}})))

      (flow "shouldn't insert withdrawal into wallet"
        (match? {:status 400
                 :body  "withdrawal amount bigger than the total in the wallet."}
                (state-flow.server/request! {:method :post
                                             :uri    "/wallet/withdrawal"
                                             :body   {:btc -2M}})))

      (flow "should list wallet deposits"
        (match? (matchers/embeds {:status 200
                                  :body {:entries [{:id string?
                                                    :btc-amount 2
                                                    :usd-amount-at 60000.0
                                                    :created-at string?}
                                                   {:id string?
                                                    :btc-amount -1
                                                    :usd-amount-at -30000.0
                                                    :created-at string?}]
                                         :total-btc 1
                                         :total-current-usd 30000.0}})
                (state-flow.server/request! {:method :get
                                             :uri    "/wallet/history"}))))))
