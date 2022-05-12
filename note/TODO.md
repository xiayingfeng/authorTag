# TODO

前提：commit是对hunk的总结，即通过commit应该能够对应到所属的hunks
Map <commit,List<hunk>> ~ List<Description> 

1. changelog 关联到**commit id**

2. changelog与**commit message文本**的相似度

前提：只要能通过关键词让description、hunk关联上，TFIDF
hunk is Commit, hunk ~ Descr => Commit ~ Descr
3. changelog 与 code hunk存在某种关联，这种关联可以通过keyword进行连接

时间选择：margin

word2vec
codeBERT
tf–idf/

单词自动补全

5.5 准备标准答案，为进一步的方法设计与实现准备好数据

需要解释为什么那六百个是可疑的