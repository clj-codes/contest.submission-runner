(ns codes.clj.contest.submission-runner.ports.http-in.submission
  (:require [codes.clj.contest.submission-runner.adapters.submission :as adapters.submission]
            [codes.clj.contest.submission-runner.controllers.submission :as controllers.submission]))

(defn submit-code-execution!
  [{{submission :body} :parameters
    _components :components}]
  (let [id (-> submission
               (adapters.submission/wire->internal)
               (controllers.submission/submit-code-execution!))]
    {:status 201 :body {:id id}}))

