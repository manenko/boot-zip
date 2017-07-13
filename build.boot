(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure                 "1.8.0"             :scope "provided"]
                 [adzerk/bootlaces                    "0.1.13"            :scope "test"]
                 [manenko/boot-download               "0.1.0-SNAPSHOT"    :scope "test"]
                 [org.apache.commons/commons-compress "1.14"]])

(require '[adzerk.bootlaces      :refer :all]
         '[boot.core             :refer [deftask]]
         '[manenko.boot-download :refer [download-file]]
         '[manenko.boot-zip      :refer [compress-into-zip extract-from-zip]])

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


(deftask test-zip-extraction
  []
  (let [root "https://github.com/electron/electron/releases/download"]
    (comp
     (download-file :url         (str root "/v1.7.4/electron-v1.7.4-linux-x64.zip")
                    :output-path "downloads/electron-v1.7.4-linux-x64.zip")
     (extract-from-zip :archive    "downloads/electron-v1.7.4-linux-x64.zip"
                       :output-dir "extracted/electron/v1.7.4/linux-x64")
     (target))))
