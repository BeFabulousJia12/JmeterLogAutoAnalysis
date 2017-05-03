
# JmeterLogAutoAnalysis
每次跑完分布式Jmeter性能测试后，都需要手动分析聚合报告和错误的日志，为了解放生产力，特别写了一个自动化分析聚合报告和错误日志的框架。
  
 该框架的功能：
1. 自动分析的日志结果；
2. 测试执行机器的日志都插入到数据库里，便于可以在前端展示测试结果；
3. 通过FTP下载，可以下载具体的错误日志信息

Perflogfile.csv和AssertError.txt这两个日志是jmeter性能测试后得到的聚合报告样本和断言结果样本，也是我们主要分析的对象。

具体过程如下：
1. 断言结果的脚本，通过运行LogAnalysisAuto.py脚本，将会生成errorRequest.txt的文件，该文件是提供给AnalysizeReqRespData.java运行的输入文件（具体可查看该java文件的main函数）
2. 运行AnalysizeReqRespData.java，最后得到errorReport.txt文件，格式如下：


==================== Error Report ====================

IMSI-TID Found: 2e2e06fe00000000012200001866003001002d586837686c784571774e3253444430303030303030303030303138363651744a50476d7834786b4a434c6f3737f7; ExpectedLatency: 70000; ActualLatency: 350479681

Latency Count: 1000

NoResponse Count: 0

===================================

同时该结果也会写入mysql数据库里，便于前端页面更好的展示

3. 运行SamplingStatCalculator.java可以自动分析聚合报告Perflogfile.csv, 分析完毕后，会生成Aggregate_Report.txt，这个结果跟打开Jmeter，导入Perflogfile.csv文件看到的聚合结果是一致的，因为我已经把Jmeter关于聚合统计计算的核心代码进行了二次开发和封装。

==================== Aggregate Report ====================

Lable | Samples | Average | Median | 90% Line | 95% Line | 99% Line | Min | Max | Error%  | TPS 
BeanShell Sampler | 1 | 25 | 25 | 25 | 25 | 25 | 25 | 25 | 0.0 | 40.0
Logon | 1000 | 20005 | 20002 | 20004 | 20009 | 20046 | 20000 | 21017 | 1.0 | 9.020141977034719
TCP_定时上报_ICall_HB_TextPush | 163000 | 60485 | 60002 | 60008 | 60017 | 85691 | 59999 | 120001 | 0.0 | 16.336045484361243

同时该结果也会被插入到mysql数据库里，便于前端页面展示测试数据



