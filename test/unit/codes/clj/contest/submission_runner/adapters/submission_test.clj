(ns unit.codes.clj.contest.submission-runner.adapters.submission-test
  (:require [clj-commons.digest :as digest]
            [clojure.test :refer [deftest is testing use-fixtures]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as properties]
            [codes.clj.contest.submission-runner.adapters.submission :as adapters.submission]
            [codes.clj.contest.submission-runner.wire.db.submission :as wire.db.submission]
            [codes.clj.contest.submission-runner.wire.in.submission :as wire.in.submission]
            [malli.core :as m]
            [malli.generator :as mg]
            [matcher-combinators.test :refer [match?]]
            [parenthesin.helpers.malli :as helpers.malli]))

(use-fixtures :once helpers.malli/with-intrumentation)

(defspec wirer->internal-spec 50
  (properties/for-all [code (mg/generator wire.in.submission/Submission)]
                      (m/validate wire.db.submission/Submission (adapters.submission/wire->internal code))))

(def id (random-uuid))
(def code "(ns runner 
                   (:require [clojure.string :as str])) 
                  (defn my-sum [a b] (+ a b))")

(def code-submission {:id (str id)
                      :code code
                      :code-hash (digest/md5 code)
                      :language :clojure
                      :test-cases {:case-1 {:input "(my-sum 1 2)"
                                            :output 3}
                                   :case-2 {:input "(my-sum 2 3)"
                                            :output 5}}})

(deftest wire->internal
  (testing "adpater to db submission"
    (is (match? {:submission/id (str id)
                 :submission/code code
                 :submission/code_hash (digest/md5 code)
                 :submission/language :clojure
                 :submission/test_cases {:case-1 {:input "(my-sum 1 2)"
                                                  :output 3}
                                         :case-2 {:input "(my-sum 2 3)"
                                                  :output 5}}}
                (adapters.submission/wire->internal code-submission)))))
