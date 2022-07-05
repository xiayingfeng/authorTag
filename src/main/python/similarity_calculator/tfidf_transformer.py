import re
import numpy as np
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.feature_extraction.text import TfidfTransformer

from constant import field
from constant import constant
from similarity_calculator import json_utils
from similarity_calculator.entities import repo


def get_tfidf(tag_list: list):
    vectorizer = CountVectorizer(constant.max_df)  # 将文本中的词语转换为词频矩阵
    X = vectorizer.fit_transform(tag_list)  # 计算个词语出现的次数
    transformer = TfidfTransformer()
    tfidf = transformer.fit_transform(X)  # 将词频矩阵X统计成TF-IDF值
    # array = tfidf.toarray()
    # print(tfidf.toarray())
    return tfidf


def calc_cos_sim(descr_vec, commit_vec):
    cos_sim = descr_vec.dot(
        commit_vec) / np.linalg.norm(descr_vec) * np.linalg.norm(commit_vec)
    return cos_sim


def is_forced_match(commit_message: str, description: str):
    id_in_commit = re.search(r'#\d+', commit_message)
    id_in_descr = re.search(r'#\d+', description)
    if id_in_commit is None or id_in_descr is None:
        return False
    return id_in_commit.group() == id_in_descr.group()


def calc_tfidf_list(repos: list):
    # merge description list and commit message list of each repo into tag list
    tag_list = []
    
    for repo_obj in repos:
        tag_list += repo_obj.get_descr_list()
        tag_list += repo_obj.get_matched_commit_message_list()
        tag_list += repo_obj.get_mismatched_commit_message_list()
    
    # calculate tfidf rectangle with
    tfidf = get_tfidf(tag_list).toarray()
    return tfidf


def fill_tfidf_list(repos: list, tfidf: list):
    left_index: int = 0
    for repo_obj in repos:
        # get the sum length of 3 lists and set the right index
        sum_len = repo_obj.get_sum_len()
        right_index = left_index + sum_len
        
        # array[A: B], take B elements start from A index
        tmp_tfidf = tfidf[left_index: right_index]
        repo_obj.set_tfidf_list(tmp_tfidf)
        # move left_index to new point
        left_index += sum_len


def find_proximate_target(cos_sim, descr_list, descr_list_len, descr_vec_list, i, matched_commit_message_list,
                          target_descr,
                          tmp_commit_vec):
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
    return cos_sim, is_forced_matched, target_descr


def match_matched_commits(repo_obj: repo.Repo):
    # get matched commit message list
    matched_commit_message_list: list = repo_obj.get_matched_commit_message_list()
    matched_commit_vec_list: list = repo_obj.tfidf_matched_commits_list
    matched_vec_list_len: int = len(matched_commit_vec_list)
    
    # get ground truth list
    ground_truth_list: list = repo_obj.truth_list
    
    # get description list
    descr_list: list = repo_obj.descr_list
    descr_list_len: int = len(descr_list)
    descr_vec_list: list = repo_obj.tfidf_descr_list
    
    for i in range(0, matched_vec_list_len):
        tmp_commit_vec = matched_commit_vec_list[i]
        ground_truth = ground_truth_list[i]
        cos_sim = 0
        target_descr = 'no__fdse__matched'
        # polarity = ""
        cos_sim, is_forced_matched, target_descr = \
            find_proximate_target(cos_sim, descr_list, descr_list_len, descr_vec_list, i, matched_commit_message_list,
                                  target_descr, tmp_commit_vec)
        
        if target_descr == ground_truth and (cos_sim >= constant.threshold or is_forced_matched is True):
            polarity = 'TP'
            field.TP_count += 1
        elif target_descr != ground_truth and (cos_sim >= constant.threshold or is_forced_matched is True):
            polarity = 'FP'
            field.FP_count += 1
        # if target_descr == ground_truth and cos_sim >= constant.threshold:
        #     polarity = 'TP'
        #     field.TP_count += 1
        # elif target_descr != ground_truth and cos_sim >= constant.threshold:
        #     polarity = 'FP'
        #     field.FP_count += 1
        elif cos_sim < constant.threshold and target_descr == ground_truth:
            polarity = 'FN'
            field.FN_count += 1
        else:
            polarity = 'TN'
            field.TN_count += 1
        
        tmp_dict = {
            "commit message": matched_commit_message_list[i],
            "ground truth": ground_truth,
            "polarity": polarity,
            "target descr": target_descr,
            "cos sim": cos_sim
        }
        
        field.result_list.append(tmp_dict)
        
        if polarity == 'FP':
            field.FP_list.append(tmp_dict)
        elif polarity == 'FN':
            field.FN_list.append(tmp_dict)


def match_mismatched_commits(repo_obj: repo.Repo):
    # get mismatched commit message list
    mismatched_commit_message_list: list = repo_obj.get_mismatched_commit_message_list()
    mismatched_commit_vec_list: list = repo_obj.tfidf_mismatch_commits_list
    mismatched_vec_list_len: int = len(mismatched_commit_vec_list)
    
    # get description list
    descr_list: list = repo_obj.descr_list
    descr_list_len: int = len(descr_list)
    descr_vec_list: list = repo_obj.tfidf_descr_list
    
    for i in range(0, mismatched_vec_list_len):
        tmp_commit_vec = mismatched_commit_vec_list[i]
        cos_sim = 0
        target_descr = 'no__fdse__matched'
        polarity = ''
        cos_sim, is_forced_matched, target_descr = find_proximate_target(cos_sim, descr_list, descr_list_len,
                                                                         descr_vec_list, i,
                                                                         mismatched_commit_message_list, target_descr,
                                                                         tmp_commit_vec)
        
        if cos_sim >= constant.threshold or is_forced_matched is True:
            polarity = 'FP'
            field.FP_count += 1
        elif cos_sim < constant.threshold and is_forced_matched is False:
            polarity = 'TN'
            field.TN_count += 1
        
        tmp_dict = {
            "commit message": mismatched_commit_message_list[i],
            "polarity": polarity,
            "target descr": target_descr,
            "cos sim": cos_sim
        }
        
        field.result_list.append(tmp_dict)
        
        if polarity == 'FP':
            field.FP_list.append(tmp_dict)


def match(repos: list):
    for repo_obj in repos:
        match_matched_commits(repo_obj)
        match_mismatched_commits(repo_obj)


# get mismatched commit message list

repo_list = json_utils.json2repo_list(constant.answer_file)

tfidf_rect = calc_tfidf_list(repo_list)
fill_tfidf_list(repo_list, tfidf_rect)

# threshold set in constant
match(repos=repo_list)

field.result_dict.update(
    {
        'threshold': constant.threshold,
        'precision': field.TP_count / (field.TP_count + field.FP_count),
        'recall': field.TP_count / (field.TP_count + field.FN_count),
        'TP': field.TP_count,
        'FP': field.FP_count,
        'FN': field.FN_count,
        'TN': field.TN_count,
        'list size': len(field.result_list),
        'result list': field.result_list
    }
)

# 加上ground truth和 cos sim之后设置阈值0.3，计算总体recall和precision
# "ground truth": "",
# "polarity": "positive", "negative"
# "truth target cos sim": "",
print('result list')
json_utils.write_json(data=field.result_dict, file=constant.output_file)
json_utils.write_json(data=field.FN_list, file=constant.FN_file)
json_utils.write_json(data=field.FP_list, file=constant.FP_file)

print(len(field.result_list))
print('threshold:' + str(field.result_dict.get("threshold")))
print('precision:' + str(field.result_dict.get("precision")))
print('recall:' + str(field.result_dict.get("recall")))
print('TP: ' + str(field.TP_count))
print('FP: ' + str(field.FP_count))
print('FN: ' + str(field.FN_count))
print('TN: ' + str(field.TN_count))
