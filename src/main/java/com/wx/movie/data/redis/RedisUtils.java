/**
 * 文件名：RedisUtils.java
 * @author dynamo
 */

package com.wx.movie.data.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import com.fasterxml.jackson.databind.JavaType;
import com.google.gson.GsonBuilder;
import com.wx.movie.data.common.util.JsonMapperUtil;

/**
 * ClassName:RedisUtils <br/>
 * ClassDesc:Redis操作工具. <br/>
 * Date:     2015-4-24 下午4:06:25 <br/>
 *
 * @author LiJianying
 * @see
 * @since JDK 1.6
 */
public class RedisUtils {    

  private final Logger log = LoggerFactory.getLogger(RedisUtils.class);

  /**
   * DEFALT_EXPIRE_TIME:默认过期时间  10days.
   */
  private static final int DEFALT_EXPIRE_TIME = 24 * 60 * 60 * 10;

  private ShardedJedisPool shardedJedisPool;

  /**
   * Creates a new instance of RedisUtils.
   *
   * @param shardingNodes   redis sharding nodes host string e.g.
   *                        "192.168.200.191:6379,192.168.200.191:6380,192.168.200.191:6381,192.168.200.191:63782"
   * @param jedisPoolConfig jedis pool config
   */
  public RedisUtils(String shardingNodes, GenericObjectPoolConfig jedisPoolConfig) {
    if (StringUtils.isBlank(shardingNodes) || shardingNodes.indexOf(":") == -1)
      throw new RuntimeException(
          "jedis init error, offered shardingNodes str illegal : " + shardingNodes);
    String[] arr = shardingNodes.split(",");
    List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
    for (String str : arr) {
      String[] tmp = str.split(":");
      String hosts = tmp[0];
      int port = Integer.parseInt(tmp[1]);
      JedisShardInfo shard = new JedisShardInfo(hosts, port);
      shards.add(shard);
    }
    shardedJedisPool = new ShardedJedisPool(jedisPoolConfig, shards);
    log.info("jedis init ok, shardingNodes is : {} ", shardingNodes);
  }

  /**
   * setT:(缓存对象到redis). <br/>
   *
   * @param key
   * @param object
   * @return 操作结果
   */
  public boolean setT(String key, Object object) {
    ShardedJedis jedis = getJ();
    try {
      String value = new JsonMapperUtil().toJson(object);
      jedis.set(key, value);
      return true;
    } catch (Exception e) {
      log.error("setT error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  /**
   * getT:(获取对象从redis). <br/>
   *
   * @param key
   * @param c
   * @return
   */
  public <T> T getT(String key, Class<T> c) {
    ShardedJedis jedis = getJ();
    try {
      String value = jedis.get(key);
      return new JsonMapperUtil().fromJson(value, c);
    } catch (Exception e) {
      log.error("getT error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  public boolean setStr(String key, String str) {
    ShardedJedis jedis = getJ();
    try {
      jedis.set(key, str);
      return true;
    } catch (Exception e) {
      log.error("setStr error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  public String getStr(String key) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.get(key);
    } catch (Exception e) {
      log.error("getStr error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * setInt:(缓存Integer对象). <br/>
   *
   * @param key
   * @param i
   * @return
   */
  public boolean setInt(String key, int i) {
    ShardedJedis jedis = getJ();
    try {
      jedis.set(key, String.valueOf(i));
      jedis.expire(key, DEFALT_EXPIRE_TIME);
      return true;
    } catch (Exception e) {
      log.error("setInt error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  /**
   * getInt:(获取Integer从redis). <br/>
   *
   * @param key
   * @return
   */
  public Integer getInt(String key) {
    ShardedJedis jedis = getJ();
    try {
      String s = jedis.get(key);
      return Integer.parseInt(s);
    } catch (Exception e) {
      log.error("getInt error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * setList:(set list数据至redis). <br/>
   *
   * @param key
   * @param list
   * @return
   */
  public <T> boolean setList(String key, List<T> list) {
    ShardedJedis jedis = getJ();
    try {
      String value = new JsonMapperUtil().toJson(list);
      jedis.set(key, value);
      return true;
    } catch (Exception e) {
      log.error("setList error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  /**
   * getList:(获取list格式数据). <br/>
   *
   * @param c
   * @param key
   * @return
   */
  public <T> List<T> getList(Class<T> c, String key) {
    ShardedJedis jedis = getJ();
    List<T> list = new ArrayList<T>();
    JsonMapperUtil mapper = new JsonMapperUtil();
    try {
      String value = jedis.get(key);
      if (value == null || "".equals(value))
        return null;
      JavaType javaType = mapper.contructCollectionType(list.getClass(), c);
      return mapper.fromJson(value, javaType);
    } catch (Exception e) {
      log.error("getList error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * remove:删除给定的 key . <br/>
   * 不存在的 key 会被忽略
   *
   * @param key
   * @return
   */
  public boolean remove(String key) {
    ShardedJedis jedis = getJ();
    try {
      jedis.del(key);
      return true;
    } catch (Exception e) {
      log.error("remove error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  /**
   * incr:将 key 中储存的数字值增一. <br/>
   * 如果 key不存在，那么 key的值会先被初始化为 0 ，然后再执行 INCR 操作 <br/>
   * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误 <br/>
   * 本操作返回值限制在 64 位(bit)有符号数字表示之内
   *
   * @param key
   * @return
   */
  public Long incr(String key) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.incr(key);
    } catch (Exception e) {
      log.error("incr error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * incrBy:将 key 所储存的值加上增量 increment. <br/>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。. <br/>
   * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误. <br/>
   * 本操作的值限制在 64 位(bit)有符号数字表示之内. <br/>
   *
   * @param key
   * @param value
   * @return
   */
  public Long incrBy(String key, int value) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.incrBy(key, value);
    } catch (Exception e) {
      log.error("incrBy error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * decr:将 key 中储存的数字值减一. <br/>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作. <br/>
   * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误. <br/>
   * 本操作的值限制在 64 位(bit)有符号数字表示之内。. <br/>
   *
   * @param key
   * @return
   */
  public Long decr(String key) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.decr(key);
    } catch (Exception e) {
      log.error("decr error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }


  /**
   * decrBy:将 key 所储存的值减去减量 decrement. <br/>
   * 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作. <br/>
   * 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误. <br/>
   * 本操作的值限制在 64 位(bit)有符号数字表示之内. <br/>
   *
   * @param key
   * @param value
   * @return
   */
  public Long decrBy(String key, int value) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.decrBy(key, value);
    } catch (Exception e) {
      log.error("decrBy error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * exists:检查给定 key 是否存在. <br/>
   *
   * @param key
   * @return
   */
  public boolean exists(String key) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.exists(key);
    } catch (Exception e) {
      log.error("exists error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  /**
   * hexists:查看哈希表 key 中，给定域 field 是否存在. <br/>
   *
   * @param key
   * @param fieldKey
   * @return
   */
  public boolean hexists(String key, String fieldKey) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.hexists(key, fieldKey);
    } catch (Exception e) {
      log.error("hexists error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  /**
   * expire:为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除. <br/>
   *
   * @param key
   * @param seconds
   * @return
   */
  public boolean expire(String key, int seconds) {
    ShardedJedis jedis = getJ();
    try {
      jedis.expire(key, seconds);
      return true;
    } catch (Exception e) {
      log.error("expire error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  public boolean expireAt(String key, long unixTime) {
    ShardedJedis jedis = getJ();
    try {
      jedis.expireAt(key, unixTime);
      return true;
    } catch (Exception e) {
      log.error("expireAt error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  /**
   * hset:将哈希表 key 中的域 field 的值设为 value . <br/>
   * 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。
   * 如果域 field 已经存在于哈希表中，旧值将被覆盖。
   *
   * @param key
   * @param fieldKey
   * @param value
   * @return 如果 field 是哈希表中的一个新建域，并且值设置成功，返回 1 。
   * 如果哈希表中域 field 已经存在且旧值已被新值覆盖，返回 0 。
   */
  public Long hset(String key, String fieldKey, Object value) {
    ShardedJedis jedis = getJ();
    try {
      Long count = jedis.hset(key, fieldKey, new JsonMapperUtil().toJson(value));
      jedis.expire(key, DEFALT_EXPIRE_TIME);
      return count;
    } catch (Exception e) {
      log.error("hset error", e);
    } finally {
      releaseJ(jedis);
    }
    return 0l;
  }

  /**
   * hget:返回哈希表 key 中给定域 field 的值。. <br/>
   * 当给定域不存在或是给定 key 不存在时，返回 nil. <br/>
   *
   * @param key
   * @param fieldKey
   * @param type
   * @return
   */
  public <T> T hget(String key, String fieldKey, Class<T> type) {
    ShardedJedis jedis = getJ();
    try {
      if (jedis.hexists(key, fieldKey)) {
        return new JsonMapperUtil().fromJson(jedis.hget(key, fieldKey), type);
      } else {
        return null;
      }
    } catch (Exception e) {
      log.error("hget error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * del:删除给定的 key . <br/> 不存在的 key 会被忽略
   */
  public boolean del(String key) {
    ShardedJedis jedis = getJ();
    try {
      jedis.del(key);
      return true;
    } catch (Exception e) {
      log.error("del error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  /**
   * hdel:删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。. <br/>
   *
   * @param key
   * @param fieldKeys
   * @return 被成功移除的域的数量，不包括被忽略的域。
   */
  public Long hdel(String key, String... fieldKeys) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.hdel(key, fieldKeys);
    } catch (Exception e) {
      log.error("hdel error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * hmset:同时将多个 field-value (域-值)对设置到哈希表 key 中。. <br/>
   * 此命令会覆盖哈希表中已存在的域。
   * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
   *
   * @param key
   * @param map
   * @param class_type
   */
  public <T> void hmset(String key, Map<String, T> map, Class<T> class_type) {
    ShardedJedis jedis = getJ();
    Map<String, String> putMap = new HashMap<String, String>();

    for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
      Entry<Object, Object> entry = (Entry<Object, Object>) iterator.next();
      putMap.put(entry.getKey().toString(), new JsonMapperUtil().toJson(entry.getValue()));
    }
    try {
      if (putMap.size() > 0) {
        jedis.hmset(key, putMap);
        jedis.expire(key, DEFALT_EXPIRE_TIME);
      }
    } catch (Exception e) {
      log.error("hmset error", e);
    } finally {
      releaseJ(jedis);
    }

  }


  /**
   * hmset:同时将多个 field-value (域-值)对设置到哈希表 key 中。. <br/>
   * 此命令会覆盖哈希表中已存在的域。新的哈希表中不存在的域会被删除
   * 如果 key 不存在，一个空哈希表被创建并执行 HMSET 操作。
   * 用jackson序列化
   *
   * @param key
   * @param map
   * @param class_type
   */
  public <T> void hmsetWithDel(String key, Map<String, T> map, Class<T> class_type) {
    ShardedJedis jedis = getJ();
    class_type.getGenericSuperclass();
    Map<String, String> putMap = new HashMap<String, String>();

    JsonMapperUtil mapper = new JsonMapperUtil();

    try {
      for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); ) {
        Entry<Object, Object> entry = (Entry<Object, Object>) iterator.next();
        putMap.put(entry.getKey().toString(), mapper.toJson(entry.getValue()));
      }

      //			Set<String> hkeys = jedis.hkeys(key);
      //			List<String> delKeys = new ArrayList<String>();
      //			for (String hkey : hkeys) {
      //				if (!putMap.keySet().contains(hkey)) {
      //					delKeys.add(hkey);
      //				}
      //			}
      //			String[] delKeysArray = delKeys.toArray(new String[delKeys.size()]);
      //			if(delKeysArray.length > 0){
      //				jedis.hdel(key, delKeysArray);
      //			}
      if (putMap.size() > 0) {
        jedis.hmset(key, putMap);
        jedis.expire(key, DEFALT_EXPIRE_TIME);
      }
    } catch (Exception e) {
      log.error("hmsetWithDel error", e);
    } finally {
      releaseJ(jedis);
    }

  }

  /**
   * hmget:返回哈希表 key 中，一个或多个给定域的值. <br/>
   * 如果给定的域不存在于哈希表，那么返回一个 nil 值. <br/>
   * 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表. <br/>
   *
   * @param key
   * @param type
   * @param fieldKeys
   * @return
   */
  public <T> List<T> hmget(String key, Class<T> type, String... fieldKeys) {
    ShardedJedis jedis = getJ();
    try {
      return (List<T>) new JsonMapperUtil().fromJson(jedis.hmget(key, fieldKeys).toString(), type);
    } catch (Exception e) {
      log.error("hmget error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * hgetAll:返回哈希表 key 中，所有的域和值。. <br/>
   * 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍. <br/>
   *
   * @param key
   * @param type
   * @return
   */
  public <T> Map<String, T> hgetAll(String key, Class<T> type) {
    ShardedJedis jedis = getJ();
    try {
      JsonMapperUtil mapper = new JsonMapperUtil();
      Map<String, T> map = new HashMap<String, T>();
      for (Iterator iterator = jedis.hgetAll(key).entrySet().iterator(); iterator.hasNext(); ) {
        Entry<String, String> entry = (Entry<String, String>) iterator.next();
        map.put(entry.getKey(), mapper.fromJson(entry.getValue(), type));
      }
      return map;
    } catch (Exception e) {
      log.error("hgetAll error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  public <T> Map<String, T> hgetAll(String key, JavaType javaType) {
    ShardedJedis jedis = getJ();
    try {
      JsonMapperUtil mapper = new JsonMapperUtil();
      Map<String, T> map = new HashMap<String, T>();
      for (Iterator iterator = jedis.hgetAll(key).entrySet().iterator(); iterator.hasNext(); ) {
        Entry<String, String> entry = (Entry<String, String>) iterator.next();
        map.put(entry.getKey(), (T) mapper.fromJson(entry.getValue(), javaType));
      }
      return map;
    } catch (Exception e) {
      log.error("hgetAll error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  public <T> boolean hsetList(String key, String fieldKey, List<T> list) {
    ShardedJedis jedis = getJ();
    try {
      jedis.hset(key, fieldKey, new JsonMapperUtil().toJson(list));
      return true;
    } catch (Exception e) {
      log.error("hsetList error", e);
    } finally {
      releaseJ(jedis);
    }
    return false;
  }

  public <T> List<T> hgetList(Class<T> c, String key, String fieldKey) {
    ShardedJedis jedis = getJ();
    JsonMapperUtil mapper = new JsonMapperUtil();
    try {
      String value = jedis.hget(key, fieldKey);
      List<T> list = new ArrayList<T>();
      JavaType javaType = mapper.contructCollectionType(list.getClass(), c);
      return mapper.fromJson(value, javaType);
    } catch (Exception e) {
      log.error("hgetList error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  public Set<String> hkeys(String key) {
    ShardedJedis jedis = getJ();
    Set<String> result = new HashSet<String>();
    try {
      result = jedis.hkeys(key);
    } catch (Exception e) {
      log.error("hkeys error", e);
    } finally {
      releaseJ(jedis);
    }
    return result;
  }

  public <T> List<T> hVals(String key, Class<T> type) {
    ShardedJedis jedis = getJ();
    List<T> list = new ArrayList<T>();
    try {
      List<String> tmp = jedis.hvals(key);
      JsonMapperUtil mapper = new JsonMapperUtil();
      for (String obj : tmp) {
        list.add(mapper.fromJson(obj, type));
      }
    } catch (Exception e) {
      log.error("hVals error", e);
    } finally {
      releaseJ(jedis);
    }
    return list;
  }

  /**
   * lpop:移除并返回列表 key 的头元素。. <br/>
   * 当 key 不存在时，返回 nil . <br/>
   *
   * @param key
   * @return
   */
  public String lpop(String key) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.lpop(key);
    } catch (Exception e) {
      log.error("lpop error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * rpush:将一个或多个值 value 插入到列表 key 的表尾(最右边) . <br/>
   * 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：
   * 比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c . <br/>
   * 如果 key 不存在，一个空列表会被创建并执行 RPUSH 操作. <br/>
   * 当 key 存在但不是列表类型时，返回一个错误。. <br/>
   *
   * @param key
   * @param values
   * @return
   */
  public Long rpush(String key, String... values) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.rpush(key, values);
    } catch (Exception e) {
      log.error("rpush error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * llen:返回列表 key 的长度. <br/>
   * 如果 key 不存在，则 key 被解释为一个空列表，返回 0 . <br/>
   * 如果 key 不是列表类型，返回一个错误. <br/>
   *
   * @param key
   * @return
   */
  public Long llen(String key) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.llen(key);
    } catch (Exception e) {
      log.error("llen error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * sadd:将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略. <br/>
   * 假如 key 不存在，则创建一个只包含 member 元素作成员的集合
   * 当 key 不是集合类型时，返回一个错误
   *
   * @param key
   * @param strings
   * @return
   */
  public Long sadd(String key, String... strings) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.sadd(key, strings);
    } catch (Exception e) {
      log.error("sadd error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  public Set<String> smembers(String key) {
    ShardedJedis jedis = getJ();
    try {
      Set<String> resultSet = jedis.smembers(key);
      if (resultSet != null)
        return resultSet;
      else
        return Collections.emptySet();
    } catch (Exception e) {
      log.error("smembers error", e);
      return Collections.emptySet();
    } finally {
      releaseJ(jedis);
    }
  }

  /**
   * spop:移除并返回集合中的一个随机元素。. <br/>
   * 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令. <br/>
   *
   * @param key
   * @return
   */
  public String spop(String key) {
    ShardedJedis jedis = getJ();
    try {
      return jedis.spop(key);
    } catch (Exception e) {
      log.error("spop error", e);
    } finally {
      releaseJ(jedis);
    }
    return null;
  }

  /**
   * keys:(性能有问题，谨慎使用). <br/>
   *
   * @param pattern
   * @return
   */
  public Set<String> keys(String pattern) {
    ShardedJedis jedis = getJ();
    Set<String> resultSet = new HashSet<String>();
    try {
      for (Jedis one : jedis.getAllShards()) {
        resultSet.addAll(one.keys(pattern));
      }
    } catch (Exception e) {
      log.error("keys error", e);
    } finally {
      releaseJ(jedis);
    }
    return resultSet;
  }

  public Long ttl(String key) {
    ShardedJedis jedis = getJ();
    Long result = null;
    try {
      result = jedis.ttl(key);
    } catch (Exception e) {
      log.error("ttl error", e);
    } finally {
      releaseJ(jedis);
    }
    return result;
  }

  /**
   * ping redis server
   *
   * @return true :所有服务器正常运行,false:服务器有异常
   */
  public Boolean ping() {
    ShardedJedis jedis = getJ();
    Collection<Jedis> jedises = jedis.getAllShards();
    try {
      for (Jedis item : jedises) {
        item.ping();
      }
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public Long setnx(String key, String value) {
    ShardedJedis jedis = getJ();
    try{
      return jedis.setnx(key,value);
    }catch (Exception ex){
      log.error("setnx error", ex);
    }finally {
      releaseJ(jedis);
    }
    return 0L;//0 if the key was not set
  }

  public String getSet(String key, String value) {
    ShardedJedis jedis = getJ();
    try{
      return jedis.getSet(key,value);
    }catch (Exception e){
      releaseJ(jedis);
    }
    return null;
  }
  /**
   * remove expire timer
   * @param key
   * @return
   */
  public  Long persist(String key){
    ShardedJedis jedis = getJ();

      try {
          return jedis.persist(key);
      } catch (Exception e) {
          e.printStackTrace();
          log.error("spop error", e);
      } finally {
            releaseJ(jedis);
      }

      return null;
  }
  
  public <T> List<T> getfromList(Class<T> c,String key){
      
    ShardedJedis jedis = getJ();

      try {
          List<String> strList = jedis.lrange(key, 0, -1);
          
          return getListFromJson(c,strList);
      } catch (Exception e) {
          e.printStackTrace();
          log.error("spop error", e);
      } finally {
        releaseJ(jedis);
      }

      return null;
  }
  
  public static <T> List<T> getListFromJson(Class<T> c, List<String> strList) {
      List<T> rtmList = new LinkedList<T>();
      for (String str: strList) {
          GsonBuilder builder = new GsonBuilder();
          rtmList.add(builder.create().fromJson(str, c));
      }
      return rtmList;
  }
  
  private ShardedJedis getJ() {
    return shardedJedisPool.getResource();
  }

  private void releaseJ(ShardedJedis jedis) {
    if (jedis != null)
      shardedJedisPool.returnResourceObject(jedis);
  }

}
