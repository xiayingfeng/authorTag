import re
import json
from tracemalloc import start
from constant import *
from repo import *
from pair import *
from commit import *


def get_tag_list(file: str):
    f = open(file=file, encoding='utf-8')
    json_dict = json.load(f)
    tag_list = []
    for item in json_dict['pair list']:
        tag_sub_list = item['description']
        tag_list.append(tag_sub_list)
    return tag_list


multi_commit_pair = 0
# strip PR url Regex.
start_git = '(https://github.com'
end_git = ')'


def trip_git_url(descr: str):
    ret_descr = descr
    # substr_list = []
    start_index = descr.find(start_git)
    while start_index != -1:
        end_index = descr.find(end_git, start_index)
        substring = descr[start_index: end_index + 1]
        ret_descr = ret_descr.replace(substring, '')

        start_index = descr.find(start_git, end_index)

    return ret_descr


def json2repo_list(file: str):
    f = open(file=file, encoding='utf-8')
    json_dict = json.load(f)
    global multi_commit_pair
    repo_list = []
    data_list = json_dict['data']
    for repo_dict in data_list:

        repo_name = repo_dict['repo']
        mis_match = repo_dict['mismatch commits']
        pair_list = []
        for tmp_pair in repo_dict['pair list']:
            pair_id = tmp_pair['pair id']
            descr = tmp_pair['description'].strip()
            descr = trip_git_url(descr)

            if len(tmp_pair['commit list']) > 1:
                multi_commit_pair += 1
            commit_list = []
            for tmp_commit in tmp_pair['commit list']:
                commit_id = tmp_commit['commit id']
                commit_type = tmp_commit['type']

                commit_message = ''
                if commit_type == 'commit message':
                    commit_message = tmp_commit['commit message content']
                # elif commit_type == 'code hunk' and tmp_commit['code hunk content'] is not None:
                #     commit_message = tmp_commit['code hunk content']

                keywords = tmp_commit['keywords']

                commit_list.append(
                    commit(commit_id=commit_id, type=commit_type, commit_message=commit_message, keywords=keywords))

            pair_list.append(
                pair(pair_id=pair_id, descr=descr, commit_list=commit_list))
        tmp_repo = repo(repo_name=repo_name,
                        pair_list=pair_list, mis_list=mis_match)
        repo_list.append(tmp_repo)
    return repo_list


def write_json(data, file: str):
    with open(file=file, mode='w') as f:
        json.dump(data, f)


repo_json = json2repo_list(answer_file)
print(multi_commit_pair)
# print(repo_json)
print('f')
