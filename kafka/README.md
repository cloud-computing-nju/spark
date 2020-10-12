目录结构
+ java
  + adapter
    + DataModelDecoder
      + 用于自定义consumer从partition中消费数据时的反序列化规则(将字节流转化为DataModel对象)
    + DataModelEncoder 
      + 用于自定义数据对象(DataModel)存入partition时的序列化规则
  + javaServer
    + Server
      + 启动器(接收python爬虫数据,存入kafka服务器并进行消费)
  + utils
    + BeanUtils
      + 工具类,提供对象和字节数组互相转换方法
  + model
    + DataModel
      + 映射python爬虫的json数据到具体对象
  + config
    + ConsumerConfig
      + 消费者配置文件
    + ProducerConfig
      + 生产者配置文件
    + ServerConfig
      + 启动器配置文件
  + producer
    + Message
      + topic+DataModel对象
    + MessageList
      + 维护所有数据写入kafka的时间
    + MessageProducer
      + Producer封装类
    + Partitioner
      + 自定义Partitioner,用于保证partition在存储数据时负载均衡
    + Response
      + 返回消息(成功?带上数据 失败？返回false)
  + consumer
    + MessageConsumer
      + Consumer封装类
    
kafka架构图：https://zhuanlan.zhihu.com/p/38269875

