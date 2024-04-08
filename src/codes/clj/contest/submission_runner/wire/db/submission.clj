(ns codes.clj.contest.submission-runner.wire.db.submission)

(def Submission
  [:map
   [:submission/id string?]
   [:submission/code string?]
   [:submission/code_hash string?]
   [:submission/language [:enum :clojure]]
   [:submission/test_cases
    [:map-of :keyword
     [:map
      [:input :any]
      [:output :any]]]]])
