(ns codes.clj.contest.submission-runner.routes
  (:require [codes.clj.contest.submission-runner.ports.http-in :as ports.http-in]
            [codes.clj.contest.submission-runner.schemas.wire-in :as schemas.wire-in]
            [reitit.swagger :as swagger]))

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "btc-wallet"
                            :description "small sample using the microservice-boilerplate"}}
           :handler (swagger/create-swagger-handler)}}]

   ["/wallet"
    {:swagger {:tags ["wallet"]}}

    ["/history"
     {:get {:summary "get all wallet entries and current total"
            :responses {200 {:body schemas.wire-in/WalletHistory}
                        500 {:body :string}}
            :handler ports.http-in/get-history}}]
    ["/deposit"
     {:post {:summary "do a deposit in btc in the wallet"
             :parameters {:body schemas.wire-in/WalletDeposit}
             :responses {201 {:body schemas.wire-in/WalletEntry}
                         400 {:body :string}
                         500 {:body :string}}
             :handler ports.http-in/do-deposit!}}]

    ["/withdrawal"
     {:post {:summary "do a withdrawal in btc in the wallet if possible"
             :parameters {:body schemas.wire-in/WalletWithdrawal}
             :responses {201 {:body schemas.wire-in/WalletEntry}
                         400 {:body :string}
                         500 {:body :string}}
             :handler ports.http-in/do-withdrawal!}}]]])
