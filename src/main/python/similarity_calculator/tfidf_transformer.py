import re
from telnetlib import TN3270E
from json_utils import *
import numpy as np
# from sklearn.metric.pairwise import cosine_similarity()
from sklearn import preprocessing
from sklearn.preprocessing import KBinsDiscretizer
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer


def get_tfidf(tag_list: list):
    vectorizer = CountVectorizer()  # 将文本中的词语转换为词频矩阵
    X = vectorizer.fit_transform(tag_list)  # 计算个词语出现的次数
    transformer = TfidfTransformer()
    tfidf = transformer.fit_transform(X)  # 将词频矩阵X统计成TF-IDF值
    array = tfidf.toarray()
    print(tfidf.toarray())
    return tfidf


def calc_cos_sim(descr_vec, commit_vec):
    cos_sim = descr_vec.dot(
        commit_vec) / np.linalg.norm(descr_vec) * np.linalg.norm(commit_vec)
    return cos_sim


def is_forced_match(commit_message: str, description: str):
    id_in_commit = re.search(r'#\d+', commit_message)
    id_in_descr = re.search(r'#\d+', description)
    if id_in_commit == None or id_in_descr == None:
        return False
    return id_in_commit.group() == id_in_descr.group()


# get description list
descr_list = []
# get matched commit message list
matched_commit_message_list = []
# get mismatched commit message list
mismatched_commit_message_list = []
# get ground truth list
ground_truth_list = []
repo_list = json2repo_list(file=answer_file)
for repo_obj in repo_list:
    descr_list += repo_obj.get_descr_list()
    matched_commit_message_list += repo_obj.get_commit_messsage_list()
    mismatched_commit_message_list += repo_obj.get_mismatched_commit_message_list()
    ground_truth_list += repo_obj.truth_list


# merge description list and commit message list into tag list
tag_list = descr_list + matched_commit_message_list + mismatched_commit_message_list
descr_list_len = len(descr_list)
matched_commit_list_len = len(matched_commit_message_list)
mismatched_commit_list_len = len(mismatched_commit_message_list)

matched_commit_start_index = descr_list_len
mismatched_commit_start_index = descr_list_len + matched_commit_list_len
tfidf = get_tfidf(tag_list=tag_list).toarray()

# commit message 后续append进去，再去将前后两部分进行比较，从而前半段中找到cos_sim最大的句子
descr_vec_list = tfidf[0: matched_commit_start_index]
matched_commit_vec_list = tfidf[matched_commit_start_index: mismatched_commit_start_index]
mismatched_commit_vec_list = tfidf[mismatched_commit_start_index:]

# do match, calculate precison and recall
threshold = 0.6

# true positive:    cos_sim >= threshold && target descr == ground truth
TP_count = 0

# false positive:
#   cos_sim >= threshold && target descr != ground truth
#   cos_sim >= threshold for mismatched commits
FP_count = 0

# false negative:   cos_sim <  threshold && target descr == ground truth
FN_count = 0

# true negative:
#   cos_sim <  threshold && target descr != ground truth
#   cos_sim >= threshold for missmatched commits
TN_count = 0
result_list = []
FN_list = []
FP_list = []
# matched commit part
for i in range(0, matched_commit_list_len):
    tmp_commit_vec = matched_commit_vec_list[i]
    ground_truth = ground_truth_list[i]
    cos_sim = 0
    target_descr = 'no__fdse__matched'
    type = ''
    is_forced_matched: bool = False

    for j in range(0, descr_list_len):
        # forced match
        is_forced_matched = is_forced_match(
            matched_commit_message_list[i], descr_list[j])
        if is_forced_matched is True:
            target_descr = descr_list[j]
            break

        # cos_sim
        tmp_descr_vec = descr_vec_list[j]
        tmp_cos_sim = calc_cos_sim(
            descr_vec=tmp_descr_vec, commit_vec=tmp_commit_vec)
        if cos_sim < tmp_cos_sim:
            cos_sim = tmp_cos_sim
            target_descr = descr_list[j]

    if target_descr == ground_truth and (cos_sim >= threshold or is_forced_matched is True):
        type = 'TP'
        TP_count += 1
    elif target_descr != ground_truth and (cos_sim >= threshold or is_forced_matched is True):
        type = 'FP'
        FP_count += 1
    # if target_descr == ground_truth and cos_sim >= threshold:
    #     type = 'TP'
    #     TP_count += 1
    # elif target_descr != ground_truth and cos_sim >= threshold:
    #     type = 'FP'
    #     FP_count += 1
    elif cos_sim < threshold and target_descr == ground_truth:
        type = 'FN'
        FN_count += 1
    else:
        type = 'TN'
        TN_count += 1

    tmp_dict = {
        "commit message": matched_commit_message_list[i],
        "ground truth": ground_truth,
        "type": type,
        "target descr": target_descr,
        "cos sim": cos_sim
    }
    result_list.append(tmp_dict)
    if type == 'FP':
        FP_list.append(tmp_dict)
    elif type == 'FN':
        FN_list.append(tmp_dict)

# mismatched commit part
for i in range(0, mismatched_commit_list_len):
    tmp_commit_vec = mismatched_commit_vec_list[i]
    cos_sim = 0
    target_descr = 'no__fdse__matched'
    type = ''
    is_forced_matched: bool = False

    for j in range(0, descr_list_len):
        # forced match
        is_forced_matched = is_forced_match(
            mismatched_commit_message_list[i], descr_list[j])
        if is_forced_matched is True:
            target_descr = descr_list[j]
            break

        # cos_sim
        tmp_descr_vec = descr_vec_list[j]
        tmp_cos_sim = calc_cos_sim(
            descr_vec=tmp_descr_vec, commit_vec=tmp_commit_vec)
        if cos_sim < tmp_cos_sim:
            cos_sim = tmp_cos_sim
            target_descr = descr_list[j]

    if cos_sim >= threshold or is_forced_matched is True:
        type = 'FP'
        FP_count += 1
    elif cos_sim < threshold and is_forced_matched is False:
        type = 'TN'
        TN_count += 1

    tmp_dict = {
        "commit message": mismatched_commit_message_list[i],
        "type": type,
        "target descr": target_descr,
        "cos sim": cos_sim
    }
    result_list.append(tmp_dict)
    if type == 'FP':
        FP_list.append(tmp_dict)


result_dict = {
    "threshold": threshold,
    "precision": TP_count / (TP_count + FP_count),
    "recall": TP_count / (TP_count + FN_count),
    "list size": len(result_list),
    "result list": result_list}

# 加上groud truth和 cos sim之后设置阈值0.3，计算总体recall和precision
# "groud truth": "",
# "polarity": "positive", "negative"
# "truth target cos sim": "",
print('result list')
write_json(data=result_dict, file=output_file)
write_json(FN_list, FN_file)
write_json(FP_list, FP_file)
print(len(result_list))
print(result_list)
