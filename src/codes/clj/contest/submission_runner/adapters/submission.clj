(ns codes.clj.contest.submission-runner.adapters.submission
  (:require [codes.clj.contest.submission-runner.wire.db.submission :as wire.db.submission]
            [codes.clj.contest.submission-runner.wire.in.submission :as wire.in.submission]))

(defn wire->internal
  {:malli/schema [:=> [:cat wire.in.submission/Submission] wire.db.submission/Submission]}
  [wire]
  {:submission/id (-> wire :id)
   :submission/code (-> wire :code)
   :submission/code_hash (-> wire :code-hash)
   :submission/language (-> wire :language)
   :submission/test_cases (-> wire :test-cases)})
