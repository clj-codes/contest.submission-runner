(ns integration.codes.clj.contest.submission-runner.submission-runner-test
  (:require [clojure.test :refer [use-fixtures]]
            [integration.codes.clj.contest.submission-runner.util :as util]
            [matcher-combinators.matchers :as matchers]
            [parenthesin.helpers.malli :as helpers.malli]
            [clj-commons.digest :as digest]
            [parenthesin.helpers.state-flow.server.pedestal :as state-flow.server]
            [state-flow.api :refer [defflow]]
            [state-flow.assertions.matcher-combinators :refer [match?]]
            [state-flow.core :as state-flow :refer [flow]]))

(use-fixtures :once helpers.malli/with-intrumentation)

(defflow
  flow-integration-wallet-test
  {:init util/start-system!
   :cleanup util/stop-system!
   :fail-fast? true}

  (flow "should receive an submission"
    [:let [id (random-uuid)
           code "(ns runner 
                   (:require [clojure.string :as str])) 
                  (defn my-sum [a b] (+ a b))"
           submission-input {:id id
                             :code code
                             :code-hash (digest/md5 code)
                             :language :clojure
                             :test-cases {:case-1 {:input "(my-sum 1 2)"
                                                   :output 3}
                                          :case-2 {:input "(my-sum 2 3)"
                                                   :output 5}}}]
     return (state-flow.server/request! {:method :post
                                         :uri    "/code/submission"
                                         :body submission-input})]

    (match? (matchers/embeds {:status 201
                              :body  {:id string?}})
            return)))

