(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure     "1.8.0"             :scope "provided"]
                 [adzerk/bootlaces        "0.1.13"            :scope "test"]
                 [manenko/boot-download   "0.1.0-SNAPSHOT"    :scope "test"]])

(require '[adzerk.bootlaces      :refer :all]
         )

(def +project+ 'manenko/boot-zip)
(def +version+ "0.1.0-SNAPSHOT")

(bootlaces! +version+)

(task-options!
 pom {:project     +project+
      :version     +version+
      :description "Boot task for (de-)compressing ZIP archives preserving the Unix permissions."
      :url         "https://github.com/manenko/boot-zip/"
      :scm         {:url "https://github.com/manenko/boot-zip/"}
      :license     {"EPL" "http://www.eclipse.org/legal/epl-v10.html"}})
