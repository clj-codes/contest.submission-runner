(ns codes.clj.contest.submission-runner.ports.http-in.submission)

(defn submit-code-execution!
  [{submission :parameters
    components :components}]
  {:status 201
   :body {:id (random-uuid)}})

