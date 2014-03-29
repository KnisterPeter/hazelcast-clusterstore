package de.matrixweb.hazelcast.clusterstore;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import com.hazelcast.core.MapStore;

/**
 * @author markusw
 */
@SuppressWarnings("rawtypes")
public class ClusterStore implements MapStore, MapLoaderLifecycleSupport {

	private HazelcastInstance cluster;

	private IMap map;

	/**
	 * @see com.hazelcast.core.MapLoaderLifecycleSupport#init(com.hazelcast.core.HazelcastInstance,
	 *      java.util.Properties, java.lang.String)
	 */
	@Override
	public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
		Config config = new Config();
		config.setLiteMember(true);
		if (properties.containsKey("group-name")) {
			config.getGroupConfig().setName(properties.getProperty("group-name"));
		}
		if (properties.containsKey("group-pass")) {
			config.getGroupConfig().setPassword(properties.getProperty("group-pass"));
		}
		config.getNetworkConfig().getInterfaces().setEnabled(false);
		config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
		config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
		for (String address : properties.getProperty("address").split("[, ]")) {
			config.getNetworkConfig().getJoin().getTcpIpConfig().addMember(address);
		}
		cluster = Hazelcast.newHazelcastInstance(config);
		map = (IMap) cluster.getMap(mapName);
	}

	/**
	 * @see com.hazelcast.core.MapLoaderLifecycleSupport#destroy()
	 */
	@Override
	public void destroy() {
		map = null;
		cluster.getLifecycleService().shutdown();
	}

	/**
	 * @see com.hazelcast.core.MapLoader#load(java.lang.Object)
	 */
	@Override
	public Object load(Object key) {
		return map.get(key);
	}

	/**
	 * @see com.hazelcast.core.MapLoader#loadAll(java.util.Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map loadAll(Collection keys) {
		return map.getAll(new HashSet(keys));
	}

	/**
	 * @see com.hazelcast.core.MapLoader#loadAllKeys()
	 */
	@Override
	public Set loadAllKeys() {
		return map.keySet();
	}

	/**
	 * @see com.hazelcast.core.MapStore#store(java.lang.Object, java.lang.Object)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void store(Object key, Object value) {
		map.put(key, value);
	}

	/**
	 * @see com.hazelcast.core.MapStore#storeAll(java.util.Map)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void storeAll(Map map) {
		map.putAll(map);
	}

	/**
	 * @see com.hazelcast.core.MapStore#delete(java.lang.Object)
	 */
	@Override
	public void delete(Object key) {
		map.remove(key);
	}

	/**
	 * @see com.hazelcast.core.MapStore#deleteAll(java.util.Collection)
	 */
	@Override
	public void deleteAll(Collection keys) {
		map.clear();
	}

}
