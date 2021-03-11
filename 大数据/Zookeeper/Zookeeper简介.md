# Zookeeper简介

### 一、Zookeeper的数据模型

* 层次化的目录结构，命名符合常规文件系统规范

* 每个节点在zookeeper中叫做znode,并且其有一个唯一的路径标识

* 节点Znode可以包含数据和子节点，但是EPHEMERAL类型的节点不能有子节点

* Znode中的数据可以有多个版本，比如某一个路径下存有多个数据版本，那么查询这个路径下的数据就需要带上版本

* 客户端应用可以在节点上设置监视器

* 节点不支持部分读写，而是一次性完整读写

### 二、Zookeeper的节点

* Znode有两种类型，短暂的（ephemeral）和持久的（persistent）

* Znode的类型在创建时确定并且之后不能再修改

* 短暂znode的客户端会话结束时，zookeeper会将该短暂znode删除，短暂znode不可以有子节点

* 持久znode不依赖于客户端会话，只有当客户端明确要删除该持久znode时才会被删除

* Znode有四种形式的目录节点，PERSISTENT、PERSISTENT_SEQUENTIAL、EPHEMERAL、EPHEMERAL_SEQUENTIAL

### 三、Zookeeper的角色

* 领导者（leader），负责进行投票的发起和决议，更新系统状态

* 学习者（learner），包括跟随者（follower）和观察者（observer），follower用于接受客户端请求并想客户端返回结果，在选主过程中参与投票

* 观察者（Observer），可以接受客户端连接，将写请求转发给leader，但observer不参加投票过程，只同步leader的状态，observer的目的是为了扩展系统，提高读取速度

* 客户端（client），请求发起方

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/zookeeper2.png"/></div>

### 四、Zookeeper的顺序号

* 创建znode时设置顺序标识，znode名称后会附加一个值

* 顺序号是一个单调递增的计数器，由父节点维护

* 在分布式系统中，顺序号可以被用于为所有的事件进行全局排序，这样客户端可以通过顺序号推断事件的顺序

### 五、Zookeeper的读写机制

* Zookeeper是一个由多个server组成的集群

* 一个leader，多个follower

* 每个server保存一份数据副本

* 全局数据一致

* 分布式读写

* 更新请求转发，由leader实施

### 六、Zookeeper的保证

* 更新请求顺序进行，来自同一个client的更新请求按其发送顺序依次执行

* 数据更新原子性，一次数据更新要么成功，要么失败

* 全局唯一数据视图，client无论连接到哪个server，数据视图都是一致的

* 实时性，在一定事件范围内，client能读到最新数据

### 七、Zookeeper的API接口

* String create(String path, byte[] data, List<ACL> acl, CreateMode createMode) 
      
    * 创建一个给定的目录节点 path, 并给它设置数据，CreateMode 标识有四种形式的目录节点，分别是 PERSISTENT：持久化目录节点，这个目录节点存储的数据不会丢失；PERSISTENT_SEQUENTIAL：顺序自动编号的目录节点，这种目录节点会根据当前已近存在的节点数自动加 1，然后返回给客户端已经成功创建的目录节点名；EPHEMERAL：临时目录节点，一旦创建这个节点的客户端与服务器端口也就是 session 超时，这种节点会被自动删除；EPHEMERAL_SEQUENTIAL：临时自动编号节点 

* Stat exists(String path, boolean watch) 

     * 判断某个 path 是否存在，并设置是否监控这个目录节点，这里的 watcher 是在创建 ZooKeeper 实例时指定的 watcher，exists方法还有一个重载方法，可以指定特定的 watcher 

* Stat exists(String path, Watcher watcher) 

     * 重载方法，这里给某个目录节点设置特定的 watcher，Watcher 在 ZooKeeper 是一个核心功能，Watcher 可以监控目录节点的数据变化以及子目录的变化，一旦这些状态发生变化，服务器就会通知所有设置在这个目录节点上的 Watcher，从而每个客户端都很快知道它所关注的目录节点的状态发生变化，而做出相应的反应 

* void delete(String path, int version) 

     * 删除 path 对应的目录节点，version 为 -1 可以匹配任何版本，也就删除了这个目录节点所有数据 

* List<String> getChildren(String path, boolean watch) 
      
     * 获取指定 path 下的所有子目录节点，同样 getChildren方法也有一个重载方法可以设置特定的 watcher 监控子节点的状态 

* Stat setData(String path, byte[] data, int version) 

     * 给 path 设置数据，可以指定这个数据的版本号，如果 version 为 -1 怎可以匹配任何版本 

* byte[] getData(String path, boolean watch, Stat stat) 

     * 获取这个 path 对应的目录节点存储的数据，数据的版本等信息可以通过 stat 来指定，同时还可以设置是否监控这个目录节点数据的状态

* void addAuthInfo(String scheme, byte[] auth) 

     * 客户端将自己的授权信息提交给服务器，服务器将根据这个授权信息验证客户端的访问权限。

* Stat setACL(String path, List<ACL> acl, int version) 
      
     * 给某个目录节点重新设置访问权限，需要注意的是 Zookeeper 中的目录节点权限不具有传递性，父目录节点的权限不能传递给子目录节点。目录节点 ACL 由两部分组成：perms 和 id。
     * Perms 有 ALL、READ、WRITE、CREATE、DELETE、ADMIN 几种 , id 标识了访问目录节点的身份列表，默认情况下有以下两种：
            
          * ANYONE_ID_UNSAFE = new Id("world", "anyone") 和 
          * AUTH_IDS = new Id("auth", "") 分别表示任何人都可以访问和创建者拥有访问权限。 

* List<ACL> getACL(String path, Stat stat) 
      
     * 获取某个目录节点的访问权限列表 

### 八、观察（watcher）

* Watcher 在 ZooKeeper 是一个核心功能，Watcher 可以监控目录节点的数据变化以及子目录的变化，一旦这些状态发生变化，服务器就会通知所有设置在这个目录节点上的 Watcher，从而每个客户端都很快知道它所关注的目录节点的状态发生变化，而做出相应的反应 

* 可以设置观察的操作：exists,getChildren,getData

* 可以触发观察的操作：create,delete,setData

### 九、写操作与zookeeper内部事件的对应关系

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/写操作与zookeeper内部事件的对应关系.png"/></div>

### 十、zookeeper内部事件与watcher的对应关系

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/zookeeper内部事件与watcher的对应关系.png"/></div>

### 十一、写操作与watcher的对应关系

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/写操作与watcher的对应关系.png"/></div>

### 十二、每个znode被创建时都会带有一个ACL列表，用于决定谁可以对它执行何种操作

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/znode被创建时ACL列表.png"/></div>

### 十三、ACL 访问控制列表（Access Control List，ACL）

* 身份验证模式有三种：

* digest:用户名，密码

* host:通过客户端的主机名来识别客户端

* ip： 通过客户端的ip来识别客户端

* new ACL(Perms.READ,new Id("host","example.com"));这个ACL对应的身份验证模式是host，符合该模式的身份是example.com，权限的组合是：READ

### 十四、Znode的节点状态

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/Znode的节点状态.png"/></div>

### 十五、 Zookeeper工作原理

* Zookeeper的核心是原子广播，这个机制保证了各个server之间的同步。实现这个机制的协议叫做Zab协议。Zab协议有两种模式，它们分别是恢复模式和广播模式。当服务启动或者在领导者崩溃后，Zab就进入了恢复模式，当领导者被选举出来，且大多数server的完成了和leader的状态同步以后，恢复模式就结束了。状态同步保证了leader和server具有相同的系统状态。

* 一旦leader已经和多数的follower进行了状态同步后，他就可以开始广播消息了，即进入广播状态。这时候当一个server加入zookeeper服务中，它会在恢复模式下启动，发现leader，并和leader进行状态同步。待到同步结束，它也参与消息广播。Zookeeper服务一直维持在Broadcast状态，直到leader崩溃了或者leader失去了大部分的followers支持。

* 广播模式需要保证proposal被按顺序处理，因此zk采用了递增的事务id号(zxid)来保证。所有的提议(proposal)都在被提出的时候加上了zxid。实现中zxid是一个64为的数字，它高32位是epoch用来标识leader关系是否改变，每次一个leader被选出来，它都会有一个新的epoch。低32位是个递增计数。

* 当leader崩溃或者leader失去大多数的follower，这时候zk进入恢复模式，恢复模式需要重新选举出一个新的leader，让所有的server都恢复到一个正确的状态。

### 十六、Leader选举

* 每个Server启动以后都询问其它的Server它要投票给谁。

* 对于其他server的询问，server每次根据自己的状态都回复自己推荐的leader的id和上一次处理事务的zxid（系统启动时每个server都会推荐自己）

* 收到所有Server回复以后，就计算出zxid最大的哪个Server，并将这个Server相关信息设置成下一次要投票的Server。

* 计算这过程中获得票数最多的的sever为获胜者，如果获胜者的票数超过半数，则改server被选为leader。否则，继续这个过程，直到leader被选举出来。

* leader就会开始等待server连接

* Follower连接leader，将最大的zxid发送给leader

* Leader根据follower的zxid确定同步点

* 完成同步后通知follower 已经成为uptodate状态

* Follower收到uptodate消息后，又可以重新接受client的请求进行服务了

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/Leader选举1.png"/></div>

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/Leader选举2.png"/></div>


### 十七、Zookeeper示例代码

      // 创建一个与服务器的连接
       ZooKeeper zk = new ZooKeeper("localhost:" + CLIENT_PORT, 
              ClientBase.CONNECTION_TIMEOUT, new Watcher() { 
                  // 监控所有被触发的事件
                  public void process(WatchedEvent event) { 
                      System.out.println("已经触发了" + event.getType() + "事件！"); 
                  } 
              }); 
       // 创建一个目录节点
       zk.create("/testRootPath", "testRootData".getBytes(), Ids.OPEN_ACL_UNSAFE,
         CreateMode.PERSISTENT); 
       // 创建一个子目录节点
       zk.create("/testRootPath/testChildPathOne", "testChildDataOne".getBytes(),
         Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
       System.out.println(new String(zk.getData("/testRootPath",false,null))); 
       // 取出子目录节点列表
       System.out.println(zk.getChildren("/testRootPath",true)); 
       // 修改子目录节点数据
       zk.setData("/testRootPath/testChildPathOne","modifyChildDataOne".getBytes(),-1); 
       System.out.println("目录节点状态：["+zk.exists("/testRootPath",true)+"]"); 
       // 创建另外一个子目录节点
       zk.create("/testRootPath/testChildPathTwo", "testChildDataTwo".getBytes(), 
         Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT); 
       System.out.println(new String(zk.getData("/testRootPath/testChildPathTwo",true,null))); 
       // 删除子目录节点
       zk.delete("/testRootPath/testChildPathTwo",-1); 
       zk.delete("/testRootPath/testChildPathOne",-1); 
       // 删除父目录节点
       zk.delete("/testRootPath",-1); 
       // 关闭连接
       zk.close();


* 输出的结果如下：

      已经触发了 None 事件！
      testRootData	[testChildPathOne] 
      目录节点状态：[5,5,1281804532336,1281804532336,0,1,0,0,12,1,6] 
      已经触发了 NodeChildrenChanged 事件！
      testChildDataTwo
      已经触发了 NodeDeleted 事件！
      已经触发了 NodeDeleted 事件！

### 十八、应用场景1－统一命名服务

* 分布式应用中，通常需要有一套完整的命名规则，既能够产生唯一的名称又便于人识别和记住，通常情况下用树形的名称结构是一个理想的选择，树形的名称结构是一个有层次的目录结构，既对人友好又不会重复。

* Name Service 是 Zookeeper 内置的功能，只要调用 Zookeeper 的 API 就能实现。如调用 create 接口就可以很容易创建一个目录节点。

### 十九、应用场景2－配置管理

* 配置的管理在分布式应用环境中很常见，例如同一个应用系统需要多台 PC Server 运行，但是它们运行的应用系统的某些配置项是相同的，如果要修改这些相同的配置项，那么就必须同时修改每台运行这个应用系统的 PC Server，这样非常麻烦而且容易出错。

* 将配置信息保存在 Zookeeper 的某个目录节点中，然后将所有需要修改的应用机器监控配置信息的状态，一旦配置信息发生变化，每台应用机器就会收到 Zookeeper 的通知，然后从 Zookeeper 获取新的配置信息应用到系统中。

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/应用场景2%EF%BC%8D配置管理.png"/></div>

### 二十、应用场景3－集群管理

* Zookeeper 能够很容易的实现集群管理的功能，如有多台 Server 组成一个服务集群，那么必须要一个“总管”知道当前集群中每台机器的服务状态，一旦有机器不能提供服务，集群中其它集群必须知道，从而做出调整重新分配服务策略。同样当增加集群的服务能力时，就会增加一台或多台 Server，同样也必须让“总管”知道。

* Zookeeper 不仅能够维护当前的集群中机器的服务状态，而且能够选出一个“总管”，让这个总管来管理集群，这就是 Zookeeper 的另一个功能 Leader Election。

* 它们的实现方式都是在 Zookeeper 上创建一个 EPHEMERAL 类型的目录节点，然后每个 Server 在它们创建目录节点的父目录节点上调用 getChildren(String path, boolean watch) 方法并设置 watch 为 true，由于是 EPHEMERAL 目录节点，当创建它的 Server 死去，这个目录节点也随之被删除，所以 Children 将会变化，这时 getChildren上的 Watch 将会被调用，所以其它 Server 就知道已经有某台 Server 死去了。新增 Server 也是同样的原理。

* Zookeeper 如何实现 Leader Election，也就是选出一个 Master Server。和前面的一样每台 Server 创建一个 EPHEMERAL 目录节点，不同的是它还是一个 SEQUENTIAL 目录节点，所以它是个 EPHEMERAL_SEQUENTIAL 目录节点。之所以它是 EPHEMERAL_SEQUENTIAL 目录节点，是因为我们可以给每台 Server 编号，我们可以选择当前是最小编号的 Server 为 Master，假如这个最小编号的 Server 死去，由于是 EPHEMERAL 节点，死去的 Server 对应的节点也被删除，所以当前的节点列表中又出现一个最小编号的节点，我们就选择这个节点为当前 Master。这样就实现了动态选择 Master，避免了传统意义上单 Master 容易出现单点故障的问题。

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/应用场景3%EF%BC%8D集群管理.png"/></div>


      zk.create("/testRootPath/testChildPath1","1".getBytes(), 
                                    Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
      zk.create(“/testRootPath/testChildPath2”,“2”.getBytes(), 
                                    Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
      zk.create("/testRootPath/testChildPath3","3".getBytes(), 
                                    Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
      zk.create("/testRootPath/testChildPath4","4".getBytes(), 
                                    Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
      System.out.println(zk.getChildren("/testRootPath", false));

打印结果：

        [testChildPath10000000000, testChildPath20000000001, testChildPath40000000003, testChildPath30000000002]

* 规定编号最小的为master,所以当我们对SERVERS节点做监控的时候，得到服务器列表，只要所有集群机器逻辑认为最小编号节点为master，那么master就被选出，而这
* 个master宕机的时候，相应的znode会消失，然后新的服务器列表就被推送到客户端，然后每个节点逻辑认为最小编号节点为master，这样就做到动态master选举。

*  Leader Election 关键代码

            void findLeader() throws InterruptedException { 
                    byte[] leader = null; 
                    try { 
                        leader = zk.getData(root + "/leader", true, null); 
                    } catch (Exception e) { 
                        logger.error(e); 
                    } 
                    if (leader != null) { 
                        following(); 
                    } else { 
                        String newLeader = null; 
                        try { 
                            byte[] localhost = InetAddress.getLocalHost().getAddress(); 
                            newLeader = zk.create(root + "/leader", localhost, 
                            ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL); 
                        } catch (Exception e) { 
                            logger.error(e); 
                        } 
                        if (newLeader != null) { 
                            leading(); 
                        } else { 
                            mutex.wait(); 
                        } 
                    } 
                }

### 二十一、应用场景4－共享锁

* 共享锁在同一个进程中很容易实现，但是在跨进程或者在不同 Server 之间就不好实现了。Zookeeper 却很容易实现这个功能，实现方式也是需要获得锁的 Server 创建一个 EPHEMERAL_SEQUENTIAL 目录节点，然后调用 getChildren方法获取当前的目录节点列表中最小的目录节点是不是就是自己创建的目录节点，如果正是自己创建的，那么它就获得了这个锁，如果不是那么它就调用 exists(String path, boolean watch) 方法并监控 Zookeeper 上目录节点列表的变化，一直到自己创建的节点是列表中最小编号的目录节点，从而获得锁，释放锁很简单，只要删除前面它自己所创建的目录节点就行了。


<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/应用场景4%EF%BC%8D共享锁.png"/></div>

* 同步锁的关键代码

      void getLock() throws KeeperException, InterruptedException{ 
              List<String> list = zk.getChildren(root, false); 
              String[] nodes = list.toArray(new String[list.size()]); 
              Arrays.sort(nodes); 
              if(myZnode.equals(root+"/"+nodes[0])){ 
                  doAction(); 
              } 
              else{ 
                  waitForLock(nodes[0]); 
              } 
          } 
          void waitForLock(String lower) throws InterruptedException, KeeperException {
              Stat stat = zk.exists(root + "/" + lower,true); 
              if(stat != null){ 
                  mutex.wait(); 
              } 
              else{ 
                  getLock(); 
              } 
          }

### 二十二、应用场景5－队列管理

* Zookeeper 可以处理两种类型的队列：

     * 当一个队列的成员都聚齐时，这个队列才可用，否则一直等待所有成员到达，这种是同步队列；

     * 队列按照 FIFO 方式进行入队和出队操作，例如实现生产者和消费者模型。

* 创建一个父目录 /synchronizing，每个成员都监控目录 /synchronizing/start 是否存在，然后每个成员都加入这个队列（创建 /synchronizing/member_i 的临时目录节点），然后每个成员获取 / synchronizing 目录的所有目录节点，判断 i 的值是否已经是成员的个数，如果小于成员个数等待 /synchronizing/start 的出现，如果已经相等就创建 /synchronizing/start。

<div align="center"><img src="https://github.com/sunnyandgood/BigData/blob/master/Zookeeper/img/应用场景5%EF%BC%8D队列管理.png"/></div>

* 同步队列的关键代码

     * 同步队列
     
           void addQueue() throws KeeperException, InterruptedException{ 
              zk.exists(root + "/start",true); 
              zk.create(root + "/" + name, new byte[0], Ids.OPEN_ACL_UNSAFE, 
              CreateMode.EPHEMERAL_SEQUENTIAL); 
              synchronized (mutex) { 
                  List<String> list = zk.getChildren(root, false); 
                  if (list.size() < size) { 
                      mutex.wait(); 
                  } else { 
                      zk.create(root + "/start", new byte[0], Ids.OPEN_ACL_UNSAFE,
                       CreateMode.PERSISTENT); 
                  } 
              } 
           }
           
     当队列没满是进入 wait()，然后会一直等待 Watch 的通知，Watch 的代码如下：
     
           public void process(WatchedEvent event) { 
              if(event.getPath().equals(root + "/start") &&
               event.getType() == Event.EventType.NodeCreated){ 
                  System.out.println("得到通知"); 
                  super.process(event); 
                  doAction(); 
              } 
           }
* FIFO 队列用 Zookeeper 实现思路：

     在特定的目录下创建 SEQUENTIAL 类型的子目录 /queue_i，这样就能保证所有成员加入队列时都是有编号的，出队列时通过 getChildren( ) 方法可以返回当前所有的队列中的元素，然后消费其中最小的一个，这样就能保证 FIFO。
     
     * 生产者代码
     
            boolean produce(int i) throws KeeperException, InterruptedException{ 
                    ByteBuffer b = ByteBuffer.allocate(4); 
                    byte[] value; 
                    b.putInt(i); 
                    value = b.array(); 
                    zk.create(root + "/element", value, ZooDefs.Ids.OPEN_ACL_UNSAFE, 
                                CreateMode.PERSISTENT_SEQUENTIAL); 
                    return true; 
            }
     
     * 消费者代码
     
           int consume() throws KeeperException, InterruptedException{ 
              int retvalue = -1; 
              Stat stat = null; 
              while (true) { 
                  synchronized (mutex) { 
                      List<String> list = zk.getChildren(root, true); 
                      if (list.size() == 0) { 
                          mutex.wait(); 
                      } else { 
                          Integer min = new Integer(list.get(0).substring(7)); 
                          for(String s : list){ 
                              Integer tempValue = new Integer(s.substring(7)); 
                              if(tempValue < min) min = tempValue; 
                          } 
                          byte[] b = zk.getData(root + "/element" + min,false, stat); 
                          zk.delete(root + "/element" + min, 0); 
                          ByteBuffer buffer = ByteBuffer.wrap(b); 
                          retvalue = buffer.getInt(); 
                          return retvalue; 
                      } 
                  } 
              } 
           }

# 总结

* Zookeeper 作为 Hadoop 项目中的一个子项目，是 Hadoop 集群管理的一个必不可少的模块，它主要用来控制集群中的数据，如它管理 Hadoop 集群中的 NameNode，还有 Hbase 中 Master Election、Server 之间状态同步等。

* Zoopkeeper 提供了一套很好的分布式集群管理的机制，就是它这种基于层次型的目录树的数据结构，并对树中的节点进行有效管理，从而可以设计出多种多样的分布式的数据管理模型











