from sklearn import preprocessing
from sklearn.preprocessing import KBinsDiscretizer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer


def onehot():
    # print("onehot testing")
    enc = preprocessing.OneHotEncoder()  # 创建对象
    enc.fit([[0, 0, 3], [1, 1, 0], [0, 2, 1], [1, 0, 2]])   # 拟合
    array = enc.transform([[0, 1, 3]]).toarray()  # 转化
    print(array)


def tfidf():
    tag_list = ['青年 吃货 唱歌',
                '少年 游戏 叛逆',
                '少年 吃货 足球']

    vectorizer = CountVectorizer()  # 将文本中的词语转换为词频矩阵
    X = vectorizer.fit_transform(tag_list)  # 计算个词语出现的次数
    """
    word_dict = vectorizer.vocabulary_
    {'唱歌': 2, '吃货': 1, '青年': 6, '足球': 5, '叛逆': 0, '少年': 3, '游戏': 4}
    """

    transformer = TfidfTransformer()
    tfidf = transformer.fit_transform(X)  # 将词频矩阵X统计成TF-IDF值
    print(tfidf.toarray())


onehot()
