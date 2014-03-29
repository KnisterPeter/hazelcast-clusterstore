Hazelcast ClusterStore
======================

Implements a Hazelcast MapStore backed by another hazelcast instance.
This could be used to backup a whole map to another cluster to get
around cluster blackouts.

Configuration Properties
------------------------

* address - A comma or space separated list of ipaddress:port declarations
* group-name - The group name (defaults to dev)
* group-pass - The group password (defaults to dev-pass)
