# JmeterLogAutoAnalysis
每次跑完分布式Jmeter性能测试后，都需要手动分析聚合报告和错误的日志，为了解放生产力，特别写了一个自动化分析聚合报告和错误日志的框架。
  
 该框架的功能：
1. 自动分析的日志结果；
2. 测试执行机器的日志都插入到数据库里，便于可以在前端展示测试结果；
3. 通过FTP下载，可以下载具体的错误日志信息

在test_template_files这个文件夹里，放的是jmeter性能测试后得到的聚合报告样本和断言结果样本。

具体过程如下：
1. 断言结果的脚本，通过运行LogAnalysisAuto.py脚本，将会生成errorRequest.txt的文件，该文件是提供给AnalysizeReqRespData.java运行的输入文件（具体可查看该java文件的main函数）
2. 运行AnalysizeReqRespData.java，最后得到errorReport.txt文件，格式如下：


==================== Error Report ====================

IMSI-TID Found: 2e2e06fe00000000012200001866003001002d586837686c784571774e3253444430303030303030303030303138363651744a50476d7834786b4a434c6f3737f7; ExpectedLatency: 70000; ActualLatency: 350479681

Latency Count: 1000

NoResponse Count: 0

===================================

同时该结果也会写入mysql数据库里，便于前端页面更好的展示
