(ns codes.clj.contest.submission-runner.wire.in.submission)

(def Submission
  [:map
   [:id string?]
   [:code string?]
   [:code-hash string?]
   [:language [:enum :clojure]]
   [:test-cases [:map-of :keyword
                 [:map
                  [:input :any]
                  [:output :any]]]]])

(comment
  (require '[malli.generator :as m])
  (require '[malli.core :as m.core])
  (m/generate Submission)
  (m.core/validate
   Submission
   {:code "(ns runner 
             (:require [clojure.test :refer [use-fixtures]])
             (defn my-sum [a b] (+ a b)))"
    :language :clojure
    :test-cases {:case-1 {:input "(my-sum 1 2)"
                          :output 3}
                 :case-2 {:input "(my-sum 2 3)"
                          :output 5}}})

;
  )
