(ns codes.clj.contest.submission-runner.ports.http-in.submission)

(defn submit-code-execution!
  [{_submission :parameters
    _components :components}]
  {:status 201
   :body {:id (random-uuid)}})

