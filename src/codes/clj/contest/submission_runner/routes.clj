(ns codes.clj.contest.submission-runner.routes
  (:require [codes.clj.contest.submission-runner.ports.http-in :as ports.http-in]
            [codes.clj.contest.submission-runner.ports.http-in.submission :as ports.http-in.submission]
            [codes.clj.contest.submission-runner.schemas.wire-in :as schemas.wire-in]
            [codes.clj.contest.submission-runner.wire.in.submission :as wire.in.submission]
            [codes.clj.contest.submission-runner.wire.out.submission :as wire.out.submission]
            [reitit.swagger :as swagger]))

(def routes
  [["/swagger.json"
    {:get {:no-doc true
           :swagger {:info {:title "Submission Runner"
                            :description "Submission Runner API"}}
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
             :handler ports.http-in/do-withdrawal!}}]]

   ;; submitions routes
   ["/code"
    {:swagger {:tags ["Code Runner"]}}
    ["/submission"
     {:post {:summary "Submit to execution your code"
             :parameters {:body wire.in.submission/Submission}
             :responses {201 {:body wire.out.submission/SubmissionResult}
                         400 {:body :string}
                         500 {:body :string}}
             :handler ports.http-in.submission/submit-code-execution!}}]]])


