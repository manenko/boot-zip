========
boot-zip
========

|clojars|  |license|

`Boot`_ task for (de-)compressing ZIP archives preserving the Unix permissions.

-----
Tasks
-----

~~~~~~~~~~~~~~~~
extract-from-zip
~~~~~~~~~~~~~~~~

.. code-block:: clojure

   (extract-from-zip
     [a archive    VAL str "Location of the ZIP archive that has to be extracted. Required."
      o output-dir VAL str "Path to the output directory. Optional."])

Extracts the given ZIP archive into the given directory preserving the
Unix permissions.

If the path to the output directory was not provided (or is empty)
then extracts the archive into the project's root.


~~~~~~~~~~~~~~~~~
compress-into-zip
~~~~~~~~~~~~~~~~~

.. code-block:: clojure

   (compress-into-zip
     [i input-dir VAL str "Location of the directory that should be compressed."
      a archive   VAL str "Location of the output ZIP archive."])

Compresses the content of the given directory and writes it to the
given ZIP file preserving Unix permissions.

The task will not put the folder itself to the archive, only its
files and subdirectories (recursively).

-------
License
-------

Copyright © 2017 Oleksandr Manenko.

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

.. _Boot: https://github.com/boot-clj/boot

.. |clojars| image:: https://img.shields.io/clojars/v/manenko/boot-zip.svg
    :alt: Clojars
    :scale: 100%
    :target: https://clojars.org/manenko/boot-zip

.. |license| image:: https://img.shields.io/badge/License-EPL%201.0-red.svg
    :alt: License: EPL-1.0
    :scale: 100%
    :target: https://opensource.org/licenses/EPL-1.0
