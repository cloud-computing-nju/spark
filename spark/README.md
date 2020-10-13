**注意,对于具体的业务场景:**

a. 请在streaming目录下实现对应的流处理类

b. 添加对应的socket端口至config/SparkConfig下

c. 实现对应的socket


**to-do list:**

a. kafka模块中需添加对应的预处理实现(提取标签文本)和socket客户端(传输预处理后的数据到spark streaming)

b. spark模块中需添加对应的socket服务端
