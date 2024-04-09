(ns codes.clj.contest.submission-runner.adapters.submission
  (:require [codes.clj.contest.submission-runner.wire.db.submission :as wire.db.submission]
            [codes.clj.contest.submission-runner.wire.in.submission :as wire.in.submission]))

(defn wire->internal
  {:malli/schema [:=> [:cat wire.in.submission/Submission] wire.db.submission/Submission]}
  [{:keys [id code code-hash language test-cases]}]
  {:submission/id id
   :submission/code code
   :submission/code_hash code-hash
   :submission/language language
   :submission/test_cases test-cases})
