(ns codes.clj.contest.submission-runner.schemas.wire-out)

(def RateFloat
  [:map
   [:rate_float number?]])

(def USD
  [:map
   [:USD RateFloat]])

(def CoinDeskResponse
  [:map
   [:bpi USD]])
